package com.jit.shuichan.mina.gsondata;

/**
 * Created by yuanyuan on 2016/12/29.
 */

public class MessageEvent {

    public MessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String message;
}
