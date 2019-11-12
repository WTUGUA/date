package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.planapps.countdown.adapter.ListViewAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.List;

public class RemindActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remind);
        String[] data={"事件发生时","1天前","3天前","5天前","10天前"};
        String str1=getSharedPreference("Remind_data");
        List list = Arrays.asList(data);
        ListView listView = findViewById(R.id.remind_list_view);
        final ListViewAdapter adapter = new ListViewAdapter(RemindActivity.this,list);
        listView.setAdapter(adapter);
        adapter.setSelectPosition(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectPosition(position);
                switch (position){
                    case 0:
                        MobclickAgent.onEvent(RemindActivity.this,UmengCount.REMIND_TIME);
                        break;
                    case 1:
                        MobclickAgent.onEvent(RemindActivity.this,UmengCount.REMIND_TIME_1);
                        break;
                    case 2:
                        MobclickAgent.onEvent(RemindActivity.this,UmengCount.REMIND_TIME_3);
                        break;
                    case 3:
                        MobclickAgent.onEvent(RemindActivity.this,UmengCount.REMIND_TIME_5);
                        break;
                    case 4:
                        MobclickAgent.onEvent(RemindActivity.this,UmengCount.REMIND_TIME_10);
                        break;
                }
                String str=adapter.getItem(position).toString();
                setSharedPreference("Remind_data",str);
                Intent data = new Intent();
                // 设置要回传的数据
                data.putExtra("contentInfo",str);
                // 设置回传码和Intent
                setResult(2, data);
                finish();
                overridePendingTransition(0,R.anim.slide_right_out);
            }
        });
        Toolbar toolbar=findViewById(R.id.remind_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public String getSharedPreference(String key) {
        SharedPreferences sp = getSharedPreferences("Remind_data", Context.MODE_PRIVATE);
        String str = sp.getString(key, "事件发生时");
        return str;
    }

    public void setSharedPreference(String key, String values) {
        SharedPreferences sp = getSharedPreferences("Remind_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, values);
        et.commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(RemindActivity.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(RemindActivity.this);
    }
}
