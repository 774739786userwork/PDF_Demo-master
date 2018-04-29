package com.example.jammy.pdf_demo.config;

/**
 * Created by bangware on 2018/3/26.
 */

public class Model {
    // 网络交互地址前段
    public static String HTTPURL = "http://112.74.47.41:10009/csbboss/";

    public static String LOGINACTION = "appLand/appUserland.page";

    //定义服务器的ip和端口号
    public static final String HOST = "112.74.47.41:10009/csbboss/webSocket/";

    //消息类型
    public static final int MESSAGE_ACTIVE = 0;//心跳包
    public static final int MESSAGE_EVENT = 1;//事件包
    public static final int MESSAGE_CLOSE = 3;//断开连接
    //定义客户端和服务器端的称呼
    public static final String NAME_SERVER = "服务器";
    public static final String NAME_CLIENT = "客户端";

    public static final int SOCKET_CONNECT_TIMEOUT = 15;//设置Socket连接超时为15秒
    public static final int SOCKET_ACTIVE_TIME = 10;//发送心跳包的时间间隔为5秒

    //收到了SocketMessage消息
    public static final String ACTION_SOCKET_MESSSAGE = "com.example.socketmessage";
    //Socket当前的状态
    public static final String ACTION_SOCKET_STATUS = "action_socket_status";
}
