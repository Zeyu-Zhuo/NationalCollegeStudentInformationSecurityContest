package com.jiacyer.attackclient.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/**
 *  Created by Jiacy-PC on 2018/1/23.
 */

public class RectView extends View {
    // 1.创建一个画笔
    private Paint mPaint = new Paint();
    private int left_block = 360;
    private int top_block = 772;
    private int width_block = 345;
    private int len_block = 20;

    private int left_line = 120;
    private int top_line = 280;
    private int dis_line = 810;
    private int height_line = 300;
    private int width_line = 30;

    public RectView(Context context) {
        super(context);
        initPaint();
    }

    private void initPaint() {
        mPaint.setColor(Color.WHITE);       //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        mPaint.setStrokeWidth(10f);         //设置画笔宽度为10px
    }

    private void initRect(Canvas canvas) {
        Rect rect1 = new Rect(left_block, top_block, left_block+len_block, top_block+len_block);
        Rect rect2 = new Rect(left_block+width_block, top_block, left_block+width_block+len_block, top_block+len_block);
        Rect rect3 = new Rect(left_block, top_block+width_block, left_block+len_block, top_block+width_block+len_block);
        Rect rect4 = new Rect(left_line, top_line, left_line+width_line, top_line+height_line);
        Rect rect5 = new Rect(left_line+dis_line, top_line, left_line+width_line+dis_line, top_line+height_line);

//        canvas.drawRect(rect1,mPaint);
        canvas.drawRect(rect2,mPaint);
        canvas.drawRect(rect3,mPaint);
        canvas.drawRect(rect4,mPaint);
        canvas.drawRect(rect5,mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initRect(canvas);
    }
}
