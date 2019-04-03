package com.example.jammy.pdf_demo.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jammy.pdf_demo.R;
import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.server.SocketService;
import com.example.jammy.pdf_demo.websocket.WsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SocketActivity extends Activity{
    private long mExitTime=System.currentTimeMillis();
    @Bind(R.id.setting)
    TextView settext;
    private SocketService mService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        /**
         * 与服务器端交互的接口方法 绑定服务的时候被回调，在这个方法获取绑定Service传递过来的IBinder对象，
         * 通过这个IBinder对象，实现和Service的交互。
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SocketService.LocalBinder binder = (SocketService.LocalBinder) iBinder;
            mService = binder.getService();
        }

        /**
         * 当取消绑定的时候被回调。但正常情况下是不被调用的，它的调用时机是当Service服务被意外销毁时，
         * 例如内存的资源不足时这个方法才被自动调用。
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        initClick();
        /**
         * 绑定服务
         */
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        /*SharedPreferences sharedPreferences = getSharedPreferences("IP", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("ip", "");
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (!isFirstRun){
            Model.FILEURL = "http://"+name+"/resource/";
            Model.HOST = name+"/webSocket/A8ED2ED1E0374BFB94C37E99B7CC3551";
        }*/
    }

    private void initClick(){
        settext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SocketActivity.this,SetActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        //解绑服务
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {// 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                WsManager.getInstance().disconnect();
                MyActivityManager.getInstance().exitApp(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
