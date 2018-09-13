package com.jiacyer.attackclient.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiacyer.attackclient.R;
import com.jiacyer.attackclient.model.CameraUtil;
import com.jiacyer.attackclient.service.DetectService;
import com.jiacyer.attackclient.tools.ConfigUtil;
import com.jiacyer.attackclient.tools.MainContralTools;

/**
 *  Created by Jiacy-PC on 2018/1/23.
 */

public class MainActivity extends Activity {

    //    private MainHandler handler;
    private Button stopButton;
//    private SurfaceView surfaceView;
//    private SurfaceView retView;
    private TextureView textureView;
//    private ImageView imageView;
    private CameraUtil cameraUtil;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "activity start");
        setContentView(R.layout.config_layout);
//        MainContralTools.setMainActivity(this);

        ConfigUtil configUtil = ConfigUtil.getInstance(this);

        editText = findViewById(R.id.server);
        editText.setText(configUtil.getDefaultServer());
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    return true;
                }
                return false;
            }
        });

        stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetectService.setFlag(false);
                Intent intent = new Intent(MainActivity.this, DetectService.class);
                stopService(intent);
            }
        });

        Button cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setVisibility(View.GONE);
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                System.out.println("Width:"+textureView.getWidth()+";Height:"+textureView.getHeight());
//
//                if (cameraUtil == null)
//                    openCamera();
//                else {
////                    cameraUtil.takePicture();
////                    Bitmap bitmap = surfaceView.getDrawingCache();
//
//                    final Bitmap bitmap = textureView.getBitmap();
//                    Log.i("Bitmap", "Width="+bitmap.getWidth());
//                    Log.i("Bitmap", "Height="+bitmap.getHeight());
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.i("Thread", "Start to upload");
//                            boolean ret = HttpUtil.uploadBitmap("attack1", 0, bitmap);
//                            Log.i("Thread", "End of upload: "+ret);
//                        }
//                    }).start();
////                    imageView.setImageBitmap(bitmap);
//                }
//            }
//        });

//        surfaceView = findViewById(R.id.surfaceView);
        textureView = findViewById(R.id.textureView);
//        textureView.setVisibility(View.GONE);
        textureView.setAlpha(0.0f);
//        imageView = findViewById(R.id.imageView);
        System.out.println("Width:"+textureView.getWidth()+";Height:"+textureView.getHeight());

        initService();

        Log.i("MainActivity", "This is the end of MainActivity!");
    }

//    public void openCamera() {
//        if (cameraUtil == null && checkCameraPermission()) {
//            try {
//                cameraUtil = new CameraUtil(this);
//            } catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        } else {
//            cameraUtil = null;
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
////            openCamera();
//        }
//    }

    private void initService() {
//        handler = new MainHandler();
        Log.i("MainActivity", "initService");
        if (DetectService.isOnlyOne()) {
            Intent intent = new Intent(this, DetectService.class);
            startService(intent);
            Log.i("MainActivity", "initService Succeed");
        }
    }

//    private boolean checkCameraPermission() {
//        return  ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
//    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DetectService.isSucceed()) {
            Intent intent = new Intent(MainActivity.this, AttackActivity.class);
            startActivity(intent);
            finish();
        }
        Log.i("MainActivity", "Activity Resume！");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DetectService.isSucceed()) {
//            Intent intent = new Intent(MainActivity.this, AttackActivity.class);
//            startActivity(intent);
            finish();
        }
        Log.i("MainActivity", "Activity Restart！");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cameraUtil != null)
            cameraUtil.stopCamera();
        cameraUtil = null;
        DetectService.setFlag(true);

        ConfigUtil configUtil = ConfigUtil.getInstance(this);
        configUtil.setDefaultServer(String.valueOf(editText.getText()).trim());

        Log.i("MainActivity", "Activity Pause!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraUtil != null)
            cameraUtil.stopCamera();
        cameraUtil = null;
        MainContralTools.removeMainActivity();
        Log.i("MainActivity", "MainActivity stopped!");
    }

//    public void addRectView() {
//        RectView rectView = new RectView(MainActivity.this);
//        setContentView(rectView);
//    }

    public void showTip(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

//    public SurfaceView getSurfaceView() {
//        return surfaceView;
//    }

    public TextureView getTextureView() {
        return textureView;
    }

//    public SurfaceView getRetView() {
////        return retView;
//    }

//    public ImageView getImageView() {
//        return imageView;
//    }

//    public void setImageBitmap(Bitmap bitmap) {
//        imageView.setImageBitmap(bitmap);
//    }
}
