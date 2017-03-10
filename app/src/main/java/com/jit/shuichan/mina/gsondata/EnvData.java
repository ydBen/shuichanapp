package com.jit.shuichan.mina.gsondata;

/**
 * Created by yuanyuan on 2016/12/26.
 */

public class EnvData {
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ContentData getContent() {
        return content;
    }

    public void setContent(ContentData content) {
        this.content = content;
    }

    public EnvData(String clientType, String clientName, String contentType, ContentData content) {
        this.clientType = clientType;
        this.clientName =clientName;
        this.contentType = contentType;
        this.content = content;
    }

    private String clientType;
    private String clientName;
    private String contentType;
    private ContentData content;





}
