package com.jiacyer.newpaydemo.handler;

import android.os.Handler;
import android.os.Message;

import com.jiacyer.newpaydemo.control.ScrollingActivity;
import com.jiacyer.newpaydemo.bean.MsgContent;
import com.jiacyer.newpaydemo.tools.JsonUtil;

import java.util.ArrayList;

/**
 *  Created by Jiacy-PC on 2018/5/23.
 */

public class ScrollingHandler extends Handler {
    private ScrollingActivity scrollingActivity;
    private static ScrollingHandler instance = null;

    private ScrollingHandler(ScrollingActivity scrollingActivity) {
        this.scrollingActivity = scrollingActivity;
    }

    public static synchronized ScrollingHandler getInstance(ScrollingActivity activity) {
        if (instance == null)
            instance = new ScrollingHandler(activity);
        return instance;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ScrollingActivity.LOGOUT: {
                // 结束所有线程，并退出
                scrollingActivity.logout();
                break;
            }
            case ScrollingActivity.USR_INFO: {
                scrollingActivity.updateUsrInfoUi((JsonUtil.UsrInfo) msg.obj);
                break;
            }
            case ScrollingActivity.PULL_MSG: {
                JsonUtil.MsgInfo obj = (JsonUtil.MsgInfo) msg.obj;
                ArrayList<MsgContent> contents = obj.getContent();

                scrollingActivity.updateAlertMsgUi(contents);
                break;
            }
            case ScrollingActivity.NORMAL_PAY: {
//                AlertCardData obj = (AlertCardData) msg.obj;
//                mainActivity.submitSecurityTipAction(obj.getMsgId(), false);
                break;
            }
            case ScrollingActivity.ABNORMAL_PAY: {
//                AlertCardData obj = (AlertCardData) msg.obj;
//                mainActivity.submitSecurityTipAction(obj.getMsgId(), true);
                break;
            }
        }
    }

    public void destroy() {
        scrollingActivity = null;
        instance = null;
    }
}
