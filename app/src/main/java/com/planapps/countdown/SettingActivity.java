package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.ark.adkit.basics.BuildConfig;
import com.ark.adkit.basics.configs.Strategy;
import com.ark.adkit.polymers.polymer.ADTool;
import com.planapps.countdown.adapter.DialogItemAdapter;
import com.planapps.countdown.adapter.RemindDialog;
import com.kyle.calendarprovider.calendar.CalendarProviderManager;
import com.umeng.analytics.MobclickAgent;
import com.youth.banner.Banner;

import java.util.Arrays;
import java.util.List;

public class SettingActivity extends AppCompatActivity {
    private Banner bannerContainer;
    private ClipboardManager cm;
    private ClipData mClipData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide_start = TransitionInflater.from(this).inflateTransition(R.transition.slide_start);
        getWindow().setExitTransition(slide_start);
        getWindow().setEnterTransition(slide_start);
        setContentView(R.layout.activity_setting);
        Toolbar set_toolbar = findViewById(R.id.setting_toolbar);
        set_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.slide_left_out);
            }
        });
        TextView textView = findViewById(R.id.setting_time);
        String str3 = getSharedPreference2("Remind_data");
        textView.setText(str3);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(SettingActivity.this, UmengCount.MATTER_REMIND);
                Intent intent = new Intent(SettingActivity.this, RemindActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.slide_right_in,0);
            }
        });
        TextView textView1 = findViewById(R.id.settig_sort);
        String str2 = getSharedPreference("Setting_data");
        textView1.setText(str2);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomDialog();
            }
        });
        CheckBox setting_select = findViewById(R.id.setting_select);
        boolean select = getSharedPreference3("Setting_select");
        setting_select.setChecked(select);
        setting_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showDialog();
                setSharedPreference("Setting_select", isChecked);
            }
        });
        TextView textView3 = findViewById(R.id.setting_advice);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(SettingActivity.this, UmengCount.FEEDBACK_YOUWANT);
                //获取剪贴板管理器：
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                mClipData = ClipData.newPlainText("Label", "3607799199");
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(SettingActivity.this, "复制成功", Toast.LENGTH_SHORT).show();

            }
        });
        TextView textView2 = findViewById(R.id.setting_point);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(SettingActivity.this, UmengCount.EVALUATE_US);
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(SettingActivity.this, "您的手机没有安装应用市场", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        //关于
        RelativeLayout textView4 = findViewById(R.id.setting_about);
        textView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_in,0);
            }
        });
        RelativeLayout layout = findViewById(R.id.setting_point2);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(SettingActivity.this, UmengCount.EVALUATE_US);
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(SettingActivity.this, "您的手机没有安装应用市场", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        bannerContainer = findViewById(R.id.banner);
        //广告加载
        loadSplash(bannerContainer);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String Info = data.getStringExtra("contentInfo");
        TextView set_repeat = findViewById(R.id.setting_time);
        set_repeat.setText(Info);
    }

    private void showBottomDialog() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(this, R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(this, R.layout.dialog_class_layout, null);
        String[] data = {"按照添加日期排序", "按照目标日期排序"};
        List list = Arrays.asList(data);
        TextView textView = view.findViewById(R.id.dialog_text);
        textView.setText("请选择事件排序方式");
        ListView listView = view.findViewById(R.id.add_class_listview);
        final DialogItemAdapter adapter = new DialogItemAdapter(SettingActivity.this, list);
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
                TextView textView1 = findViewById(R.id.settig_sort);
                textView1.setText(str1);
                setSharedPreference("Setting_data", str1);
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

    public String getSharedPreference(String key) {
        SharedPreferences sp = getSharedPreferences("Setting_data", Context.MODE_PRIVATE);
        String str = sp.getString(key, "按照添加顺序排序");
        return str;
    }

    public void setSharedPreference(String key, String values) {
        SharedPreferences sp = getSharedPreferences("Setting_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, values);
        et.commit();
    }

    public String getSharedPreference2(String key) {
        SharedPreferences sp = getSharedPreferences("Remind_data", Context.MODE_PRIVATE);
        String str = sp.getString(key, "事件发生时");
        return str;
    }

    public boolean getSharedPreference3(String key) {
        SharedPreferences sp = getSharedPreferences("Setting_select", Context.MODE_PRIVATE);
        boolean str = sp.getBoolean(key, true);
        return str;
    }

    public void setSharedPreference(String key, boolean values) {
        SharedPreferences sp = getSharedPreferences("Setting_select", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putBoolean(key, values);
        et.commit();
    }

    public void showDialog() {
        //实例化自定义对话框
        final RemindDialog mdialog = new RemindDialog(this);
        //对话框中退出按钮事件
        mdialog.setOnExitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果对话框处于显示状态
                if (mdialog.isShowing()) {
                    //关闭对话框
                    CalendarProviderManager.startCalendarForIntentToInsert(SettingActivity.this, System.currentTimeMillis(),
                            System.currentTimeMillis() + 60000, "修改提醒", "修改提醒请修改日历设置界面提醒设置", "修改提醒请修改日历设置界面提醒设置",
                            false);
                    mdialog.dismiss();
                }
            }
        });
        //对话框中取消按钮事件
        mdialog.setOnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdialog != null && mdialog.isShowing()) {
                    mdialog.dismiss();
                }
            }
        });
        mdialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(SettingActivity.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(SettingActivity.this);
    }

    public void loadSplash(ViewGroup adContainer) {


//        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//         String DEVICE_ID = tm.getDeviceId();
        ADTool.initialize(new ADTool.Builder()
                .setStrategy(Strategy.cycle)//按sort轮流排序策略
                //.setLoadOtherWhenVideoDisable(true)//视频广告失败后尝试其他原生横图广告填充
                //.setLocalConfig(JsonUtils.getJson(this,"localconfig.json"))//使用本地json字符串配置
                .setDebugMode(BuildConfig.DEBUG)//调试模式（日志打印，平台标识）
//                .setDeviceId(DEVICE_ID)
                .build());

        ADTool.getADTool().getManager()
                .getNativeWrapper()
                .loadBannerView(SettingActivity.this, adContainer);

    }
}

