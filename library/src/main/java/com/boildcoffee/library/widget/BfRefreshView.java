package com.boildcoffee.library.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.FrameLayout;

/**
 * @author zjh
 *         2019/3/8
 */

public class BfRefreshView extends FrameLayout implements IRefreshView{
    private PointView mPointView1;
    private PointView mPointView2;
    private PointView mPointView3;
    private ObjectAnimator mAnimator1;
    private ObjectAnimator mAnimator2;
    private ObjectAnimator mAnimator3;
    private float mRawX;
    private float mRawY;

    public BfRefreshView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BfRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BfRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mRawX = mPointView1.getX();
        mRawY = mPointView1.getY();
    }

    private void init(Context context) {
        int widthAndHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,10,getResources().getDisplayMetrics());
        LayoutParams params = new LayoutParams(widthAndHeight,widthAndHeight);
        params.gravity = Gravity.CENTER;

        mPointView1= new PointView(context);
        mPointView2= new PointView(context);
        mPointView3= new PointView(context);

        mPointView1.setLayoutParams(params);
        mPointView2.setLayoutParams(params);
        mPointView3.setLayoutParams(params);

        addView(mPointView1);
        addView(mPointView2);
        addView(mPointView3);
    }


    @Override
    public void releaseToRefresh() {
    }

    @Override
    public void refreshing() {
        float[] values = new float[]{mRawY -10, mRawY, mRawY +10};

        mAnimator1 = ObjectAnimator.ofFloat(mPointView1,"y",values);
        mAnimator1.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator1.setRepeatCount(Animation.INFINITE);
        mAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator1.setDuration(500);
        mAnimator1.start();

        mAnimator2 = ObjectAnimator.ofFloat(mPointView2,"y",values);
        mAnimator2.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator2.setRepeatCount(Animation.INFINITE);
        mAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator2.setDuration(500);
        mAnimator2.start();

        mAnimator3 = ObjectAnimator.ofFloat(mPointView3,"y",values);
        mAnimator3.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator3.setRepeatCount(Animation.INFINITE);
        mAnimator3.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator3.setDuration(500);
        mAnimator3.start();
    }

    @Override
    public void refreshComplete() {
        mAnimator1.cancel();
        mAnimator2.cancel();
        mAnimator3.cancel();
        mAnimator1 = null;
        mAnimator2 = null;
        mAnimator3 = null;
        mPointView1.setX(mRawX);
        mPointView3.setX(mRawX);
    }

    @Override
    public void getPercentRage(float rate) {
        mPointView1.setScaleX(rate);
        mPointView1.setScaleY(rate);
        mPointView1.setAlpha(rate);
        mPointView1.setX(mRawX + rate * -100);

        mPointView2.setScaleX(rate);
        mPointView2.setScaleY(rate);
        mPointView2.setAlpha(rate);

        mPointView3.setScaleX(rate);
        mPointView3.setScaleY(rate);
        mPointView3.setAlpha(rate);
        mPointView3.setX(mRawX + rate * 100);

    }

    @Override
    public View getRealView() {
        return this;
    }
}
