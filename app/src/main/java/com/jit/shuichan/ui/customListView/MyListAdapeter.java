package com.jit.shuichan.ui.customListView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jit.shuichan.EzvizApplication;

import java.util.ArrayList;

/**
 * Created by yuanyuan on 2017/3/16.
 */

public abstract class MyListAdapeter<T> extends BaseAdapter {

    //从0开始写 方便子类扩展
    private static final int TYPE_NORMAL = 0; // 正常布局
    private static final int TYPE_MORE = 1; // 加载更多

    private ArrayList<T> data;

    public MyListAdapeter(ArrayList<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size() + 1;  // 增加加载更多
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // 反回布局类型


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    // 类型


    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1){ // 最后一个
            return TYPE_MORE;
        }else {
            return getInnerType();
        }
    }

    private int getInnerType() {
        return TYPE_NORMAL;   // 默认普通类型
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BaseHolder holder;
        if (view == null){
            // 加载布局文件  初始化控件  打标记
            if (getItemViewType(i) == TYPE_MORE){
                // 加载更多类型
                holder = new MoreHolder(hasMore());
            }else {
                holder = getHolder();  // 子类返回具体对象
            }
        }else {
            holder = (BaseHolder) view.getTag();
        }

        // 根据数据刷新界面
        if (getItemViewType(i) != TYPE_MORE){
            holder.setData(getItem(i));
        }else {
            //加载更多
            MoreHolder moreHolder = (MoreHolder) holder;
            if (moreHolder.getData() == MoreHolder.STATE_MORE_MORE){
                loadMore(moreHolder);
            }
        }

        return null;
    }

    private boolean isLoadMore = false;   // 标记是否正在加载更多

    public void loadMore(final MoreHolder holder) {
        if (!isLoadMore) {
            isLoadMore = true;

            new Thread(){
                @Override
                public void run() {

                    final ArrayList<T> moreData = onLoadMore();

                    EzvizApplication.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (moreData != null){
                                if (moreData.size() < 20){
                                    holder.setData(MoreHolder.STATE_MORE_NONE);
                                    Log.e("数据","没有更多数据了");
                                }else {
                                    // 还有更多数据
                                    holder.setData(MoreHolder.STATE_MORE_MORE);
                                }

                                // 将更多数据添加到集合中
                                data.addAll(moreData);

                                // 刷新界面
                                MyListAdapeter.this.notifyDataSetChanged();
                            }else {
                                // 加载更多失败
                                holder.setData(MoreHolder.STATE_MORE_ERROR);
                            }

                            isLoadMore = false;
                        }

                    });
                }
            }.start();


        }
    }


    public abstract ArrayList<T> onLoadMore();

    // 子类可重写此方法决定是否加载更多
    public boolean hasMore() {
        return true;  // 默认有更多
    }

    public abstract BaseHolder<T>getHolder();

    // 获取当前集合大小
    public int getListSize(){
        return data.size();
    }
}
