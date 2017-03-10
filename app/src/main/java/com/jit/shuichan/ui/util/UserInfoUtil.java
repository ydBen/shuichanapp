package com.jit.shuichan.ui.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanyuan on 2017/2/17.
 */
public class UserInfoUtil {

    // 保存用户名密码
    public static boolean saveUserInfo(Context applicationContext,String username,String password){
        try {
            //1，通过Context对象创建一个SharedPreferences对象
            SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("userinfo.txt", Context.MODE_PRIVATE);

            //2，通过SharedPreferences对象创建一个Editor对象
            SharedPreferences.Editor editor = sharedPreferences.edit();

            //3,往Editor中添加数据
            editor.putString("username",username);
            editor.putString("password",password);

            //4,提交Editor对象
            editor.commit();

            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }


    //获取用户名密码
    public static Map<String,String> getUserInfo(Context applicationContext) {
        try {
            //1，通过Context对象创建一个SharedPreferences对象
            SharedPreferences sharedPreferences = applicationContext.getSharedPreferences("userinfo.txt", Context.MODE_PRIVATE);

            //2，通过SharedPreferences获取存放数据
            //key:存放数据时的key defValue：默认值，根据业务需求来写
            String username = sharedPreferences.getString("username", "");
            String password = sharedPreferences.getString("password", "");

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("username",username);
            hashMap.put("password",password);

            return hashMap;

        }catch (Exception e){
            e.printStackTrace();

        }
        return null;

    }
}
