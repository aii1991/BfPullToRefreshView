package com.boildcoffee.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author zjh
 *         2019/3/7
 */

public class BfPullToRefreshView extends BasePullToRefreshView<View>{
    public BfPullToRefreshView(Context context) {
        super(context);
    }

    public BfPullToRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BfPullToRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        BfRefreshView bfRefreshView = new BfRefreshView(getContext());
        bfRefreshView.setBackgroundColor(Color.BLUE);
        bfRefreshView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics())));
        setRefreshView(bfRefreshView);

        BfLoadMoreView loadMoreView = new BfLoadMoreView(getContext());
        loadMoreView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,60,getResources().getDisplayMetrics())));
        setLoadMoreView(loadMoreView);
    }

    @Override
    protected View createContentView() {
        return getChildAt(0);
    }

}
