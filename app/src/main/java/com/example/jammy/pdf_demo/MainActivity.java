package com.example.jammy.pdf_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jammy.pdf_demo.util.MyActivityManager;
/*
 *下载pdf
 */


public class MainActivity extends Activity {
    private OkHttpClient okHttpClient;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;

    String id = "";
    String url = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //下载进度
                    int i = msg.arg1;
                    progressBar.setProgress(i);
                    textView.setText("加载进度"+i+"%");
                    break;
                case 118:
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"服务器连接超时！", Toast.LENGTH_SHORT).show();
                    break;
                case 404:
                    Toast.makeText(MainActivity.this,"文件下载失败，请重新发送下载！", Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        id = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");

        requestPermission();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void requestPermission(){
        //判断当前Activity是否已经获得了该权限
        if(Build.VERSION.SDK_INT >= 23){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this,"请开启访问文件夹权限", Toast.LENGTH_SHORT).show();
                } else {
                    //进行权限请求
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            EXTERNAL_STORAGE_REQ_CODE);
                }
            }else{
                downloadPdfFile();
            }
        }else{
            downloadPdfFile();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功，进行相应操作
                    downloadPdfFile();
                } else {
                    //申请失败，可以继续向用户解释。
                    requestPermission();
                }
                return;
            }
        }
    }
    private void downloadPdfFile(){
        okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(null != call){
                    Log.e("h_bl", "onFailure"+e.getMessage());
                    Message msg = handler.obtainMessage();
                    msg.what = 118;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(SDPath, url.substring(url.lastIndexOf("/") + 1));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.e("h_bl", "progress=" + progress);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.arg1 = progress;
                        handler.sendMessage(msg);
                    }
                    fos.flush();
                    fos.close();
                    Log.e("h_bl", "文件下载成功");
                    Intent intent = new Intent(MainActivity.this,PDFActivity.class);
                    intent.putExtra("id",id);
                    intent.putExtra("url",url);
                    startActivity(intent);
                    MyActivityManager.getInstance().popActivity(MainActivity.this);
                } catch (Exception e) {
                    Log.e("h_b2", "文件下载失败"+"   ----"+e.getMessage());
                    Message msg = handler.obtainMessage();
                    msg.what = 404;
                    handler.sendMessage(msg);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }
}
