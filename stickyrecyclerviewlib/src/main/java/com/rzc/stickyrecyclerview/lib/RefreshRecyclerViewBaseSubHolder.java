package com.rzc.stickyrecyclerview.lib;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by rzc on 17/10/18.
 */

public class RefreshRecyclerViewBaseSubHolder extends RecyclerView.ViewHolder {
    int subPosition;
    RefreshRecyclerViewBaseSubAdapter subAdapter;

    public RefreshRecyclerViewBaseSubHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subAdapter.onItemClick(subPosition, getItemViewType());
            }
        });
    }

    public int getSubPosition() {
        return subPosition;
    }

}
