package com.rzc.stickyrecyclerview.lib;

import android.view.ViewGroup;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rzc on 17/10/17.
 */

public abstract class RefreshRecyclerViewBaseSubAdapter<VH extends RefreshRecyclerViewBaseSubHolder> {
    private InnerAdapter mInnerAdapter;
    Set<Integer> viewTypeSet = new HashSet<Integer>();

    public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindViewHolder(VH holder, int position);

    /**
     * 返回值必须大于等于StickyRecyclerViewHolderType.HOLDER_TYPE_SUB_BASE，
     * 并且所有的subAdapter返回值不能相同
     * @param position
     * @return
     */
    public abstract int getItemViewType(int position);

    public abstract int getItemCount();

    public abstract void onItemClick(int position, int itemViewType);

    public void notifyDataSetChanged() {
        mInnerAdapter.notifyDataSetChanged();
    };

    public int getColumnCount() {
        return 1;
    }

    public float getColumnWeight(int position) {
        return 1;
    }

    void setRefreshRecyclerViewAdapter(InnerAdapter innerAdapter) {
        mInnerAdapter = innerAdapter;
    }
}
