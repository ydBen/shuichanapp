package com.jit.shuichan.ui.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuanyuan on 2017/2/14.
 */
public class StreamUtil {
    /*
    * 流转字符串
    * inputStream 流对象
    * */
    public static String streamToString(InputStream inputStream) {
        //1,读取内容存储到缓存中，一次性转串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //2,读流操作，读到没有为止
        byte[] buffer = new byte[1024];
        //3，记录读取内容的临时变量
        int temp = -1;
        try {
            while ((temp = inputStream.read(buffer))!= -1){
                bos.write(buffer,0,temp);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
