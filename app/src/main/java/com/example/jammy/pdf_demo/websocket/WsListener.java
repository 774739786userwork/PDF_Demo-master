package com.example.jammy.pdf_demo.websocket;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.example.jammy.pdf_demo.MainActivity;
import com.example.jammy.pdf_demo.R;
import com.example.jammy.pdf_demo.server.JsonUtil;
import com.example.jammy.pdf_demo.user.SocketMessage;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * webSocket 监听消息，连接状态
 * Created by bangware on 2018/4/27.
 */

public class WsListener extends WebSocketAdapter {
    private final String TAG = this.getClass().getSimpleName();
    //通知的ID，为了分开显示，需要根据Id区分
    private int nId = 0;
    public WsListener() {
    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {
        super.onTextMessage(websocket, text);
        if (TextUtils.isEmpty(text)){
            Logger.t(TAG).d(text);
            return;
        }else {
            SocketMessage sMessage = JsonUtil.parseJson(text);
            sendNotification(sMessage);
            Intent intent = new Intent(WsApplication.getContext(), MainActivity.class);
            intent.putExtra("id",sMessage.getId());
            intent.putExtra("url",sMessage.getFileUrl());
            Logger.t(TAG).d(text);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            WsApplication.getContext().startActivity(intent);
        }
    }


    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
            throws Exception {
        super.onConnected(websocket, headers);
        Logger.t(TAG).d("连接成功");
        WsManager.getInstance().setStatus(WsStatus.CONNECT_SUCCESS);
        WsManager.getInstance().cancelReconnect();//连接成功的时候取消重连,初始化连接次数
        WsManager.getInstance().startHeartbeat();//开始发送心跳包
    }


    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception)
            throws Exception {
        super.onConnectError(websocket, exception);
        Logger.t(TAG).d("连接错误");
        WsManager.getInstance().setStatus(WsStatus.CONNECT_FAIL);
        WsManager.getInstance().reconnect();//连接错误的时候调用重连方法
    }


    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
            throws Exception {
        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
        Logger.t(TAG).d("断开连接");
        WsManager.getInstance().setStatus(WsStatus.CONNECT_FAIL);
        WsManager.getInstance().reconnect();//连接断开的时候调用重连方法
    }

    /**
     * 收到时间消息，发送通知提醒
     *
     * @param sMessage
     */
    private void sendNotification(SocketMessage sMessage) {
        //为了版本兼容，使用v7包的ＢＵＩＬＤＥＲ
        NotificationCompat.Builder builder = new NotificationCompat.Builder(WsApplication.getContext());
        //状态栏显示的提示，有的手机不显示
        builder.setTicker("温馨提示！");
        //通知栏标题
        builder.setContentTitle("获取的是：" + sMessage.getEmployeename()+"的日结单");
        /*//通知栏内容
        builder.setContentText(sMessage.getMessage());
        //通知内容摘要
        builder.setSubText(sMessage.getUserId());*/
        //在通知右侧的时间下面用来展示一些其他信息
//        builder.setContentInfo("其他");
        //用来显示同种通知的数量，如果设置了ContentInfo属性，则NUmber属性会被覆盖，因为二者显示的位置相同
//        builder.setNumber(3);
        //可以点击通知栏的删除按钮
        builder.setAutoCancel(true);
        //系统状态栏显示的小图标
        builder.setSmallIcon(R.drawable.jpush_notification_icon);
        //通知下拉显示的大图标
        builder.setLargeIcon(BitmapFactory.decodeResource(WsApplication.getContext().getResources(), R.mipmap.ic_launcher));
        //点击通知跳转的INTENT
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
//        builder.setContentIntent(pendingIntent);//点击跳转
        //通知默认的声音，震动，呼吸灯
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager)WsApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        manager.notify(nId, notification);
//        nId++;
    }
}
