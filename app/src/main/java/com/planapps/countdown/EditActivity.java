package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.planapps.countdown.adapter.DialogItemAdapter;
import com.planapps.countdown.adapter.IosDialog;
import com.planapps.countdown.uitl.Event;
import com.kyle.calendarprovider.calendar.AdvanceTime;
import com.kyle.calendarprovider.calendar.CalendarEvent;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;
import com.umeng.analytics.MobclickAgent;

import org.litepal.LitePal;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_DAILY;
import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_MONTHLY;
import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_WEEKLY;
import static com.planapps.countdown.uitl.RRule.REPEAT_CYCLE_YEARLY;

public class EditActivity extends AppCompatActivity {
    private static String CALENDER_URL = "content://com.android.calendar/calendars";
    private static String CALENDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDER_REMINDER_URL = "content://com.android.calendar/reminders";
    Calendar ca = Calendar.getInstance();
    int mYear = ca.get(Calendar.YEAR);
    int mMonth = ca.get(Calendar.MONTH);
    int mDay = ca.get(Calendar.DAY_OF_MONTH);
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
            TextView edit_date = findViewById(R.id.edit_date);
            edit_date.setText(days);
        }
    };

    private static Date StringToDate(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(time, pos);
        return strtodate;
    }

    private static String DateToString(Date time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String str = sdf.format(time);
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition silde_end = TransitionInflater.from(this).inflateTransition(R.transition.slide_end);
        getWindow().setExitTransition(silde_end);
        getWindow().setEnterTransition(silde_end);
        setContentView(R.layout.activity_edit);
        Bundle bundle = getIntent().getExtras();
        int id= bundle.getInt("edit_data");
        final Event event1=LitePal.find(Event.class,id);
        final CheckBox checkBox = findViewById(R.id.btn_select);
        checkBox.setChecked(event1.isTop());
        final TextView textView = findViewById(R.id.edit_event);
        textView.setText(event1.getEvent());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        final TextView textView1 = findViewById(R.id.edit_repeat);
        textView1.setText(event1.getRepeat());
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(EditActivity.this,UmengCount.REPETITION);
                Intent intent = new Intent(EditActivity.this, RepeatActivity.class);
                String str=textView1.getText().toString();
                intent.putExtra("contentInfo",str);
                startActivityForResult(intent, 1);
                overridePendingTransition(R.anim.slide_right_in,0);
            }
        });
        final TextView textView3 = findViewById(R.id.edit_class);
        textView3.setText(event1.getClassify());
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(EditActivity.this,UmengCount.CLASSIFY);
                showBottomDialog();
            }
        });
        final TextView textView2 = findViewById(R.id.edit_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        textView2.setText(sdf.format(event1.getDate()));
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditActivity.this, onDateSetListener, mYear, mMonth, mDay).show();
            }
        });
        ImageView imageView = findViewById(R.id.edit_check);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                int id= bundle.getInt("edit_data");
                String str=Integer.toString(id);
                deleteCalendarEvent(EditActivity.this,str);
                String event = textView.getText().toString();
                String repeat = textView1.getText().toString();
                String classify = textView3.getText().toString();
                String data = textView2.getText().toString();
                Date date = StringToDate(data);
                boolean top = checkBox.isChecked();
                if(top){
                    MobclickAgent.onEvent(EditActivity.this,UmengCount.STICK);
                }
                if(event.equals("")){
                    Toast.makeText(EditActivity.this, "请输入事件名称", Toast.LENGTH_SHORT).show();
                }else {
                    if (top==true) {
                        Event events1 = new Event();
                        events1.setToDefault("top");
                        events1.updateAll();
                    }
                    Event updateEvent = new Event();
                    updateEvent.setEvent(event);
                    updateEvent.setRepeat(repeat);
                    updateEvent.setClassify(classify);
                    updateEvent.setDate(date);
                    updateEvent.setTop(top);
                    updateEvent.update(event1.getId());
                    long dateTime = date.getTime();
                    String str3 = getSharedPreference2("Remind_data");
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
                    int id1 = event1.getId();
                    String str1 = Integer.toString(id1);
                    CalendarEvent calendarEvent = new CalendarEvent(
                            str1,
                            classify,
                            event,
                            dateTime,
                            dateTime,
                            remind, add_repeat
                    );
                    int result = CalendarProviderManager.addCalendarEvent(EditActivity.this, calendarEvent);
                    if (result == 0) {
                        Toast.makeText(EditActivity.this, "插入成功", Toast.LENGTH_SHORT).show();
                    } else if (result == -1) {
                        Toast.makeText(EditActivity.this, "插入失败", Toast.LENGTH_SHORT).show();
                    } else if (result == -2) {
                        Toast.makeText(EditActivity.this, "没有权限", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(EditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    Intent intent1=new Intent();
                    setResult(0, intent1);
                    finish();
                    overridePendingTransition(0, R.anim.slide_right_out);

                }
            }
        });
        Button edit_delete = findViewById(R.id.edit_delete);
        edit_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LitePal.delete(Event.class, event1.getId());
                int id= event1.getId();
                String str=Integer.toString(id);
                deleteCalendarEvent(EditActivity.this,str);
                DetailsActivity.instance.finish();
                finish();
                overridePendingTransition(0, R.anim.slide_right_out);
            }
        });
        Toolbar toolbar=findViewById(R.id.edit_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.slide_right_out);
            }
        });
    }

    public void showDialog() {
        //实例化自定义对话框
        final IosDialog mdialog = new IosDialog(this);
        Bundle bundle = getIntent().getExtras();
        int id= bundle.getInt("event_data");
        Event event1=LitePal.find(Event.class,id);
        if(event1!=null) {
            EditText editText = mdialog.getDialog_edittext();
            editText.setHint(" "+event1.getEvent());
        }else{
            TextView textView=findViewById(R.id.edit_event);
            String str=textView.getText().toString();
            EditText editText = mdialog.getDialog_edittext();
            editText.setHint(" "+str);
        }
        //对话框中退出按钮事件
        mdialog.setOnExitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果对话框处于显示状态
                if (mdialog.isShowing()) {
                    //关闭对话框
                    String str = mdialog.getText();
                    TextView textView = findViewById(R.id.edit_event);
                    textView.setText(str);
                    mdialog.dismiss();
                }
            }
        });
        //对话框中取消按钮事件
        mdialog.setOnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdialog != null && mdialog.isShowing()) {
                    //关闭对话框
                    mdialog.dismiss();
                }
            }
        });
        mdialog.show();
    }

    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog_class_layout, null);
        String[] data = getSharedPreference("key");
        List list = Arrays.asList(data);
        ListView listView = view.findViewById(R.id.add_class_listview);
        final DialogItemAdapter adapter = new DialogItemAdapter(EditActivity.this, list);
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
                TextView textView1 = findViewById(R.id.edit_class);
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
        TextView edit_repeat = findViewById(R.id.edit_repeat);
        edit_repeat.setText(Info);
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
    public static void deleteCalendarEvent(Context context, String title) {
        if (context == null) {
            return;
        }
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);
        try {
            if (eventCursor == null) { //查询返回空值
                return;
            }
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(EditActivity.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(EditActivity.this);
    }
    @Override
    public void onBackPressed() {
             finish();
    }
}

