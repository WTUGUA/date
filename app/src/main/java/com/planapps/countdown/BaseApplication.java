package com.planapps.countdown;

import android.app.Application;
import android.content.Context;


import com.adesk.polymers.common.CommonTool;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import org.litepal.LitePal;

public class BaseApplication extends Application {


    public static Context context;

    @Override
    public void onCreate() {
        CommonTool.showUpdate(this);
        //初始化LitePal
        LitePal.initialize(this);
        // 初始化SDK
        UMConfigure.init(this,"5dc3e81f4ca3571b9b0008d9","Umeng", UMConfigure.DEVICE_TYPE_PHONE, null);
        // 选用LEGACY_AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL);
        // 支持在子进程中统计自定义事件
        UMConfigure.setProcessEvent(true);
        // 打开统计SDK调试模式
        UMConfigure.setLogEnabled(true);

        super.onCreate();
        context = getApplicationContext();
        /**
         * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
         * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
         * UMConfigure.init调用中appkey和channel参数请置为null）。
         */

    }

    public static Context getContext(){return context;}
}