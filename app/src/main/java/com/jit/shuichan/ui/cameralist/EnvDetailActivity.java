package com.jit.shuichan.ui.cameralist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.jit.shuichan.EzvizApplication;
import com.jit.shuichan.R;
import com.jit.shuichan.mina.MinaService;
import com.jit.shuichan.mina.SessionManager;
import com.jit.shuichan.mina.gsondata.ConnectionEvent;
import com.jit.shuichan.mina.gsondata.ContentData;
import com.jit.shuichan.mina.gsondata.EnvData;
import com.jit.shuichan.mina.gsondata.MessageEvent;
import com.jit.shuichan.mina.gsondata.SensorInfo;
import com.jit.shuichan.widget.UserPermission;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lecho.lib.hellocharts.view.LineChartView;

import static com.jit.shuichan.http.NetUtils.getCurrentMiaoTime;
import static com.jit.shuichan.http.NetUtils.getCurrentTimeDate;
import static com.jit.shuichan.http.NetUtils.getCurrentTimeMiao;
import static com.jit.shuichan.http.NetUtils.getForeDateTimeDate;
import static com.jit.shuichan.http.NetUtils.getMiaoTime;
import static com.jit.shuichan.http.NetUtils.getSearchRequest;
import static com.jit.shuichan.http.NetUtils.postImage;
import static com.jit.shuichan.http.NetUtils.searchURL;

/**
 * 鸡舍环境详情页面
 * Created by yuanyuan on 2017/1/3.
 */

public class EnvDetailActivity extends Activity implements View.OnClickListener {
    private EnvData mEnvData;
    private ArrayList<SensorInfo> mSensorInfos;
    private TextView mTV_temp;
    private TextView mTV_PH;
    private TextView mTV_Do;
    private TextView mTV_Title;
    private Switch mSwitch;
    private Switch mSwitch1;
    private int mFactoryId;
    private String mFaId;
    private String mdoId;
    private String mphOrtempId;
    private String mdoString;
    private String mphString;
    private String mtempString;
    private String mMessage = null;
    private String mUser;
    private boolean mIsOpen;
    private String mOpenOrNotStr;

    private Button btnType;
    private Button btnSearch;
    private TextView tvStartTime;
    private TextView tvEndTime;

    private LineChartView do2Chart;
    private LineChartView PHChart;
    private LineChartView tempChart;

    private ArrayList<String> typeList = new ArrayList<String>();
    private ArrayList<String> timeList = new ArrayList<String>();
    private ArrayList<Double> valueList = new ArrayList<Double>();

    private ArrayList<String> PHTimeList = new ArrayList<String>();
    private ArrayList<Double> PHValueList = new ArrayList<Double>();

    private ArrayList<String> tempTimeList = new ArrayList<String>();
    private ArrayList<Double> tempValueList = new ArrayList<Double>();


    /**
     * 获取采购数据
     */
    protected static final int SHOW_DO2 = 100;
    protected static final int SHOW_PH = 101;
    protected static final int SHOW_TEMP = 102;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SHOW_DO2:
                    new ChartUtils(timeList,valueList,do2Chart,3,3.2,"溶解氧");
                    getPHFromServer();
                    break;
                case SHOW_PH:
                    new ChartUtils(PHTimeList,PHValueList,PHChart,5,12,"PH");
                    getTempFromServer();
                    break;
                case SHOW_TEMP:
                    new ChartUtils(tempTimeList,tempValueList,tempChart,0,8,"水温");
                    break;
            }
        }
    };




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

        VarifyID(mFactoryId);

        initView();
        initData();
    }

    private void VarifyID(int mFactoryId) {
        if (mFactoryId == 1){
            mFaId = "1";
            mdoId = "1";
            mphOrtempId = "2";

            mdoString = "do2_1";
            mphString = "ph_1";
            mtempString = "water_temp_1";
        }else if (mFactoryId == 2){
            mFaId = "1";
            mdoId = "3";
            mphOrtempId = "4";

            mdoString = "do2_2";
            mphString = "ph_2";
            mtempString = "water_temp_2";

        }else {
            mFaId =  String.valueOf(mFactoryId - 1);
            mdoId = "1";
            mphOrtempId = "2";

            mdoString = "do2_1";
            mphString = "ph_1";
            mtempString = "water_temp_1";
        }
    }

    private void initData() {
        getHistoryFromServer();
    }

    private void getHistoryFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message message = Message.obtain();

                String s = getSearchRequest("2", mFaId, mdoId, mdoString, getForeDateTimeDate(), getCurrentTimeDate(), getCurrentTimeMiao(), getCurrentTimeMiao());
//                String s1 = getSearchRequest("2", "1", "1", "do2_1", "2017-04-01", "2017-04-02", "15:00:00", "16:00:00");
//                String s2 = getSearchRequest("2", "1", "2", "water_temp_1", "2017-04-01", "2017-04-02", "15:00:00", "16:00:00");


