package com.jit.shuichan.ui.diary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jit.shuichan.R;
import com.jit.shuichan.ui.customListView.BuyLogInfo;

import java.util.ArrayList;

/**
 * Created by yuanyuan on 2017/3/20.
 */

public class LogAdapter extends BaseAdapter {
    private Context mContext = null;
    private ArrayList<BuyLogInfo> mBuyLogInfoList = null;


    public void clearItem() {
        //mExecuteItemMap.clear();
        mBuyLogInfoList.clear();
    }

    public void clearAll(){
        mBuyLogInfoList.clear();
        notifyDataSetChanged();
    }

    // 需要重新考虑的逻辑
    public void addItem(BuyLogInfo item) {
        mBuyLogInfoList.add(item);
    }

    public static class ViewHolder {
        TextView tvActiveTime,tvInputTypeName,tvName,tvCount,tvPrice,tvValue;
    }

    public LogAdapter(Context context, ArrayList<BuyLogInfo> info) {
        mBuyLogInfoList = info;
        mContext = context;
        Log.e("logAdapter","来到了logAdapter的构造方法，获取到到data数量是:" + mBuyLogInfoList.size());
    }

    @Override
    public int getCount() {
        return mBuyLogInfoList.size();
    }

    @Override
    public BuyLogInfo getItem(int i) {
        BuyLogInfo info = mBuyLogInfoList.get(i);
        return info;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 自定义视图
        ViewHolder viewHolder = null;
        if (view == null){
            viewHolder = new ViewHolder();
            //  加载布局
           view = LayoutInflater.from(mContext).inflate(R.layout.log_lv_item,null);

            // 2. 初始化控件
            viewHolder.tvActiveTime = (TextView) view.findViewById(R.id.tv_logTime);
            viewHolder.tvInputTypeName = (TextView) view.findViewById(R.id.tv_logType);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tv_logName);
            viewHolder.tvCount = (TextView) view.findViewById(R.id.tv_logMount);
            viewHolder.tvPrice = (TextView) view.findViewById(R.id.tv_logPrice);
            viewHolder.tvValue = (TextView) view.findViewById(R.id.tv_logSum);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BuyLogInfo info = getItem(i);

        viewHolder.tvActiveTime.setText(info.logTime);
        viewHolder.tvInputTypeName.setText(info.logType);
        viewHolder.tvName.setText(info.logName);
        viewHolder.tvCount.setText(info.logAmount);
        viewHolder.tvPrice.setText(info.logPrice);
        viewHolder.tvValue.setText(info.logSum);



        return view;
    }
}
