/* 
 * @ProjectName VideoGoJar
 * @Copyright HangZhou Hikvision System Technology Co.,Ltd. All Right Reserved
 * 
 * @FileName CameraListActivity.java
 * @Description 这里对文件进行描述
 * 
 * @author xia xingsuo
 * @data 2015-11-5
 * 
 * @note 这里写本文件的详细功能描述和注释
 * @note 历史记录
 * 
 * @warning 这里写本文件的相关警告
 */
package com.jit.shuichan.ui.cameralist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jit.shuichan.EzvizApplication;
import com.jit.shuichan.R;
import com.jit.shuichan.devicemgt.EZDeviceSettingActivity;
import com.jit.shuichan.mina.gsondata.EnvData;
import com.jit.shuichan.mina.gsondata.SensorInfo;
import com.jit.shuichan.remoteplayback.list.PlayBackListActivity;
import com.jit.shuichan.remoteplayback.list.RemoteListContant;
import com.jit.shuichan.scan.main.CaptureActivity;
import com.jit.shuichan.ui.diary.FeedingActivity;
import com.jit.shuichan.ui.diary.ShoppingActivity;
import com.jit.shuichan.ui.message.EZMessageActivity2;
import com.jit.shuichan.ui.realplay.EZRealPlayActivity;
import com.jit.shuichan.ui.util.EZUtils;
import com.jit.shuichan.widget.PullToRefreshFooter;
import com.jit.shuichan.widget.PullToRefreshHeader;
import com.jit.shuichan.widget.WaitDialog;
import com.jit.shuichan.widget.pulltorefresh.IPullToRefresh.Mode;
import com.jit.shuichan.widget.pulltorefresh.IPullToRefresh.OnRefreshListener;
import com.jit.shuichan.widget.pulltorefresh.LoadingLayout;
import com.jit.shuichan.widget.pulltorefresh.PullToRefreshBase;
import com.jit.shuichan.widget.pulltorefresh.PullToRefreshListView;
import com.videogo.constant.Constant;
import com.videogo.constant.IntentConsts;
import com.videogo.errorlayer.ErrorInfo;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZGlobalSDK;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZOpenSDKListener;
import com.videogo.openapi.bean.EZAreaInfo;
import com.videogo.openapi.bean.EZCameraInfo;
import com.videogo.openapi.bean.EZDeviceInfo;
import com.videogo.openapi.bean.EZLeaveMessage;
import com.videogo.util.ConnectionDetector;
import com.videogo.util.DateTimeUtil;
import com.videogo.util.LogUtil;
import com.videogo.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 摄像头列表
 *
 * @author xiaxingsuo
 * @data 2014-7-14
 */
public class EZCameraListActivity extends Activity implements OnClickListener,SelectCameraDialog.CameraItemClick{
    protected static final String TAG = "CameraListActivity";
    private final static int REQUEST_CODE = 100;
    /**
     * 删除设备
     */
    private final static int SHOW_DIALOG_DEL_DEVICE = 1;

    //private EzvizAPI mEzvizAPI = null;
    private BroadcastReceiver mReceiver = null;
    // 视频ListView
    private PullToRefreshListView mListView = null;
    // 水产 ListView
    private ListView listView;
    // 养殖日志 UI
    private LinearLayout llDiary;
    private String[] mTitleStrs;
    private String[] mDetailStrs;
    private int[] mMipMapIds;
    private GridView gvDiary;

    private View mNoMoreView;
    private EZCameraListAdapter mAdapter = null;

    private LinearLayout mNoCameraTipLy = null;
    private LinearLayout mGetCameraFailTipLy = null;
    private TextView mCameraFailTipTv = null;
    private Button mAddBtn;
    private Button mUserBtn;
    private TextView mMyDevice;
    private TextView mShareDevice;
    private TextView mDiaryUI;

    private boolean bIsFromSetting = false;

