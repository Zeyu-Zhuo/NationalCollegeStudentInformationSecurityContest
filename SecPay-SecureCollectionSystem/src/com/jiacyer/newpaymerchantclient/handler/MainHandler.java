package com.jiacyer.newpaymerchantclient.handler;

import com.jiacyer.newpaymerchantclient.control.MainControl;

public class MainHandler {
    private static MainHandler instance = null;
    private MainControl control;

    private MainHandler(MainControl control) {
        this.control = control;
    }

    public static synchronized MainHandler getInstance() {
        return instance;
    }

    public static synchronized MainHandler init(MainControl control) {
        if (instance == null && control != null)
            instance = new MainHandler(control);
        return instance;
    }

    public void updatePublicKey() {
        control.updatePublicKey();
    }

    public void updatePublicKeyQRCodeUI(String publicKey) {
        control.updatePublicKeyQRCodeUI(publicKey);
    }

    public void newPayRequest() {
        control.newPayRequest();
    }

    public void showPaymentSucceed() {
        control.showPaymentSucceed();
    }

    public void pushErrorPaymentWarningMsg() {
        control.pushErrorPaymentWarningMsg();
    }

    public void showMoneyNotEnough() {
        control.showMoneyNotEnough();
    }

    public void logout(boolean forcible) {
        control.logout(forcible);
        control = null;
        instance = null;
    }

    public void setMerchantInfo() {
        control.setMerchantInfo();
    }
}
