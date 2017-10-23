package com.rzc.stickyrecyclerview.lib;

/**
 * Created by rzc on 17/9/28.
 */

class HolderData<GD, CD> {
    int groupIndex;
    int childIndex;
    int holderType;
    GD groupData;
    CD childData;

    public HolderData(GD groupData, CD childData, int groupIndex, int childIndex, int holderType) {
        this.groupIndex = groupIndex;
        this.childIndex = childIndex;
        this.holderType = holderType;
        this.groupData = groupData;
        this.childData = childData;
    }
}
