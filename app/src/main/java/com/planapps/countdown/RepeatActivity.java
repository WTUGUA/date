package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toolbar;

import com.planapps.countdown.adapter.ListViewAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.List;

public class RepeatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
        String[] data={"永不","每天","每周","每月","每年"};
        List list = Arrays.asList(data);
        ListView listView = findViewById(R.id.repeat_list_view);
        final ListViewAdapter adapter = new ListViewAdapter(RepeatActivity.this,list);
        listView.setAdapter(adapter);
        adapter.setSelectPosition(1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectPosition(position);
                switch (position){
                    case 0:
                        break;
                    case 1:
                        MobclickAgent.onEvent(RepeatActivity.this,UmengCount.EVERY_DAY);
                        break;
                    case 2:
                        MobclickAgent.onEvent(RepeatActivity.this,UmengCount.EVERY_WEEK);
                        break;
                    case 3:
                        MobclickAgent.onEvent(RepeatActivity.this,UmengCount.EVERY_MONTH);
                        break;
                    case 4:
                        MobclickAgent.onEvent(RepeatActivity.this,UmengCount.EVERY_YEAR);
                        break;
                }
                String str=adapter.getItem(position).toString();
                Intent data = new Intent();
                // 设置要回传的数据
                data.putExtra("contentInfo",str);
                // 设置回传码和Intent
                setResult(0, data);
                finish();
                overridePendingTransition(0,R.anim.slide_right_out);
            }
        });
        Toolbar toolbar=findViewById(R.id.repeat_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.slide_right_out);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(RepeatActivity.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(RepeatActivity.this);
    }
}
