package com.example.jammy.pdf_demo.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jammy.pdf_demo.R;
import com.example.jammy.pdf_demo.SignatureView;
import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.user.SocketMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserSignActivity extends Activity {
    private SignatureView mSignaturePad;
    @Bind(R.id.iv_clear)
    ImageView clearIv;
    @Bind(R.id.iv_commit)
    ImageView finishIv;
    @Bind(R.id.signIv)
    ImageView signimg;

    private SocketMessage socketMessage = null;
    private ProgressDialog progressDialog = null;

    private String fileName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_user_sign);

        ButterKnife.bind(this);
        findViews();
        init();
    }

    protected void findViews() {
        mSignaturePad = (SignatureView) findViewById(R.id.signature_pad);
        socketMessage = (SocketMessage) getIntent().getSerializableExtra("sMessage");
    }

    protected void init() {
        clearIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
                signimg.setImageBitmap(null);
                mSignaturePad.setVisibility(View.VISIBLE);
            }
        });

        finishIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                if (signatureBitmap != null){
                    saveMyBitmap(UserSignActivity.this,signatureBitmap);
                    mSignaturePad.clear();
                    mSignaturePad.setVisibility(View.GONE);
                }
            }
        });

        mSignaturePad.setOnSignedListener(new SignatureView.OnSignedListener() {
            @Override
            public void onSigned() {
                clearIv.setEnabled(true);
            }

            @Override
            public void onClear() {
                clearIv.setEnabled(true);
            }
        });
    }

    //保存文件到指定路径
    public void saveMyBitmap(Context context,Bitmap bitmap) {
        String sdCardDir= Environment.getExternalStorageDirectory()+"/DCIM/";
        File appDir =new File(sdCardDir, "SignImage");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir,fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            signimg.setImageBitmap(bitmap);
            sendDataToServer(file);
            fos.flush();
            fos.close();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDataToServer(File file){
        progressDialog = ProgressDialog.show(UserSignActivity.this, null, "正在提交...");
        String url = Model.SIGNSAVEURL;
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("image/png");

        RequestBody fileBody = RequestBody.create(mediaType,file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("userId", socketMessage.getUserId())
                .addFormDataPart("file", file.getName(),fileBody).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = handler.obtainMessage();
                msg.what = 404;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200){
                    Message msg = handler.obtainMessage();
                    msg.what = 200;
                    handler.sendMessage(msg);
                }else {
                    Message msg = handler.obtainMessage();
                    msg.what = 118;
                    handler.sendMessage(msg);
                }
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 200:
                    progressDialog.dismiss();
                    /*File file = new File(fileName);
                    Log.e("TAG", "handleMessage: 删除=================成功！"+file.getName());
                    if (file.exists()){
                        file.delete();
                        Log.e("TAG", "handleMessage: 删除-------------------------成功！" );
                    }*/
                    showToast("保存成功!");
                    Intent intent = new Intent(UserSignActivity.this,SocketActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case 118:
                    progressDialog.dismiss();
                    showToast("服务器异常，保存失败，请稍后再试!");
                    break;
                case 404:
                    progressDialog.dismiss();
                    showToast("服务器连接失败!");
                    break;
            }
        }
    };

    private void showToast(String str){
        Toast.makeText(UserSignActivity.this, str, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
