package com.easemob.helpdeskdemo;

import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	private static final String TAG = Preferences.class.getSimpleName();
	static private Preferences instance = null;
	static private String PREFERENCE_NAME = "info";
	static private String KEY_USERNAME = "username";
	static private String KEY_PASSWORD = "password";
	static private String KEY_APPKEY = "appkey";
	static private String KEY_CUSTOMER_ACCOUNT = "customer_account";
	static private String KEY_NICKNAME = "nickname";
	static private String KEY_TENANT_ID = "tenantId";
	
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
    		username = getRandomUsername();
    		editor.putString(KEY_USERNAME, username);
    		editor.commit();
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
    
    public String getTenantId() {
    	return pref.getString(KEY_TENANT_ID, Constant.DEFAULT_TENANT_ID);
    }
    
    public void setTenantId(String tenantId) {
    	editor.putString(KEY_TENANT_ID, tenantId);
    	editor.commit();
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
    
    
    /**
     * demo为了演示功能，此处随机生成账号。
     * @return
     */
    private String getRandomUsername(){
    	String val = "";
    	Random random = new Random();
    	for(int i = 0; i < 10; i++){
    		String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num"; //输出字幕还是数字
    		if("char".equalsIgnoreCase(charOrNum)){// 字符串
    			int choice = random.nextInt(2) % 2 == 0 ? 65 : 97; //取得大写字母还是小写字母
    			val += (char) (choice + random.nextInt(26));
    		}else if("num".equalsIgnoreCase(charOrNum)){// 数字
    			val += String.valueOf(random.nextInt(10));
    		}
    	}
    	return val.toLowerCase(Locale.getDefault());
    }
    
    
}
