package com.jiacyer.newpaydemo.control;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jiacyer.newpaydemo.R;
import com.jiacyer.newpaydemo.handler.SecPayHandler;
import com.jiacyer.newpaydemo.tools.CodeUtil;
import com.jiacyer.newpaydemo.tools.ProtectUtil;
import com.jiacyer.newpaydemo.tools.SystemInfoUtil;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 *  Created by Jiacy-PC on 2018/4/30.
 */

public class SecPayActivity extends AppCompatActivity {
    private String payCode;
    private String publicKey;
    private String usrId;

    private ImageView oneCodeView;
    private ImageView qrCodeView;
    private Bitmap oneCodeImage;
    private Bitmap qrcodeImage;

    private SecPayHandler handler;
    private boolean isRunningFlag;
    private boolean isProtected;

    public static final int PROTECT_QRCODE = 1;
    public static final int SHOW_QRCODE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec_pay);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        initView();
        initHandler();
        initProtectThread();
        generatePayCode();
        Log.i("SecPayActivity", publicKey);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        showQRCodeAction();
    }

    @Override
    protected void onPause() {
        super.onPause();
        protectQRCodeAction();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.destroy();
        isRunningFlag = false;
    }

    private void initHandler() {
        handler = SecPayHandler.getInstance(this);
    }

    private void initProtectThread() {
        isRunningFlag = true;
        isProtected = false;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//            Window window = getWindow();
//            WindowManager wm = getWindowManager();
//            wm.removeViewImmediate(window.getDecorView());
//            wm.addView(window.getDecorView(), window.getAttributes());
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                String phoneMaker = SystemInfoUtil.getDeviceBrand();
                while (isRunningFlag) {
                    try {
                        if (!ProtectUtil.isRunningForeground(getPackageName(), phoneMaker) && !isProtected) {
                            Message message = Message.obtain();
                            message.what = PROTECT_QRCODE;
                            handler.sendMessage(message);
//                        } else if (ProtectUtil.isRunningForeground(getPackageName()) && isProtected) {
//                            Message message = Message.obtain();
//                            message.what = SHOW_QRCODE;
//                            handler.sendMessage(message);
                        }
                        synchronized (this) {
                            wait(500);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initView() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        changeScreenBrightness(200);

        // 改变状态栏颜色
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.alipayBlue, null));

        oneCodeView = (ImageView) findViewById(R.id.oneCode);
        qrCodeView = (ImageView) findViewById(R.id.qrcode);
        qrCodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQRCodeAction();
            }
        });
        ImageView backView = (ImageView) findViewById(R.id.pay_view_back_button);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        oneCodeView.post(new Runnable() {
            @Override
            public void run() {
                WindowManager manager = SecPayActivity.this.getWindowManager();
                DisplayMetrics outMetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(outMetrics);
                int height = outMetrics.heightPixels;
                oneCodeImage = createQRCode(payCode, oneCodeView.getMeasuredWidth(), height/8);
                oneCodeView.setImageBitmap(oneCodeImage);
            }
        });

        qrCodeView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                int color = getColor(R.color.colorBlack);
                int windowSize = qrCodeView.getMeasuredWidth()<qrCodeView.getMeasuredHeight()?qrCodeView.getMeasuredWidth():qrCodeView.getMeasuredHeight();
                System.out.println("windowSize = " + windowSize + "---" + qrCodeView.getMeasuredWidth() + "---" + qrCodeView.getMeasuredHeight());
                qrcodeImage = QRCodeEncoder.syncEncodeQRCode(payCode, windowSize/2, color, logo);
                qrCodeView.setImageBitmap(qrcodeImage);
            }
        });
    }

    private void generatePayCode() {
        Intent intent = getIntent();
        publicKey = intent.getStringExtra("PublicKey");
        usrId = intent.getStringExtra("usrId");
        payCode = CodeUtil.encode(usrId, "123456789012345678", publicKey);
    }

    private void changeScreenBrightness(int brightness) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / 255.0f;
        getWindow().setAttributes(layoutParams);
    }

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static BarcodeFormat barcodeFormat= BarcodeFormat.CODE_128;
    public  static Bitmap createQRCode(String contents, int desiredWidth, int desiredHeight) {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result=null;
        try {
            result = writer.encode(contents, barcodeFormat, desiredWidth,
                    desiredHeight);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public void protectQRCodeAction() {
        isProtected = true;
        oneCodeView.setVisibility(View.GONE);
        Drawable drawable = this.getDrawable(R.drawable.qrcode_status_warning);
        qrCodeView.setImageDrawable(drawable);
    }

    public void showQRCodeAction() {
        isProtected = false;
        oneCodeView.setVisibility(View.VISIBLE);
        qrCodeView.setImageBitmap(qrcodeImage);
    }
}
