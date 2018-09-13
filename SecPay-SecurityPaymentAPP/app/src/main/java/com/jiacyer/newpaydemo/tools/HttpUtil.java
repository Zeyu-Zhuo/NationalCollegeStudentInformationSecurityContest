package com.jiacyer.newpaydemo.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *  Created by Jiacy-PC on 2018/3/4.
 */

public class HttpUtil {

    private static OkHttpClient client = new OkHttpClient();
    private static final String server_url = "/pay/"; // 正式
    private static final String pull_msg_url = "pullMsg";
    private static final String login_url = "login";
    private static final String logout_url = "logout";
    private static final String usr_info_url = "usrInfo";
    private static final String check_msg_url = "checkMsg";
    private static final String bill_url = "billList";
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

    public static String getMsgFromServer(String usId, String token) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("usr_id", usId)
                    .add("token", token)
                    .build();
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+pull_msg_url).post(requestBody).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            System.out.println("返回码："+response.code());
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                response.code();
                return null;
            }
        }
        System.out.println(responseData);
        return responseData;
    }

    public static String login(String usId, String password) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("usr_id", usId)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+login_url).post(requestBody).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            System.out.println("login---"+responseData);
            System.out.println("返回码："+response.code());
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                response.code();
                return null;
            }
        }
        return responseData;
    }

    public static boolean logout(String usrId, String token) {
        boolean ret = false;
        Response response;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("usr_id", usrId)
                    .add("token", token)
                    .build();
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+logout_url).post(requestBody).build();
            response = client.newCall(request).execute();

            if (response.code() == 200) {
                ret = true;
            } else if (response.code() == 404) {
                ret = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static String getUsrInfo(String usId, String token) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("usr_id", usId)
                    .add("token", token)
                    .build();
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+usr_info_url).post(requestBody).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            System.out.println("返回码："+response.code());
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                response.code();
                return null;
            }
        }
        return responseData;
    }

    public static String checkMsg(String usrId, String token, int msgId, boolean isAbnormalPay) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            MediaType mediaType = MediaType.parse("application/json");
            //使用JSONObject封装参数
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("usr_id", usrId);
                jsonObject.put("token", token);
                jsonObject.put("msg_id", msgId);
                jsonObject.put("is_abnormal_pay", isAbnormalPay);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //创建RequestBody对象，将参数按照指定的MediaType封装
            RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+check_msg_url).post(requestBody).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            System.out.println("返回码："+response.code());
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                response.code();
                return null;
            }
        }
        System.out.println(responseData);
        return responseData;
    }

    public static String getBillList(String usrId, String token, int page_index) {
        String responseData = null;
        Response response = null;
        ConfigUtil configUtil = ConfigUtil.getInstance(null);

        try {
            MediaType mediaType = MediaType.parse("application/json");
            //使用JSONObject封装参数
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("usr_id", usrId);
                jsonObject.put("token", token);
                jsonObject.put("page", page_index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //创建RequestBody对象，将参数按照指定的MediaType封装
            RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+bill_url).post(requestBody).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            System.out.println("返回码："+response.code());
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                System.out.println("返回码："+response.code());
                return null;
            }
        }
        System.out.println(responseData);
        return responseData;
    }
}
