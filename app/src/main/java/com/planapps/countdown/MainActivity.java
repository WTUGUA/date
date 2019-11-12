package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.planapps.countdown.adapter.EventAdapter;

import com.planapps.countdown.uitl.Event;
import com.planapps.countdown.uitl.maintoolbar;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.umeng.analytics.MobclickAgent;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.planapps.countdown.adapter.EventAdapter.daysBetween;
import static com.planapps.countdown.adapter.EventAdapter.getWeekDay;

public class MainActivity extends AppCompatActivity {


    private boolean run = false;
    private final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR}, 1);
        }
        if (Build.VERSION.SDK_INT >= 23) {
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, mPermissionList, 123);
        }
        //存储图片
        Hawk.init(this)
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.MEDIUM)
                .setStorage(HawkBuilder.newSharedPrefStorage(this))
                .setLogLevel(LogLevel.FULL)
                .build();
        String path = Hawk.get("KEY", "");
        if (TextUtils.isEmpty(path)) {
        } else {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            Drawable drawable = new BitmapDrawable(bitmap);
            FrameLayout frame = findViewById(R.id.backgroundmap);
            frame.setBackground(drawable);
        }
        mianshow();
        SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
        // 判断是不是首次登录
        if (preferences.getBoolean("firstStart", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstStart", false);
            editor.commit();
            String[] value = { "生日", "旅行", "还款日", "其他"};
            setSharedPreference("key", value);
        }
        init();
        String sortNot = getSharedPreference("Setting_data");
        if (sortNot.equals("按照目标日期排序")) {
            List<Event> all = LitePal.findAll(Event.class);
            List<Event> event2 = sort(all);
            showListView(event2);
        } else {
            List<Event> all = LitePal.findAll(Event.class);
            showListView(all);
        }
        run = true;
        handler.postDelayed(task, 1000);
    }

    private void init() {
        maintoolbar maintoolbar = findViewById(R.id.maintoolbar);
        maintoolbar.setOnLeftButtonClickListener(new maintoolbar.OnLeftButtonClickListener() {
            @Override
            public void onClick() {
                MobclickAgent.onEvent(MainActivity.this,UmengCount.SETTING);
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
        maintoolbar.setOnRightButtonClickListener(new maintoolbar.OnRightButtonClickListener() {
            @Override
            public void onClick() {
                MobclickAgent.onEvent(MainActivity.this,UmengCount.ADD_INCIDENT);
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("add_data",0);
                startActivityForResult(intent,5);

            }
        });
        final TextView title = findViewById(R.id.title);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(MainActivity.this,UmengCount.CLASSIFY);
                Intent intent = new Intent(MainActivity.this, fenleiActivity.class);
                intent.putExtra("title_data", title.getText().toString());
                startActivityForResult(intent, 0, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });
    }

    private void mianshow() {
        Event fistevent = LitePal.where("top=?","1").findFirst(Event.class);
        if (fistevent != null) {
            TextView textView1 = findViewById(R.id.text1);
            TextView textView2 = findViewById(R.id.text2);
            TextView textView3 = findViewById(R.id.text3);
            TextView textView4 = findViewById(R.id.text4);
            String str = getWeekDay(fistevent.getDate().toString());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            textView4.setText(sdf.format(fistevent.getDate()) + "(" + str + ")");
            Date date1 = new Date(System.currentTimeMillis());
            Date date2 = fistevent.getDate();
            int daysBetween = daysBetween(date1, date2);
            textView1.setText(String.valueOf(Math.abs(daysBetween)));
            if (daysBetween > 0) {
                textView2.setText("天后");
            } else {
                textView2.setText("天前");
            }
            textView3.setText(fistevent.getEvent());
        } else {
            Date date=new Date(System.currentTimeMillis());
            String weekofday=getWeekDay(date.toString());
            TextView textView1 = findViewById(R.id.text1);
            TextView textView2 = findViewById(R.id.text2);
            TextView textView3 = findViewById(R.id.text3);
            TextView textView4 = findViewById(R.id.text4);
            if (weekofday.equals("周日")) {
                textView1.setText("5");
                textView2.setText("天后");
                textView3.setText("周末");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,6);
                Date date1=c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf.format(date1);
                textView4.setText(date2+"周六");
                SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
                if (preferences.getBoolean("firstStart", true)) {
                    Event events = new Event();
                    events.setEvent("周末");
                    events.setClassify("无");
                    events.setTop(false);
                    events.setDate(c.getTime());
                    events.setRepeat(null);
                    events.save();
                }
            }
            if (weekofday.equals("周一")) {
                textView1.setText("4");
                textView2.setText("天后");
                textView3.setText("周末");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,5);
                Date date1=c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf.format(date1);
                textView4.setText(date2+"周六");
                SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
                if (preferences.getBoolean("firstStart", true)) {
                    Event events = new Event();
                    events.setEvent("周末");
                    events.setTop(false);
                    events.setClassify("无");
                    events.setDate(c.getTime());
                    events.setRepeat(null);
                    events.save();
                }
            }
            if (weekofday.equals("周二")) {
                textView1.setText("3");
                textView2.setText("天后");
                textView3.setText("周末");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,4);
                Date date1=c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf.format(date1);
                textView4.setText(date2+"周六");
                SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
                if (preferences.getBoolean("firstStart", true)) {
                    Event events = new Event();
                    events.setEvent("周末");
                    events.setClassify("无");
                    events.setTop(false);
                    events.setDate(c.getTime());
                    events.setRepeat(null);
                    events.save();
                }
            }
            if (weekofday.equals("周三")) {
                textView1.setText("2");
                textView2.setText("天后");
                textView3.setText("周末");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,3);
                Date date1=c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf.format(date1);
                textView4.setText(date2+"周六");
                SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
                if (preferences.getBoolean("firstStart", true)) {
                    Event events = new Event();
                    events.setEvent("周末");
                    events.setClassify("无");
                    events.setTop(false);
                    events.setDate(c.getTime());
                    events.setRepeat(null);
                    events.save();
                }
            }
            if (weekofday.equals("周四")) {
                textView1.setText("1");
                textView2.setText("天后");
                textView3.setText("周末");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,2);
                Date date1=c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf.format(date1);
                textView4.setText(date2+"周六");
                SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
                if (preferences.getBoolean("firstStart", true)) {
                    Event events = new Event();
                    events.setEvent("周末");
                    events.setClassify("无");
                    events.setTop(false);
                    events.setDate(c.getTime());
                    events.setRepeat(null);
                    events.save();
                }
            }
            if (weekofday.equals("周五")) {
                textView1.setText("0");
                textView2.setText("天后");
                textView3.setText("周末");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,1);
                Date date1=c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf.format(date1);
                textView4.setText(date2+"周六");
                SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
                if (preferences.getBoolean("firstStart", true)) {
                    Event events = new Event();
                    events.setEvent("周末");
                    events.setClassify("无");
                    events.setTop(false);
                    events.setDate(c.getTime());
                    events.setRepeat(null);
                    events.save();
                }
            }
            if (weekofday.equals("周六")) {
                textView1.setText("6");
                textView2.setText("天后");
                textView3.setText("周末");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH,7);
                Date date1=c.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date2 = sdf.format(date1);
                textView4.setText(date2+"周六");
                SharedPreferences preferences = getSharedPreferences("guideActivity", MODE_PRIVATE);
                if (preferences.getBoolean("firstStart", true)) {
                    Event events = new Event();
                    events.setEvent("周末");
                    events.setClassify("无");
                    events.setTop(false);
                    events.setDate(c.getTime());
                    events.setRepeat(null);
                    events.save();
                }
            }
        }
    }

    public void setSharedPreference(String key, String[] values) {
        String regularEx = "#";
        String str = "";
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        if (values != null && values.length > 0) {
            for (String value : values) {
                str += value;
                str += regularEx;
            }
            SharedPreferences.Editor et = sp.edit();
            et.putString(key, str);
            et.commit();
        }
    }

    public String getSharedPreference(String key) {
        SharedPreferences sp = getSharedPreferences("Setting_data", Context.MODE_PRIVATE);
        String values;
        values = sp.getString(key, "按照添加日期排序");
        return values;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==4){
            List<Event> all = LitePal.findAll(Event.class);
            showListView(all);
        }
        if(requestCode==5){
            List<Event> all = LitePal.findAll(Event.class);
            showListView(all);
        }
        if (requestCode == 0 && resultCode == 0 && null != data) {
            String Info = data.getStringExtra("title_data");
            TextView fenlei_title = findViewById(R.id.title);
            fenlei_title.setText(Info);
            String sortNot = getSharedPreference("Setting_data");
            if (Info.equals("全部")) {
                if (sortNot.equals("按照目标日期排序")) {
                    List<Event> all2 = LitePal.findAll(Event.class);
                    List<Event> event2 = sort(all2);
                    showListView(event2);
                } else {
                    List<Event> all = LitePal.findAll(Event.class);
                    showListView(all);
                }
            } else {
                if (sortNot.equals("按照目标日期排序")) {
                    List<Event> all3 = LitePal.where("classify=?", Info).find(Event.class);
                    List<Event> event2 = sort(all3);
                    showListView(event2);
                } else {
                    List<Event> all = LitePal.where("classify=?", Info).find(Event.class);
                    showListView(all);
                }
            }
        }
    }

    public void showListView(List<Event> all) {
        final EventAdapter adapter = new EventAdapter(MainActivity.this, R.layout.listitem, all);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event2 = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("event_data", event2.getId());
                startActivityForResult(intent,4);
            }
        });
    }

    public List<Event> sort(List<Event> list) {
        list = new ArrayList<Event>(list);
        Collections.sort(list, new Comparator<Event>() {
            /**
             * @param lhs
             * @param rhs
             * @return an integer < 0 if lhs is less than rhs, 0 if they are
             * equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间
             */
            @Override
            public int compare(Event lhs, Event rhs) {
                Date date1 = lhs.getDate();
                Date date2 = rhs.getDate();
                // 对日期字段进行升序，如果欲降序可采用after方法
                if (date1.before(date2)) {
                    return 1;
                }
                return -1;
            }
        });
        return list;
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    // 第一次按下返回键的事件
    private long firstPressedTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstPressedTime < 2000) {
            super.onBackPressed();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            firstPressedTime = System.currentTimeMillis();
        }
    }
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (run) {
                handler.postDelayed(this, 1000);
            }
        }
    };

}
