package com.example.jammy.pdf_demo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jammy.pdf_demo.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SetActivity extends Activity {
    @Bind(R.id.ipedit)
    EditText edit;
    @Bind(R.id.savebtn)
    Button save_btn;

    String localhost = "192.168.1.111:8080";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        edit.setText(localhost);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("IP", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
                editor.putString("ip", edit.getText().toString());
                editor.putBoolean("isFirstRun", false);
                editor.commit();

                Intent intent = new Intent(SetActivity.this,SocketActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
