package com.planapps.countdown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.planapps.countdown.uitl.Event;
import com.orhanobut.hawk.Hawk;
import com.umeng.analytics.MobclickAgent;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.planapps.countdown.adapter.EventAdapter.daysBetween;
import static com.planapps.countdown.adapter.EventAdapter.getWeekDay;


public class DetailsActivity extends AppCompatActivity {
    public static DetailsActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition silde_end = TransitionInflater.from(this).inflateTransition(R.transition.slide_end);
        getWindow().setExitTransition(silde_end);
        getWindow().setEnterTransition(silde_end);
        setContentView(R.layout.activity_details);
        String path = Hawk.get("KEY","");
        if(TextUtils.isEmpty(path)){
            Toast.makeText(DetailsActivity.this,"没有找到图片路径",Toast.LENGTH_SHORT).show();
        }else{
            Bitmap bitmap= BitmapFactory.decodeFile(path);
            Drawable drawable= new BitmapDrawable(bitmap);
            FrameLayout frame=findViewById(R.id.Frame);
            frame.setBackground(drawable);
        }
        detailshow();
        ImageView share=findViewById(R.id.share);
        ImageView edit=findViewById(R.id.edit);
        ImageView pic=findViewById(R.id.pic);
        ImageView back=findViewById(R.id.back);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                int id= bundle.getInt("event_data");
                Event  event1=LitePal.find(Event.class,id);
                String text=event1.toString();
                String title="标配";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, text);
                DetailsActivity.this.startActivity(Intent.createChooser(intent, title));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                int id= bundle.getInt("event_data");
                Event  event1=LitePal.find(Event.class,id);
                Intent intent = new Intent(DetailsActivity.this,EditActivity.class);
                intent.putExtra("edit_data",event1.getId());
                startActivityForResult(intent,0);
            }
        });
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetailsActivity.this,Manifest.permission
                        .WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(DetailsActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    //执行启动相册的方法
                    Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,1);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,R.anim.slide_right_out);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO 自动生成的方法存根
        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //查询我们需要的数据
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmaps=BitmapFactory.decodeFile(picturePath);
            saveBitmap(bitmaps);
            Drawable drawable= new BitmapDrawable(bitmaps);
            FrameLayout frame=findViewById(R.id.Frame);
            //拿到了图片的路径picturePath可以自行使用
            frame.setBackground(drawable);
            LayoutInflater factory=LayoutInflater.from(DetailsActivity.this);
            View layout=factory.inflate(R.layout.activity_main,null);
            FrameLayout frameLayout=layout.findViewById(R.id.backgroundmap);
            frameLayout.setBackground(drawable);
        }if(requestCode==0){
            finish();
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
    public void saveBitmap(Bitmap bitmap) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "background");       //目录名
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = "background" + ".jpg";                     //背景图片文件名
        File file = new File(appDir, fileName);
        if (file.exists()) {                                         //每次保存都把旧的背景图片覆盖成新的
            try {
                file.delete();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Hawk.put("KEY",file.getAbsolutePath());  //把背景图片的路径保存下来，待加载时取出
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void detailshow(){
        Bundle bundle = getIntent().getExtras();
        int id= bundle.getInt("event_data");
        Event  event1=LitePal.find(Event.class,id);
        TextView textView1=findViewById(R.id.text1);
        TextView textView2=findViewById(R.id.text2);
        TextView textView3=findViewById(R.id.text3);
        TextView textView4=findViewById(R.id.text4);
        String str=getWeekDay(event1.getDate().toString());
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        textView4.setText(sdf.format(event1.getDate())+"("+str+")");
        Date date1 = new Date(System.currentTimeMillis());
        Date date2 =event1.getDate();
        int  daysBetween=daysBetween(date1,date2);
        textView1.setText(String.valueOf(Math.abs(daysBetween)));
        if(daysBetween>0){
            textView2.setText("天后");
        }else{
            textView2.setText("天前");
        }
        textView3.setText(event1.getEvent());
    }
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(DetailsActivity.this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(DetailsActivity.this);
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
