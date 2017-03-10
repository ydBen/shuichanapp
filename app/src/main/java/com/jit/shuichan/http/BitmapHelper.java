package com.jit.shuichan.http;

import com.jit.shuichan.EzvizApplication;
import com.lidroid.xutils.BitmapUtils;

/**
 * Created by yuanyuan on 2017/1/10.
 */

public class BitmapHelper {
    private static BitmapUtils mBitmapUtils = null;

    //单例  懒汉模式 懒加载
    public  static BitmapUtils getBitmapUtils(){
        if (mBitmapUtils == null){
            synchronized (BitmapHelper.class){
                if (mBitmapUtils == null){
                    mBitmapUtils = new BitmapUtils(EzvizApplication.getContext());
                }
            }
        }

        return mBitmapUtils;
    }
}
