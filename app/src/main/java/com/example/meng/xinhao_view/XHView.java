package com.example.meng.xinhao_view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 一个自定义钟表
 */

public class XHView extends View {

    //新的圆宽

    private int sizeW;

    private int sizeH;

    private String[] timeString = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};

    private Context mContext;
    private RectF rectF;
    private Paint mPaint;
    private Paint mTextPaint;
    //缩放距离
    private int sc = 20;
    //线的长度
    private int lineW = 40;

    //秒表最长
    private int lineS = 360;
    private Paint mPaintS;
    //分钟
    private int lineM = 260;
    private Paint mPaintM;
    //时钟
    private int lineH = 220;
    private Paint mPaintH;
    private int drawH;
    private int drawM;
    private int drawS;

    public XHView(Context context) {
        super(context);
        initData(context);
    }

    public XHView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public XHView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    private void initData(Context mContext) {
        this.mContext = mContext;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#ad0015"));
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(5);
        //初始化
        mTextPaint = new Paint();
        mTextPaint.setTextSize(40);
        mTextPaint.setAntiAlias(true);

        mPaintS = new Paint();
        mPaintS.setStrokeWidth(20);
        mPaintM = new Paint();
        mPaintM.setColor(Color.parseColor("#c40302"));
        mPaintM.setStrokeWidth(5);
        mPaintM.setAntiAlias(true);
        mPaintH = new Paint();
        mPaintH.setColor(Color.parseColor("#a59632"));
        mPaintH.setStrokeWidth(5);
        mPaintH.setAntiAlias(true);
        mPaintS.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        startDarwOR(canvas);
        startDrawSMH(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sizeW = w;
        sizeH = h;
        initW2H();
        invalidate();
        startTime();
    }


    //初始化宽高

    private void initW2H() {

        rectF = new RectF();
        rectF.left = sc;
        rectF.right = sizeW - sc;
        rectF.top = sc;
        rectF.bottom = sizeW - sc;


    }

    //开始画圆/表的刻度

    private void startDarwOR(Canvas canvas) {
        canvas.drawOval(rectF, mPaint);
        //开始画刻度表
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                //为时间段
                lineW = 40;
            } else {
                //为分钟段
                lineW = 20;
            }
            if (lineW == 40) {
                //画字
                canvas.drawText(timeString[i / 5], ((rectF.bottom / 2) + (sc / 2)), sc + lineW + 40/*加上字体大小*/, mTextPaint);
            }

            canvas.drawLine((rectF.right / 2) + (sc / 2), sc, (rectF.right / 2) + (sc / 2), sc + lineW, mPaint);
            canvas.rotate(360 / 60, (rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2));

            canvas.drawOval((rectF.right / 2) + (sc / 2) - 10, (rectF.bottom / 2) + (sc / 2) - 10, (rectF.right / 2) + (sc / 2) + 10, (rectF.bottom / 2) + (sc / 2) + 10, mPaintS);

        }
    }

    //开始画秒钟/分钟/时钟

    private void startDrawSMH(Canvas canvas) {
        canvas.save();
        canvas.rotate(drawH, (rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2));
        canvas.drawLine((rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2) + 50, (rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2) - lineH, mPaintH);
        canvas.restore();

        canvas.save();
        canvas.rotate(drawM, (rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2));
        canvas.drawLine((rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2) + 50, (rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2) - lineM, mPaintM);
        canvas.restore();

        canvas.save();
        canvas.rotate(drawS, (rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2));
        canvas.drawLine((rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2) + 50, (rectF.right / 2) + (sc / 2), (rectF.bottom / 2) + (sc / 2) - lineS, mPaint);
        canvas.restore();

        canvas.drawText(time, ((rectF.right / 2) + (sc / 2)), (rectF.bottom / 4) + (sc / 2), mTextPaint);


    }
    //开始处理时间逻辑

    //开始处理角度

    //秒旋转角度
    private int angle = 6;
    //分旋转角度
    private int angleM = 6;
    //时旋转角度
    private int angleH = 6;

    private String time = "00:00:00";

    private void startTime() {

        //该逻辑处理放到子线程中
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        new Thread(new Runnable() {
            @Override
            public void run() {

                //死循环,如果有需要的同学请更改此逻辑

                while (true) {
                    //获取时间

                    Date date = new Date();
                    String format = simpleDateFormat.format(date);
                    final String[] split = format.split(":");

                    time = format;
                    if (angle > 360) {
                        angle = 6;
                    }
                    if (angleM > 360) {
                        angleM = 6;
                    }
                    if (angleH > 360) {
                        angleH = 6;
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MainActivity.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                            int h = Integer.parseInt(split[0]);
                            int m = Integer.parseInt(split[1]);
                            int s = Integer.parseInt(split[2]);
                            drawH = angle * h;
                            drawM = angleM * m;
                            drawS = angleH * s;
                        }
                    });
                }


            }
        }).start();


    }


}
