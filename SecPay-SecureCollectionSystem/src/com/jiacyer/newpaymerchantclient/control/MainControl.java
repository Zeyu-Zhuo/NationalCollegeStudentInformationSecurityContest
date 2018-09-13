package com.jiacyer.newpaymerchantclient.control;

import com.jiacyer.newpaymerchantclient.handler.MainHandler;
import com.jiacyer.newpaymerchantclient.utils.CodeUtil;
import com.jiacyer.newpaymerchantclient.utils.HttpUtil;
import com.jiacyer.newpaymerchantclient.utils.JsonUtil;
import com.jiacyer.newpaymerchantclient.utils.RSAUtil;
import com.jiacyer.newpaymerchantclient.view.MainView;

import java.util.Date;
import java.util.HashMap;

public class MainControl {
    private MainHandler handler;
    private MainView mainView;

    private String usrId;
    private String token;
    private String usrName;
    private String usrLoc;

    private RSAUtil rsaUtil;
    private Runnable generateKeyRunnable;
    private boolean generateKeyThreadRunning;

    private Thread newPayRequestThread;
    private Thread pushMsgThread;

    public MainControl() {
        handler = MainHandler.init(this);
        mainView = new MainView();
        generateKeyThreadRunning = false;
        updatePublicKey();
    }

    private void initMerchantInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String data = HttpUtil.getUsrInfo(usrId, token);
                JsonUtil.UsrInfo obj = JsonUtil.parseUsrInfo(data);
                usrName = obj.getUsr_name();
                usrLoc = obj.getUsr_loc();
                handler.setMerchantInfo();
            }
        }).start();
    }

    public void setLoginMsg(JsonUtil.LoginMsg msg) {
        this.usrId = msg.getUsr_id();
        this.token = msg.getToken();
        System.out.println(usrId+ "-----"+token);
        initMerchantInfo();
    }

    public void updatePublicKey() {
        if (generateKeyThreadRunning) {
            return;
        } else if (generateKeyRunnable != null) {
            new Thread(generateKeyRunnable).start();
        } else {
            generateKeyRunnable = new Runnable() {
                @Override
                public void run() {
                    generateKeyThreadRunning = true;
                    if (rsaUtil == null)
                        rsaUtil = new RSAUtil();
                    rsaUtil.generateKeyPair();
                    handler.updatePublicKeyQRCodeUI(rsaUtil.getPublicKey());
                    generateKeyThreadRunning = false;
                }
            };
            new Thread(generateKeyRunnable).start();
        }
    }

    public void updatePublicKeyQRCodeUI(String publicKey) {
        System.out.println(CodeUtil.encode("12345", "123456789012345678", publicKey));
        System.out.println(CodeUtil.encode("12345", "123456789012345670", publicKey));
        mainView.updatePublicKeyQRCode(publicKey);
    }

    public void newPayRequest() {
        if (newPayRequestThread != null) {
            return;
        }

        double payAmount = mainView.getPayAmount();
        String payCode = mainView.getPayCode();
        String payContent = mainView.getPayContent();
        String payClass = mainView.getPayClass();

        newPayRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data;
                    HashMap<String, String> retSet = CodeUtil.decode(payCode, rsaUtil.getPublicKey(), rsaUtil.getPrivateKey());
                    String code = retSet.get(CodeUtil.PAY_CODE);
                    if (code.equals(CodeUtil.ERROR_PAY_CODE) || code.length() != 18) {
                        handler.pushErrorPaymentWarningMsg();
                        return;
                    } else {
                        data = HttpUtil.newPayRequest(usrId, token, payAmount, code, payContent, payClass);
                    }
                    JsonUtil.BaseResponse response = JsonUtil.parsePayResponse(data);
                    if (response.getCode() == 200) {
                        handler.showPaymentSucceed();
                    } else if (response.getCode() == 300) {
                        // 帐户余额不足
                        handler.showMoneyNotEnough();
                    } else if (response.getCode() == 404) {
                        // 登陆状态错误
                        handler.logout(true);
                    } else if (response.getCode() == 512) {
                        // 付款码异常
                        handler.pushErrorPaymentWarningMsg();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    newPayRequestThread = null;
                }
            }
        });
        newPayRequestThread.start();
    }

    public void showPaymentSucceed() {
        mainView.showPaymentSucceed();
    }

    public void pushErrorPaymentWarningMsg() {
        if (pushMsgThread != null) {
            return;
        }
        String payCode = mainView.getPayCode();
        mainView.showErrorPaymentWarningMsg();
        pushMsgThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, String> retSet = CodeUtil.decode(payCode, rsaUtil.getPublicKey(), rsaUtil.getPrivateKey());
                    String customerId = retSet.get(CodeUtil.USR_ID);
                    System.out.println("customerId="+customerId);
                    String msg = "你的支付异常!";
                    String data = HttpUtil.pushErrorMsg(customerId, msg, usrLoc, new Date());
                    JsonUtil.BaseResponse response = JsonUtil.parsePayResponse(data);
                    if (response.getCode() == 512) {
                        //
                        handler.pushErrorPaymentWarningMsg();
                    } else if (response.getCode() == 404) {
                        //
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    pushMsgThread = null;
                }
            }
        });
        pushMsgThread.start();
    }

    public void showMoneyNotEnough() {
        mainView.showMoneyNotEnough();
    }

    /*
     * 登出线程
     */
    public void logout(boolean forcible) {
        if (!forcible) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpUtil.logout(usrId, token);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            mainView.showStatusError();
        }

        mainView.dispose();
        new LoginControl();
        handler = null;
        mainView = null;
        rsaUtil = null;
        generateKeyRunnable = null;
        newPayRequestThread = null;
        pushMsgThread = null;
    }

    public void setMerchantInfo() {
        mainView.setMerchantInfo(usrName, usrLoc);
    }
}
