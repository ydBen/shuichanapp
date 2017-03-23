package com.jit.shuichan.mina;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jit.shuichan.mina.gsondata.ConnectionEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/9 0009
 * Time: 17:17
 */
public class MinaService extends Service{

    private ConnectionThread thread;


    @Override
    public void onCreate() {
        super.onCreate();
        thread = new ConnectionThread("mina", getApplicationContext());
        thread.start();
        Log.e("tag", "启动线程尝试连接");
//        EventBus.getDefault().register(this);
    }

//    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    public void parseConnection(ConnectionEvent event){
//        if (!event.isConneted){
//            //判断网络
//            thread = new ConnectionThread("mina", getApplicationContext());
//            thread.start();
//            Log.e("tag", "启动线程尝试连接");
//        }else {
//            Log.e("tag","重连成功");
//        }
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnect();
        thread=null;

//        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    class ConnectionThread extends HandlerThread{

        private Context context;
        boolean isConnection;
        ConnectionManager mManager;
        public ConnectionThread(String name, Context context){
            super(name);
            this.context = context;

            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp("218.94.144.228")
                    .setPort(8008)
                    .setReadBufferSize(10240)
                    .setConnectionTimeout(10000).builder();

            mManager = new ConnectionManager(config);
        }

        @Override
        protected void onLooperPrepared() {
            while(true){
                isConnection = mManager.connnect();
                if(isConnection){
                    Log.e("tag", "连接成功");
                    EventBus.getDefault().post(new ConnectionEvent(isConnection));
                    break;
                }
                try {
                    Log.e("tag", "尝试重新连接");
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void disConnect(){
            mManager.disContect();
        }
    }




}
