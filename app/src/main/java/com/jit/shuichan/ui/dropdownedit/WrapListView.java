package com.jit.shuichan.ui.dropdownedit;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by yuanyuan on 2017/3/7.
 */

public class WrapListView extends ListView {

    private int mWitth = 0;

    public WrapListView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public WrapListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();
        for (int i=0;i<getChildCount();i++){
            int measuredWidth = getChildAt(i).getMeasuredWidth();
            mWitth = Math.max(mWitth,measuredWidth);
        }
        setMeasuredDimension(mWitth,height);
    }

    protected void setListWith(int with){
        mWitth = with;
    }
}
