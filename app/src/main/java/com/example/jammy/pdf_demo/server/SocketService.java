package com.example.jammy.pdf_demo.server;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import com.example.jammy.pdf_demo.websocket.WsManager;
import com.orhanobut.logger.Logger;

/*
*与服务器保持长连接的service
 */
public class SocketService extends Service {
    private final String TAG = SocketService.class.getSimpleName();
    //Service实例，用于在Activity中进行连接断开发消息等图形界面化的操作
    private LocalBinder binder = new LocalBinder();
    //监控服务被杀死重启的广播，保持服务不被杀死
    private BroadcastReceiver restartBR;
    PowerManager.WakeLock wakeLock = null;
    public SocketService() {
        super();
    }

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Logger.t(TAG).d("onCreate()");
        acquireWakeLock();
        WsManager.getInstance().init();
        //收到Service被杀死的广播，立即重启
        restartBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "服务器重启了......");
                String action = intent.getAction();
                if (!TextUtils.isEmpty(action) && action.equals("socketService_killed")){
                    Intent sIntent = new Intent(SocketService.this, SocketService.class);
                    startService(sIntent);
                }
            }
        };
        registerReceiver(restartBR, new IntentFilter("socketService_killed"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand(Intent intent, int flags, int startId)");
        return START_STICKY;//设置START_STICKY为了使服务被意外杀死后可以重启
    }

    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != wakeLock)
        {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * 创建Binder对象，返回给客户端即Activity使用，提供数据交换的接口
     */
    public class LocalBinder extends Binder {
        // 声明一个方法，getService。（提供给客户端调用）
        public SocketService getService() {
            // 返回当前对象LocalService,这样我们就可在客户端端调用Service的公共方法了
            return SocketService.this;
        }
    }

    /**
     * 销毁Service
     * 注销广播
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy()");
        sendBroadcast(new Intent("socketService_killed"));
        unregisterReceiver(restartBR);
    }


    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind(Intent intent)");
        releaseWakeLock();
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG, "onBind(Intent intent)");
        super.onRebind(intent);
    }

    /**
     * 绑定服务时才会调用
     * 必须要实现的方法
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onRebind(Intent intent)");
        return binder;
    }
}
