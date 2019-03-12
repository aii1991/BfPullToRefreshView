package com.boildcoffee.library.widget;

import android.view.View;

/**
 * @author zjh
 *         2019/3/6
 */

public interface IRefreshView {
    void releaseToRefresh();

    void refreshing();

    void refreshComplete();

    /**
     *  rate = 下拉距离/RealView.height
     * @param rate
     */
    void getPercentRage(float rate);

    /**
     * 获取真正的刷新view
     * @return
     */
    View getRealView();
}
