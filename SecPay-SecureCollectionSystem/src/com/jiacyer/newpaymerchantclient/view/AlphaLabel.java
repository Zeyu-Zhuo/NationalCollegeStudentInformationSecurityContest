package com.jiacyer.newpaymerchantclient.view;

import javax.swing.*;
import java.awt.*;

public class AlphaLabel extends JLabel {

    public AlphaLabel(){
        setFont(new Font("黑体",Font.BOLD,30));
    }

    public AlphaLabel(String title) {
        super(title);
        setForeground(new Color(0xFFFFFF));
        setFont(new Font("黑体",Font.BOLD,30));
    }
}
