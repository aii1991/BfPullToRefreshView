package com.boildcoffee.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.boildcoffee.library.widget.listener.OnLoadMoreListener;
import com.boildcoffee.library.widget.listener.OnRefreshListener;

/**
 * @author zjh
 *         2019/3/7
 */

public abstract class BasePullToRefreshView<T extends View> extends LinearLayout {
    private static final String TAG = BasePullToRefreshView.class.getSimpleName();

    private final static float SCROLL_RATIO = 0.6f; //阻尼系数

    private final static int STATE_INIT = -1;
    private final static int STATE_PULL_UP = 0; //上拉状态
    private final static int STATE_PULL_DOWN = 1; //下拉状态

    private final static int STATE_PRE_TO_REFRESH = 2; //准备开始刷新状态
    private final static int STATE_RELEASE_TO_REFRESH = 3; //释放刷新
    private final static int STATE_REFRESHING = 4; //正在刷新中

    private final static int STATE_PRE_TO_LOAD_MORE = 5; //准备开始加载更多
    private final static int STATE_RELEASE_TO_LOAD_MORE = 6; //释放加载更多
    private final static int STATE_LOADING = 7; //正在加载更多

    protected T mContentView;
    private IRefreshView mRefreshView;
    private View mRealRefreshView;
    private View mRealLoadMoreView;
    private ILoadMoreView mLoadMoreView;
    private OnLoadMoreListener mLoadMoreListener;
    private OnRefreshListener mRefreshListener;

    private int mPullState = STATE_INIT;
    private int mRefreshState = STATE_INIT;
    private int mLoadMoreState = STATE_INIT;
    private boolean openLoadMore;
    private boolean openPullToRefresh;

    private int mLastY;

    private Scroller mScroller;



    public BasePullToRefreshView(Context context) {
        super(context);
        init();
    }

