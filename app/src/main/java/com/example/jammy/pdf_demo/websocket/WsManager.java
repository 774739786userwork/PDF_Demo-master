package com.example.jammy.pdf_demo.websocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.user.SocketMessage;
import com.example.jammy.pdf_demo.user.User;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.orhanobut.logger.Logger;
import java.io.IOException;
import static android.content.Context.MODE_PRIVATE;

/**
 *webSocket 连接管理类
 * Created by bangware on 2018/4/27.
 */

public class WsManager {
    private static WsManager mInstance;
    private final String TAG = this.getClass().getSimpleName();
    private WsStatus mStatus;
    private WebSocket ws;
    private WsListener mListener;
    private String url = "";
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private String serialNumber = "";
    private SharedPreferences sharedPreferences;

    private static final long HEARTBEAT_INTERVAL = 10000;//心跳间隔

    private WsManager(){

    }

    public static WsManager getInstance() {
        if (mInstance == null) {
            synchronized (WsManager.class) {
                if (mInstance == null) {
                    mInstance = new WsManager();
                }
            }
        }
        return mInstance;
    }

    public void init() {
        sharedPreferences = WsApplication.getContext().getSharedPreferences("serialNumber",Context.MODE_PRIVATE);
        serialNumber = sharedPreferences.getString("deviceNo", "");
        try {
            /**
             * 初始化连接
             */
            ws = new WebSocketFactory().createSocket("ws://"+Model.HOST + serialNumber, CONNECT_TIMEOUT)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(mListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
            setStatus(WsStatus.CONNECTING);
            Logger.t(TAG).d("第一次连接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStatus(WsStatus status) {
        this.mStatus = status;
    }

    public WsStatus getStatus() {
        return mStatus;
    }

    public void disconnect() {
        if (ws != null) {
            Logger.t("TAG").d("断开连接......");
            ws.disconnect();
        }
    }

    private Handler mHandler = new Handler();

    private int reconnectCount = 0;//重连次数
    private long minInterval = 3000;//重连最小时间间隔
    private long maxInterval = 60000;//重连最大时间间隔

    /**检测重连状态*/
    public void reconnect() {
        if (!isNetConnect()) {
            reconnectCount = 0;
            Logger.t(TAG).d("重连失败网络不可用");
            return;
        }

        if (ws != null &&
                !ws.isOpen() &&//当前连接断开了
                getStatus() != WsStatus.CONNECTING) {//不是正在重连状态

            reconnectCount++;
            setStatus(WsStatus.CONNECTING);
            cancelHeartbeat();
            //当重连次数小于等于3次的时候都以最小重连时间间隔min去尝试重连
            //当重连次数大于3次的时候将重连时间间隔按min*(重连次数-2)递增最大不不超过max.
            long reconnectTime = minInterval;
            if (reconnectCount > 3) {
                long temp = minInterval * (reconnectCount - 2);
                reconnectTime = temp > maxInterval ? maxInterval : temp;
            }

            Logger.t(TAG).d("准备开始第%d次重连,重连间隔%d -- url:%s", reconnectCount, reconnectTime, url);
            mHandler.postDelayed(mReconnectTask, reconnectTime);
        }
    }

    /**重连调用*/
    private Runnable mReconnectTask = new Runnable() {

        @Override
        public void run() {
            try {
                ws = new WebSocketFactory().createSocket("ws://"+Model.HOST + serialNumber, CONNECT_TIMEOUT)
                        .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                        .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                        .addListener(mListener = new WsListener())//添加回调监听
                        .connectAsynchronously();//异步连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    /**取消重连*/
    public void cancelReconnect() {
        reconnectCount = 0;
        mHandler.removeCallbacks(mReconnectTask);
    }

    /**心跳机制线程控制*/
    private long sendTime = 0;
    public Runnable heartbeatTask = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime >= HEARTBEAT_INTERVAL) {
                SocketMessage socketMessage = new SocketMessage();
                if (!sendText(socketMessage)){
                    cancelHeartbeat();//取消心跳
                    disconnect();
                    reconnect();
                }else {
                    //处于连接状态
                    Logger.t("TAG").d("连接中");
                }
                sendTime = System.currentTimeMillis();//每次发送完数据，就改一下最后成功发送的时间，节省心跳间隔时间
            }
                Logger.t("TAG").d("心跳开始。。。。。。");
            mHandler.postDelayed(this, HEARTBEAT_INTERVAL);
        }
    };
    //开启心跳机制
    public void startHeartbeat() {
        mHandler.postDelayed(heartbeatTask, HEARTBEAT_INTERVAL);
    }

    //取消心跳机制
    public void cancelHeartbeat() {
        sendTime = 0;
        mHandler.removeCallbacks(heartbeatTask);
    }

    //发送消息
    public boolean sendText(SocketMessage socketMessage) {
        if(null != ws && ws.isOpen()) {
            Logger.t("TAG").d("wsclient sendText ="+socketMessage.getFeature());
            ws.sendText(socketMessage.getFeature());
        }
        return true;
    }


    private boolean isNetConnect() {
        ConnectivityManager connectivity = (ConnectivityManager) WsApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }
}
