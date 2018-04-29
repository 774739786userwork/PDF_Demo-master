package com.example.jammy.pdf_demo.user;
import java.io.Serializable;

/**
 * Created by bangware
 * 模拟数据模型，用于客户端与服务端的传输
 */
public class SocketMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    public String id;
    public String fileUrl;
    public String employeename;

    public SocketMessage() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getEmployeename() {
        return employeename;
    }

    public void setEmployeename(String employeename) {
        this.employeename = employeename;
    }

}