    public final static int TAG_CLICK_PLAY = 1;
    public final static int TAG_CLICK_REMOTE_PLAY_BACK = 2;
    public final static int TAG_CLICK_SET_DEVICE = 3;
    public final static int TAG_CLICK_ALARM_LIST = 4;

    private int mClickType;

    private final static int LOAD_MY_DEVICE = 0;
    private final static int LOAD_SHARE_DEVICE = 1;
    private int mLoadType = LOAD_MY_DEVICE;

    private EnvData mEnvData;
    private ArrayList<SensorInfo> mSensorInfos;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtil.d(TAG,"handleMessage "+msg);
            /***获取下载的留言ID***/
            String mId;
            switch (msg.what){
                case 301:
                    // TODO: 2016/10/13   下载留言失败
                    mId = (String) msg.obj;
                    break;
                case 302:
                    // TODO: 2016/10/13  下载留言成功
                    /***获取下载成功的留言ID***/
                    mId = (String) msg.obj;
                    break;
                default:break;
            }
        }
    };



    @Override
    public void onCameraItemClick(EZDeviceInfo deviceInfo, int camera_index) {
        EZCameraInfo cameraInfo = null;
        Intent intent = null;
        switch (mClickType) {
            case TAG_CLICK_PLAY:
//                String pwd = DESHelper.decryptWithBase64("EyCs73n8m5s=", Utils.getAndroidID(this));
                cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, camera_index);
                if (cameraInfo == null) {
                    return;
                }


                intent = new Intent(EZCameraListActivity.this, EZRealPlayActivity.class);
                intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case TAG_CLICK_REMOTE_PLAY_BACK:
                cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, camera_index);
                if (cameraInfo == null) {
                    return;
                }
                intent = new Intent(EZCameraListActivity.this, PlayBackListActivity.class);
                intent.putExtra(RemoteListContant.QUERY_DATE_INTENT_KEY, DateTimeUtil.getNow());
                intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cameralist_page);
        initData();
        initView();
        Utils.clearAllNotification(this);


//        EventBus.getDefault().register(this);

