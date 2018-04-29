package com.example.jammy.pdf_demo.user;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户信息模型
 */
public class User implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2140348541648884704L;
	public static final String SHARED_NAME = "user";

	private static User user = new User();
	public static User getInstance(){
		return user;
	}
	private String login_token;//登陆令牌
	private String user_id;//用户ID
	private String user_name;//账号
	private String password;//密码
	private String user_realname;//真实姓名
	private String org_name;//所属机构
	private String org_id;//所属机构ID

	public User readFromSharedPreferences(SharedPreferences sharedPreferences){
		setUser_name(sharedPreferences.getString("username", ""));
		setPassword(sharedPreferences.getString("password", ""));
		setLogin_token(sharedPreferences.getString("login_token", ""));
		setUser_realname(sharedPreferences.getString("user_real_name", ""));
		setOrg_id(sharedPreferences.getString("organization_id", ""));
		setOrg_name(sharedPreferences.getString("organization_name", ""));
		setUser_id(sharedPreferences.getString("user_id", ""));
		return user;
	}
	/**
	 * 为UserInfo赋值
	 * @param jsonObject
	 */
	public void setValues(JSONObject jsonObject){
		try{
			if(jsonObject.has("token")){
				setLogin_token(jsonObject.getString("token"));
			}
			if(jsonObject.has("user_id")){
				setUser_id(jsonObject.getString("user_id"));
			}
			if(jsonObject.has("username")){
				setUser_name(jsonObject.getString("username"));
			}
			if(jsonObject.has("password")){
				setPassword(jsonObject.getString("password"));
			}
			if(jsonObject.has("user_real_name")){
				setUser_realname(jsonObject.getString("user_real_name"));
			}
			if(jsonObject.has("organization_id")){
				setOrg_id(jsonObject.getString("organization_id"));
			}
			if(jsonObject.has("organization_name")){
				setOrg_name(jsonObject.getString("organization_name"));
			}
		}catch(JSONException ex){

		}
	}

	public String getLogin_token() {
		return login_token;
	}

	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUser_realname() {
		return user_realname;
	}
	public void setUser_realname(String user_realname) {
		this.user_realname = user_realname;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}

}
