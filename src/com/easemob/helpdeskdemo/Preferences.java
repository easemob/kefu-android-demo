package com.easemob.helpdeskdemo;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	static private Preferences instance = null;
	static private String PREFERENCE_NAME = "info";
	static private String KEY_USERNAME = "username";
	static private String KEY_PASSWORD = "password";
	static private String KEY_APPKEY = "appkey";
	static private String KEY_CUSTOMER_ACCOUNT = "customer_account";
	static private String KEY_NICKNAME = "nickname";
	
	private SharedPreferences pref = null;
	private SharedPreferences.Editor editor = null;
	
	static public Preferences getInstance(){
		return instance;
	}
	
	public static void init(Context context){
		instance = new Preferences();
		instance.pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE );
		instance.editor = instance.pref.edit();
	}
    public void setUserName(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }
    
    public String getUserName(){
    	String username;
    	
    	username = pref.getString(KEY_USERNAME, null);
    	if(username == null) {
    		username = createUsername();
    	}
    	return username;
    	
    }
    
    public void setPassword(String password) {
        editor.putString(KEY_PASSWORD, password);
        editor.commit();
    }
    
    public String getPassword() {
    	return pref.getString(KEY_PASSWORD, Constant.DEFAULT_ACCOUNT_PWD);
    }
    
    public void setAppKey(String key) {
        editor.putString(KEY_APPKEY, key);
        editor.commit();
    	
    	
    }
    
    public String getAppKey(){
        return pref.getString(KEY_APPKEY, Constant.DEFAULT_CUSTOMER_APPKEY);	
    }
    
    public void setCustomerAccount(String account){
        editor.putString(KEY_CUSTOMER_ACCOUNT, account);
        editor.commit();
    	
    }
    
    public String getCustomerAccount(){
    	return pref.getString(KEY_CUSTOMER_ACCOUNT, Constant.DEFAULT_CUSTOMER_ACCOUNT);
    }
    
    public void setNickName(String nickname){
        editor.putString(KEY_NICKNAME, nickname);
        editor.commit();
    }
    
    public String getNickName(){
       return pref.getString(KEY_NICKNAME, Constant.DEFAULT_NICK_NAME);
    }
    
    private String createUsername() {
        UUID uuid = UUID.randomUUID();
        return Constant.DEFAULT_CUSTOMER_PREFIX + "001";// uuid.hashCode();
    }
}