//                Log.e("时间","当前日" + getCurrentTimeDate() + "当前秒" + getCurrentTimeMiao() + "前一天" +getForeDateTimeDate());
//                Log.e("对应id","mFaId" + mFaId + "mdoId" + mdoId + "mdoString" + mdoString);


                Log.e("溶解氧数据n",s);
//                Log.e("PH数据n",s1);
//                Log.e("水温数据n",s2);
                try {
                    JSONObject jo = new JSONObject(s);
                    JSONArray dataArray = jo.getJSONArray("data");
                    for (int i=0;i<dataArray.length();i++){
                        JSONObject jo1 = (JSONObject) dataArray.opt(i);
                        timeList.add(jo1.getString("time"));
                        valueList.add(jo1.getDouble("value"));


                    }
                    //for循环结束展示数据
                    message.what = SHOW_DO2;
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }




    private void getPHFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message message = Message.obtain();


                MultipartEntity multipartEntity = new MultipartEntity();
                try {
                    multipartEntity.addPart("comid", new StringBody("2", Charset.forName("UTF-8")));
                    multipartEntity.addPart("facid", new StringBody(mFaId, Charset.forName("UTF-8")));
                    multipartEntity.addPart("senid", new StringBody(mphOrtempId, Charset.forName("UTF-8")));
                    multipartEntity.addPart("type", new StringBody(mphString, Charset.forName("UTF-8")));
                    multipartEntity.addPart("startDate", new StringBody(getForeDateTimeDate(), Charset.forName("UTF-8")));
                    multipartEntity.addPart("endDate", new StringBody(getCurrentTimeDate(), Charset.forName("UTF-8")));
                    multipartEntity.addPart("startTime", new StringBody(getCurrentTimeMiao(), Charset.forName("UTF-8")));
                    multipartEntity.addPart("endTime", new StringBody(getCurrentTimeMiao(), Charset.forName("UTF-8")));


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String s = postImage(searchURL, multipartEntity);

//                String s = getSearchRequest("2", "1", "1", "ph_1", "2017-04-01", "2017-04-03", "15:00:00", "16:00:00");
                Log.e("PH数据",s);


                try {
                    JSONObject jo = new JSONObject(s);
                    JSONArray dataArray = jo.getJSONArray("data");
                    for (int i=0;i<dataArray.length();i++){
                        JSONObject jo1 = (JSONObject) dataArray.opt(i);
                        PHTimeList.add(jo1.getString("time"));
                        PHValueList.add(jo1.getDouble("value"));


                    }
                    //for循环结束展示数据
                    message.what = SHOW_PH;
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }


    private void getTempFromServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Message message = Message.obtain();

                MultipartEntity multipartEntity = new MultipartEntity();
                try {
                    multipartEntity.addPart("comid", new StringBody("2", Charset.forName("UTF-8")));
                    multipartEntity.addPart("facid", new StringBody(mFaId, Charset.forName("UTF-8")));
                    multipartEntity.addPart("senid", new StringBody(mphOrtempId, Charset.forName("UTF-8")));
                    multipartEntity.addPart("type", new StringBody(mtempString, Charset.forName("UTF-8")));
                    multipartEntity.addPart("startDate", new StringBody(getForeDateTimeDate(), Charset.forName("UTF-8")));
                    multipartEntity.addPart("endDate", new StringBody(getCurrentTimeDate(), Charset.forName("UTF-8")));
                    multipartEntity.addPart("startTime", new StringBody(getCurrentTimeMiao(), Charset.forName("UTF-8")));
                    multipartEntity.addPart("endTime", new StringBody(getCurrentTimeMiao(), Charset.forName("UTF-8")));


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String s = postImage(searchURL, multipartEntity);

//                String s = getSearchRequest("2", "1", "1", "water_temp_1", "2017-04-01", "2017-04-03", "15:00:00", "16:00:00");
                Log.e("水温数据",s);

                try {
                    JSONObject jo = new JSONObject(s);
                    JSONArray dataArray = jo.getJSONArray("data");
                    for (int i=0;i<dataArray.length();i++){
                        JSONObject jo1 = (JSONObject) dataArray.opt(i);
                        tempTimeList.add(jo1.getString("time"));
                        tempValueList.add(jo1.getDouble("value"));


                    }
                    //for循环结束展示数据
                    message.what = SHOW_TEMP;
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    mHandler.sendMessage(message);
                }
            }
        }).start();
    }

    private void initView() {
        mTV_temp = (TextView)findViewById(R.id.tvTemp);
        mTV_PH = (TextView)findViewById(R.id.tvPH);
        mTV_Do = (TextView)findViewById(R.id.tvDo);
        mSwitch = (Switch)findViewById(R.id.switch_button);
        mSwitch1 = (Switch)findViewById(R.id.switch_button1);
        mTV_Title = (TextView) findViewById(R.id.area_title_Tv);

        btnType = (Button) findViewById(R.id.btn_type);
        btnSearch = (Button) findViewById(R.id.btn_search);
        tvStartTime = (TextView) findViewById(R.id.tv_starttime);
        tvEndTime = (TextView) findViewById(R.id.tv_endtime);

        do2Chart = (LineChartView) findViewById(R.id.do2_chart);
        PHChart = (LineChartView) findViewById(R.id.PH_chart);
        tempChart = (LineChartView) findViewById(R.id.temp_chart);

        typeList.add("溶解氧");
        typeList.add("PH");
        typeList.add("水温");

        btnType.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
        tvEndTime.setOnClickListener(this);

        tvStartTime.setText(getCurrentMiaoTime());
        tvEndTime.setText(getCurrentMiaoTime());







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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_type:

                OptionsPickerView typePickerView = new OptionsPickerView.Builder(EnvDetailActivity.this, new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        btnType.setText(typeList.get(options1));
                    }
                }).build();

                typePickerView.setPicker(typeList);
                typePickerView.show();

                break;
            case R.id.btn_search:

                refreshLineChart(btnType.getText().toString(),tvStartTime.getText().toString(),tvEndTime.getText().toString());

                break;
            case R.id.tv_starttime:

                TimePickerView pickerView = new TimePickerView.Builder(EnvDetailActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        tvStartTime.setText(getMiaoTime(date));
                    }
                }).build();

                pickerView.setDate(Calendar.getInstance());
                pickerView.show();

                break;
            case R.id.tv_endtime:

                TimePickerView pickerView1 = new TimePickerView.Builder(EnvDetailActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        tvEndTime.setText(getMiaoTime(date));
                    }
                }).build();

                pickerView1.setDate(Calendar.getInstance());
                pickerView1.show();

                break;
        }
    }

    private void refreshLineChart(String searchType, String startTime, String endTime) {
        if (searchType.equals("溶解氧")){
            timeList.clear();
            valueList.clear();
            searchFromServer(searchType,mdoId,mdoString,startTime,endTime,timeList,valueList);
        }else if (searchType.equals("PH")){
            PHTimeList.clear();
            PHValueList.clear();
            searchFromServer(searchType,mphOrtempId,mphString,startTime,endTime,PHTimeList,PHValueList);
        }else if (searchType.equals("水温")){
            tempTimeList.clear();
            tempValueList.clear();
            searchFromServer(searchType,mphOrtempId,mtempString,startTime,endTime,tempTimeList,tempValueList);
        }
    }

    private void searchFromServer(final String searchType, final String mSensorId, final String mSensorString, final String startTime, final String endTime, final ArrayList<String> timeTypeList, final ArrayList<Double> valueTypeList) {
        new Thread(new Runnable() {
            @Override
            public void run() {


                String startDate = startTime.substring(0,10);
                String startTimeS = startTime.substring(startTime.length() - 8);

                String endDate = endTime.substring(0,10);
                String endTimeS = endTime.substring(endTime.length() - 8);

                MultipartEntity multipartEntity = new MultipartEntity();
                try {
                    multipartEntity.addPart("comid", new StringBody("2", Charset.forName("UTF-8")));
                    multipartEntity.addPart("facid", new StringBody(mFaId, Charset.forName("UTF-8")));
                    multipartEntity.addPart("senid", new StringBody(mSensorId, Charset.forName("UTF-8")));
                    multipartEntity.addPart("type", new StringBody(mSensorString, Charset.forName("UTF-8")));
                    multipartEntity.addPart("startDate", new StringBody(startDate, Charset.forName("UTF-8")));
                    multipartEntity.addPart("endDate", new StringBody(endDate, Charset.forName("UTF-8")));
                    multipartEntity.addPart("startTime", new StringBody(startTimeS, Charset.forName("UTF-8")));
                    multipartEntity.addPart("endTime", new StringBody(endTimeS, Charset.forName("UTF-8")));


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String s = postImage(searchURL, multipartEntity);

//                String s = getSearchRequest("2", "1", "1", "ph_1", "2017-04-01", "2017-04-03", "15:00:00", "16:00:00");
                Log.e("PH数据",s);


                try {
                    JSONObject jo = new JSONObject(s);
                    JSONArray dataArray = jo.getJSONArray("data");
                    for (int i=0;i<dataArray.length();i++){
                        JSONObject jo1 = (JSONObject) dataArray.opt(i);
                        timeTypeList.add(jo1.getString("time"));
                        valueTypeList.add(jo1.getDouble("value"));


                    }


                    EzvizApplication.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EzvizApplication.getContext(),"查询成功",Toast.LENGTH_LONG).show();
                            if (searchType.equals("溶解氧")){
                                new ChartUtils(timeTypeList,valueTypeList,do2Chart,3,3.2,"溶解氧");
                            }else if (searchType.equals("PH")){
                                new ChartUtils(timeTypeList,valueTypeList,PHChart,3,3.2,"PH");
                            }else if (searchType.equals("水温")){
                                new ChartUtils(timeTypeList,valueTypeList,tempChart,3,3.2,"水温");
                            }

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


}
