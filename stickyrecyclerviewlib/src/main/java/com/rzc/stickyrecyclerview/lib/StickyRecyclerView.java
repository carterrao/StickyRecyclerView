package com.rzc.stickyrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by rzc on 17/9/28.
 */

public class StickyRecyclerView extends FrameLayout {
    RefreshRecyclerView mRecyclerView;
    private View layoutStickyGroup;
    View headerView;
    View footerView;
    private StickyRecyclerViewAdapter stickyRecyclerViewAdapter;

    public StickyRecyclerView(Context context) {
        super(context);
        mRecyclerView = new RefreshRecyclerView(context);
        init();
    }

    public StickyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRecyclerView = new RefreshRecyclerView(context, attrs);
        init();
    }

    private void init() {
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(mRecyclerView, lp);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, int dx, int dy) {
                if (stickyRecyclerViewAdapter != null) {
                    stickyRecyclerViewAdapter.onScrolled(recyclerView, dx, dy);
                    stickyRecyclerViewAdapter.onStickyGroupScrolled(recyclerView, dx, dy);
                }
            }
        });
    }

    void addStickyGroupView(View layoutStickyGroup) {
        if (this.layoutStickyGroup != null) {
            removeView(this.layoutStickyGroup);
        }
        this.layoutStickyGroup = layoutStickyGroup;
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(layoutStickyGroup, lp);

        layoutStickyGroup.setVisibility(View.GONE);
    }

    public void setAdapter(StickyRecyclerViewAdapter stickyRecyclerViewAdapter) {
        this.stickyRecyclerViewAdapter = stickyRecyclerViewAdapter;
        stickyRecyclerViewAdapter.setAdapter();
    }

    public void setHeaderFooter(View headerView, View footerView) {
        this.headerView = headerView;
        this.footerView = footerView;
    }

    public void setPullRefreshEnable(boolean enable) {
        mRecyclerView.setPullRefreshEnable(enable);
    }

    /**
     * enable or disable pull up load more feature.
     *
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mRecyclerView.setPullLoadEnable(enable);
    }

    public void loadMoreCurrOver() {
        mRecyclerView.loadMoreCurrOver();
    }

    public void loadMoreResetState() {
        mRecyclerView.loadMoreResetState();
    }

    public void loadMoreNoMoreState() {
        mRecyclerView.loadMoreNoMoreState();
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        mRecyclerView.stopRefresh();
    }

    /**
     * set last refresh time
     *
     * @param time
     */
    public void setRefreshTime(String time) {
        mRecyclerView.setRefreshTime(time);
    }

    public void setRefreshLoadMoreListener(RefreshLoadMoreListener listener) {
        mRecyclerView.setRefreshLoadMoreListener(listener);
    }
}
