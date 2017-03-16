package com.jit.shuichan.ui.customListView;

import android.view.View;

/**
 * Created by yuanyuan on 2017/3/16.
 */

public abstract class BaseHolder<T> {
    private View mRootView;   //一个item的根布局

    private T data;

    // holder作用 加载布局 初始化控件 设置Tag
    public BaseHolder() {
        mRootView = initView();

        // 打标记
        mRootView.setTag(this);
    }

    //1. 加载布局文件
    //2. 初始化控件
    public abstract View initView();

        // 返回item的布局对象
        public View getRootView(){
            return mRootView;
        }

        //设置当前item的数据
        public void setData(T data){
            this.data = data;
            refreshView(data);
        }

        //  获取当前item的数据
        public T getData(){
            return  data;
        }


    // 4
    public abstract void refreshView(T data);
}
