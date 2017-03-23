/* 
 * @ProjectName ezviz-openapi-android-demo
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName LoginSelectActivity.java
 * @Description 这里对文件进行描述
 * 
 * @author chenxingyf1
 * @data 2014-12-6
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.jit.shuichan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.jit.shuichan.ui.cameralist.EZCameraListActivity;
import com.jit.shuichan.ui.util.StreamUtil;
import com.jit.shuichan.ui.util.UserInfoUtil;
import com.jit.shuichan.widget.UserPermission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;


/**
 * 登录选择演示
 * @author xiaxingsuo
 * @data 2015-11-6
 */
public class LoginSelectActivity extends Activity implements OnClickListener{
    /**
     * 密码保存成功
     */
    protected static final int SAVE_SUCCESS = 100;
    /**
     * 保存失败
     */
    protected static final int SAVE_FAIL = 101;

    /**
     * url地址出错状态码
     */
    protected static final int URL_ERROR = 102;
    protected static final int IO_ERROR = 103;
    protected static final int JSON_ERROR = 104;

    /**
     * 进入主页面
     */
    protected static final int ENTER_HOME = 105;
    /**
     * 进入主页面
     */
    protected static final int PWD_ERROR = 106;

    private EditText etUser;
    private EditText etPwd;
    private CheckBox cbRem;
    private Context mContext;

    private String mUsername;
    private String mPassword;
    private UserPermission mUserPermission;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SAVE_SUCCESS:
                    //弹出对话框,提示用户更新
                    Toast.makeText(mContext,"保存成功",Toast.LENGTH_SHORT).show();
                    break;
                case SAVE_FAIL:
                    //进入应用程序主界面,activity跳转过程
                    Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
                    break;
                case URL_ERROR:
                    Toast.makeText(getApplicationContext(),"url异常",Toast.LENGTH_SHORT).show();
//                    ToastUtil.show(getApplicationContext(), "url异常");
                    break;
                case IO_ERROR:
                    Toast.makeText(getApplicationContext(),"读取异常",Toast.LENGTH_SHORT).show();
//                    ToastUtil.show(getApplicationContext(), "读取异常");
                    break;
                case JSON_ERROR:
                    Toast.makeText(getApplicationContext(),"json解析异常",Toast.LENGTH_SHORT).show();
//                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    break;
                case ENTER_HOME:
                    //将用户名密码带给adapter
                    Intent toIntent = new Intent(LoginSelectActivity.this, EZCameraListActivity.class);
                    toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    LoginSelectActivity.this.startActivity(toIntent);

                    break;
                case PWD_ERROR:
                    Toast.makeText(getApplicationContext(),"用户名或密码错误",Toast.LENGTH_SHORT).show();
//                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        mContext =this;

