/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */

package com.rzc.stickyrecyclerview.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

class RefreshRecyclerViewLoadMoreView extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_NO_MORE = 1;

    private int mState = STATE_NORMAL;

    private View contentView;
    private View mProgressBar;
    private TextView mHintView;

    public RefreshRecyclerViewLoadMoreView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshRecyclerViewLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void setState(int state) {
        if (state == mState) {
            return;
        }
        mState = state;

        if (state == STATE_NORMAL) {
            mProgressBar.setVisibility(View.VISIBLE);
            mHintView.setText(R.string.refresh_recycler_view_loading);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mHintView.setText(R.string.refresh_recycler_view_footer_no_more);
        }
    }

    public boolean canLoadMore() {
        return mState == STATE_NORMAL;
    }

    public void disable() {
        contentView.setVisibility(View.GONE);
    }

    public void enable() {
        contentView.setVisibility(View.VISIBLE);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.refresh_recycler_view_load_more, this);

        contentView = findViewById(R.id.xlistview_footer_content);
        mProgressBar = findViewById(R.id.xlistview_footer_progressbar);
        mHintView = (TextView) findViewById(R.id.xlistview_footer_hint_textview);
    }

}
