package com.jiacyer.newpaymerchantclient.handler;

import com.jiacyer.newpaymerchantclient.control.LoginControl;
import com.jiacyer.newpaymerchantclient.utils.JsonUtil;

public class LoginHandler {
    private static LoginHandler instance = null;
    private LoginControl control;

    private LoginHandler(LoginControl control) {
        this.control = control;
    }

    public static synchronized LoginHandler getInstance() {
        return instance;
    }

    public static synchronized LoginHandler init(LoginControl control) {
        if (instance == null && control != null)
            instance = new LoginHandler(control);
        return instance;
    }

    public void login() {
        control.login();
    }

    public void createMainControl(JsonUtil.LoginMsg msg) {
        control.createMainControl(msg);
    }

    public void destroy() {
        control = null;
        instance = null;
    }

    public void showLoginError() {
        control.showLoginError();
    }
}
