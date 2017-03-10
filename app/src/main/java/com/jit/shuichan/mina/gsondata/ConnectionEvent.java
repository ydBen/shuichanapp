package com.jit.shuichan.mina.gsondata;

/**
 * Created by yuanyuan on 2016/12/29.
 */

public class ConnectionEvent {
    public Boolean isConneted;

    public ConnectionEvent(Boolean isConneted) {
        this.isConneted = isConneted;
    }

    public Boolean getConneted() {
        return isConneted;
    }

    public void setConneted(Boolean conneted) {
        isConneted = conneted;
    }
}