//        EventBus.getDefault().register(this);
//        Log.e("tag", "点击启动服务");
//        Intent intent = new Intent(this, MinaService.class);
//        startService(intent);
    }

    private void initView() {
        mMyDevice = (TextView) findViewById(R.id.text_my);
        mShareDevice = (TextView) findViewById(R.id.text_share);
        mDiaryUI = (TextView) findViewById(R.id.text_diary);
        mAddBtn = (Button) findViewById(R.id.btn_add);
        mUserBtn = (Button) findViewById(R.id.btn_user);

        // 个人中心
        mUserBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                popLogoutDialog();
                Toast.makeText(getApplicationContext(),"预留的个人中心接口",Toast.LENGTH_SHORT).show();
            }
        });

        mAddBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EZCameraListActivity.this, CaptureActivity.class);
                startActivity(intent);
            }
        });

        mShareDevice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击鸡舍环境按钮获取相关信息，决定ListView的显示


                mShareDevice.setTextColor(getResources().getColor(R.color.orange_text));
                mMyDevice.setTextColor(getResources().getColor(R.color.black_text));
                mDiaryUI.setTextColor(getResources().getColor(R.color.black_text));


                listView.setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
                mGetCameraFailTipLy.setVisibility(View.GONE);
                llDiary.setVisibility(View.GONE);
                /*mAdapter.clearAll();
                mLoadType = LOAD_SHARE_DEVICE;
                getCameraInfoList(true);*/
            }
        });

        mMyDevice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareDevice.setTextColor(getResources().getColor(R.color.black_text));
                mMyDevice.setTextColor(getResources().getColor(R.color.orange_text));
                mDiaryUI.setTextColor(getResources().getColor(R.color.black_text));


                mListView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                llDiary.setVisibility(View.GONE);
                /*mAdapter.clearAll();
                mLoadType = LOAD_MY_DEVICE;
                getCameraInfoList(true);*/
            }
        });

        mDiaryUI.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDiaryUI.setTextColor(getResources().getColor(R.color.orange_text));
                mShareDevice.setTextColor(getResources().getColor(R.color.black_text));
                mMyDevice.setTextColor(getResources().getColor(R.color.black_text));

                listView.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                mGetCameraFailTipLy.setVisibility(View.GONE);
                llDiary.setVisibility(View.VISIBLE);
            }
        });

        mNoMoreView = getLayoutInflater().inflate(R.layout.no_device_more_footer, null);
        mAdapter = new EZCameraListAdapter(this);
        mAdapter.setOnClickListener(new EZCameraListAdapter.OnClickListener() {
            @Override
            public void onPlayClick(BaseAdapter adapter, View view, int position) {
                mClickType = TAG_CLICK_PLAY;
                final EZDeviceInfo deviceInfo = mAdapter.getItem(position);
                if (deviceInfo.getCameraNum() <= 0 || deviceInfo.getCameraInfoList() == null || deviceInfo.getCameraInfoList().size() <= 0) {
                    LogUtil.d(TAG,"cameralist is null or cameralist size is 0");
                    return;
                }



                // 普通设备
                if (deviceInfo.getCameraNum() == 1 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() == 1) {
                    LogUtil.d(TAG,"cameralist have one camera");
                    final EZCameraInfo cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, 0);
                    if (cameraInfo == null) {
                        return;
                    }

                    Intent intent = new Intent(EZCameraListActivity.this, EZRealPlayActivity.class);
                    intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                    intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
                    startActivityForResult(intent, REQUEST_CODE);
                    return;
                }


                // 硬盘录像机
                SelectCameraDialog selectCameraDialog = new SelectCameraDialog();
                selectCameraDialog.setEZDeviceInfo(deviceInfo);
                selectCameraDialog.setCameraItemClick(EZCameraListActivity.this);
                selectCameraDialog.show(getFragmentManager(), "onPlayClick");
            }

            @Override
            public void onRemotePlayBackClick(BaseAdapter adapter, View view, int position) {
                mClickType = TAG_CLICK_REMOTE_PLAY_BACK;
                EZDeviceInfo deviceInfo = mAdapter.getItem(position);
                if (deviceInfo.getCameraNum() <= 0 || deviceInfo.getCameraInfoList() == null || deviceInfo.getCameraInfoList().size() <= 0) {
                    LogUtil.d(TAG,"cameralist is null or cameralist size is 0");
                    return;
                }
                if (deviceInfo.getCameraNum() == 1 && deviceInfo.getCameraInfoList() != null && deviceInfo.getCameraInfoList().size() == 1) {
                    LogUtil.d(TAG,"cameralist have one camera");
                    EZCameraInfo cameraInfo = EZUtils.getCameraInfoFromDevice(deviceInfo, 0);
                    if (cameraInfo == null) {
                        return;
                    }
                    Intent  intent = new Intent(EZCameraListActivity.this, PlayBackListActivity.class);
                    intent.putExtra(RemoteListContant.QUERY_DATE_INTENT_KEY, DateTimeUtil.getNow());
                    intent.putExtra(IntentConsts.EXTRA_CAMERA_INFO, cameraInfo);
                    startActivity(intent);
                    return;
                }
                SelectCameraDialog selectCameraDialog = new SelectCameraDialog();
                selectCameraDialog.setEZDeviceInfo(deviceInfo);
                selectCameraDialog.setCameraItemClick(EZCameraListActivity.this);
                selectCameraDialog.show(getFragmentManager(), "RemotePlayBackClick");
            }

            @Override
            public void onSetDeviceClick(BaseAdapter adapter, View view, int position) {
                mClickType = TAG_CLICK_SET_DEVICE;
                EZDeviceInfo deviceInfo = mAdapter.getItem(position);
                Intent intent = new Intent(EZCameraListActivity.this, EZDeviceSettingActivity.class);
                intent.putExtra(IntentConsts.EXTRA_DEVICE_INFO, deviceInfo);
                startActivity(intent);
                bIsFromSetting = true;
            }

            @Override
            public void onDeleteClick(BaseAdapter adapter, View view, int position) {
                showDialog(SHOW_DIALOG_DEL_DEVICE);
            }

            @Override
            public void onAlarmListClick(BaseAdapter adapter, View view, int position) {
                mClickType = TAG_CLICK_ALARM_LIST;
                final EZDeviceInfo deviceInfo = mAdapter.getItem(position);
                LogUtil.d(TAG,"cameralist is null or cameralist size is 0");
                Intent intent = new Intent(EZCameraListActivity.this, EZMessageActivity2.class);
                intent.putExtra(IntentConsts.EXTRA_DEVICE_ID, deviceInfo.getDeviceSerial());
                startActivity(intent);
            }

            @Override
            public void onDevicePictureClick(BaseAdapter adapter, View view, int position) {
            }

            @Override
            public void onDeviceVideoClick(BaseAdapter adapter, View view, int position) {
            }

            @Override
            public void onDeviceDefenceClick(BaseAdapter adapter, View view,
                                             int position) {
            }

        });


        //初始化 视频  listView
        mListView = (PullToRefreshListView) findViewById(R.id.camera_listview);
        // 初始化  水产 listView
        listView = (ListView) findViewById(R.id.id_lv);
        // 初始化  养殖日志 UI
        llDiary = (LinearLayout) findViewById(R.id.ll_diary);



        MyAdapter adapter = new MyAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //position + 1
