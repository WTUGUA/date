package com.planapps.countdown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ark.adkit.basics.models.OnSplashImpl;
import com.ark.adkit.polymers.polymer.ADTool;
import com.umeng.analytics.MobclickAgent;
//广告展示
public class SplashActivity extends AppCompatActivity {
    private FrameLayout flSplash;

    private FrameLayout flContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        flSplash = findViewById(R.id.fl_splash);
        flContainer = findViewById(R.id.adContainer);
        loadSplash(flContainer, flSplash);

        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */
    }

    /**
     * 加载开屏广告
     *
     * @param adContainer 广告展示容器
     * @param rootView    跳过按钮所在父容器(根容器，最好和广告容器分开(不要使用LinearLayout，否则跳过按钮可能不可见)
     */
    public void loadSplash(ViewGroup adContainer, ViewGroup rootView) {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // String DEVICE_ID = tm.getDeviceId();
        ADTool.initialize(new ADTool.Builder()
                //.setStrategy(Strategy.cycle)//按sort轮流排序策略
                //.setLoadOtherWhenVideoDisable(true)//视频广告失败后尝试其他原生横图广告填充
                //.setLocalConfig(JsonUtils.getJson(this,"localconfig.json"))//使用本地json字符串配置
                //.setDebugMode(BuildConfig.DEBUG)//调试模式（日志打印，平台标识）
//                .setDeviceId(DEVICE_ID)
                .build());
        ADTool.getADTool().getManager()
                .getSplashWrapper()
                .setMills(3000, 5000, 500)//设置倒计时时间
                .loadSplash(SplashActivity.this, adContainer, rootView, new OnSplashImpl() {
                    @Override
                    public void onAdDisplay(@NonNull String platform) {
                        super.onAdDisplay(platform);
                        flContainer.setVisibility(View.VISIBLE);  //广告可见
                    }

                    @Override
                    public void onAdTimeTick(long tick) {

                    }

                    @Override
                    public void onAdShouldLaunch() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAdClosed(@NonNull String platform) {
                        super.onAdClosed(platform);
                    }

                    @Override
                    public void onAdDisable() {
                        super.onAdDisable();
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        //友盟埋点
        super.onResume();
        MobclickAgent.onResume(SplashActivity.this);

    }

    @Override
    protected void onPause() {
        //友盟埋点
        super.onPause();
        MobclickAgent.onPause(SplashActivity.this);
    }


}
