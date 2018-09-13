package com.jiacyer.newpaymerchantclient.control;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import javax.swing.*;
import java.awt.*;

public class MainActivity {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try
                {
                    org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
                    UIManager.put("RootPane.setupButtonVisible", false);
                    BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
                    new LoginControl();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
