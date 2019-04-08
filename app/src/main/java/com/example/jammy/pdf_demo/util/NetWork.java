package com.example.jammy.pdf_demo.util;

import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * 网络判断工具类
 * */
public class NetWork {

	

	private static final int CMNET = 3;
	private static final int CMWAP = 2;
	private static final int WIFI = 1;


	private static final NetWork network = new NetWork();
	public static NetWork getInstance() {
		return network;
	}

	public boolean IsConnect(Context ctx) {
		ConnectivityManager manager = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		State stata = null;
		if (info != null) {
			stata = info.getState();
			if (stata == State.CONNECTED)
				return true;
		}
		info = null;
		info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		stata = null;
		if (info != null) {
			stata = info.getState();
			if (stata == State.CONNECTED) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 获取当前的网络状态  -1：没有网络  1：WIFI网络2：wap网络3：net网络
	 * @param context
	 * @return
	*/ 
	public static int getAPNType(Context context){ 
		int netType = -1;  
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
		if(networkInfo==null){ 
			return netType; 
		} 
		int nType = networkInfo.getType(); 
		if(nType==ConnectivityManager.TYPE_MOBILE){
			if(networkInfo.getExtraInfo().toLowerCase(Locale.CHINA).equals("cmnet")){ 
				netType = CMNET; 
			}else{
				netType = CMWAP; 
			} 
		}else if(nType==ConnectivityManager.TYPE_WIFI){ 
			netType = WIFI; 
		} 
		return netType; 
	}
	
}
