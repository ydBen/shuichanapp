package com.jit.shuichan.mina.gsondata;

/**
 * Created by yuanyuan on 2017/1/3.
 */

public class FactoryIdEvent {
    public int getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public FactoryIdEvent(int factoryId) {
        this.factoryId = factoryId;
    }

    public int factoryId;
}
