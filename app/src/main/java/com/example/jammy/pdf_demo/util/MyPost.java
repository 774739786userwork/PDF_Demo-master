package com.example.jammy.pdf_demo.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;


/**
 * 我的post请求方式工具类
 * */

public class MyPost {

	/**
	 * 发送POST请求数据
	 * @param url 请求地址
	 * @param encode 编码（UTF-8，GBK）
	 * @param parameters 请求参数
	 * @return
	 */
	public String doPost(String url,String encode,Map<String, String> parameters){
		String result = null;
		HttpResponse httpResponse = null;
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				30000); // 超时设置
		client.getParams().setIntParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 10000);// 连接超时
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
		nameValuePairs.add(new BasicNameValuePair("mycode", encode));
		for (String key : parameters.keySet()) {
			nameValuePairs.add(new BasicNameValuePair(key, parameters.get(key)));
		}

		try {
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, encode));
			httpResponse = client.execute(post);
			Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity(), encode);
				Log.e("HTTP", "result:" + result);
			} else {
				result = null;
			}
		} catch (UnsupportedEncodingException e) {
			result = null;
		} catch (ClientProtocolException e) {
			result = null;
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
	
	/**
	 * 发送JSON数据到后台
	 * @param url  请求地址
	 * @param encode 编码
	 * @param value JSON对象数据
	 * @return
	 */
	public String doPost(String url,String encode,JSONObject value){
		String result = null;
		HttpResponse httpResponse = null;
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/json");
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 30000); // 超时设置
		client.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 10000);// 连接超时
		
		try {
			StringEntity se = new StringEntity(value.toString(),HTTP.UTF_8);
	        se.setContentType("text/json;charset=utf-8");
	        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	        post.setEntity(se);
			httpResponse = client.execute(post);
			Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity(), encode);
				Log.e("HTTP", "result:" + result);
			} else {
				result = null;
			}
		} catch (UnsupportedEncodingException e) {
			result = null;
		} catch (ClientProtocolException e) {
			result = null;
		} catch (Exception e) {
			result = null;
		}
		return result;
	}

	/**
	 * 发送JSON数据到后台
	 */
	public String doPost(String url, String encode, MultipartEntity multipartEntity){
		String result = null;
		HttpResponse httpResponse = null;
		HttpClient httpclient= new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(multipartEntity);
			httpResponse = httpclient.execute(post);
			Log.e("HTTP", "CODE" + httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(httpResponse.getEntity(), encode);
				Log.e("HTTP", "result:" + result);
			} else {
				result = null;
			}
		} catch (UnsupportedEncodingException e) {
			result = null;
		} catch (ClientProtocolException e) {
			result = null;
		} catch (Exception e) {
			result = null;
		}
		return result;
	}
}
