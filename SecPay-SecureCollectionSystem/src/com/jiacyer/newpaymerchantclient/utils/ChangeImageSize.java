package com.jiacyer.newpaymerchantclient.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ChangeImageSize {

    /** *//**
     * 缩放图像
     * @param scale        缩放比例
     * @param flag         缩放选择:true 放大; false 缩小;
     */
    public static Image scale(BufferedImage srcImage, int scale, boolean flag) {
        int width = srcImage.getWidth(); // 得到源图宽
        int height = srcImage.getHeight(); // 得到源图长
        if (flag)
        {
            // 放大
            width = width * scale;
            height = height * scale;
        } else {
            // 缩小
            width = width / scale;
            height = height / scale;
        }
        return srcImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
    }

}
