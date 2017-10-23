package com.rzc.stickyrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by rzc on 17/9/28.
 */

class HeaderFooterHolder extends RecyclerView.ViewHolder {
    public ViewGroup container;

    public HeaderFooterHolder(View itemView) {
        super(itemView);
        container = (ViewGroup) itemView;
    }
}
