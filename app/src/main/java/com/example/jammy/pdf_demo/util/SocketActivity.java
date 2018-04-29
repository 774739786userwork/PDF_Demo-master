package com.example.jammy.pdf_demo.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.jammy.pdf_demo.LoginActivity;
import com.example.jammy.pdf_demo.R;
import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.server.SocketService;
import com.example.jammy.pdf_demo.user.User;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SocketActivity extends Activity{
    private long mExitTime=System.currentTimeMillis();
    @Bind(R.id.main_logout)
    RelativeLayout mRl_logout;
    private User user = null;

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
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(User.SHARED_NAME,MODE_PRIVATE);
        user = User.getInstance().readFromSharedPreferences(sharedPreferences);

        /**
         * 绑定服务
         */
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        initView();
    }


    private void initView() {
        mRl_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearUserCache();
                signOut();
                Intent intent = new Intent(SocketActivity.this,LoginActivity.class);
                startActivity(intent);
                MyActivityManager.getInstance().popAllActivity();
            }
        });
    }

    private void signOut(){
        String logoutUrl = Model.HTTPURL+"mobileServiceManager/user/signOut.page?userid="+user.getUser_id()+"&token="+user.getLogin_token();
        ThreadPoolUtils.execute(new HttpGetThread(handler,logoutUrl));
    }

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            if(msg.what == 404){
            }
            if(msg.what == 100){
            }
            if(msg.what == 200){
                String result = (String) msg.obj;
                if (result != null) {
                    Logger.t("TAG").d(result);
                }
            }
        };
    };
    /**
     * 清除本地用户信息缓存
     */
    private void clearUserCache(){
        SharedPreferences sharedPreferences = this.getSharedPreferences(User.SHARED_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString("username", user.getUser_name());
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        Logger.t("TAG").d("onDestorty===================");
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
                MyActivityManager.getInstance().exitApp(this);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
