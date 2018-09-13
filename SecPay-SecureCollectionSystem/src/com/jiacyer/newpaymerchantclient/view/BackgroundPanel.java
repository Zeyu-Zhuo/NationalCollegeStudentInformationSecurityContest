package com.jiacyer.newpaymerchantclient.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BackgroundPanel extends JPanel {

    private BufferedImage background;

    public BackgroundPanel(boolean isLoginView) {
        init(isLoginView);
    }

    public BackgroundPanel(LayoutManager layoutManager, boolean isLoginView) {
        super(layoutManager);
        init(isLoginView);
    }

    private void init(boolean isLoginView) {
        setOpaque(false);
        if (isLoginView) {
            try {
                background = ImageIO.read(BackgroundPanel.class.getResourceAsStream("/com/jiacyer/newpaymerchantclient/image/banner_login.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                background = ImageIO.read(BackgroundPanel.class.getResourceAsStream("/com/jiacyer/newpaymerchantclient/image/banner.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g){
        int x = 0,y = 0;
        g.drawImage(background, x, y, getSize().width, getSize().height, this);
        while (true) {
            g.drawImage(background, x, y, this);
            if (x > getSize().width && y > getSize().height) break;
            if (x > getSize().width) {
                x = 0;
                y += background.getHeight();
            } else x += background.getWidth();
        }
    }
}
