package com.jit.shuichan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jit.shuichan.ui.androidpn.NotifierUtils;
import com.jit.shuichan.ui.cameralist.EZCameraListActivity;
import com.videogo.constant.Constant;
import com.videogo.constant.IntentConsts;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EzvizAPI;
import com.videogo.openapi.bean.EZAccessToken;
import com.videogo.openapi.bean.EZPushAlarmMessage;
import com.videogo.openapi.bean.EZPushBaseMessage;
import com.videogo.openapi.bean.EZPushTransferMessage;
import com.videogo.util.Utils;

/**
 * 监听广播
 * 
 * @author fangzhihua
 * @data 2013-1-17
 */
public class EzvizBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "EzvizBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String message = intent.getStringExtra("message_extra");
        if (action.equals("com.ezviz.push.sdk.android.intent.action.MESSAGE")) {
            EZPushBaseMessage baseMessage =  EZOpenSDK.getInstance().parsePushMessage(message);
            if (baseMessage != null) {
                switch(baseMessage.getMessageType()) {
                    case 1:
                        EZPushAlarmMessage alarmMessage= (EZPushAlarmMessage) baseMessage;
                        Log.i(TAG, "onReceive: Push get alarm message:" + alarmMessage);
                        break;
                    case 99:
                        EZPushTransferMessage transferMessage = (EZPushTransferMessage) baseMessage;
                        Log.i(TAG, "onReceive: Push get transfer message:" + transferMessage);
                        break;
                    default:
                        Log.i(TAG, "onReceive: Push get other message:");
                }
            }
        }
        if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            EzvizAPI.getInstance().refreshNetwork();
        } else if(action.equals(Constant.ADD_DEVICE_SUCCESS_ACTION)) {
            String deviceId = intent.getStringExtra(IntentConsts.EXTRA_DEVICE_ID);
            Utils.showToast(context, context.getString(R.string.device_is_added, deviceId));
        } else if(action.equals(Constant.OAUTH_SUCCESS_ACTION)) {
            Intent toIntent = new Intent(context, EZCameraListActivity.class);
            toIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            
            /*******   获取登录成功之后的EZAccessToken对象   *****/
            EZAccessToken token = EzvizApplication.getOpenSDK().getEZAccessToken();
            context.startActivity(toIntent);
        } else if ("com.videogo.androidpn.NOTIFICATION_RECEIVED_ACTION".equals(action)) {
            NotifierUtils.showNotification(context, intent);
        }
    }

}
