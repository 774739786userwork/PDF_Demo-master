package com.example.jammy.pdf_demo;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;
import com.artifex.mupdf.ReaderView;
import com.artifex.mupdf.SavePdf;
import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.user.User;
import com.example.jammy.pdf_demo.util.HttpPostThread;
import com.example.jammy.pdf_demo.util.MyActivityManager;
import com.example.jammy.pdf_demo.util.SocketActivity;
import com.example.jammy.pdf_demo.util.ThreadPoolUtils;
import com.example.jammy.pdf_demo.websocket.WsManager;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;


import butterknife.Bind;
import butterknife.ButterKnife;



/**
 * Created by Jammy on 2016/6/23.
 */
public class PDFActivity extends Activity {
    @Bind(R.id.readerView)
    ReaderView readerView;

    @Bind(R.id.rel_sign)
    RelativeLayout rlSign;
    @Bind(R.id.rl_clear)
    RelativeLayout rlClear;
    @Bind(R.id.rl_save)
    RelativeLayout rlSave;

    boolean isUpdate = false;
    String in_path;
    String out_path;
    String update_path;
    String new_path;
    PopupWindow popupWindow;
    SignatureView signatureView;
    float density; //屏幕分辨率密度
    int first = 1;
    ProgressDialog progressDialog;
    MuPDFCore muPDFCore;
    Save_Pdf save_pdf;
    String id = "";
    String url = "";
    private User user = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_pdf);
        ButterKnife.bind(this);

        View screenView = this.getWindow().getDecorView();
        screenView.setDrawingCacheEnabled(true);
        screenView.buildDrawingCache();

        //计算分辨率密度
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        density = metric.density;
        /*int width = metric.widthPixels;
        int height = metric.heightPixels;
        Log.e("info", "屏幕宽度：："+width+"屏幕高度：：："+height+"屏幕分辨率：：：："+density);*/

        init();
        initClick();
    }

    private void init(){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(User.SHARED_NAME,MODE_PRIVATE);
        user = User.getInstance().readFromSharedPreferences(sharedPreferences);
        id = getIntent().getStringExtra("id");
        url = getIntent().getStringExtra("url");
        in_path = Environment.getExternalStorageDirectory().getPath() + "/" + url.substring(url.lastIndexOf("/") + 1);
        out_path = in_path.substring(0, in_path.length() - 4) + "1.pdf";
        try {
            muPDFCore = new MuPDFCore(in_path);//PDF的文件路径
        } catch (Exception e) {
            e.printStackTrace();
        }
        readerView.setAdapter(new MuPDFPageAdapter(this, muPDFCore));
        View view = LayoutInflater.from(this).inflate(R.layout.signature_layout, null);
        signatureView = (SignatureView) view.findViewById(R.id.qianming);
        readerView.setDisplayedViewIndex(0);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
    }

    private void initClick(){
        rlSign.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (rlSave.getVisibility() == View.GONE) {
                    popupWindow.showAsDropDown(rlSign, 0, 0);
                    rlSave.setVisibility(View.VISIBLE);
                    rlClear.setVisibility(View.VISIBLE);
                } else {
                    popupWindow.dismiss();
                    signatureView.clear();
                    rlSave.setVisibility(View.GONE);
                    rlClear.setVisibility(View.GONE);
                }
            }
        });
        rlSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float scale = readerView.getmScale();///得到放大因子
                SavePdf savePdf = new SavePdf(in_path, out_path);
                savePdf.setScale(scale);
                savePdf.setPageNum(readerView.getDisplayedViewIndex() + 1);

                savePdf.setWidthScale(1.0f * readerView.scrollX / readerView.getDisplayedView().getWidth());//计算宽偏移的百分比
                savePdf.setHeightScale(1.0f * readerView.scrollY / readerView.getDisplayedView().getHeight());//计算长偏移的百分比

                savePdf.setDensity(density);
                Bitmap bitmap = Bitmap.createBitmap(signatureView.getWidth(), signatureView.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                signatureView.draw(canvas);
                savePdf.setBitmap(bitmap);
                save_pdf = new Save_Pdf(savePdf);
                save_pdf.execute();
                popupWindow.dismiss();
                rlSave.setVisibility(View.GONE);
                rlClear.setVisibility(View.GONE);
                ///显示隐藏probar
                progressDialog = ProgressDialog.show(PDFActivity.this, null, "正在存储...");
                signatureView.clear();
            }
        });

        rlClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clear();
            }
        });
    }

    /*
    * 用于存储的异步,并上传更新
    * */
    class Save_Pdf extends AsyncTask {

        SavePdf savePdf;

        public Save_Pdf(SavePdf savePdf) {
            this.savePdf = savePdf;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            savePdf.addText();
            if (first == 1) {
                update_path = in_path.substring(0, in_path.length() - 4) + ".pdf";
                new_path = in_path.substring(0, in_path.length() - 4) + "2.pdf";
                first++;
                Log.e("tag", "完成：：：：：："+new_path);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //提交数据到后台的接口
            String actionUrl = Model.HTTPURL + "db/balanceAccouts/saveSignImage.page";
            try {
                muPDFCore = new MuPDFCore(out_path);
                readerView.setAdapter(new MuPDFPageAdapter(PDFActivity.this, muPDFCore));
//                String temp = new_path;
//                new_path = out_path;
//                out_path = temp;
                Log.e("tag", "存储完成--------"+out_path);
                readerView.setmScale(0.0f);
                readerView.setDisplayedViewIndex(readerView.getDisplayedViewIndex());
                progressDialog.dismiss();
                MultipartEntity mulentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,null, Charset.forName("UTF-8"));
                File file = new File(out_path);
                FileBody filebody = new FileBody(file);
                mulentity.addPart("files",filebody);
                mulentity.addPart("balance_id", new StringBody(id));
                ThreadPoolUtils.execute(new HttpPostThread(handler,actionUrl,"utf-8",mulentity));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what == 404){
                Toast.makeText(PDFActivity.this,"服务器地址错误！", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 100){
                Toast.makeText(PDFActivity.this,"网络传输失败！", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 200){
                String result = (String) msg.obj;
                if(result != null){
                    JSONObject response = null;
                    try {
                        response = new JSONObject(result);
                        int status = response.getInt("status");
                        if(response != null){
                            if(status == 0){
                                Log.e("tag", response.getString("msg"));
                                Toast.makeText(PDFActivity.this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                                File file = new File(in_path);
                                if (file.exists()) file.delete();
                                File file_save = new File(out_path);
                                if (file_save.exists()) file_save.delete();
                                rlSave.setVisibility(View.GONE);
                                rlClear.setVisibility(View.GONE);
                                readerView.setVisibility(View.GONE);
                                rlSign.setVisibility(View.GONE);
                                /*Intent intent = new Intent(PDFActivity.this,SocketActivity.class);
                                startActivity(intent);
                                finish();*/
                            }else{
                                Toast.makeText(PDFActivity.this,"服务器异常，保存失败，请稍后再试！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(PDFActivity.this,"服务器连接失败！", Toast.LENGTH_SHORT).show();
                }
            }
        };
    };

    /**
     * 返回按钮，退出时删除那两个文件
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否退出？");
        builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //删除缓冲的存储
                PDFActivity.this.finish();
                File file = new File(in_path);
                if (file.exists()) file.delete();
                WsManager.getInstance().disconnect();
    }}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
        }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (first != 1) {
            File file = new File(new_path);
            File file1 = new File(out_path);
            File file2 = new File(update_path);
            if (file.exists()) file.delete();
            if (file1.exists()) file1.delete();
            if (file2.exists() && isUpdate) file2.delete();
            save_pdf.cancel(true);
        }
        MyActivityManager.getInstance().exitApp(PDFActivity.this);
//        popupWindow.dismiss();
    }
}
