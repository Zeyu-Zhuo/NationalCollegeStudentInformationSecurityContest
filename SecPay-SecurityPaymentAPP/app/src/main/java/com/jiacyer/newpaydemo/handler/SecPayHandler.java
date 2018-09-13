package com.jiacyer.newpaydemo.handler;

import android.os.Handler;
import android.os.Message;

import com.jiacyer.newpaydemo.control.SecPayActivity;

/**
 *  Created by Jiacy-PC on 2018/5/29.
 */

public class SecPayHandler extends Handler {
    private SecPayActivity activity;
    private static SecPayHandler instance = null;

    private SecPayHandler(SecPayActivity activity) {
        this.activity = activity;
    }

    public static synchronized SecPayHandler getInstance(SecPayActivity activity) {
        if (instance == null)
            instance = new SecPayHandler(activity);
        return instance;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SecPayActivity.PROTECT_QRCODE: {
                activity.protectQRCodeAction();
                break;
            }
            case SecPayActivity.SHOW_QRCODE: {
                activity.showQRCodeAction();
                break;
            }
        }
    }

    public void destroy() {
        activity = null;
        instance = null;
    }
}
