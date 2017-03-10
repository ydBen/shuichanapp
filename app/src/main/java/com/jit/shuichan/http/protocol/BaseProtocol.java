package com.jit.shuichan.http.protocol;

import android.util.Log;

import com.jit.shuichan.EzvizApplication;
import com.jit.shuichan.http.HttpHelper;
import com.jit.shuichan.http.IOUtils;
import com.jit.shuichan.http.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by yuanyuan on 2017/1/9.
 */

public abstract class BaseProtocol <T>{
    public T getData(){
        //判断是否有缓存 有的话加载缓存
        String result = getCache();
        if (StringUtils.isEmpty(result)){//如果没有缓存
            //请求服务器
            result = getDataFromServer();
        }

        //开始解析
        if (result != null){
            T data = ParseData(result);
            return data;
        }
        return null;
    }

    //从网络获取数据
    private String getDataFromServer() {
        HttpHelper.HttpResult httpResult = HttpHelper.get(HttpHelper.URL + getKey() + getParams());

        if (httpResult != null){
            String result = httpResult.getString();
            Log.e("Result","访问结果:" + result);


            //写缓存
            if (!StringUtils.isEmpty(result)){
                setCache(result);
            }



            return result;
        }

        return null;
    }


    //网络请求关键字 必须实现
    public abstract String getKey();

    //获取网络连接的参数
    public abstract String getParams();

    //解析数据 子类必须实现
    public abstract T ParseData(String result);

    //写缓存 url为key json为value
    public void setCache(String json){
        //url为文件名，以json为文件内容，保存在本地
        File cacheDir = EzvizApplication.getContext().getCacheDir(); //本地应用的缓存文件夹
        //生成缓存文件
        File cacheFile = new File(cacheDir, getKey() + getParams());

        FileWriter writer = null;

        try {
            writer = new FileWriter(cacheFile);
            writer.write(json);   //写入json
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(writer);
        }

    }

    //读缓存
    public String getCache(){
        File cacheDir = EzvizApplication.getContext().getCacheDir(); //本地应用的缓存文件夹

        //生成缓存文件
        File cacheFile = new File(cacheDir, getKey() + getParams());

        BufferedReader reader = null;

        //判断缓存是否存在
        if (cacheFile.exists()){
        try {
            reader = new BufferedReader(new FileReader(cacheFile));

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null){
                stringBuffer.append(line);
            }

            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(reader);
        }


        }

        return null;
    }




}
