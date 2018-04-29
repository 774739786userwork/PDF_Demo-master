package com.example.jammy.pdf_demo.user;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

import com.example.jammy.pdf_demo.R;
import com.example.jammy.pdf_demo.config.Model;
import com.example.jammy.pdf_demo.util.CustomProgressDialog;
import com.example.jammy.pdf_demo.util.HttpGetThread;
import com.example.jammy.pdf_demo.util.NetWork;
import com.example.jammy.pdf_demo.util.SocketActivity;
import com.example.jammy.pdf_demo.util.ThreadPoolUtils;


/**
 * 用户登录接口
 */
public class UserLogin {
	private Context context;
	private Activity currentActivity;
	public CustomProgressDialog progressDialog;
	private boolean isRememberMe = false;
	
	public UserLogin(Context context,Activity currentActivity){
		this.context = context;
		this.currentActivity = currentActivity;
		progressDialog = CustomProgressDialog.createDialog(currentActivity, R.drawable.frame);
	}
	
	public void doLogin(String username,String password, boolean isRememberMe){
		String login_url = Model.HTTPURL + Model.LOGINACTION+"?username="+username+"&password="+password;
		try{
			//todo:这里要进行判断，用户是否需要重新登陆处理。。。
			progressDialog.show();
			NetWork network = NetWork.getInstance();
			//有网络的情况下，重新登陆处理。。。
			if(network.IsConnect(context)){
				JSONObject userData = new JSONObject();
				userData.put("username", username);
				userData.put("password", password);
				this.isRememberMe = isRememberMe;
				ThreadPoolUtils.execute(new HttpGetThread(hand,login_url));
				progressDialog.dismiss();
			}else{
				progressDialog.dismiss();
				Toast.makeText(context, "您从未登陆过系统，没有网络的情况下不可使用！！", Toast.LENGTH_LONG).show();
			}
		}catch(Exception ex){
			Toast.makeText(context, "请求JSON封装错误！", Toast.LENGTH_SHORT).show();
			progressDialog.dismiss();
		}
	}
	
	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			progressDialog.hide();
			if (msg.what == 404) {
				Toast.makeText(context, "服务器地址错误", Toast.LENGTH_SHORT).show();
				
			} else if (msg.what == 100) {
				Toast.makeText(context, "网络传输失败", Toast.LENGTH_SHORT).show();
				
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				// 在activity当中获取网络交互的数据
				if (result != null) {
					try {
						JSONObject response = new JSONObject(result);
						int status = response.getInt("status");
						String message = response.getString("msg");
						if(response!=null){
							if(status == -1){
								Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
								return;
							}else if(status == -2){
								Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
								return;
							}else if (status == -3){
								Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
								return;
							}else{
								// 1次网络请求返回的数据
								JSONObject data = response.getJSONObject("data");
								User.getInstance().setValues(data);
								if(isRememberMe){
									cacheUserInfo(data);
								}
								Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
								Intent intent = new Intent(context,SocketActivity.class);
								context.startActivity(intent);

							}
						}
						progressDialog.dismiss();
						currentActivity.finish();
					} catch (JSONException e) {
						progressDialog.dismiss();
						Toast.makeText(context, "服务器返回结果无法解析，登陆失败！", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(context, "服务器连接失败！", Toast.LENGTH_SHORT).show();
				}
			}
		};
	};
	
	/**
	 * 缓存用户信息
	 * @param jsonObject
	 */
	private void cacheUserInfo(JSONObject jsonObject){

		SharedPreferences sharedPreferences = this.context.getSharedPreferences(User.SHARED_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		try{
			if (jsonObject.has("token")){
				editor.putString("login_token", jsonObject.getString("token"));
			}
			if(jsonObject.has("user_id")){
				editor.putString("user_id", jsonObject.getString("user_id"));
			}
			if(jsonObject.has("username")){
				editor.putString("username", jsonObject.getString("username"));
			}
			if(jsonObject.has("password")){
				editor.putString("password", jsonObject.getString("password"));
			}
			if(jsonObject.has("user_real_name")){
				editor.putString("user_real_name", jsonObject.getString("user_real_name"));
			}
			if(jsonObject.has("organization_id")){
				editor.putString("organization_id", jsonObject.getString("organization_id"));
			}
			if(jsonObject.has("organization_name")){
				editor.putString("organization_name", jsonObject.getString("organization_name"));
			}
			if(jsonObject.has("roles")){
				editor.putString("roles", jsonObject.getString("roles"));
			}
		}catch(JSONException ex){
			Toast.makeText(this.context, "缓存数据解析错误！", Toast.LENGTH_SHORT).show();
		}
		editor.commit();
	}
}
