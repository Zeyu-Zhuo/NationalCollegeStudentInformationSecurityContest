package com.jiacyer.newpaydemo.tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *  Created by Jiacy-PC on 2018/5/10.
 */

public class ConfigUtil {
    private SharedPreferences sharedPreferences;
    private static ConfigUtil instance = null;

    public synchronized static ConfigUtil getInstance(Context context) {
        if (instance == null && context != null) {
            instance = new ConfigUtil(context);
        }
        return instance;
    }

    private ConfigUtil(Context context) {
        sharedPreferences = context.getSharedPreferences("DefaultConfig", Context.MODE_PRIVATE);
    }

    public String getDefaultServer() {
        return  sharedPreferences.getString("Server", "http://127.0.0.1");
    }

    public void setDefaultServer(String server) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Server", server);
        editor.apply();
    }

    public void setAutoLogin(boolean f) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("AutoLogin", f);
        editor.apply();
    }

    public boolean isAutoLogin() {
        return sharedPreferences.getBoolean("AutoLogin", false);
    }

    public void setUerInfo(String usr_id, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("usr_id", usr_id);
        editor.putString("password", password);
        editor.apply();
    }

    public String getUsrId() {
        return sharedPreferences.getString("usr_id", "");
    }

    public String getPassword() {
        return sharedPreferences.getString("password", "");
    }
}
