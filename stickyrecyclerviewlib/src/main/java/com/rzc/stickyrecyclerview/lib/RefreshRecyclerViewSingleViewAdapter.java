package com.rzc.stickyrecyclerview.lib;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rzc on 17/10/19.
 */

public class RefreshRecyclerViewSingleViewAdapter extends
        RefreshRecyclerViewBaseSubAdapter {
    private int itemViewType;
    private View contentView;

    public RefreshRecyclerViewSingleViewAdapter(int itemViewType, View contentView) {
        this.itemViewType = itemViewType;
        this.contentView = contentView;
    }

    @Override
    public RefreshRecyclerViewBaseSubHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RefreshRecyclerViewBaseSubHolder(contentView);
    }

    @Override
    public void onBindViewHolder(RefreshRecyclerViewBaseSubHolder holder, int position) {
    }

    @Override
    public int getItemViewType(int position) {
        return itemViewType;
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    @Override
    public void onItemClick(int position, int itemViewType) {

    }
}
