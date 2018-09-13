package com.jiacyer.newpaymerchantclient.view;

import javax.swing.*;
import java.awt.*;

public class EditView extends JTextField {
    public EditView() {
        setPreferredSize(new Dimension(200,20));
        setFont(new Font("黑体",Font.BOLD,30));
        setMargin(new Insets(2,10,2,10));
    }
}
