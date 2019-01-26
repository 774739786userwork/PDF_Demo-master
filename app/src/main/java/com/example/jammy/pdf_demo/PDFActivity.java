package com.example.jammy.pdf_demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;

import android.content.Intent;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdf.BitmapHolder;
import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;
import com.artifex.mupdf.ReaderView;
import com.artifex.mupdf.SavePdf;
import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.util.MyActivityManager;
import com.example.jammy.pdf_demo.util.SocketActivity;
import java.io.File;
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

/**
 *pdf显示预览
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

    @Bind(R.id.rl_submit)
    RelativeLayout rlSubmit;

    @Bind(R.id.sign_text)
    TextView textSign;

    @Bind(R.id.btn_sign)
    ImageView imgBtnSign;

    private boolean isUpdate = false;
    private String in_path;
    private String out_path;
    private String update_path;
    private String new_path;
    private PopupWindow popupWindow;
    private SignatureView signatureView;
    private float density; //屏幕分辨率密度
    private int first = 1;
    private ProgressDialog progressDialog;
    private  MuPDFCore muPDFCore;
    private Save_Pdf save_pdf;
    private String id = "";
    private String fid = "";
    private String feature = "";

    /**
     * 判断是否为预览pdf模式
     */
    private static boolean isPreviewPDF = false;
    private Bitmap bitmap = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        init();
        initClick();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        fid = getIntent().getStringExtra("fid");
        feature = getIntent().getStringExtra("feature");
        in_path = Environment.getExternalStorageDirectory().getPath() + "/"+id+".pdf";//"/20170414.pdf";
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

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
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
                    textSign.setText("点击阅读");
                } else {
                    popupWindow.dismiss();
                    signatureView.clear();
                    rlSave.setVisibility(View.GONE);
                    rlClear.setVisibility(View.GONE);
                    rlSubmit.setVisibility(View.GONE);
                    textSign.setText("点击签名");

                }
            }
        });
        rlSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!signatureView.getHasDraw()){
                    showToast("请先签名!");
                }else {
                    float scale = readerView.getmScale();///得到放大因子
                    SavePdf savePdf = new SavePdf(in_path, out_path);
                    savePdf.setScale(scale);
                    savePdf.setPageNum(readerView.getDisplayedViewIndex() + 1);
                    savePdf.setWidthScale(1.0f * readerView.scrollX / readerView.getDisplayedView().getWidth());//计算宽偏移的百分比
                    savePdf.setHeightScale(1.0f * readerView.scrollY / readerView.getDisplayedView().getHeight());//计算长偏移的百分比
                    savePdf.setDensity(density);
                    bitmap = Bitmap.createBitmap(signatureView.getWidth(), signatureView.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    signatureView.draw(canvas);
                    savePdf.setBitmap(bitmap);
                    save_pdf = new Save_Pdf(savePdf)                                  ;
                    save_pdf.execute();
                    popupWindow.dismiss();
                    isPreviewPDF = true;
                    rlSave.setVisibility(View.GONE);
                    rlSign.setVisibility(View.GONE);
                    rlClear.setVisibility(View.VISIBLE);
                    rlSubmit.setVisibility(View.VISIBLE);
                    ///显示隐藏probar
                    progressDialog = ProgressDialog.show(PDFActivity.this, null, "正在存储...");
                    signatureView.clear();
                }
            }
        });

        rlClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPreviewPDF){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PDFActivity.this);
                    builder.setTitle("提醒：已签名文件文件将无法恢复，是否继续？")
                    .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // TODO Auto-generated method stub
                            rlSubmit.setVisibility(View.GONE);
                            rlSave.setVisibility(View.GONE);
                            rlClear.setVisibility(View.GONE);
                            rlSign.setVisibility(View.VISIBLE);
                            textSign.setText("点击签名");
                            File file = new File(out_path);
                            if (file.exists()){
                                file.delete();
                            }
                            isPreviewPDF = false;//重新解析pdf，恢复初始值
                            ReaderView.NoTouch = true;//重新释放对pdf手势操作
                            init();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
                }else{
                    if (signatureView.getHasDraw()){
                        signatureView.clear();
                    }else {
                        showToast("请先签名！");
                    }
                }
            }
        });

        rlSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //提交数据到后台的接口
                String actionUrl = Model.FILEURL;
                final File file = new File(out_path);
                progressDialog = ProgressDialog.show(PDFActivity.this, null, "正在提交...");
                OkHttpClient okHttpClient = new OkHttpClient();
                MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
                RequestBody fileBody = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("id", id)
                        .addFormDataPart("fid",fid)
                        .addFormDataPart("feature",feature)
                        .addFormDataPart("file", file.getName(),fileBody).build();
                Request request = new Request.Builder().url(actionUrl).post(requestBody).build();
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
        });
    }

    private void showToast(String str){
        Toast.makeText(PDFActivity.this, str, Toast.LENGTH_LONG).show();
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
                in_path = in_path.substring(0, in_path.length() - 4) + "2.pdf";
                first++;
                Log.e("tag", "完成：：：：：："+update_path);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            try {
                muPDFCore = new MuPDFCore(out_path);
                readerView.setAdapter(new MuPDFPageAdapter(PDFActivity.this, muPDFCore));
                readerView.setmScale(1.0f);
                readerView.setDisplayedViewIndex(readerView.getDisplayedViewIndex());
                progressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    rlSave.setVisibility(View.GONE);
                    rlClear.setVisibility(View.GONE);
                    readerView.setVisibility(View.VISIBLE);
                    rlSign.setVisibility(View.GONE);
                    File file = new File(in_path);
                    if (file.exists()) file.delete();
                    File file2 = new File(update_path);
                    if (file2.exists()) file2.delete();
                    showToast("保存成功!");
                    progressDialog.dismiss();
                    Intent intent = new Intent(PDFActivity.this,SocketActivity.class);
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
            File file = new File(in_path);
            if (file.exists()) file.delete();
            File file1 = new File(out_path);
            if (file1.exists()) file1.delete();
            File file2 = new File(update_path);
            if (file2.exists() && isUpdate) file2.delete();
            save_pdf.cancel(true);
        }
        if (popupWindow != null){
            popupWindow.dismiss();
        }
        if (progressDialog != null){
            progressDialog.dismiss();
        }
        if (muPDFCore != null){
            muPDFCore.onDestroy();
            muPDFCore = null;
        }
        isPreviewPDF = false;//重新解析pdf，恢复初始值
        ReaderView.NoTouch = true;//重新释放对pdf手势操作
        BitmapHolder bitmapHolder = new BitmapHolder();
        bitmapHolder.recycleBitmap(bitmap);
    }
}
