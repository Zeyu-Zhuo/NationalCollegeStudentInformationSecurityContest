package com.jiacyer.attacker.control;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jiacyer.attacker.R;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 *  Created by Jiacy-PC on 2018/4/30.
 */

public class AlipayActivity extends AppCompatActivity {
    private String qrcode;
    private ImageView oneCodeView;
    private ImageView qrCodeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay_activity);
        oneCodeView = findViewById(R.id.oneCode);
        qrCodeView = findViewById(R.id.qrcode);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        changeScreenBrightness(200);

        // 改变状态栏颜色
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.alipayColor, null));

        Intent intent = getIntent();
        qrcode = intent.getStringExtra("qrcode");

        Log.i("AlipayActivity", qrcode);

        oneCodeView.post(new Runnable() {
            @Override
            public void run() {
                WindowManager manager = AlipayActivity.this.getWindowManager();
                DisplayMetrics outMetrics = new DisplayMetrics();
                manager.getDefaultDisplay().getMetrics(outMetrics);
                int height = outMetrics.heightPixels;

                oneCodeView.setImageBitmap(creatBarcode(qrcode, oneCodeView.getMeasuredWidth(), height/8));
            }
        });

        qrCodeView.post(new Runnable() {
            @Override
            public void run() {
                Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.alipay_logo);
                int color = getColor(R.color.colorBlack);
                int windowSize = qrCodeView.getMeasuredWidth()<qrCodeView.getMeasuredHeight()?qrCodeView.getMeasuredWidth():qrCodeView.getMeasuredHeight();
                System.out.println("windowSize = " + windowSize + "---" + qrCodeView.getMeasuredWidth() + "---" + qrCodeView.getMeasuredHeight());
                Bitmap qrcodeImage = QRCodeEncoder.syncEncodeQRCode(qrcode, windowSize/2, color, logo);
                qrCodeView.setImageBitmap(qrcodeImage);
            }
        });
    }

    private void changeScreenBrightness(int brightness) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = brightness / 255.0f;
        getWindow().setAttributes(layoutParams);
    }

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static BarcodeFormat barcodeFormat= BarcodeFormat.CODE_128;
    public  static Bitmap creatBarcode(String contents, int desiredWidth,int desiredHeight) {
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

}
