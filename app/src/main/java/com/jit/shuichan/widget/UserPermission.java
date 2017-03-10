package com.jit.shuichan.widget;

import android.content.Context;

/**
 * Created by yuanyuan on 2017/2/20.
 */

public class UserPermission {
    public String getmUserName() {
        return mUserName;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    private String mUserName;
    private static UserPermission sUserPermission;
    private Context mAppContext;


    public static synchronized UserPermission getsUserPermission(){
        if (sUserPermission == null)
            sUserPermission = new UserPermission();
            sUserPermission.mUserName = sUserPermission.getmUserName();
            return sUserPermission;

    }


}
