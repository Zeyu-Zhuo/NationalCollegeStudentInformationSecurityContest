package com.jiacyer.attacker.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *  Created by Jiacy-PC on 2018/3/4.
 */

public class HttpUtil {

    private static OkHttpClient client = new OkHttpClient();
    private static final String server_url = "/pic/"; // 正式
    private static final String getAttackList_url = "attack_list";
    private static final String getQRCode_url = "qrcode";
//    private static final String local_url = "http://10.133.23.178:8888/pic/"; // 有线
//    private static final String local_url = "http://192.168.43.130:8888/pic/"; // 有线
//    private static final String local_url = "http://10.135.31.49:8888/pic/"; // wifi

    public static final int NotWork = 0;
    public static final int Work = 1;

    /**
     * 获取当前的网络状态 ：没有网络-0：有网络-1
     * @param context
     * @return 网络状态
     */
    public static int getNetworkState(Context context) {
        int netType;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null)
            netType = NotWork;
        else
            netType = Work;

        return netType;
    }

    public static String getGetAttackListFromServer() {
        Response response;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+getAttackList_url).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            Log.i("HttpUtil", "Response Data : "+responseData);
            Log.i("HttpUtil", "Response Code : "+response.code());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseData;
    }

    public static String getQRCodeFromServer(String attack_id) {
        Response response;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+attack_id+"/"+getQRCode_url).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            Log.i("HttpUtil", "Response Data : "+responseData);
            Log.i("HttpUtil", "Response Code : "+response.code());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseData;
    }

}
