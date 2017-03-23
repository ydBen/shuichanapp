package com.jit.shuichan.ui.diary;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.jit.shuichan.R;
import com.jit.shuichan.ui.customListView.BuyLogInfo;
import com.jit.shuichan.widget.PullToRefreshFooter;
import com.jit.shuichan.widget.PullToRefreshHeader;
import com.jit.shuichan.widget.pulltorefresh.IPullToRefresh;
import com.jit.shuichan.widget.pulltorefresh.LoadingLayout;
import com.jit.shuichan.widget.pulltorefresh.PullToRefreshBase;
import com.jit.shuichan.widget.pulltorefresh.PullToRefreshListView;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.StringBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;

import static com.jit.shuichan.http.NetUtils.buyLogURL;
import static com.jit.shuichan.http.NetUtils.postImage;

/**
 * Created by yuanyuan on 2017/3/15.
 */

public class LogActivity extends Activity {

    // 采购日志 ListView
    private PullToRefreshListView mListView = null;
    private View mNoMoreView;
    private LogAdapter mAdapter = null;
    private boolean mHeaderOrFooter = true;

    //private EzvizAPI mEzvizAPI = null;
    private BroadcastReceiver mReceiver = null;



    ArrayList<BuyLogInfo> data;
    ArrayList<BuyLogInfo> moreData;
    ListView lvLog;
    String currentPage = "1";
    String totalPages;


    /**
     * 获取采购数据
     */
    protected static final int ADD_DATA = 100;
//    protected static final int ADD_MOREDATA = 101;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ADD_DATA:
                    mListView.onRefreshComplete();
                        Log.e("adapter","来到了setAdapter方法" + data.size());
                        mAdapter = new LogAdapter(LogActivity.this,data);
                        mListView.setAdapter(mAdapter);
                        if (data != null){
                            if (mHeaderOrFooter) {
                                CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                                for (LoadingLayout layout : mListView.getLoadingLayoutProxy(true, false).getLayouts()) {
                                    ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
                                }
                                Log.e("refresh","下拉刷新");
                            }
                            if (mAdapter.getCount() == 0 && data.size() == 0) {
                                mListView.setVisibility(View.GONE);
                                mListView.getRefreshableView().removeFooterView(mNoMoreView);
                            } else if (currentPage.equals(totalPages)) {
                                mHeaderOrFooter = false;
                                mListView.setFooterRefreshEnabled(false);
                                mListView.setHeaderRefreshEnabled(false);
                                for (LoadingLayout layout : mListView.getLoadingLayoutProxy(true, false).getLayouts()) {
                                    ((PullToRefreshHeader) layout).setLastRefreshTime(":" + "没有更多内容了");
                                }
                                mListView.getRefreshableView().addFooterView(mNoMoreView);
                            } else if (mHeaderOrFooter) {
                                mListView.setFooterRefreshEnabled(true);
                                mListView.getRefreshableView().removeFooterView(mNoMoreView);
                            }
//                            addBuyLogInfoList(data);
                            mAdapter.notifyDataSetChanged();
                        }


                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        initView();
        initData();
    }

    private void initData() {
//        mListView.setRefreshing();
    }

    private void initView() {

        mNoMoreView = getLayoutInflater().inflate(R.layout.no_device_more_footer, null);
        mListView = (PullToRefreshListView) findViewById(R.id.lv_log);
        mListView.setLoadingLayoutCreator(new PullToRefreshBase.LoadingLayoutCreator() {
            @Override
            public LoadingLayout create(Context context, boolean headerOrFooter, PullToRefreshBase.Orientation orientation) {
                if (headerOrFooter){
                    return new PullToRefreshHeader(context);
                }else {
                    return new PullToRefreshFooter(context, PullToRefreshFooter.Style.EMPTY_NO_MORE);
                }
            }
        });

        mListView.setMode(IPullToRefresh.Mode.BOTH);
        mListView.setOnRefreshListener(new IPullToRefresh.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView, boolean headerOrFooter) {
                // 将事件交给线程处理
                Log.e("事件处理","事件处理方法");
                getBuyLogData(headerOrFooter);
            }
        });
        mListView.getRefreshableView().addFooterView(mNoMoreView);
        mListView.setAdapter(mAdapter);
        mListView.getRefreshableView().removeFooterView(mNoMoreView);

        getDataFromServer(currentPage);
    }

    private void getBuyLogData(boolean headerOrFooter) {
//        if (this.isFinishing()){
//            return;
//        }
        mHeaderOrFooter = headerOrFooter;
        Log.e("getBuyLogData","getBuyLogData方法");
        getDataFromServer(currentPage);
    }

    private void getDataFromServer(final String page) {


                        Log.e("getDataFromServer","getDataFromServer方法");

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                MultipartEntity multipartEntity = new MultipartEntity();
                        try {
                            multipartEntity.addPart("page", new StringBody(page, Charset.forName("UTF-8")));
                            multipartEntity.addPart("kind", new StringBody("buy", Charset.forName("UTF-8")));
                            multipartEntity.addPart("userId", new StringBody("2", Charset.forName("UTF-8")));

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        String buyThingRequest = postImage(buyLogURL, multipartEntity);
                        Log.e("采购日志:",buyThingRequest);
                try {
                    JSONObject jo = new JSONObject(buyThingRequest);
                    currentPage = jo.getString("currentPage");
                    totalPages = jo.getString("totalPages");
                    if (!currentPage.equals(totalPages)){
                        currentPage = String.valueOf(Integer.valueOf(currentPage)+1);
                    }
                    JSONArray ja = jo.getJSONArray("recordsByPage");
                    if (data == null){
                        data = new ArrayList<BuyLogInfo>();
                    }
                    for (int i = 0;i < ja.length();i++){
                        JSONObject jo1 = (JSONObject) ja.opt(i);
                        BuyLogInfo info = new BuyLogInfo();
                        info.logTime = jo1.optString("ActiveTime");
                        info.logType = jo1.getString("InputTypeName");
                        info.logName = jo1.getString("Name");
                        info.logAmount = String.valueOf(jo1.getInt("Count"));
                        info.logUnit = jo1.getString("Unit");
                        info.logPrice = String.valueOf(jo1.getInt("Price"));
                        info.logSum = String.valueOf(jo1.getInt("Value"));
                        info.logImage = jo1.getString("ImageUrl");
                        Log.e("InputTypeName","InputTypeName" + info.logType);
                        Log.e("for循环","实时数据量" + data.size() + info.logTime);
                        data.add(info);
                    }

                    //for循环结束展示数据
                    message.what = ADD_DATA;
                } catch (JSONException e) {

                    Log.e("解析异常",e.toString());
                }finally {
                    mHandler.sendMessage(message);
                }

            }
        }).start();




    }


