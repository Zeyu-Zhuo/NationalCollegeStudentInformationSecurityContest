package com.jiacyer.newpaydemo.handler;

import android.os.Handler;
import android.os.Message;

import com.jiacyer.newpaydemo.bean.Bill;
import com.jiacyer.newpaydemo.control.BillActivity;

import java.util.ArrayList;

/**
 *  Created by Jiacy-PC on 2018/5/24.
 */

public class BillHandler extends Handler {
    private BillActivity billActivity;
    private static BillHandler instance = null;

    private BillHandler(BillActivity billActivity) {
        this.billActivity = billActivity;
    }

    public static synchronized BillHandler getInstance(BillActivity activity) {
        if (instance == null)
            instance = new BillHandler(activity);
        return instance;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case BillActivity.GET_BILLS: {
                if (msg.obj instanceof ArrayList) {
                    ArrayList<Bill> list = (ArrayList<Bill>) msg.obj;
                    billActivity.loadData(list);
                }
                break;
            }
            case BillActivity.LOGOUT: {
                billActivity.logout();
                break;
            }
            case BillActivity.UPDATE_BILLS_UI: {
                billActivity.updateBillsUi();
                break;
            }
            case BillActivity.STOP_LOAD_MORE: {
                billActivity.stopLoadMoreBills();
                break;
            }
        }

    }

    public void destroy() {
        billActivity = null;
        instance = null;
    }
}
