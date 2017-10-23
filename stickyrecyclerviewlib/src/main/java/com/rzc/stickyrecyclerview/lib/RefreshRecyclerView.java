package com.rzc.stickyrecyclerview.lib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;


/**
 * Created by rzc on 17/10/16.
 */

class RefreshRecyclerView extends RecyclerView {
    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back

    // the interface to trigger refresh and load more.
    private RefreshLoadMoreListener mRefreshLoadMoreListener;

    // -- header view
    private RefreshRecyclerViewRefreshView mRefreshView;
    // header view content, use it to calculate the Header's height. And disable it
    // when disable pull refresh.
    private RelativeLayout mRefreshViewContent;
    private TextView mRefreshTimeView;
    private int mRefreshViewHeight; // header view's height
    private boolean mEnablePullRefresh = false;
    private boolean mPullRefreshing = false; // is refreashing.

    // -- footer viewo
    private RefreshRecyclerViewLoadMoreView mLoadMoreView;
    private boolean mEnablePullLoad;
    private boolean mPullLoading;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    // at bottom, trigger
    // load more.
    private final static float OFFSET_RADIO = 1.8f;

    private float loadMoreDelta;

    RefreshRecyclerViewRefreshHolder refreshHolder;
    RefreshRecyclerViewLoadMoreHolder loadMoreHolder;

    public RefreshRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        loadMoreDelta = getResources().getDisplayMetrics().density * 12;

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mLoadMoreView.canLoadMore() && isLastItemLoadMore()) {
                    if (mEnablePullLoad && !mPullLoading && footerShouldShowLoadMore()) {
                        startLoadMore();
                    }
                }
            }
        });

        setRefreshLoadMore(context);
    }

    private void setRefreshLoadMore(Context context) {
        mRefreshView = new RefreshRecyclerViewRefreshView(context);
        refreshHolder = new RefreshRecyclerViewRefreshHolder(mRefreshView);
        mLoadMoreView = new RefreshRecyclerViewLoadMoreView(context);
        loadMoreHolder = new RefreshRecyclerViewLoadMoreHolder(mLoadMoreView);

        mLoadMoreView.disable();

        mScroller = new Scroller(getContext(), new DecelerateInterpolator());

        // 设置属性，适配魅族手机对listview效果的修改，防止listview下拉无法弹回的问题
//        this.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mRefreshViewContent = (RelativeLayout) mRefreshView
                .findViewById(R.id.xlistview_header_content);
        mRefreshViewContent.setVisibility(View.INVISIBLE);
        mRefreshTimeView = (TextView) mRefreshView
                .findViewById(R.id.xlistview_header_time);

        mRefreshView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mRefreshViewHeight = mRefreshViewContent.getHeight();
                        getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                    }
                });
    }

    /**
     * enable or disable pull down refresh feature.
     *
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, disable the content
            mRefreshViewContent.setVisibility(View.INVISIBLE);
        } else {
            mRefreshViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (!mEnablePullLoad) {
            mLoadMoreView.disable();
        } else {
            mPullLoading = false;
            mLoadMoreView.enable();
        }
    }

    public void loadMoreCurrOver() {
        mPullLoading = false;

        mLoadMoreView.post(new Runnable() {
            @Override
            public void run() {
                if (isLastItemLoadMore() && mLoadMoreView.canLoadMore()) {
                    smoothScrollBy(0, mLoadMoreView.getTop() - RefreshRecyclerView.this.getHeight());
                }
            }
        });
    }

    public void loadMoreResetState() {
        mPullLoading = false;
        mLoadMoreView.setState(RefreshRecyclerViewLoadMoreView.STATE_NORMAL);
    }

    public void loadMoreNoMoreState() {
        mLoadMoreView.setState(RefreshRecyclerViewLoadMoreView.STATE_NO_MORE);
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mRefreshTimeView.setText(time);
    }

    private void updateHeaderHeight(float delta) {
        mRefreshView.setVisiableHeight((int) delta
                + mRefreshView.getVisiableHeight());
        if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
            if (mRefreshView.getVisiableHeight() > mRefreshViewHeight) {
                mRefreshView.setState(RefreshRecyclerViewRefreshView.STATE_READY);
            } else {
                mRefreshView.setState(RefreshRecyclerViewRefreshView.STATE_NORMAL);
            }
        }
        scrollTo(0, 0);//setSelection(0); // scroll to top each time
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        int height = mRefreshView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mRefreshViewHeight) {
            return;
        }
        int finalHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to enable all the header.
        if (mPullRefreshing && height > mRefreshViewHeight) {
            finalHeight = mRefreshViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;
        mScroller.startScroll(0, height, 0, finalHeight - height,
                SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
    }

    private void startLoadMore() {
        mPullLoading = true;
        if (mRefreshLoadMoreListener != null) {
            mRefreshLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mEnablePullRefresh) {
            if (mLastY == -1) {
                mLastY = ev.getRawY();
            }

            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float deltaY = ev.getRawY() - mLastY;
                    mLastY = ev.getRawY();
                    if (isFirstItemRefresh()
                            && (mRefreshView.getVisiableHeight() > 0 || deltaY > 0)) {
                        // the first item is showing, header has shown or pull down.
                        updateHeaderHeight(deltaY / OFFSET_RADIO);
                    }
                    break;
                default:
                    mLastY = -1; // reset
                    if (isFirstItemRefresh()) {
                        // invoke refresh
                        if (mEnablePullRefresh
                                && mRefreshView.getVisiableHeight() > mRefreshViewHeight) {
                            if (mRefreshLoadMoreListener != null && !mPullRefreshing) {
                                mRefreshLoadMoreListener.onRefresh();
                            }
                            mPullRefreshing = true;
                            mRefreshView.setState(RefreshRecyclerViewRefreshView.STATE_REFRESHING);

                        }
                        resetHeaderHeight();
                    }
                    break;
            }
        }

        try {
            return super.onTouchEvent(ev);
        } catch (IndexOutOfBoundsException e) {
            //error!!    return true from here,not receive any following events any more
            return true;
        }
    }

    private boolean footerShouldShowLoadMore() {
        if (getHeight() - mLoadMoreView.getTop() > loadMoreDelta) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mRefreshView.setVisiableHeight(mScroller.getCurrY());
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    private boolean isFirstItemRefresh() {
        ViewHolder holder = getChildViewHolder(getChildAt(0));
        return holder instanceof RefreshRecyclerViewRefreshHolder;
    }

    private boolean isLastItemLoadMore() {
        ViewHolder holder = getChildViewHolder(getChildAt(getChildCount() - 1));
        return holder instanceof RefreshRecyclerViewLoadMoreHolder;
    }

    public void setRefreshLoadMoreListener(RefreshLoadMoreListener listener) {
        mRefreshLoadMoreListener = listener;
    }
}
