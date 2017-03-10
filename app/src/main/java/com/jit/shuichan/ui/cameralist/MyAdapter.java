package com.jit.shuichan.ui.cameralist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jit.shuichan.R;
import com.jit.shuichan.widget.UserPermission;

/**
 * Created by QZ on 2016/12/21.
 */

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private String mUser;

    public MyAdapter(Context context){
        mContext = context;
        Log.e("adapter","来到了自定义adapter的构造方法");
        //获取用户权限根据用户权限返回ListView
        Log.e("adapter","得到了权限信息" + UserPermission.getsUserPermission().getmUserName());
        mUser = UserPermission.getsUserPermission().getmUserName();
    }

    @Override
    public int getCount() {
        if (mUser.equals("admin")){
            return 5;
        }else {
            return 1;
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.house_lv_item,null);

            //获取控件对象
            viewHolder.mItemTitleTv = (TextView) convertView.findViewById(R.id.water_area_Tv);

            // 设置控件集到convertView
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if (mUser.equals("admin")){
            switch (position){
                case 0:
                    viewHolder.mItemTitleTv.setText("固城湖1");
                    break;
                case 1:
                    viewHolder.mItemTitleTv.setText("固城湖2");
                    break;
                case 2:
                    viewHolder.mItemTitleTv.setText("渔网大市场1");
                    break;
                case 3:
                    viewHolder.mItemTitleTv.setText("渔网大市场2");
                    break;
                case 4:
                    viewHolder.mItemTitleTv.setText("渔网大市场3");
                    break;

            }
        }else if (mUser.equals("渔网1")){
            viewHolder.mItemTitleTv.setText("渔网大市场1");
        }else if (mUser.equals("渔网2")){
            viewHolder.mItemTitleTv.setText("渔网大市场2");
        }else if (mUser.equals("渔网3")){
            viewHolder.mItemTitleTv.setText("渔网大市场3");
        }







        return convertView;
    }


    public static class ViewHolder{
        TextView mItemTitleTv;
    }
}
