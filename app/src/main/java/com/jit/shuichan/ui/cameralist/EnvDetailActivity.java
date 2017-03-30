package com.jit.shuichan.ui.cameralist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jit.shuichan.R;
import com.jit.shuichan.mina.MinaService;
import com.jit.shuichan.mina.SessionManager;
import com.jit.shuichan.mina.gsondata.ConnectionEvent;
import com.jit.shuichan.mina.gsondata.ContentData;
import com.jit.shuichan.mina.gsondata.EnvData;
import com.jit.shuichan.mina.gsondata.MessageEvent;
import com.jit.shuichan.mina.gsondata.SensorInfo;
import com.jit.shuichan.widget.UserPermission;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 鸡舍环境详情页面
 * Created by yuanyuan on 2017/1/3.
 */

public class EnvDetailActivity extends Activity {
    private EnvData mEnvData;
    private ArrayList<SensorInfo> mSensorInfos;
    private TextView mTV_temp;
    private TextView mTV_PH;
    private TextView mTV_Do;
    private TextView mTV_Title;
    private Switch mSwitch;
    private Switch mSwitch1;
    private int mFactoryId;
    private String mMessage = null;
    private String mUser;
    private boolean mIsOpen;
    private String mOpenOrNotStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_envdetail);
//        EventBus.getDefault().register(this);

        Bundle bundle = new Bundle();
        bundle = this.getIntent().getExtras();
        mFactoryId = bundle.getInt("factoryId");
        Log.e("TAG","工厂号:" + mFactoryId);

        Log.e("adapter","得到了权限信息" + UserPermission.getsUserPermission().getmUserName());
        mUser = UserPermission.getsUserPermission().getmUserName();


        EventBus.getDefault().register(this);
        Log.e("tag", "点击启动服务");
        Intent intent = new Intent(this, MinaService.class);
        startService(intent);

        initView();
        initData();
    }

    private void initData() {
        getHistoryFromServer();
    }

    private void getHistoryFromServer() {
    }

    private void initView() {
        mTV_temp = (TextView)findViewById(R.id.tvTemp);
        mTV_PH = (TextView)findViewById(R.id.tvPH);
        mTV_Do = (TextView)findViewById(R.id.tvDo);
        mSwitch = (Switch)findViewById(R.id.switch_button);
        mSwitch1 = (Switch)findViewById(R.id.switch_button1);
        mTV_Title = (TextView) findViewById(R.id.area_title_Tv);





        if (mUser.equals("admin")){
            switch (mFactoryId){
                case 1:
                    mTV_Title.setText("固城湖1");
                    break;
                case 2:
                    mTV_Title.setText("固城湖2");
                    break;
                case 3:
                    mTV_Title.setText("渔网大市场1");
                    break;
                case 4:
                    mTV_Title.setText("渔网大市场2");
                    break;
                case 5:
                    mTV_Title.setText("渔网大市场3");
                    break;
            }
        }else if(mUser.equals("渔网1")){
            mTV_Title.setText("渔网大市场1");
        }else if(mUser.equals("渔网2")){
            mTV_Title.setText("渔网大市场2");
        }else if(mUser.equals("渔网3")){
            mTV_Title.setText("渔网大市场3");
        }


        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser.equals("admin")){


                    if (mFactoryId == 1){
                        if (mSwitch.isChecked()){//开
//                            Log.e("tag","点击事件点开");

                            mIsOpen = true;
                            mOpenOrNotStr = "1";
                            showSwitch1Dialog(mIsOpen,mFactoryId,mOpenOrNotStr);
                        }else {//关
                            mIsOpen = false;
                            mOpenOrNotStr = "0";
                            showSwitch1Dialog(mIsOpen,mFactoryId,mOpenOrNotStr);
                        }
                    }else {
                        if (mSwitch.isChecked()){//开
                            Log.e("tag","点击事件点开");
                            mIsOpen = true;
                            mOpenOrNotStr = "1";
                            showSwitch1Dialog(mIsOpen,mFactoryId - 1,mOpenOrNotStr);
                        }else {//关
                            Log.e("tag","点击事件点关");
                            mIsOpen = false;
                            mOpenOrNotStr = "0";
                            showSwitch1Dialog(mIsOpen,mFactoryId - 1,mOpenOrNotStr);
                        }

                    }

                }else if (mUser.equals("渔网1")){
                    if (mSwitch.isChecked()){//开
                        Log.e("tag","点击事件点开");
                        mIsOpen = true;
                        mOpenOrNotStr = "1";
                        showSwitch1Dialog(mIsOpen,2,mOpenOrNotStr);
                    }else {//关
                        Log.e("tag","点击事件点关");
                        mIsOpen = false;
                        mOpenOrNotStr = "0";
                        showSwitch1Dialog(mIsOpen,2,mOpenOrNotStr);
                    }
                }else if (mUser.equals("渔网2")){
                    if (mSwitch.isChecked()){//开
                        Log.e("tag","点击事件点开");
                        mIsOpen = true;
                        mOpenOrNotStr = "1";
                        showSwitch1Dialog(mIsOpen,3,mOpenOrNotStr);
                    }else {//关
                        Log.e("tag","点击事件点关");
                        mIsOpen = false;
                        mOpenOrNotStr = "0";
                        showSwitch1Dialog(mIsOpen,3,mOpenOrNotStr);
                    }
                }else if (mUser.equals("渔网3")){
                    if (mSwitch.isChecked()){//开
                        Log.e("tag","点击事件点开");
                        mIsOpen = true;
                        mOpenOrNotStr = "1";
                        showSwitch1Dialog(mIsOpen,4,mOpenOrNotStr);
                    }else {//关
                        Log.e("tag","点击事件点关");
                        mIsOpen = false;
                        mOpenOrNotStr = "0";
                        showSwitch1Dialog(mIsOpen,4,mOpenOrNotStr);
                    }
                }


            }
        });


        mSwitch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser.equals("admin")){


                    if (mFactoryId == 1){
                        if (mSwitch1.isChecked()){//开
//                            Log.e("tag","点击事件点开");

                            mIsOpen = true;
                            mOpenOrNotStr = "1";
                            showSwitch2Dialog(mIsOpen,mFactoryId,mOpenOrNotStr);
                        }else {//关
                            mIsOpen = false;
                            mOpenOrNotStr = "0";
                            showSwitch2Dialog(mIsOpen,mFactoryId,mOpenOrNotStr);
                        }
                    }else {
                        if (mSwitch1.isChecked()){//开
                            Log.e("tag","点击事件点开");
                            mIsOpen = true;
                            mOpenOrNotStr = "1";
                            showSwitch2Dialog(mIsOpen,mFactoryId - 1,mOpenOrNotStr);
                        }else {//关
                            Log.e("tag","点击事件点关");
                            mIsOpen = false;
                            mOpenOrNotStr = "0";
                            showSwitch2Dialog(mIsOpen,mFactoryId - 1,mOpenOrNotStr);
                        }

                    }

                }else if (mUser.equals("渔网1")){
                    if (mSwitch1.isChecked()){//开
                        Log.e("tag","点击事件点开");
                        mIsOpen = true;
                        mOpenOrNotStr = "1";
                        showSwitch2Dialog(mIsOpen,2,mOpenOrNotStr);
                    }else {//关
                        Log.e("tag","点击事件点关");
                        mIsOpen = false;
                        mOpenOrNotStr = "0";
                        showSwitch2Dialog(mIsOpen,2,mOpenOrNotStr);
                    }
                }else if (mUser.equals("渔网2")){
                    if (mSwitch1.isChecked()){//开
                        Log.e("tag","点击事件点开");
                        mIsOpen = true;
                        mOpenOrNotStr = "1";
                        showSwitch2Dialog(mIsOpen,3,mOpenOrNotStr);
                    }else {//关
                        Log.e("tag","点击事件点关");
                        mIsOpen = false;
                        mOpenOrNotStr = "0";
                        showSwitch2Dialog(mIsOpen,3,mOpenOrNotStr);
                    }
                }else if (mUser.equals("渔网3")){
                    if (mSwitch1.isChecked()){//开
                        Log.e("tag","点击事件点开");
                        mIsOpen = true;
                        mOpenOrNotStr = "1";
                        showSwitch2Dialog(mIsOpen,4,mOpenOrNotStr);
                    }else {//关
                        Log.e("tag","点击事件点关");
                        mIsOpen = false;
                        mOpenOrNotStr = "0";
                        showSwitch2Dialog(mIsOpen,4,mOpenOrNotStr);
                    }
                }


            }
        });

        Button backButton = (Button) findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        //停止mina服务
        stopService(new Intent(this, MinaService.class));
    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void parseConnection(ConnectionEvent event){
        if (event.isConneted){
            if (mUser.equals("admin")){
                if (mFactoryId == 1){
                    Log.e("tag","来到了溏口5");
                    //查询传感器参数
                    Gson gson = new Gson();
                    EnvData env = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,mFactoryId,null,"query#all"));
                    Log.e("TestJson",gson.toJson(env));
                    String msg = gson.toJson(env).toString();
                    SessionManager.getInstance().writeToServer(msg);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //查询设备状态
                    EnvData con = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,mFactoryId,null,"status"));
                    Log.e("TestJson",gson.toJson(con));
                    String control = gson.toJson(con).toString();
                    SessionManager.getInstance().writeToServer(control);
                }else {
                    //查询传感器参数
                    Gson gson = new Gson();
                    EnvData env = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,mFactoryId-1,null,"query#all"));
                    Log.e("TestJson",gson.toJson(env));
                    String msg = gson.toJson(env).toString();
                    SessionManager.getInstance().writeToServer(msg);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //查询设备状态
                    EnvData con = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,mFactoryId-1,null,"status"));
                    Log.e("TestJson",gson.toJson(con));
                    String control = gson.toJson(con).toString();
                    SessionManager.getInstance().writeToServer(control);
                }
            }else if (mUser.equals("渔网1")){
                //查询传感器参数
                Gson gson = new Gson();
                EnvData env = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,2,null,"query#all"));
                Log.e("TestJson",gson.toJson(env));
                String msg = gson.toJson(env).toString();
                SessionManager.getInstance().writeToServer(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //查询设备状态
                EnvData con = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,2,null,"status"));
                Log.e("TestJson",gson.toJson(con));
                String control = gson.toJson(con).toString();
                SessionManager.getInstance().writeToServer(control);
            }else if (mUser.equals("渔网2")){
                //查询传感器参数
                Gson gson = new Gson();
                EnvData env = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,3,null,"query#all"));
                Log.e("TestJson",gson.toJson(env));
                String msg = gson.toJson(env).toString();
                SessionManager.getInstance().writeToServer(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //查询设备状态
                EnvData con = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,3,null,"status"));
                Log.e("TestJson",gson.toJson(con));
                String control = gson.toJson(con).toString();
                SessionManager.getInstance().writeToServer(control);
            }else if (mUser.equals("渔网3")){
                //查询传感器参数
                Gson gson = new Gson();
                EnvData env = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,4,null,"query#all"));
                Log.e("TestJson",gson.toJson(env));
                String msg = gson.toJson(env).toString();
                SessionManager.getInstance().writeToServer(msg);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //查询设备状态
                EnvData con = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,4,null,"status"));
                Log.e("TestJson",gson.toJson(con));
                String control = gson.toJson(con).toString();
                SessionManager.getInstance().writeToServer(control);
            }


        }
    }



    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void parseData(MessageEvent event){
//        Log.e("TAG",event.getMessage());
//        mMessage = event.getMessage();

        if (event.getMessage().trim().equals("ping")){//给服务器回pong

        }else{
            processData(event.getMessage());
        }


        if (mEnvData.getContent().getOrder().equals("disconnected")){
            Toast.makeText(getApplicationContext(),"设备不在线",Toast.LENGTH_SHORT).show();
        }else if (mEnvData.getContent().getOrder().equals("sensor")){
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mFactoryId == 2){
                        Log.e("tag","来到了第二路获取参数方法");
                        mSensorInfos = mEnvData.getContent().getInfos();
                        for (int i=0;i<mSensorInfos.size();i++){
                            if (mSensorInfos.get(i).getType().equals("water_temp_2")){
                                Double temp = mSensorInfos.get(i).getValue();
                                mTV_temp.setText(temp.toString() + "℃");
                            }else if (mSensorInfos.get(i).getType().equals("ph_2")){
                                Double PH = mSensorInfos.get(i).getValue();
                                mTV_PH.setText( PH.toString());
                            }else if (mSensorInfos.get(i).getType().equals("do2_2")){
                                Double temp = mSensorInfos.get(i).getValue();
                                mTV_Do.setText( temp.toString() + "mg/L");
                            }
                        }
                    }else {
                        mSensorInfos = mEnvData.getContent().getInfos();
                        for (int i=0;i<mSensorInfos.size();i++){
                            if (mSensorInfos.get(i).getType().equals("water_temp_1")){
                                Double temp = mSensorInfos.get(i).getValue();
                                mTV_temp.setText(temp.toString() + "℃");
                            }else if (mSensorInfos.get(i).getType().equals("ph_1")){
                                Double PH = mSensorInfos.get(i).getValue();
                                mTV_PH.setText( PH.toString());
                            }else if (mSensorInfos.get(i).getType().equals("do2_1")){
                                Double temp = mSensorInfos.get(i).getValue();
                                mTV_Do.setText( temp.toString() + "mg/L");
                            }
                        }
                    }
                }
            });

        }else if (mEnvData.getContent().getOrder().equals("control")){//判断是否控制成功
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSensorInfos = mEnvData.getContent().getInfos();
                    Double controlResult = mSensorInfos.get(0).getValue();
                    if (controlResult.toString().equals("1.0")){
                        Toast.makeText(getApplicationContext(),"控制成功",Toast.LENGTH_SHORT).show();
                    }else if (controlResult.toString().equals("0.0")){
                        Toast.makeText(getApplicationContext(),"控制失败",Toast.LENGTH_SHORT).show();
//                        if (mSensorInfos.get(0).getType().toString().equals())
                    }
                }
            });
        }else if (mEnvData.getContent().getOrder().equals("status")){//判断控制器状态

            Log.e("tag",event.getMessage());
            final Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("tag","来到了第二路查询控制方法");
                    mSensorInfos = mEnvData.getContent().getInfos();
                    for (int i=0;i<mSensorInfos.size();i++){
                        Double status = mSensorInfos.get(i).getValue();
                        switch (Integer.parseInt(mSensorInfos.get(i).getType())){
                            case 1:
                                Log.e("type","解析到了type1");
                                if (status.toString().equals("1.0")){
                                    mSwitch.setChecked(true);
                                }else {
                                    mSwitch.setChecked(false);
                                }
                                break;

                            case 2:
                                Log.e("type","解析到了type2");
                                if (status.toString().equals("1.0")){
                                    mSwitch1.setChecked(true);
                                }else {
                                    mSwitch1.setChecked(false);
                                }
                                break;

                            case 3:
                                Log.e("type","解析到了type3");
//                                if (status.toString().equals("1.0")){
//                                    mSwitch1.setChecked(true);
//                                }else {
//                                    mSwitch1.setChecked(false);
//                                }
                                break;

                            case 4:
                                Log.e("type","解析到了type4");
//                                if (status.toString().equals("1.0")){
//                                    mSwitch1.setChecked(true);
//                                }else {
//                                    mSwitch1.setChecked(false);
//                                }
                                break;

                            case 5:
                                Log.e("type","解析到了type5");
//                                if (status.toString().equals("1.0")){
//                                    mSwitch1.setChecked(true);
//                                }else {
//                                    mSwitch1.setChecked(false);
//                                }
                                break;

                            case 6:
                                Log.e("type","解析到了type6");
//                                if (status.toString().equals("1.0")){
//                                    mSwitch1.setChecked(true);
//                                }else {
//                                    mSwitch1.setChecked(false);
//                                }
                                break;

                            case 7:
                                Log.e("type","解析到了type7");
//                                if (status.toString().equals("1.0")){
//                                    mSwitch1.setChecked(true);
//                                }else {
//                                    mSwitch1.setChecked(false);
//                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            });
        }


    }

    private void processData(String json) {
        Gson gson = new Gson();
        mEnvData = gson.fromJson(json, EnvData.class);
        Log.e("TAG","解析结果:" + mEnvData);
    }


    /**
     * 弹出对话框,提示用户更新
     */
    protected void showSwitch1Dialog(final boolean isOpen, final int factory, final String openOrNot) {
        //对话框,是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("提示！！！");
        //设置描述内容
        if (isOpen){
            builder.setMessage("即将打开水车");
        }else {
            builder.setMessage("即将关闭水车");
        }


        //积极按钮,立即更新
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk,apk链接地址,downloadUrl
                Gson gson = new Gson();
                EnvData env = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,factory,null,"control#1#1#" + openOrNot));
                Log.e("TestJson",gson.toJson(env));
                String msg = gson.toJson(env).toString();
                SessionManager.getInstance().writeToServer(msg);

                //自定义定时器
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //要做的事情
                        if (mMessage.equals(null)){
                            Toast.makeText(getApplicationContext(),"控制失败",Toast.LENGTH_SHORT).show();
                        }
                        handler.postDelayed(this, 2000);
                    }
                };
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框,进入主界面
                mSwitch.setChecked(!isOpen);

            }
        });

        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消,也需要让其进入应用程序主界面
                mSwitch.setChecked(!isOpen);
                dialog.dismiss();
            }
        });

        builder.show();
    }




    protected void showSwitch2Dialog(final boolean isOpen, final int factory, final String openOrNot) {
        //对话框,是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("提示！！！");
        //设置描述内容
        if (isOpen){
            builder.setMessage("即将打开水车");
        }else {
            builder.setMessage("即将关闭水车");
        }


        //积极按钮,立即更新
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk,apk链接地址,downloadUrl
                Gson gson = new Gson();
                EnvData env = new EnvData("app","username","communication",new ContentData("192.168.1.198",2,factory,null,"control#1#2#" + openOrNot));
                Log.e("TestJson",gson.toJson(env));
                String msg = gson.toJson(env).toString();
                SessionManager.getInstance().writeToServer(msg);

                //自定义定时器
                final Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //要做的事情
                        if (mMessage.equals(null)){
                            Toast.makeText(getApplicationContext(),"控制失败",Toast.LENGTH_SHORT).show();
                        }
                        handler.postDelayed(this, 2000);
                    }
                };
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框,进入主界面
                mSwitch1.setChecked(!isOpen);

            }
        });

        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消,也需要让其进入应用程序主界面
                mSwitch1.setChecked(!isOpen);
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
