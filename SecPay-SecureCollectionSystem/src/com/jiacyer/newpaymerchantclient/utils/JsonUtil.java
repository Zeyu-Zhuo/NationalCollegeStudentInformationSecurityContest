package com.jiacyer.newpaymerchantclient.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *  Created by Jiacy-PC on 2018/3/5.
 */

public class JsonUtil {
    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static class LoginMsg {
        int code;
        String usr_id;
        String msg;
        String token;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setUsr_id(String usr_id) {
            this.usr_id = usr_id;
        }

        public String getUsr_id() {
            return usr_id;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class UsrInfo {
        private int code;
        private String usr_id;
        private String msg;
        private String usr_name;
        private String usr_loc;

        public String getUsr_loc() {
            return usr_loc;
        }

        public int getCode() {
            return code;
        }

        public String getUsr_id() {
            return usr_id;
        }

        public String getMsg() {
            return msg;
        }

        public String getUsr_name() {
            return usr_name;
        }
    }

    public static class BaseResponse {
        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }


    public static boolean isLoginSucceed(String responseData) {
        LoginMsg loginMsg = gson.fromJson(responseData, LoginMsg.class);
        if (loginMsg.code == 200)
            return true;
        else
            return false;
    }

    public static LoginMsg parseLoginMsg(String data) {
        return gson.fromJson(data, LoginMsg.class);
    }

    public static UsrInfo parseUsrInfo(String data) {
        return gson.fromJson(data, UsrInfo.class);
    }

    public static BaseResponse parsePayResponse(String data) {
        return gson.fromJson(data, BaseResponse.class);
    }
}
