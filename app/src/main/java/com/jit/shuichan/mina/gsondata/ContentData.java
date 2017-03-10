package com.jit.shuichan.mina.gsondata;

import java.util.ArrayList;

/**
 * Created by yuanyuan on 2016/12/27.
 */

public class ContentData {
    public ContentData(String to, int companyId, int factoryId,ArrayList<SensorInfo> infos,String order) {
        this.to = to;
        this.companyId =companyId;
        this.factoryId = factoryId;
        this.infos = infos;
        this.order = order;
    }

    private int companyId;
    private int factoryId;


    private String to;

    public ArrayList<SensorInfo> getInfos() {
        return infos;
    }

    public void setInfos(ArrayList<SensorInfo> infos) {
        this.infos = infos;
    }

    private ArrayList<SensorInfo> infos;
    private String order;


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getFactoryId() {
        return factoryId;
    }

    public void setFactoryId(int factoryId) {
        this.factoryId = factoryId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}

