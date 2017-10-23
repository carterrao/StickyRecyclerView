package com.rzc.stickyrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by rzc on 17/9/28.
 */

class StickyRecyclerViewUtil {
    static HolderData getHolderDataByHolderView(RecyclerView recyclerView, View holderView) {
        HolderData data = null;
        if (holderView != null) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(holderView);
            if (holder instanceof BaseHolder) {
                data = ((BaseHolder) holder).holderData;
            }
        }
        return data;
    }
}
