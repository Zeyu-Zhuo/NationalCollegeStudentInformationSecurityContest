package com.jiacyer.newpaydemo.handler;

import android.os.Handler;
import android.os.Message;

import com.jiacyer.newpaydemo.control.LoginActivity;

/**
 * 处理LoginActivity类各种网络访问放回的Message信息的类
 * Created by Jiacy-PC on 2017/12/17.
 */

public class LoginHandler extends Handler {
    private LoginActivity loginActivity;

    public LoginHandler(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case LoginActivity.NETWORK_FALSE: {
                loginActivity.showNetworkFalse();
                break;
            }

            case LoginActivity.REFRESH_UI: {
                loginActivity.refreshUI();
                break;
            }

            case LoginActivity.LOGIN_FAILED: {
                loginActivity.showLoginFailed();
                break;
            }

            case LoginActivity.LOGIN_SUCCEED: {
                loginActivity.showLoginSucceed();
                break;
            }
        }
    }

}
