package com.example.jammy.pdf_demo.util;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.jammy.pdf_demo.MainActivity;
import com.example.jammy.pdf_demo.PDFActivity;
import com.example.jammy.pdf_demo.R;


public class SplashActivity extends Activity {
    private long mExitTime=System.currentTimeMillis();
    NetWork network = NetWork.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_splash);
        Message msg = hand.obtainMessage();
        hand.sendMessage(msg);
    }

    Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isFristRun()) {
                new Thread() {//等待1.5秒
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                Intent intent = new Intent(SplashActivity.this,PDFActivity.class);
                startActivity(intent);
                finish();
            } else {
                if (network.IsConnect(SplashActivity.this)){
                    Intent intent = new Intent(SplashActivity.this,PDFActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(SplashActivity.this, "请检查网络连接！", Toast.LENGTH_SHORT).show();
                }
            }
        };
    };

    private boolean isFristRun() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(
                "share", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (!isFirstRun) {
            return false;
        } else {
            editor.putBoolean("isFirstRun", false);
            editor.commit();
            return true;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {// 如果两次按键时间间隔大于2000毫秒，则不退出
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
