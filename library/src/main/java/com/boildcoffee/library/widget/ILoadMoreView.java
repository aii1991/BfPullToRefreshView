package com.boildcoffee.library.widget;

import android.view.View;

/**
 * @author zjh
 *         2019/3/6
 */

public interface ILoadMoreView {
    void preToLoadMore();

    void releaseToLoadMore();

    void loading();

    void loadComplete();

    /**
     *  rate = 下拉距离/RealView.height
     * @param rate
     */
    void getPercentage(float rate);

    /**
     * 获取真正的刷新view
     * @return
     */
    View getRealView();
}
