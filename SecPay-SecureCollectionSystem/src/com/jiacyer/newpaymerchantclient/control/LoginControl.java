package com.jiacyer.newpaymerchantclient.control;

import com.jiacyer.newpaymerchantclient.handler.LoginHandler;
import com.jiacyer.newpaymerchantclient.utils.Encrypt;
import com.jiacyer.newpaymerchantclient.utils.HttpUtil;
import com.jiacyer.newpaymerchantclient.utils.JsonUtil;
import com.jiacyer.newpaymerchantclient.view.LoginView;

public class LoginControl {
    private LoginView loginView;
    private Thread loginThread;
    private LoginHandler handler;

    public LoginControl() {
        handler = LoginHandler.init(this);
        loginView = new LoginView();
    }

    public void login() {
        String usrId = loginView.getAccount();
        String password = loginView.getPassword();
        password = Encrypt.md5(password).toLowerCase();
        password = Encrypt.md5(password).toLowerCase();
        String finalPassword = password;

        if (loginThread != null) {
            return;
        }

        loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                 try {
                     System.out.println(usrId+"------"+finalPassword);
                     String responseData = HttpUtil.login(usrId, finalPassword);
                     boolean isSucceed = JsonUtil.isLoginSucceed(responseData);
                     if (isSucceed) {
                         System.out.println("login succeed!");
                         JsonUtil.LoginMsg msg = JsonUtil.parseLoginMsg(responseData);
                         handler.createMainControl(msg);
                     } else {
                         handler.showLoginError();
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                     loginThread = null;
                 }
            }
        });
        loginThread.start();    }


    public void createMainControl(JsonUtil.LoginMsg msg) {
        new MainControl().setLoginMsg(msg);
        finish();
    }

    public void finish() {
        handler.destroy();
        loginView.dispose();
        handler = null;
        loginView = null;
        loginThread = null;
    }

    public void showLoginError() {
        loginView.showLoginError();
        loginThread = null;
    }
}