//                EventBus.getDefault().post(new FactoryIdEvent(position + 1));
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("factoryId",position+1);
//                Log.e("TAG","点击了第几行" + position);
                intent.putExtras(bundle);
                intent.setClass(EZCameraListActivity.this,EnvDetailActivity.class);
                startActivity(intent);
            }
        });
        mListView.setLoadingLayoutCreator(new PullToRefreshBase.LoadingLayoutCreator() {

            @Override
            public LoadingLayout create(Context context, boolean headerOrFooter, PullToRefreshBase.Orientation orientation) {
                if (headerOrFooter)
                    return new PullToRefreshHeader(context);
                else
                    return new PullToRefreshFooter(context, PullToRefreshFooter.Style.EMPTY_NO_MORE);
            }
        });
        mListView.setMode(Mode.BOTH);
        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView, boolean headerOrFooter) {
                Log.e("refreshing","refreshing");
                getCameraInfoList(headerOrFooter);
            }
        });
        mListView.getRefreshableView().addFooterView(mNoMoreView);
        mListView.setAdapter(mAdapter);
        mListView.getRefreshableView().removeFooterView(mNoMoreView);

        mNoCameraTipLy = (LinearLayout) findViewById(R.id.no_camera_tip_ly);
        mGetCameraFailTipLy = (LinearLayout) findViewById(R.id.get_camera_fail_tip_ly);
        mCameraFailTipTv = (TextView) findViewById(R.id.get_camera_list_fail_tv);
    }



    private void initData() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                LogUtil.debugLog(TAG, "onReceive:" + action);
                if (action.equals(Constant.ADD_DEVICE_SUCCESS_ACTION)) {
                    refreshButtonClicked();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ADD_DEVICE_SUCCESS_ACTION);
        registerReceiver(mReceiver, filter);


        // 养殖日志 数据
        mTitleStrs = new String[]{
            "日常采购","日常投放","巡视检查","生产措施"
        };
        mDetailStrs = new String[]{
            "饲养必需品购买","饲料投放情况","轻松记录异常","积累丰富经验"
        };
        mMipMapIds = new int[]{
                R.mipmap.shopping_btn,R.mipmap.feeding_btn,R.mipmap.checking_btn,R.mipmap.solution_ptn
        };

        gvDiary = (GridView)findViewById(R.id.gv_diary);

        gvDiary.setAdapter(new GridAdapter());

        //点击事件
        gvDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:// 日常采购
                        startActivity(new Intent(EZCameraListActivity.this, ShoppingActivity.class));
                        break;
                    case 1:// 日常投放
                        startActivity(new Intent(EZCameraListActivity.this, FeedingActivity.class));
                        break;
                    case 2:// 巡视检查
                        Log.e("gv","点击item2");
                        break;
                    case 3:// 生产措施
                        Log.e("gv","点击item3");
                        break;
                }
            }
        });
    }

    class GridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mTitleStrs.length;
        }

        @Override
        public Object getItem(int i) {
            return mTitleStrs[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View contentView = View.inflate(getApplicationContext(),R.layout.gv_item_diary,null);
            TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
            TextView tvDetail = (TextView) contentView.findViewById(R.id.tv_title_detail);
            ImageView ivIcon = (ImageView) contentView.findViewById(R.id.iv_icon);

            tvTitle.setText(mTitleStrs[i]);
            tvDetail.setText(mDetailStrs[i]);
            ivIcon.setBackgroundResource(mMipMapIds[i]);

            return contentView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bIsFromSetting || (mAdapter != null && mAdapter.getCount() == 0)) {
            refreshButtonClicked();
            bIsFromSetting = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.shutDownExecutorService();
        }
    }

    /**
     * 从服务器获取最新事件消息
     */
    private void getCameraInfoList(boolean headerOrFooter) {
        if (this.isFinishing()) {
            return;
        }
        new GetCamersInfoListTask(headerOrFooter).execute();
    }

    /**
     * 获取事件消息任务
     */
    private class GetCamersInfoListTask extends AsyncTask<Void, Void, List<EZDeviceInfo>> {
        private boolean mHeaderOrFooter;
        private int mErrorCode = 0;

        public GetCamersInfoListTask(boolean headerOrFooter) {
            mHeaderOrFooter = headerOrFooter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mListView.setFooterRefreshEnabled(true);
            if (mHeaderOrFooter){
                mListView.setVisibility(View.VISIBLE);
                mNoCameraTipLy.setVisibility(View.GONE);
                mGetCameraFailTipLy.setVisibility(View.GONE);
            }
            mListView.getRefreshableView().removeFooterView(mNoMoreView);
        }

        @Override
        protected List<EZDeviceInfo> doInBackground(Void... params) {
            if (EZCameraListActivity.this.isFinishing()) {
                return null;
            }
            if (!ConnectionDetector.isNetworkAvailable(EZCameraListActivity.this)) {
                mErrorCode = ErrorCode.ERROR_WEB_NET_EXCEPTION;
                return null;
            }

            try {
                List<EZDeviceInfo> result = null;
                if (mLoadType == LOAD_MY_DEVICE) {
                    if (mHeaderOrFooter) {
                        result = EzvizApplication.getOpenSDK().getDeviceList(0, 20);
                    } else {
                        result = EzvizApplication.getOpenSDK().getDeviceList((mAdapter.getCount() / 20) + 1, 20);
                    }
                }else if(mLoadType == LOAD_SHARE_DEVICE){
                    if (mHeaderOrFooter) {
                        result = EzvizApplication.getOpenSDK().getSharedDeviceList(0, 20);
                    } else {
                        result = EzvizApplication.getOpenSDK().getSharedDeviceList((mAdapter.getCount() / 20) + 1, 20);
                    }
                }
                Log.e("摄像头数量不为0",String.valueOf(result.size()));
                return result;

            } catch (BaseException e) {
                ErrorInfo errorInfo = (ErrorInfo) e.getObject();
                mErrorCode = errorInfo.errorCode;
                LogUtil.debugLog(TAG, errorInfo.toString());

                return null;
            }
        }

        @Override
        protected void onPostExecute(List<EZDeviceInfo> result) {
            super.onPostExecute(result);
            mListView.onRefreshComplete();
            if (EZCameraListActivity.this.isFinishing()) {
                return;
            }

            if (result != null) {
                if (mHeaderOrFooter) {
                    CharSequence dateText = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());
                    for (LoadingLayout layout : mListView.getLoadingLayoutProxy(true, false).getLayouts()) {
                        ((PullToRefreshHeader) layout).setLastRefreshTime(":" + dateText);
                        Log.e("刷新摄像头","刷新摄像头");
                    }

                    mAdapter.clearItem();
                }
                if (mAdapter.getCount() == 0 && result.size() == 0) {
                    mListView.setVisibility(View.GONE);
                    mNoCameraTipLy.setVisibility(View.VISIBLE);
                    mGetCameraFailTipLy.setVisibility(View.GONE);
                    mListView.getRefreshableView().removeFooterView(mNoMoreView);
                } else if (result.size() < 10) {
                    mListView.setFooterRefreshEnabled(false);
                    mListView.getRefreshableView().addFooterView(mNoMoreView);
                } else if (mHeaderOrFooter) {
                    mListView.setFooterRefreshEnabled(true);
                    mListView.getRefreshableView().removeFooterView(mNoMoreView);
                }
                addCameraList(result);
                mAdapter.notifyDataSetChanged();
            }

            if (mErrorCode != 0) {
                onError(mErrorCode);
            }
        }

        protected void onError(int errorCode) {
            switch (errorCode) {
                case ErrorCode.ERROR_WEB_SESSION_ERROR:
                case ErrorCode.ERROR_WEB_SESSION_EXPIRE:
                case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_ERROR:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                List<EZAreaInfo> areaList = EZGlobalSDK.getInstance().getAreaList();
                                if (areaList != null) {
                                    LogUtil.debugLog("application", "list count: " + areaList.size());

                                    EZAreaInfo areaInfo = areaList.get(0);
                                    EZGlobalSDK.getInstance().openLoginPage(areaInfo.getId());
                                }
                            } catch (BaseException e) {
                                e.printStackTrace();

                                EZOpenSDK.getInstance().openLoginPage();
                            }
                        }
                    }).start();
                    break;
                default:
                    if (mAdapter.getCount() == 0) {
                        mListView.setVisibility(View.GONE);
                        mNoCameraTipLy.setVisibility(View.GONE);
                        mCameraFailTipTv.setText(Utils.getErrorTip(EZCameraListActivity.this, R.string.get_camera_list_fail, errorCode));
                        mGetCameraFailTipLy.setVisibility(View.VISIBLE);
                    } else {
                        Utils.showToast(EZCameraListActivity.this, R.string.get_camera_list_fail, errorCode);
                    }
                    break;
            }
        }
    }

    private void addCameraList(List<EZDeviceInfo> result) {
        int count = result.size();
        EZDeviceInfo item = null;
        for (int i = 0; i < count; i++) {
            item = result.get(i);
            mAdapter.addItem(item);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }

        // 解除注册
//        EventBus.getDefault().unregister(this);
//
//        //停止mina服务
//        stopService(new Intent(this, MinaService.class));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_list_refresh_btn:
            case R.id.no_camera_tip_ly:
                refreshButtonClicked();
                break;
            case R.id.camera_list_gc_ly:
//                Intent intent = new Intent(EZCameraListActivity.this, SquareColumnActivity.class);
//                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 刷新点击
     */
    private void refreshButtonClicked() {
        mListView.setVisibility(View.VISIBLE);
        mNoCameraTipLy.setVisibility(View.GONE);
        mGetCameraFailTipLy.setVisibility(View.GONE);
        mListView.setMode(Mode.BOTH);
        mListView.setRefreshing();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case SHOW_DIALOG_DEL_DEVICE:
                break;
        }
        return dialog;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, R.string.update_exit).setIcon(R.drawable.exit_selector);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        if (dialog != null) {
            removeDialog(id);
            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {// 得到被点击的item的itemId
            case 1:// 对应的ID就是在add方法中所设定的Id
                popLogoutDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 弹出登出对话框
     *
     * @see
     * @since V1.0
     */
    private void popLogoutDialog() {
        Builder exitDialog = new AlertDialog.Builder(EZCameraListActivity.this);
        exitDialog.setTitle(R.string.exit);
        exitDialog.setMessage(R.string.exit_tip);
        exitDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new LogoutTask().execute();
            }
        });
        exitDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        exitDialog.show();
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        private Dialog mWaitDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mWaitDialog = new WaitDialog(EZCameraListActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
            mWaitDialog.setCancelable(false);
            mWaitDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            EzvizApplication.getOpenSDK().logout();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mWaitDialog.dismiss();
            /************国内处理方式*********/
            {
                EZOpenSDK.getInstance().openLoginPage();
                finish();

            }
            /*************海外处理方式***************/
            {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//
//                            List<EZAreaInfo> areaList = EZGlobalSDK.getInstance().getAreaList();
//                            if (areaList != null) {
//                                LogUtil.debugLog("application", "list count: " + areaList.size());
//
//                                EZAreaInfo areaInfo = areaList.get(0);
//                                EZGlobalSDK.getInstance().openLoginPage(areaInfo.getId());
//                                finish();
//                            }
//                        } catch (BaseException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode){
            case REQUEST_CODE:
                getCameraInfoList(true);
                break;
            default:
                break;
        }
    }

    /**
     * 查询语音留言并下载获取到的留言文件, 可以直接播放或者存放文件
     * 文件名称需要注意防重，重复下载名称需要区分
     */
    private void downloadLeaveMassage(final String deviceSerial){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar mStartTime = Calendar.getInstance();
                mStartTime.set(Calendar.AM_PM, 0);
                mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH)-1,
                        mStartTime.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                Calendar mEndTime = Calendar.getInstance();
                mEndTime.set(Calendar.AM_PM, 0);
                mEndTime.set(mEndTime.get(Calendar.YEAR), mEndTime.get(Calendar.MONTH),
                        mEndTime.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
                try {
                    List<EZLeaveMessage> leaveMessages = EZOpenSDK.getInstance().getLeaveMessageList(deviceSerial,0,20,mStartTime,mEndTime);
                    if (leaveMessages == null || leaveMessages.size() <= 0){
                        LogUtil.d(TAG,"no leaveMessage");
                    }
                    for (EZLeaveMessage ezLeaveMessage:leaveMessages){
                        /***文件名称需要注意防重，重复下载名称需要区别**/
                        final File file = new File(Environment.getExternalStorageDirectory(),"EZOpenSDK/LeaveMessage/"+deviceSerial+"-"+ezLeaveMessage.getMsgId());
                        File parent = file.getParentFile();
                        if (parent == null || !parent.exists() || parent.isFile()) {
                            parent.mkdirs();
                        }
                        EZOpenSDK.getInstance().getLeaveMessageData(mHandler,ezLeaveMessage,new EZOpenSDKListener.EZLeaveMessageFlowCallback(){
                            @Override
                            public void onLeaveMessageFlowCallback(int i, byte[] bytes, int i1, String s) {
                                LogUtil.d(TAG,"");
                                LogUtil.d(TAG,"bytes"+bytes);
                                if (!file.exists() || !file.isFile()){
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                FileOutputStream fileOutputStream = null;
                                try {
                                    fileOutputStream = new FileOutputStream(file,false);
                                    if (fileOutputStream != null){
                                        fileOutputStream.write(bytes);
                                    }
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    if (fileOutputStream != null){
                                        try {
                                            fileOutputStream.flush();
                                            fileOutputStream.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                    }
                } catch (BaseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
