package com.example.jammy.pdf_demo.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jammy.pdf_demo.R;
import com.example.jammy.pdf_demo.config.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SetActivity extends Activity {
    @Bind(R.id.saveText)
    TextView save_text;

    @Bind(R.id.Linear)
    LinearLayout linearLayout;

    @Bind(R.id.compact_check)
    CheckBox compactCheck;

    @Bind(R.id.charge_check)
    CheckBox chargeCheck;

    @Bind(R.id.balance_check)
    CheckBox balanceCheck;

    @Bind(R.id.sign_check)
    CheckBox signCheck;

    @Bind(R.id.back)
    ImageView backImg;
    private ProgressDialog progressDialog;
    private Map<String, Object> listMap = new HashMap<String, Object>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyActivityManager.getInstance().pushActivity(this);
        setContentView(R.layout.activity_set);
        ButterKnife.bind(this);
        initListener();
        SharedPreferences sp = getSharedPreferences("MAP",Context.MODE_PRIVATE);
        String result = sp.getString("listData", "");
        try {
            if (result != null){
                JSONArray array = new JSONArray(result);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject itemObject = array.getJSONObject(i);
                    JSONArray names = itemObject.names();
                    if (names != null) {
                        for (int j = 0; j < names.length(); j++) {
                            String name = names.getString(j);
                            String value = itemObject.getString(name);
                            if (value.equals("CONTRACT_SIGNING")){
                                compactCheck.setChecked(true);
                            }
                            if (value.equals("CHARGE")){
                                chargeCheck.setChecked(true);
                            }
                            if (value.equals("SETTLEMENT")){
                                balanceCheck.setChecked(true);
                            }
                            if (value.equals("SIGNATURE")){
                                signCheck.setChecked(true);
                            }
                            listMap.put(name, value);
                        }
                    }
                }
            }
        } catch (JSONException e) {

        }
    }

    private void initListener(){
        compactCheck.setOnCheckedChangeListener(changeListener);
        chargeCheck.setOnCheckedChangeListener(changeListener);
        balanceCheck.setOnCheckedChangeListener(changeListener);
        signCheck.setOnCheckedChangeListener(changeListener);

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listMap.size() > 0){
                    getIMEI(getApplicationContext());

                    JSONArray mJsonArray = new JSONArray();
                    JSONObject object = new JSONObject();
                    Iterator<Map.Entry<String, Object>> iterator = listMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Object> entry = iterator.next();
                        try {
                            object.put(entry.getKey(), entry.getValue());
                        } catch (JSONException e) {

                        }
                    }
                    mJsonArray.put(object);
                    SharedPreferences sharedPreferences = getSharedPreferences("MAP",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("listData", mJsonArray.toString());
                    editor.commit();
                }else {
                    showToast("您还未选择签名类型！");
                }
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            int cNum = linearLayout.getChildCount();
            for (int i = 0; i < cNum; i++){
                CheckBox cb = (CheckBox) linearLayout.getChildAt(i);
                if (cb.isChecked()){
                    if (cb.getId() == R.id.compact_check){
                        listMap.put(String.valueOf(R.id.compact_check),"CONTRACT_SIGNING");
                    }
                    if (cb.getId() == R.id.charge_check){
                        listMap.put(String.valueOf(R.id.charge_check),"CHARGE");
                    }
                    if (cb.getId() == R.id.balance_check){
                        listMap.put(String.valueOf(R.id.balance_check),"SETTLEMENT");
                    }
                    if (cb.getId() == R.id.sign_check){
                        listMap.put(String.valueOf(R.id.sign_check),"SIGNATURE");
                    }
                }else {
                    if (cb.getId() == R.id.compact_check) {
                        listMap.remove(String.valueOf(R.id.compact_check));
                    }
                    if (cb.getId() == R.id.charge_check){
                        listMap.remove(String.valueOf(R.id.charge_check));
                    }
                    if (cb.getId() == R.id.balance_check){
                        listMap.remove(String.valueOf(R.id.balance_check));
                    }
                    if (cb.getId() == R.id.sign_check){
                        listMap.remove(String.valueOf(R.id.sign_check));
                    }
                }
            }
        }
    };

    private void getPhoneNumber(String serialNumber){
        String url = Model.DEVICEURL;
        progressDialog = ProgressDialog.show(SetActivity.this, null, "正在提交...");
        OkHttpClient okHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jb = null;
        try {
            for (Map.Entry<String,Object> item:listMap.entrySet()){
                jb = new JSONObject();
                jb.put("deviceCode",item.getValue());
                jb.put("deviceIdentifier",serialNumber);
                jsonArray.put(jb);
            }
            jsonObject.put("entityList",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
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
                    showToast("保存成功!");
                    progressDialog.dismiss();
                    Intent intent = new Intent(SetActivity.this,SocketActivity.class);
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
        Toast.makeText(SetActivity.this, str, Toast.LENGTH_LONG).show();
    }

    /** 获取设备序列号*/
    public String getIMEI(Context context)
    {
        TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(TELEPHONY_SERVICE);
        String szImei = TelephonyMgr.getDeviceId();

        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 + Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 + Build.USER.length()%10 ; //13 digits

        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();

        BluetoothAdapter m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();; // Local Bluetooth adapter
        String m_szBTMAC = m_BluetoothAdapter.getAddress();

        String m_szLongID = szImei + m_szDevIDShort + m_szAndroidID+ m_szWLANMAC + m_szBTMAC;
        // compute md5
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(m_szLongID.getBytes(),0,m_szLongID.length());
        byte p_md5Data[] = m.digest();
        String m_szUniqueID = new String();
        for (int i=0;i<p_md5Data.length;i++) {
            int b =  (0xFF & p_md5Data[i]);
            if (b <= 0xF)
                m_szUniqueID+="0";
            m_szUniqueID += Integer.toHexString(b);
        }
        m_szUniqueID = m_szUniqueID.toUpperCase();
        getPhoneNumber(m_szUniqueID);
        return m_szUniqueID;
    }
}
