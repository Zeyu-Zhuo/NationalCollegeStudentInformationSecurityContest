package com.jiacyer.attacker.tools;

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
}
