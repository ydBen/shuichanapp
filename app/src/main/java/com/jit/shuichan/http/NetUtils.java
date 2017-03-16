package com.jit.shuichan.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;

import com.lidroid.xutils.http.client.multipart.MultipartEntity;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanyuan on 2017/2/27.
 */

public class NetUtils {

    //172.19.73.103
    public static final String Url = "http://172.19.73.103:8080/IntelligentAgriculture/";
    public static String JSESSIONID;
    private static HttpParams httpParams;
    private static DefaultHttpClient httpClient;

    // 登录
    public static final String loginURL = Url + "user/checklogin";
    public static ArrayList<BasicNameValuePair> loginValues = new ArrayList<BasicNameValuePair>();

    // 获取溏口信息
    public static final String pondInfoURL = Url + "PondInfo/showPondinfos";
    public static ArrayList<BasicNameValuePair> pondInfoValues = new ArrayList<BasicNameValuePair>();

    // 获取巡视检查数据
    public static final String manualDataURL = Url + "patrolManage/showManualData";
    public static ArrayList<BasicNameValuePair> manualDataValues = new ArrayList<BasicNameValuePair>();

    // 获取日常采购物品类别
    public static final String buyTypeURL = Url + "buyManage/dailyInputType";
    public static ArrayList<BasicNameValuePair> buyTypeValues = new ArrayList<BasicNameValuePair>();

    // 获取 巡视检查 检查对象
    public static final String patrolTypeURL = Url + "patrolManage/showPatrolType";
    public static ArrayList<BasicNameValuePair> patrolTypeValues = new ArrayList<BasicNameValuePair>();

    // 获取 检查措施 类别
    public static final String measureTypeURL = Url + "emergMeasureManage/showMeasureType";
    public static ArrayList<BasicNameValuePair> measureTypeValues = new ArrayList<BasicNameValuePair>();

    // 获取 日常投放物 名称
    public static final String buyNameURL = Url + "buyManage/buyName";
    public static ArrayList<BasicNameValuePair> buyNameValues = new ArrayList<BasicNameValuePair>();


    // 提交 日常采购
    public static final String buySubmitURL = Url + "buyManage/buy?page=1&kind=buy";
    public static ArrayList<BasicNameValuePair> buySubmitValues = new ArrayList<BasicNameValuePair>();

    // 提交 日常投放
    public static final String throwSubmitURL = Url + "throwManage/throw?page=1&kind=throw";
    public static ArrayList<BasicNameValuePair> throwSubmitValues = new ArrayList<BasicNameValuePair>();

    // 提交 巡视检查
    public static final String patrolSubmitURL = Url + "patrolManage/patrol?page=1&kind=patro";
    public static ArrayList<BasicNameValuePair> patrolSubmitValues = new ArrayList<BasicNameValuePair>();

    // 提交 措施
    public static final String measureSubmitURL = Url + "emergMeasureManage/emergMeasure?page=1&kind=measure";
    public static ArrayList<BasicNameValuePair> measureSubmitValues = new ArrayList<BasicNameValuePair>();

    // 日志 日常采购
    public static final String buyLogURL = Url + "system/showMyLogForAndroid";
    public static ArrayList<BasicNameValuePair> buyLogValues = new ArrayList<BasicNameValuePair>();

    // 日志 日常投放
    public static final String throwLogURL = Url + "system/index_log";
    public static ArrayList<BasicNameValuePair> throwLogValues = new ArrayList<BasicNameValuePair>();

    // 日志 巡视检查
    public static final String patrolLogURL = Url + "system/index_log";
    public static ArrayList<BasicNameValuePair> patrolLogValues = new ArrayList<BasicNameValuePair>();

    // 日志 措施
    public static final String measureLogURL = Url + "system/index_log";
    public static ArrayList<BasicNameValuePair> measureLogValues = new ArrayList<BasicNameValuePair>();

    // 溏口 投放成本
    public static final String costURL = Url + "cost/dailyInputData";
    public static ArrayList<BasicNameValuePair> costValues = new ArrayList<BasicNameValuePair>();

    // 溏口 经济效益
    public static final String profitURL = Url + "cost/pondresultData";
    public static ArrayList<BasicNameValuePair> profitValues = new ArrayList<BasicNameValuePair>();

    // 租金成本
    public static final String rentURL = Url + "cost/rentData";
    public static ArrayList<BasicNameValuePair> rentValues = new ArrayList<BasicNameValuePair>();

    // 蟹苗成本
    public static final String seedURL = Url + "cost/seedData";
    public static ArrayList<BasicNameValuePair> seedValues = new ArrayList<BasicNameValuePair>();