//    // 获取消息事件任务
//    private class GetBuyLogInfoTask extends AsyncTask<Void,Void,ArrayList<BuyLogInfo>> {
//        private boolean mHeaderOrFooter;
//
//        public GetBuyLogInfoTask(boolean headerOrFooter) {
//            mHeaderOrFooter = headerOrFooter;
//            Log.e("GetBuyLogInfoTask","GetBuyLogInfoTask方法");
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mListView.getRefreshableView().removeFooterView(mNoMoreView);
//        }
//
//        @Override
//        protected ArrayList<BuyLogInfo> doInBackground(Void... params) {
////            if (LogActivity.this.isFinishing()){
////                return null;
////            }
//            String result = getDataFromServer(currentPage);
//            ArrayList<BuyLogInfo> buyLogInfos = parseData(result);
//            Log.e("有问题",String.valueOf(buyLogInfos.size()));
//            return buyLogInfos;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<BuyLogInfo> buyLogInfos) {
//            super.onPostExecute(buyLogInfos);
//            mListView.onRefreshComplete();
////            if (LogActivity.this.isFinishing()){
////                return;
////            }
//            Log.e("onPostExecute","onPostExecute方法");
//            if (buyLogInfos != null){
//                if (mHeaderOrFooter) {
//                    CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
//                    for (LoadingLayout layout : mListView.getLoadingLayoutProxy(true, false).getLayouts()) {
//                        ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
//                    }
//                    mAdapter.clearItem();
//                }
//                if (mAdapter.getCount() == 0 && buyLogInfos.size() == 0) {
//                    mListView.setVisibility(View.GONE);
//                    mListView.getRefreshableView().removeFooterView(mNoMoreView);
//                } else if (currentPage.equals(totalPages)) {
//                    mListView.setFooterRefreshEnabled(false);
//                    mListView.getRefreshableView().addFooterView(mNoMoreView);
//                } else if (mHeaderOrFooter) {
//                    mListView.setFooterRefreshEnabled(true);
//                    mListView.getRefreshableView().removeFooterView(mNoMoreView);
//                }
//                addBuyLogInfoList(buyLogInfos);
//                mAdapter.notifyDataSetChanged();
//            }
//        }
//    }



    private void addBuyLogInfoList(ArrayList<BuyLogInfo> buyLogInfos) {
//        mAdapter.clearItem();
        int count = buyLogInfos.size();
        Log.e("数据","数据量" + String.valueOf(count) + "data数量" + data.size());
        BuyLogInfo item = null;
        for (int i = 0; i < count; i++) {
            item = buyLogInfos.get(i);
            mAdapter.addItem(item);
        }
    }


    /**
     * 刷新点击
     */
//    private void refreshButtonClicked() {
//        currentPage = "1";
//        getDataFromServer(currentPage);
//    }
}
