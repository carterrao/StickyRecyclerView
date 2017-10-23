package com.rzc.stickyrecyclerview.lib;

import android.view.View;

/**
 * Created by rzc on 17/9/28.
 */

public class BaseChildHolder extends BaseHolder {

    public BaseChildHolder(View itemView) {
        super(itemView);
    }

    public Object getChildData() {
        return holderData != null ? holderData.childData : null;
    }

}