    public BasePullToRefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BasePullToRefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        setOrientation(VERTICAL);
    }

    /**
     * 设置刷新View
     * @param refreshView
     */
    public void setRefreshView(IRefreshView refreshView){
        mRefreshView = refreshView;
        mRealRefreshView = mRefreshView.getRealView();
        openPullToRefresh = true;
        initContentView();

        initLayoutParams(mRealRefreshView);

        addView(mRealRefreshView,0);
        post(new Runnable() {
            @Override
            public void run() {
               hideRefreshView(); //设置margin-top=-refreshView.height让其移除屏幕
            }
        });
    }

    /**
     * 设置加载更多View
     * @param loadMoreView
     */
    public void setLoadMoreView(ILoadMoreView loadMoreView){
        mLoadMoreView = loadMoreView;
        mRealLoadMoreView = loadMoreView.getRealView();
        openLoadMore = true;

        initContentView();

        initLayoutParams(mRealLoadMoreView);

        addView(mRealLoadMoreView);
    }

    /**
     * 设置刷新完成
     */
    public void setRefreshComplete(){
        resetState();
        resetScrollPosition();
        mRefreshView.refreshComplete();
    }

    /**
     * 设置加载完成
     */
    public void setLoadMoreComplete(){
        resetState();
        resetScrollPosition();
        mLoadMoreView.loadComplete();
    }

    public void setRefreshListener(OnRefreshListener refreshListener) {
        this.mRefreshListener = refreshListener;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.mLoadMoreListener = loadMoreListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mPullState == STATE_REFRESHING || mLoadMoreState == STATE_LOADING){
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = (int) (ev.getY() - mLastY);
                Log.d(TAG,"deltaY=>"+deltaY);
                if (deltaY >= -20 && deltaY <= 20){ //滚动距离过小，不拦截
                    return false;
                }
                setPullState(deltaY);
                if (isScrollToTopOrBottom()){
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = (int) (event.getY() - mLastY);
                deltaY = (int)(deltaY * SCROLL_RATIO);
                int scrollY = getScrollY();
                Log.d(TAG,"getY()="+getScrollY());
                if (openPullToRefresh && mPullState == STATE_PULL_DOWN){
                    prepareToRefresh(deltaY,scrollY,event); //准备刷新
                }
                if (openLoadMore && mPullState == STATE_PULL_UP){
                    prepareToLoadMore(deltaY,scrollY); //准备加载更多
                }
                mLastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if ((openPullToRefresh && mRefreshState == STATE_PRE_TO_REFRESH) || (openLoadMore && mLoadMoreState == STATE_PRE_TO_LOAD_MORE)){
                    //刷新View/加载更多View未完全显示出来
                    resetScrollPosition(); //复位
                }
                if (openPullToRefresh && mPullState == STATE_PULL_DOWN && mRefreshState == STATE_RELEASE_TO_REFRESH){
                    handleRefreshing(); // 处理正在刷新
                }
                if (openLoadMore && mPullState == STATE_PULL_UP && mLoadMoreState == STATE_RELEASE_TO_LOAD_MORE){
                    handleLoadMore(); // 处理正在加载更多
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void hideRefreshView() {
        if (mRefreshView == null || mRealRefreshView == null) return;
        ViewGroup.LayoutParams layoutParams = mRealRefreshView.getLayoutParams();
        Log.d(TAG,"realHeaderViewHeight="+mRealRefreshView.getHeight());
        ((LayoutParams)layoutParams).topMargin = -mRealRefreshView.getHeight();
        mRefreshView.getRealView().setLayoutParams(layoutParams);
    }

    private void prepareToRefresh(int deltaY,int scrollY,MotionEvent event) {
        if (scrollY > 0){ //scrollY>0证明刷新view出现了并且是在向上滑动
            mRefreshState = STATE_PRE_TO_REFRESH;
            mContentView.onTouchEvent(event);
            return;
        }else {
            scrollY = -scrollY;
        }
        final int height = mRealRefreshView.getHeight();
        if (scrollY >= height){
            Log.d(TAG,"STATE_RELEASE_TO_REFRESH");
            //刷新View完全出现
            mRefreshState = STATE_RELEASE_TO_REFRESH;
            mRefreshView.releaseToRefresh();
            mRefreshView.getPercentRage(1.0f);
        }else {
            mRefreshState = STATE_PRE_TO_REFRESH;
            float rate = scrollY / (height * 1.0f);
            mRefreshView.getPercentRage(rate);
        }
        scrollBy(0,-deltaY); //滚动内容
    }

    private void prepareToLoadMore(int deltaY,int scrollY) {
        if (scrollY < 0){ //scrollY>0证明当前是在像下滑动
            mLoadMoreState = STATE_PRE_TO_LOAD_MORE;
            return;
        }
        final int height = mRealLoadMoreView.getHeight();
        if (scrollY >= height){ //加载View完全出现
            Log.d(TAG,"STATE_RELEASE_TO_LOAD_MORE");
            mLoadMoreState = STATE_RELEASE_TO_LOAD_MORE;
            mLoadMoreView.releaseToLoadMore();
            mLoadMoreView.getPercentage(1.0f);
        }else {
            mLoadMoreState = STATE_PRE_TO_LOAD_MORE;
            float rate = scrollY / (height * 1.0f);
            mLoadMoreView.getPercentage(rate);
            mLoadMoreView.preToLoadMore();
        }
        scrollBy(0,-deltaY);
    }

    private void handleRefreshing() {
        Log.d(TAG,"refreshing");
        mRefreshState = STATE_REFRESHING;
        scrollToRefreshingPosition(); //使用scroller实现弹性回滚
        mRefreshView.refreshing();
        if (mRefreshListener != null){
            mRefreshListener.refreshListener(this);
        }
    }

    private void handleLoadMore() {
        Log.d(TAG,"Loading");
        mLoadMoreState = STATE_LOADING;
        scrollToLoadingPosition();
        mLoadMoreView.loading();
        if (mLoadMoreListener != null){
            mLoadMoreListener.loadMoreListener(this);
        }
    }

    /**
     * 复位
     */
    private void resetScrollPosition(){
        int sy = getScrollY();
        Log.d(TAG,"sy=>"+sy);
        mScroller.startScroll(0,sy,0,-sy,800);
        invalidate();
    }

    private void scrollToRefreshingPosition(){
        Log.d(TAG,"getScrollY()=>"+getScrollY()+",height=>"+(-mRealRefreshView.getHeight()));
        int sy = getScrollY();
        int dx = Math.abs(sy + mRealRefreshView.getHeight());
        mScroller.startScroll(0,sy,0,dx,800);
        invalidate();
    }

    private void scrollToLoadingPosition() {
        int sy = getScrollY();
        int dx = Math.abs(sy - mRealLoadMoreView.getHeight());
        mScroller.startScroll(0,sy,0,-dx,800);
        invalidate();
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
            invalidate();
        }
    }

    /**
     * 复位状态
     */
    private void resetState(){
        mPullState = STATE_INIT;
        mRefreshState = STATE_INIT;
        mLoadMoreState = STATE_INIT;
    }

    /**
     * 是否滚动到顶部或者底部
     * @return
     */
    private boolean isScrollToTopOrBottom() {
        if (mRefreshState == STATE_REFRESHING || mLoadMoreState == STATE_LOADING){
            return false;
        }

        if (mContentView instanceof AbsListView){
            return isScrollToTopOrBottom((AbsListView)mContentView);
        }
        if (mContentView instanceof RecyclerView){
            return isScrollToTopOrBottom((RecyclerView) mContentView);
        }
        if (mContentView instanceof ScrollView){
            ScrollView scrollView = (ScrollView) mContentView;
            return isScrollToTopOrBottom(scrollView);
        }
        return false;
    }

    private void setPullState(int deltaY) {
        if (deltaY > 0){
            mPullState = STATE_PULL_DOWN;
        }else if (deltaY < 0){
            mPullState = STATE_PULL_UP;
        }
    }

    private boolean isScrollToTopOrBottom(AbsListView absListView){
        if (mPullState == STATE_PULL_DOWN){
            View firstChild = absListView.getChildAt(0);
            return firstChild != null && firstChild.getTop() == 0 && absListView.getFirstVisiblePosition() == 0;
        }

        if (mPullState == STATE_PULL_UP){
            int lastPosition = absListView.getChildCount() - 1;
            View lastChild = absListView.getChildAt(lastPosition);

            return lastChild != null && lastChild.getBottom() == getHeight()
                    && absListView.getLastVisiblePosition() == absListView.getAdapter().getCount() - 1;
        }

        return false;
    }

    private boolean isScrollToTopOrBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = ((LinearLayoutManager)recyclerView.getLayoutManager());
        if (mPullState == STATE_PULL_DOWN){
            View firstChild = recyclerView.getChildAt(0);
            return (firstChild != null && firstChild.getTop() == 0 && layoutManager.findFirstVisibleItemPosition() == 0);
        }
        if (mPullState == STATE_PULL_UP){
            int lastPosition = recyclerView.getChildCount() - 1;
            Log.d(TAG,"lastPosition=>"+lastPosition);
            View lastChild = recyclerView.getChildAt(lastPosition);
            return lastChild != null && lastChild.getBottom() == getHeight()
                    && layoutManager.findLastVisibleItemPosition() == recyclerView.getAdapter().getItemCount() - 1;
        }
        return false;
    }

    private boolean isScrollToTopOrBottom(ScrollView scrollView){
        return scrollView.getScrollY() == 0 || scrollView.getScrollY() == getHeight();
    }

    private void initContentView() {
        if (mContentView == null) {
            synchronized (this) {
                if (mContentView == null) {
                    mContentView = createContentView();
                    mContentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        }
    }

    private void initLayoutParams(View view) {
        if (view.getLayoutParams() == null || !(view.getLayoutParams() instanceof LayoutParams)){
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }
    }

    protected abstract T createContentView();

}
