package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;

import android.view.Window;
import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.planapps.countdown.adapter.IosDialog;
import com.planapps.countdown.adapter.ListViewAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;


public class fenleiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        final Transition silde_top = TransitionInflater.from(this).inflateTransition(R.transition.slide_top);
        getWindow().setExitTransition(silde_top);
        getWindow().setEnterTransition(silde_top);
        setContentView(R.layout.activity_fenlei);
        String[] data = getSharedPreference("key");
        List<String> list1=new ArrayList<String>();
        list1.add("全部");
        List list = Arrays.asList(data);
        list1.addAll(list);
        ListView listView = findViewById(R.id.fenlei_list_view);
        final ListViewAdapter adapter = new ListViewAdapter(fenleiActivity.this, list1);
        adapter.setSelectPosition(0);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectPosition(position);
                switch (position){
                    case 0:
                        break;
                    case 1:
                        MobclickAgent.onEvent(fenleiActivity.this,UmengCount.BIRTHDAY);
                        break;
                    case 2:
                        MobclickAgent.onEvent(fenleiActivity.this,UmengCount.TRAVEL);
                        break;
                    case 3:
                        MobclickAgent.onEvent(fenleiActivity.this,UmengCount.REPAYMENT_DAY);
                        break;
                    case 4:
                        MobclickAgent.onEvent(fenleiActivity.this,UmengCount.OTHER);
                        break;
                }
                String str=adapter.getItem(position).toString();
                Intent data = new Intent();
                // 设置要回传的数据
                data.putExtra("title_data",str);
                // 设置回传码和Intent
                setResult(0, data);
                finish();
                overridePendingTransition(0,R.anim.slide_top_out);
            }
        });
        TextView add_title = findViewById(R.id.title);
        add_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(fenleiActivity.this,UmengCount.ADD_CLASSIFY);
                showDialog();
            }
        });
        ImageView imageView=findViewById(R.id.fl_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.slide_top_out);
            }
        });
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

    public void showDialog() {
        //实例化自定义对话框
        final IosDialog mdialog = new IosDialog(this);
        mdialog.setTv("新增分类");
        mdialog.setTv2("请输入要新增的分类名称");
        //对话框中退出按钮事件
        mdialog.setOnExitListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果对话框处于显示状态
                if (mdialog.isShowing()) {
                    //关闭对话框
                    String str = mdialog.getText();
                    if(str.equals("")){
                        Toast.makeText(fenleiActivity.this, "请输入分类名", Toast.LENGTH_SHORT).show();
                    }else {
                        String[] data = getSharedPreference("key");
                        List<String> list = Arrays.asList(data);
                        List<String> arrList = new ArrayList(list);
                        arrList.add(str);
                        String[] arr = arrList.toArray(new String[arrList.size()]);
                        setSharedPreference("key", arr);
                        ListView listView = findViewById(R.id.fenlei_list_view);
                        ListViewAdapter adapter = new ListViewAdapter(fenleiActivity.this, list);
                        listView.setAdapter(adapter);
                        adapter.add(str);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(fenleiActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        mdialog.dismiss();
                    }
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
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(fenleiActivity.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(fenleiActivity.this);
    }
}
