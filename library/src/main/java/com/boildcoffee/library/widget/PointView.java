package com.boildcoffee.library.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author zjh
 *         2019/3/8
 */

public class PointView extends View{
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int radius;

    public PointView(Context context) {
        super(context);
        init();
    }

    public PointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.GRAY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        radius = mWidth > mHeight ? mHeight / 2 : mWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mWidth/2,mHeight/2,radius,mPaint);
    }
}
