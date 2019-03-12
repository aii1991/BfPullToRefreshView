package com.boildcoffee.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author zjh
 *         2019/3/11
 */

public class BfLoadMoreView extends LinearLayout implements ILoadMoreView{
    TextView mTv;
    ProgressBar mPb;

    public BfLoadMoreView(Context context) {
        super(context);
        init(context);
    }

    public BfLoadMoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BfLoadMoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mPb = new ProgressBar(context);
        mPb.setIndeterminate(true);
        int pbWidthAndHeight = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,displayMetrics);
        LayoutParams pbLayoutParams = new LayoutParams(pbWidthAndHeight,pbWidthAndHeight);
        pbLayoutParams.leftMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,displayMetrics);
        mPb.setLayoutParams(pbLayoutParams);
        mPb.setVisibility(GONE);
        addView(mPb);

        mTv = new TextView(context);
        mTv.setTextColor(Color.BLACK);
        mTv.setTextSize(18);
        LayoutParams tvLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pbLayoutParams.leftMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,displayMetrics);
        mTv.setLayoutParams(tvLayoutParams);
        addView(mTv);
    }

    @Override
    public void preToLoadMore() {
        mPb.setVisibility(GONE);
        mTv.setText("加载更多");
    }

    @Override
    public void releaseToLoadMore() {

    }

    @Override
    public void loading() {
        mPb.setVisibility(VISIBLE);
        mTv.setText("正在加载中...");
    }

    @Override
    public void loadComplete() {

    }

    @Override
    public void getPercentage(float rate) {

    }

    @Override
    public View getRealView() {
        return this;
    }
}
