package com.example.jammy.pdf_demo.config;

/**
 * Created by bangware on 2018/3/26.
 */

public class Model {
    // 网络交互地址前段
    public static String FILEURL = "http://water.hunandingyi.com:9000/resource/";//"http://192.168.1.163:8080/resource/";

    //定义服务器的ip和端口号
    public static String HOST = "47.92.245.167:9000/webSocket/A8ED2ED1E0374BFB94C37E99B7CC3551";//"192.168.1.163:8080/webSocket/A8ED2ED1E0374BFB94C37E99B7CC3551";

    //定义设备类型、设备序列号接口
    public static String DEVICEURL = "http://192.168.1.163:8080/device/updateIdentifier";

    //签名保存接口
    public static String SIGNSAVEURL = "http://192.168.1.163:8080/signature/save";
}
