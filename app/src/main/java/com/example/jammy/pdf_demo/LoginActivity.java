package com.example.jammy.pdf_demo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jammy.pdf_demo.user.User;
import com.example.jammy.pdf_demo.user.UserLogin;
import com.example.jammy.pdf_demo.util.MyActivityManager;
import com.example.jammy.pdf_demo.util.NetWork;

public class LoginActivity extends Activity {
    private long mExitTime=System.currentTimeMillis();

    private EditText mLogin_user, mLogin_password;
    private TextView mLogin_OK;
    private CheckBox mIsRememberMe;
    private Activity currentActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_login);

        this.currentActivity = this;
        init();
    }

    public void init(){
        mLogin_user = (EditText) findViewById(R.id.Login_user);
        mLogin_password = (EditText) findViewById(R.id.Login_password);
        mIsRememberMe = (CheckBox)findViewById(R.id.Login_rememberme);
        mLogin_OK = (TextView) findViewById(R.id.Login_OK);

        MyOnclickListener listener = new MyOnclickListener();
        mLogin_OK.setOnClickListener(listener);

        SharedPreferences sharedPreferences = this.getSharedPreferences(User.SHARED_NAME, Context.MODE_PRIVATE);
        mLogin_user.setText(sharedPreferences.getString("username",""));
    }

    private class MyOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id){
                case R.id.Login_OK:
                    String username = mLogin_user.getText().toString().trim();
                    String password = mLogin_password.getText().toString().trim();
                    Boolean isRememberMe = mIsRememberMe.isChecked();
                    if (username == null || "".equals(username)){
                        Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(password==null || "".equals(password)){
                        Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                        return;
                    }
                    NetWork network = NetWork.getInstance();
                    if(network.IsConnect(LoginActivity.this)){
                        UserLogin userLogin = new UserLogin(LoginActivity.this, currentActivity);
                        userLogin.doLogin(username, password, isRememberMe);
                    }else {
                        Toast.makeText(LoginActivity.this, "请检查你的网络再进行登录！", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
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