        initView();
        initData();

    }
    
    private void initData() {
        getAccessToken();
    }
    
    private void initView() {
       etUser = (EditText)findViewById(R.id.et_user);
       etPwd = (EditText)findViewById(R.id.et_pwd);
       cbRem = (CheckBox)findViewById(R.id.cb_rem);

        // 回显密码
        Map<String, String> map = UserInfoUtil.getUserInfo(mContext);//获取用户名密码
        if (map != null){
            if (TextUtils.isEmpty(map.get("username")) || TextUtils.isEmpty(map.get("password"))){//第一次进入
                Log.e("map","第一次进入程序");
//                return;
            }else {
                etUser.setText(map.get("username"));
                etPwd.setText(map.get("password"));
            }


            cbRem.setChecked(true);   //设置复选框选中状态
        }
    }

    /* (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch(v.getId()) {
//        	case R.id.interface_call_btn:
//        		intent = new Intent(LoginSelectActivity.this, InterfaceDemoActivity.class);
//                startActivity(intent);
//        		break;
            case R.id.web_login_btn:
//                if (EZGlobalSDK.class.isInstance(EzvizApplication.getOpenSDK())) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                List<EZAreaInfo> areaList = EZGlobalSDK.getInstance().getAreaList();
//                                if (areaList != null) {
//                                    LogUtil.debugLog("application", "list count: " + areaList.size());
//                                    Log.e("TAG", "list count: " + areaList.size());
//                                    EZAreaInfo areaInfo = areaList.get(0);
//                                    EZGlobalSDK.getInstance().openLoginPage(areaInfo.getId());
//                                }
//                            } catch (BaseException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
//                } else {
//                    EZOpenSDK.getInstance().openLoginPage();
//                }
                Log.e("userInfo", "点击事件响应了");
                loginMainPage();



                break;
//            case R.id.goto_cameralist_btn:
//                openPlatformLoginDialog();
//                break;
//            case R.id.id_ll_join_qq_group:
//                String key = "p57CNgQ_uf2gZMY0eYTvgQ_S_ZDzZz44";
//                joinQQGroup(key);
//                break;
            default:
                break;
        }
    }

    private void loginMainPage() {
        Log.e("userInfo", "登录方法调用了");
        mUsername= etUser.getText().toString().trim();
        mPassword = etPwd.getText().toString().trim();
//        boolean isRem = cbRem.isChecked();

        //判断用户名密码是否为空，不为空请求服务器
        if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)){
            Toast.makeText(mContext,"用户名密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }else {//请求服务器比对用户名密码
            Log.e("userInfo", "向服务器发送请求");
            getUserInfoFromServer(mUsername,mPassword);
        }



    }

    private void getUserInfoFromServer(final String username, final String password) {
        new Thread(){
            public void run() {
                //发送请求获取数据,参数则为请求json的链接地址
                //http://192.168.13.99:8080/update74.json	测试阶段不是最优
                //仅限于模拟器访问电脑tomcat
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //1,封装url地址
                    URL url = new URL("http://218.94.144.228:8088/author.json");
                    //2,开启一个链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //3,设置常见请求参数(请求头)

                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时
                    connection.setReadTimeout(2000);

                    //默认就是get请求方式,
//					connection.setRequestMethod("POST");

                    //4,获取请求成功响应码
                    if(connection.getResponseCode() == 200){
                        Log.e("userInfo", "请求成功");
                        //5,以流的形式,将数据获取下来
                        InputStream is = connection.getInputStream();
                        //6,将流转换成字符串(工具类封装)
                        String json = StreamUtil.streamToString(is);
                        Log.e("userInfo", json);
                        //7,json解析
                        JSONObject jsonObject = new JSONObject(json);

                        //debug调试,解决问题
//                        String versionName = jsonObject.getString("versionName");
//                        mVersionDes = jsonObject.getString("versionDes");
//                        String versionCode = jsonObject.getString("versionCode");
//                        mDownloadUrl = jsonObject.getString("downloadUrl");


                        JSONArray authorArray = jsonObject.getJSONArray("author");
                        for (int i = 0;i<authorArray.length();i++){
                            JSONObject jo = (JSONObject)authorArray.opt(i);
                            String severUser = jo.getString("username");
                            String serverPwd = jo.getString("password");
                            if (username.equals(severUser) && password.equals(serverPwd)){

                                if (cbRem.isChecked()){
                                    boolean result = UserInfoUtil.saveUserInfo(mContext, username, password);
                                    if (result){
                                        Log.e("userInfo", "保存成功");
                                        msg.what = SAVE_SUCCESS;
//                                        Toast.makeText(mContext,"保存成功",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Log.e("userInfo", "保存失败");
                                        msg.what = SAVE_FAIL;
//                                        Toast.makeText(mContext,"保存失败",Toast.LENGTH_SHORT).show();
                                    }
                                }


                                UserPermission.getsUserPermission().setmUserName(username);

                                msg.what = ENTER_HOME;
//                                Intent toIntent = new Intent(LoginSelectActivity.this, EZCameraListActivity.class);
//                                toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                LoginSelectActivity.this.startActivity(toIntent);
                                return;
                            }else {
                                Log.e("userInfo", "用户名或密码错误" + username + "=" + severUser+ password + "=" + serverPwd);
                                msg.what = PWD_ERROR;
//                                Toast.makeText(getApplicationContext(),"用户名或密码错误",Toast.LENGTH_LONG).show();
                            }

                        }



                    }
                }catch(MalformedURLException e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();
                }catch (IOException e) {
                    msg.what = IO_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_ERROR;
                    e.printStackTrace();
                }finally{
                    mHandler.sendMessage(msg);
                }
            };
        }.start();
    }

    private void getAccessToken() {
        new Thread(){
            public void run() {
                //发送请求获取数据,参数则为请求json的链接地址
                //http://192.168.13.99:8080/update74.json	测试阶段不是最优
                //仅限于模拟器访问电脑tomcat
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //1,封装url地址
                    URL url = new URL("http://218.94.144.228:8088/YinshiServlet/getToken?phone=18168092635");
                    //2,开启一个链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //3,设置常见请求参数(请求头)

                    //请求超时
                    connection.setConnectTimeout(2000);
                    //读取超时
                    connection.setReadTimeout(2000);

                    //默认就是get请求方式,
//					connection.setRequestMethod("POST");

                    //4,获取请求成功响应码
                    if(connection.getResponseCode() == 200){
                        //5,以流的形式,将数据获取下来
                        InputStream is = connection.getInputStream();
                        //6,将流转换成字符串(工具类封装)
                        String json = StreamUtil.streamToString(is);
                        Log.e("login", json);
                        //7,json解析
                        JSONObject jsonObject = new JSONObject(json);

                        //debug调试,解决问题
//                        String versionName = jsonObject.getString("versionName");
//                        mVersionDes = jsonObject.getString("versionDes");
//                        String versionCode = jsonObject.getString("versionCode");
//                        mDownloadUrl = jsonObject.getString("downloadUrl");


                        JSONObject joResult = jsonObject.getJSONObject("result");
                        JSONObject joData = joResult.getJSONObject("data");
                        String accessToken = joData.getString("accessToken");

                        //日志打印
                        Log.e("token", accessToken);

                        EzvizApplication.getOpenSDK().setAccessToken(accessToken);
//                        Intent toIntent = new Intent(LoginSelectActivity.this, EZCameraListActivity.class);
//                        toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        LoginSelectActivity.this.startActivity(toIntent);



                    }
                }catch(MalformedURLException e) {
                    msg.what = URL_ERROR;
//                    Toast.makeText(getApplicationContext(),"url异常",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }catch (IOException e) {
                    msg.what = IO_ERROR;
//                    Toast.makeText(getApplicationContext(),"io异常",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_ERROR;
//                    Toast.makeText(getApplicationContext(),"JSON解析异常",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }finally{
                    mHandler.sendMessage(msg);
                }
            };
        }.start();
    }

    private void openPlatformLoginDialog() {
        final EditText editText = new EditText(this);
        new  AlertDialog.Builder(this)  
        .setTitle(R.string.please_input_platform_accesstoken_txt)
        .setIcon(android.R.drawable.ic_dialog_info)   
        .setView(editText)  
        .setPositiveButton(R.string.certain, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String getAccessTokenSign = SignUtil.getGetAccessTokenSign();

                EzvizApplication.getOpenSDK().setAccessToken(editText.getText().toString());
                Intent toIntent = new Intent(LoginSelectActivity.this, EZCameraListActivity.class);
                toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LoginSelectActivity.this.startActivity(toIntent);
            }
            
        })
        .setNegativeButton(R.string.cancel, null)
        .show();  
    }

    private boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }
}
