package com.phongbm.libraries;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import java.util.Timer;
import java.util.TimerTask;

public class CallingRippleView extends View {
    private static final int SPEED = 4;
    private static final int BLACK = 0xFFFFFFFF;
    private static final int COLOR = 0xFF4CAF50;
    private static final int ALPHA = 0xFF000000;
    private static final int PAINTER_COUNT = 10;
    private static final int LINE_COUNT = 2;

    private Paint[] painter;
    private int[] currentRadius;
    private int minRadius, maxRadius;
    private Timer timer;
    private int currentSpeed;
    private int colorBase;
    private int x;
    private int y;
    private boolean isInitialized;

    public CallingRippleView(Context context) {
        super(context);
        this.initialize();
    }

    public CallingRippleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize();
    }

    public CallingRippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initialize();
    }

    private void initialize() {
        isInitialized = false;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (x == 0 || y == 0) {
                    x = CallingRippleView.this.getWidth() / 2;
                    y = CallingRippleView.this.getHeight() / 2;
                    return;
                } else {
                    if (!isInitialized) {
                        CallingRippleView.this.initializeValues();
                        isInitialized = true;
                    }
                }
                for (int i = 0; i < currentRadius.length; ++i)
                    if (currentRadius[i] > maxRadius) {
                        currentRadius[i] = minRadius;
                    } else {
                        currentRadius[i] += currentSpeed;
                    }
                CallingRippleView.this.postInvalidate();
            }
        }, 0, 20);
    }

    private void initializeValues() {
        x = this.getWidth() / 2;
        y = this.getHeight() / 2;
        colorBase = COLOR / PAINTER_COUNT;
        this.initializePainter();
        currentSpeed = SPEED;
        minRadius = 48;
        maxRadius = this.getWidth() < getHeight() ? this.getWidth() / 2 : getHeight() / 2;
        currentRadius = new int[LINE_COUNT];
        for (int i = 0; i < currentRadius.length; ++i) {
            currentRadius[i] = minRadius + (maxRadius - minRadius) * i / currentRadius.length;
        }
    }

    private void initializePainter() {
        painter = new Paint[PAINTER_COUNT];
        for (int i = 0; i < painter.length; ++i) {
            painter[i] = new Paint();
            painter[i].setColor((BLACK - colorBase * (PAINTER_COUNT - i)) | ALPHA);
            painter[i].setAntiAlias(true);
            painter[i].setStrokeWidth(PAINTER_COUNT - i);
            painter[i].setStyle(Style.STROKE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInitialized) {
            for (int i : currentRadius)
                canvas.drawCircle(x, y, i, getPaint(i));
        }
    }

    private Paint getPaint(int radius) {
        int divide = ((maxRadius - minRadius) / PAINTER_COUNT) + 1;
        return painter[(radius - minRadius) / divide];
    }

    @Override
    protected void onDetachedFromWindow() {
        if (timer != null)
            timer.cancel();
        super.onDetachedFromWindow();
    }

}