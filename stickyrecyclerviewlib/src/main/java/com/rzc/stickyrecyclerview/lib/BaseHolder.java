package com.rzc.stickyrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by rzc on 17/9/28.
 */

class BaseHolder extends RecyclerView.ViewHolder {
    HolderData holderData;

    public BaseHolder(View itemView) {
        super(itemView);
    }

    public Object getGroupData() {
        return holderData != null ? holderData.groupData : null;
    }

    public int getGroupIndex() {
        return holderData != null ? holderData.groupIndex : -1;
    }
}
