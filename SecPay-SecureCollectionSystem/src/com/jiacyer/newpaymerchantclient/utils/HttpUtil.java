package com.jiacyer.newpaymerchantclient.utils;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *  Created by Jiacy-PC on 2018/3/4.
 */

public class HttpUtil {

    private static OkHttpClient client = new OkHttpClient();
    private static final String server_url = "/pay/"; // 正式
    private static final String push_msg_url = "pushMsg";
    private static final String login_url = "login/merchant";
    private static final String logout_url = "logout";
    private static final String usr_info_url = "usrInfo";
    private static final String new_pay_request_url = "newPayRequest";
//    private static final String local_url = "http://10.133.23.178:8888/pic/"; // 有线
//    private static final String local_url = "http://192.168.43.130:8888/pic/"; // 有线
//    private static final String local_url = "http://10.135.31.49:8888/pic/"; // wifi
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

    public static String login(String usId, String password) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance();

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
                response.close();
                return null;
            }
        } finally {
            if (response != null)
                response.close();
        }
        return responseData;
    }

    public static boolean logout(String usrId, String token) {
        boolean ret = false;
        Response response = null;
        ConfigUtil configUtil = ConfigUtil.getInstance();

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
            if (response != null) {
                response.close();
                return false;
            }
        } finally {
            if (response != null)
                response.close();
        }

        return ret;
    }

    public static String getUsrInfo(String usId, String token) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance();

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
                response.close();
                return null;
            }
        } finally {
            if (response != null)
                response.close();
        }

        return responseData;
    }

    public static String newPayRequest(String usrId, String token,
                                       double payAccount, String payCode, String payContent, String payClass) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance();

        try {
            MediaType mediaType = MediaType.parse("application/json");
            //使用JSONObject封装参数
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("merchant_usr_id", usrId);
                jsonObject.put("token", token);
                jsonObject.put("pay_amount", payAccount);
                jsonObject.put("pay_code", payCode);
                jsonObject.put("pay_content", payContent);
                jsonObject.put("pay_class", payClass);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //创建RequestBody对象，将参数按照指定的MediaType封装
            RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+new_pay_request_url).post(requestBody).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            System.out.println("返回码："+response.code());
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                response.close();
                return null;
            }
        } finally {
            if (response != null)
                response.close();
        }

        System.out.println(responseData);
        return responseData;
    }

    public static String pushErrorMsg(String usrId,
                                       String msg, String loc, Date date) {
        Response response = null;
        String responseData = null;
        ConfigUtil configUtil = ConfigUtil.getInstance();

        try {
            MediaType mediaType = MediaType.parse("application/json");
            //使用JSONObject封装参数
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("usr_id", usrId);
                jsonObject.put("msg", msg);
                jsonObject.put("loc", loc);
                jsonObject.put("push_time", dateFormat.format(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //创建RequestBody对象，将参数按照指定的MediaType封装
            RequestBody requestBody = RequestBody.create(mediaType,jsonObject.toString());
            Request request = new Request.Builder().url(configUtil.getDefaultServer()+server_url+push_msg_url).post(requestBody).build();
            response = client.newCall(request).execute();
            responseData = response.body().string();
            System.out.println("返回码："+response.code());
        } catch (Exception e) {
            e.printStackTrace();
            if (response != null) {
                response.close();
                return null;
            }
        } finally {
            if (response != null)
                response.close();
        }

        System.out.println(responseData);
        return responseData;
    }
}
