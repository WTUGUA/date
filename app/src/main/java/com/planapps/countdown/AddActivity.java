package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.ark.adkit.basics.BuildConfig;
import com.ark.adkit.polymers.polymer.ADTool;
import com.planapps.countdown.adapter.DialogItemAdapter;
import com.planapps.countdown.uitl.Event;
import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.AdvanceTime;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_DAILY;
import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_MONTHLY;
import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_WEEKLY;
import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_YEARLY;

public class AddActivity extends AppCompatActivity {
    Calendar ca = Calendar.getInstance();
    int mYear = ca.get(Calendar.YEAR);
    int mMonth = ca.get(Calendar.MONTH);
    int mDay = ca.get(Calendar.DAY_OF_MONTH);
    private Banner add_banner;
    private boolean ischeck;
    private String event;
    private Date date1;
    private String classify;
    private String repeat;
    private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String days;
            if (mMonth + 1 < 10) {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("/").append("0").
                            append(mMonth + 1).append("/").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("/").append("0").
                            append(mMonth + 1).append("/").append(mDay).toString();
                }

            } else {
                if (mDay < 10) {
                    days = new StringBuffer().append(mYear).append("/").
                            append(mMonth + 1).append("/").append("0").append(mDay).toString();
                } else {
                    days = new StringBuffer().append(mYear).append("/").
                            append(mMonth + 1).append("/").append(mDay).toString();
                }

            }
            TextView add_date = findViewById(R.id.add_date);
            add_date.setText(days);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition silde_end = TransitionInflater.from(this).inflateTransition(R.transition.slide_end);
        getWindow().setExitTransition(silde_end);
        getWindow().setEnterTransition(silde_end);
        setContentView(R.layout.activity_add);
        init();
        add_banner=findViewById(R.id.add_banner);
        loadBanner(add_banner);
    }

    private void init() {
        final Date date = new Date(System.currentTimeMillis());
        TextView textView = findViewById(R.id.add_date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String str = simpleDateFormat.format(date);
        textView.setText(str);
        ImageView add_check = findViewById(R.id.add_check);
        final EditText add_event = findViewById(R.id.add_event);
        final TextView add_date = findViewById(R.id.add_date);
        final CheckBox add_button = findViewById(R.id.btn_select);
        final TextView add_re = findViewById(R.id.add_repeat);
        add_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        add_re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(AddActivity.this,UmengCount.REPETITION);
                Intent intent = new Intent(AddActivity.this, RepeatActivity.class);
                String str = add_re.getText().toString();
                intent.putExtra("contentInfo", str);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.slide_right_in,0);
            }
        });
        final TextView add_class = findViewById(R.id.add_class);
        add_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(AddActivity.this,UmengCount.CLASSIFY);
                showBottomDialog();
            }
        });
        add_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Event events = new Event();
                event = add_event.getText().toString();
                try {
                    date1 = StringToDate(add_date.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ischeck = add_button.isChecked();
                if (ischeck) {
                    MobclickAgent.onEvent(AddActivity.this, UmengCount.STICK);
                }
                repeat = add_re.getText().toString();
                classify = add_class.getText().toString();
                events.setDate(date1);
                events.setEvent(event);
                events.setClassify(classify);
                events.setTop(ischeck);
                events.setRepeat(repeat);
                if (ischeck==true) {
                    Event events1 = new Event();
                    events1.setToDefault("top");
                    events1.updateAll();
                }
                    if (event.equals("")) {
                        Toast.makeText(AddActivity.this, "请输入事件名称", Toast.LENGTH_SHORT).show();
                    } else {
                        events.save();
                        int id = events.getId();
                        String str = Integer.toString(id);
                        String str3 = getSharedPreference2("Remind_data");
                        long dateTime = date1.getTime();
                        int remind = 0;
                        switch (str3) {
                            case "事件发生时":
                                remind = 0;
                                break;
                            case "1天前":
                                remind = AdvanceTime.ONE_DAY;
                                break;
                            case "3天前":
                                remind = AdvanceTime.THREE_DAY;
                                break;
                            case "5天前":
                                remind = AdvanceTime.FIVE_DAY;
                                break;
                            case "10天前":
                                remind = AdvanceTime.TEN_DAY;
                                break;
                        }
                        String str4 = repeat;
                        String add_repeat = null;
                        switch (str4) {
                            case "永不":
                                add_repeat = null;
                                break;
                            case "每天":
                                add_repeat = REPEAT_CYCLE_DAILY;
                                break;
                            case "每周":
                                add_repeat = REPEAT_CYCLE_WEEKLY;
                                break;
                            case "每月":
                                add_repeat = REPEAT_CYCLE_MONTHLY;
                                break;
                            case "每年":
                                add_repeat = REPEAT_CYCLE_YEARLY;
                                break;
                        }
                        CalendarEvent calendarEvent = new CalendarEvent(
                                str,
                                classify,
                                event,
                                dateTime,
                                dateTime,
                                remind, add_repeat
                        );
                        int result = CalendarProviderManager.addCalendarEvent(AddActivity.this, calendarEvent);
                        if (result == 0) {
                            Toast.makeText(AddActivity.this, "活动已保存到日历", Toast.LENGTH_SHORT).show();
                        } else if (result == -1) {
                            Toast.makeText(AddActivity.this, "插入失败", Toast.LENGTH_SHORT).show();
                        } else if (result == -2) {
                            Toast.makeText(AddActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent1=new Intent();
                        setResult(5, intent1);
                        finish();
                        overridePendingTransition(0, R.anim.slide_right_out);
                    }
                }
        });
        Toolbar toolbar = findViewById(R.id.add_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.slide_right_out);
            }
        });
    }
    private Date StringToDate(String time) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date;
        date = format.parse(time);
        SimpleDateFormat format1 = new SimpleDateFormat("MM-dd HH:mm");
        String s = format1.format(date);
        return date;
    }

    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog_class_layout, null);
        String[] data = getSharedPreference("key");
        List list = Arrays.asList(data);
        ListView listView = view.findViewById(R.id.add_class_listview);
        final DialogItemAdapter adapter = new DialogItemAdapter(AddActivity.this, list);
        listView.setAdapter(adapter);
        adapter.setSelectPosition(0);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画

        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectPosition(position);
                String str1 = adapter.getItem(position).toString();
                TextView textView1 = findViewById(R.id.add_class);
                textView1.setText(str1);
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String Info = data.getStringExtra("contentInfo");
        TextView add_repeat = findViewById(R.id.add_repeat);
        add_repeat.setText(Info);
    }

    public String[] getSharedPreference(String key) {
        String regularEx = "#";
        String[] str = null;
        SharedPreferences sp = getSharedPreferences("data", Context.MODE_PRIVATE);
        String values;
        values = sp.getString(key, "");
        str = values.split(regularEx);
        return str;
    }

    public String getSharedPreference2(String key) {
        SharedPreferences sp = getSharedPreferences("Remind_data", Context.MODE_PRIVATE);
        String str = sp.getString(key, "事件发生时");
        return str;
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
    public void loadBanner(ViewGroup adContainer) {


//        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//         String DEVICE_ID = tm.getDeviceId();
        ADTool.initialize(new ADTool.Builder()
                //.setStrategy(Strategy.cycle)//按sort轮流排序策略
                //.setLoadOtherWhenVideoDisable(true)//视频广告失败后尝试其他原生横图广告填充
                //.setLocalConfig(JsonUtils.getJson(this,"localconfig.json"))//使用本地json字符串配置
                .setDebugMode(BuildConfig.DEBUG)//调试模式（日志打印，平台标识）
//                .setDeviceId(DEVICE_ID)
                .build());

        ADTool.getADTool().getManager()
                .getNativeWrapper()
                .loadBannerView(AddActivity.this, adContainer);
    }
}
