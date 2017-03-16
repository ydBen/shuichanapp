/* 
 * @ProjectName VideoGoJar
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName EzvizApplication.java
 * @Description 这里对文件进行描述
 * 
 * @author chenxingyf1
 * @data 2014-7-12
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.jit.shuichan;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.videogo.errorlayer.ErrorInfo;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZOpenSDKListener;
import com.videogo.openapi.EzvizAPI;
import com.videogo.util.LogUtil;

import static com.jit.shuichan.http.NetUtils.writeFileToSD;

/**
 * 自定义应用
 * 
 * @author xiaxingsuo
 * 
 */
public class EzvizApplication extends Application {
	// 开放平台申请的APP key & secret key
	// open
//    public static String APP_KEY = "bd9731ed82d6413daeecd9841c54a1cc"; // APP_KEY请替换成自己申请的
//    public static String API_URL = "https://open.ys7.com";
//    public static String WEB_URL = "https://auth.ys7.com";

    private static Context context;
    private static Handler handler;
    private static int mainThreadId;

    public static String APP_KEY = "373f90fa7c534e02bbbf020ade384bf5";
    public static String APP_PUSH_SECRETE = "128e1a9fc38cd5d6a748e8ae3379c173";
    public static String API_URL = "https://open.ys7.com";
    public static String WEB_URL = "https://auth.ys7.com";




	@Override
	public void onCreate() {
		super.onCreate();

        context = getApplicationContext();
        handler = new Handler();
        mainThreadId = android.os.Process.myTid();



        writeFileToSD();

        /**********国内版本初始化EZOpenSDK**************/
        {
            /**
             * sdk日志开关，正式发布需要去掉
             */
            EZOpenSDK.showSDKLog(true);

            /**
             * 设置是否支持P2P取流,详见api
             */
            EZOpenSDK.enableP2P(true);

            /**
             * APP_KEY请替换成自己申请的
             */
            EZOpenSDK.initLib(this, APP_KEY, "");
        }

        /**********海外版本初始化EZGlobalSDK**************/
        {
            /**
             * sdk日志开关，正式发布需要去掉
             */
//        EZGlobalSDK.showSDKLog(true);

            /**
             * 设置是否支持P2P取流,详见api
             */
//        EZGlobalSDK.enableP2P(true);
            /**
             * APP_KEY请替换成自己申请的
             */
//        EZGlobalSDK.initLib(this, APP_KEY, "");
        }

        EzvizAPI.getInstance().setServerUrl(API_URL, WEB_URL);

        /**
         * 如果需要推送服务，需要再初始化EZOpenSDK后，调用以下方法初始化推送服务
         * push_secret_key 推送服务secret_key需要单独申请
         */
        {
            EZOpenSDK.getInstance().initPushService(this.getApplicationContext(), APP_PUSH_SECRETE, pushServerListener);
        }


		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
	}

    public static EZOpenSDK getOpenSDK() {
        return EZOpenSDK.getInstance();
    }

//    public static EZGlobalSDK getOpenSDK() {
//        return EZGlobalSDK.getInstance();
//    }


    public EZOpenSDKListener.EZPushServerListener pushServerListener = new EZOpenSDKListener.EZPushServerListener() {
        @Override
        public void onStartPushServerSuccess(boolean bSuccess, ErrorInfo errorInfo) {
            LogUtil.debugLog("PUSH", "start push server " + bSuccess);
        }
    };


    public static Context getContext() {
        return context;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static boolean isRunOnUIThread(){
        int myTid = android.os.Process.myTid();
        if (myTid == getMainThreadId()){
            return true;
        }
        return false;
    }

    // 运行在主线程
    public static void runOnUIThread(Runnable r){
        if (isRunOnUIThread()){
            r.run();
        }else {
            getHandler().post(r);
        }

    }
}
