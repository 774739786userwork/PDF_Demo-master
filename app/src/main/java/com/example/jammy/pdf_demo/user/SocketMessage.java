package com.example.jammy.pdf_demo.user;
import java.io.Serializable;

/**
 * Created by bangware
 * 模拟数据模型，用于客户端与服务端的传输
 */
public class SocketMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    public String userId;
    public String id;
    public String  feature;
    public String fid;
    public SocketMessage() {

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }
}
