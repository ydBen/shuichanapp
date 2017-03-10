package com.jit.shuichan.mina.gsondata;

/**
 * Created by yuanyuan on 2016/12/30.
 */
public class SensorInfo {
    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public SensorInfo(int sensorId,String type,double value) {
        this.sensorId =sensorId;
        this.type = type;
        this.value = value;
    }

    private int sensorId;
    private String type;
    private double value;
}
