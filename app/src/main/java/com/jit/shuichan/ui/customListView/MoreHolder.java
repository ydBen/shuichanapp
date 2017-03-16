package com.jit.shuichan.ui.customListView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jit.shuichan.EzvizApplication;
import com.jit.shuichan.R;

/**
 * Created by yuanyuan on 2017/3/16.
 */

public class MoreHolder extends BaseHolder<Integer>{

    // 加载更多的几种状态
    // 1. 可以加载更多
    // 2. 加载更多失败
    // 3. 没有更多数据
    public  static final int STATE_MORE_MORE = 1;
    public  static final int STATE_MORE_ERROR = 2;
    public  static final int STATE_MORE_NONE = 3;

    private LinearLayout llLoadMore;
    private TextView tvLoadError;

    public MoreHolder(boolean hasMore) {
        setData(hasMore?STATE_MORE_MORE:STATE_MORE_NONE);  //setData结束后一定会调refreshView
    }

    @Override
    public View initView() {
        View view = View.inflate(EzvizApplication.getContext(), R.layout.list_item_more, null);

        llLoadMore = (LinearLayout) view.findViewById(R.id.ll_load_more);
        tvLoadError = (TextView) view.findViewById(R.id.tv_load_error);

        return view;
    }



    @Override
    public void refreshView(Integer data) {
        switch (data){
            case STATE_MORE_MORE:
                // 显示加载更多
                llLoadMore.setVisibility(View.VISIBLE);
                tvLoadError.setVisibility(View.GONE);
                break;
            case STATE_MORE_NONE:
                // 隐藏加载更多
                llLoadMore.setVisibility(View.GONE);
                tvLoadError.setVisibility(View.GONE);
                break;
            case STATE_MORE_ERROR:
                // 显示加载失败的布局
                llLoadMore.setVisibility(View.GONE);
                tvLoadError.setVisibility(View.VISIBLE);
                break;
        }
    }


}
