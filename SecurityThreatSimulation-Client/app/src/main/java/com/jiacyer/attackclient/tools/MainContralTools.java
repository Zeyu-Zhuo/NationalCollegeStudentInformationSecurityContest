package com.jiacyer.attackclient.tools;

import android.view.TextureView;

import com.jiacyer.attackclient.face.MainActivity;

/**
 *  Created by Jiacy-PC on 2018/1/23.
 */

public class MainContralTools {
    private static MainActivity mainActivity = null;

    public static void setMainActivity(MainActivity activity) {
        mainActivity = activity;
    }

    public static void removeMainActivity() {
        mainActivity = null;
    }

    public static void startAttack() {
        if (mainActivity != null) {
//            mainActivity.addRectView();
//            mainActivity.openCamera();
        }
    }

    public static void finishActivity() {
        if (mainActivity != null) {
            mainActivity.finish();
            mainActivity = null;
        }
    }

    public static void showTip(String s) {
        if (mainActivity != null) {
            mainActivity.showTip(s);
        }
    }

//    public static SurfaceView getSurfaceView() {
//        if (mainActivity != null) {
//            return mainActivity.getSurfaceView();
//        }
//        return null;
//    }

//    public static SurfaceView getRetView() {
//        if (mainActivity != null) {
//            return mainActivity.getRetView();
//        }
//        return null;
//    }

//    public static ImageView getImageView() {
//        if (mainActivity != null) {
//            return mainActivity.getImageView();
//        }
//        return null;
//    }

    public static TextureView getTextureView() {
//        if (mainActivity != null) {
//            return mainActivity.getTextureView();
//        }
        return null;
    }

//    public static void setImageBitmap(Bitmap bitmap) {
//        if (mainActivity != null) {
//            mainActivity.setImageBitmap(bitmap);
//        }
//    }
}