    // 套养种苗成本
    public static final String summaryURL = Url + "cost/seedData";
    public static ArrayList<BasicNameValuePair> summaryValues = new ArrayList<BasicNameValuePair>();

    // 劳务成本
    public static final String labourURL = Url + "cost/labourData";
    public static ArrayList<BasicNameValuePair> labourValues = new ArrayList<BasicNameValuePair>();

    // 螃蟹销售额
    public static final String crabURL = Url + "sales/CrabSale";
    public static ArrayList<BasicNameValuePair> crabValues = new ArrayList<BasicNameValuePair>();

    // 套养总销售额
    public static final String salesURL = Url + "sales/PolycultureSale";
    public static ArrayList<BasicNameValuePair> salesValues = new ArrayList<BasicNameValuePair>();

    // 指定水域  或 所有水域  历史数据
    public static final String historyURL = Url + "history/allhistoryData";
    public static ArrayList<BasicNameValuePair> historyValues = new ArrayList<BasicNameValuePair>();



    private static final String TAG = "uploadFile";


    private static final int TIME_OUT = 10 * 1000; // 超时时间


    private static final String CHARSET = "utf-8"; // 设置编码

    public static final String SUCCESS = "1";
    public static final String FAILURE = "0";

    /**
     * 对网络连接状态进行判断
     *
     * @return true, 可用； false， 不可用
     */
    public static boolean isOpenNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager.getActiveNetworkInfo() != null) {
            return connManager.getActiveNetworkInfo().isAvailable();
        }

        return false;
    }



    /**
     * get请求
     *
     * @param urlString
     * @param params
     * @return
     */
    public static String getRequest(String urlString, Map<String, String> params) {



        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(urlString);

            if (null != params) {

                urlBuilder.append("?");

                Iterator<Map.Entry<String, String>> iterator = params.entrySet()
                        .iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, String> param = iterator.next();
                    urlBuilder
                            .append(URLEncoder.encode(param.getKey(), "UTF-8"))
                            .append('=')
                            .append(URLEncoder.encode(param.getValue(), "UTF-8"));
                    if (iterator.hasNext()) {
                        urlBuilder.append('&');
                    }
                }
            }
            // 创建HttpClient对象
            HttpClient client = new DefaultHttpClient();
            // 发送get请求创建HttpGet对象
            HttpGet getMethod = new HttpGet(urlBuilder.toString());
            HttpResponse response = client.execute(getMethod);
            // 获取状态码
            int res = response.getStatusLine().getStatusCode();
            if (res == 200) {
                Log.e("POST","POST请求成功");
                StringBuilder builder = new StringBuilder();
                // 获取响应内容
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                for (String s = reader.readLine(); s != null; s = reader
                        .readLine()) {
                    builder.append(s);
                }
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("POST",e.toString());
        }

        return null;
    }




    /**
     * post请求
     *
     * @param urlString
     * @param params
     * @return
     */
    public static String postRequest(String urlString,
                                     List<BasicNameValuePair> params) {
        //        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
//                values.add(new BasicNameValuePair("page","1"));
//                values.add(new BasicNameValuePair("kind","none"));
//                byte[] bytes = values.toString().getBytes();
//                String s = postRequest("http://210.28.188.98:8080/IntelligentAgriculture/system/index_log", values);
//
//                Log.e("POST",s);
//            }
//        }).start();


        try {
            // 1. 创建HttpClient对象
            getHttpClient();
            // 2. 发get请求创建HttpGet对象
            HttpPost postMethod = new HttpPost(urlString);
            postMethod.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            if(null != JSESSIONID){
                postMethod.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
                Log.e("POST","comes here" + "JSESSIONID="+JSESSIONID);
            }

            HttpResponse response = httpClient.execute(postMethod);
            int statueCode = response.getStatusLine().getStatusCode();

            if (statueCode == 200) {
                System.out.println(statueCode);

                /* 获取cookieStore */
                CookieStore cookieStore = httpClient.getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();

                if (cookies.size() != 0){
                    for(int i=0;i<cookies.size();i++){

                        if("JSESSIONID".equals(cookies.get(i).getName())){
                            Log.e("POST","comes here" + cookies.size());
                            JSESSIONID = cookies.get(i).getValue();
                            break;
                        }
                    }
                }




                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("POST异常",e.toString());
        }

        return null;
    }




    /**
     * post请求
     *
     * @param urlString
     * @param params
     * @return
     */
    public static String postImage(String urlString,MultipartEntity multipartEntity) {
        //        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ArrayList<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>();
//                values.add(new BasicNameValuePair("page","1"));
//                values.add(new BasicNameValuePair("kind","none"));
//                byte[] bytes = values.toString().getBytes();
//                String s = postRequest("http://210.28.188.98:8080/IntelligentAgriculture/system/index_log", values);
//
//                Log.e("POST",s);
//            }
//        }).start();


        try {
            // 1. 创建HttpClient对象
            getHttpClient();
            // 2. 发get请求创建HttpGet对象
            HttpPost postMethod = new HttpPost(urlString);

            postMethod.setEntity(multipartEntity);





            if(null != JSESSIONID){
                postMethod.setHeader("Cookie", "JSESSIONID="+JSESSIONID);
                Log.e("POST","comes here" + "JSESSIONID="+JSESSIONID);
            }

            HttpResponse response = httpClient.execute(postMethod);
            int statueCode = response.getStatusLine().getStatusCode();

            if (statueCode == 200) {
                System.out.println(statueCode);

                /* 获取cookieStore */
                CookieStore cookieStore = httpClient.getCookieStore();
                List<Cookie> cookies = cookieStore.getCookies();

                if (cookies.size() != 0){
                    for(int i=0;i<cookies.size();i++){

                        if("JSESSIONID".equals(cookies.get(i).getName())){
                            Log.e("POST","comes here" + cookies.size());
                            JSESSIONID = cookies.get(i).getValue();
                            break;
                        }
                    }
                }




                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("POST异常",e.toString());
        }

        return null;
    }

    /**
     * @Title:              getHttpClient
     * @Description:        获取HttpClient
     * @return
     * @throws Exception
     * HttpClient               返回
     */
    public static HttpClient getHttpClient(){

        // 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
        httpParams = new BasicHttpParams();
        // 设置连接超时和 Socket 超时，以及 Socket 缓存大小
        HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 10 * 1000);
//        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        // 设置重定向，缺省为 true
        HttpClientParams.setRedirecting(httpParams, true);
        // 设置 user agent
//        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
//        HttpProtocolParams.setUserAgent(httpParams, userAgent);
        // 创建一个 HttpClient 实例
        // 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient
        // 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient
        httpClient = new DefaultHttpClient(httpParams);
        return httpClient;
    }




    public static String loginRequest(){
        loginValues.add(new BasicNameValuePair("power","0"));
        loginValues.add(new BasicNameValuePair("userName","zhangsan"));
        loginValues.add(new BasicNameValuePair("password","12121212"));
        String s = postRequest(loginURL, loginValues);



        return s;
    }


    public static String getBuyLogRequest(){
        buyLogValues.add(new BasicNameValuePair("page","0"));
        buyLogValues.add(new BasicNameValuePair("kind","buy"));
        String s = postRequest(buyLogURL,buyLogValues);
        return s;
    }



    public static String getBuyTypeRequest(){
        String currentTime = getCurrentTime();

        buyTypeValues.add(new BasicNameValuePair("time",currentTime));
        String s = postRequest(buyTypeURL, buyTypeValues);
        return s;
    }

    public static String getPondInfoRequest(){
        String currentTime = getCurrentTime();

        pondInfoValues.add(new BasicNameValuePair("time",currentTime));
        String s = postRequest(pondInfoURL, pondInfoValues);
        return s;
    }

    public static String getBuyThingRequest(String throwType){


        Log.e("最终参数",throwType);
        String currentTime = getCurrentTime();

        buyNameValues.add(new BasicNameValuePair("time",currentTime));
        buyNameValues.add(new BasicNameValuePair("throwType",throwType));
        String s = postRequest(buyNameURL, buyNameValues);
        return s;
    }


    public static String getCurrentTime() {
        //"yyyy年MM月dd日   HH:mm:ss"
        SimpleDateFormat formatter   =   new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate =  new Date(System.currentTimeMillis());
//获取当前时间
        String   str   =   formatter.format(curDate);

        return str;
    }


    public static void writeFileToSD() {
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            Log.d("TestFile", "SD card is not avaiable/writeable right now.");
            return;
        }
        try {
            String pathName="/sdcard/test/";
            String fileName="file.txt";
            File path = new File(pathName);
            File file = new File(pathName + fileName);
            if( !path.exists()) {
                Log.d("TestFile", "Create the path:" + pathName);
                path.mkdir();
            }
            if( !file.exists()) {
                Log.d("TestFile", "Create the file:" + fileName);
                file.createNewFile();
            }
            FileOutputStream stream = new FileOutputStream(file);
            String s = "this is a test string writing to file.";
            byte[] buf = s.getBytes();
            stream.write(buf);
            stream.close();

        } catch(Exception e) {
            Log.e("TestFile", "Error on writeFilToSD.");
            e.printStackTrace();
        }
    }

}
