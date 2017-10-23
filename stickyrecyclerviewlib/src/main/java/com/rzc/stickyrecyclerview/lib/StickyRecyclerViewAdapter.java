package com.rzc.stickyrecyclerview.lib;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;
import java.util.Map;

/**
 * Created by rzc on 17/9/28.
 */

public abstract class StickyRecyclerViewAdapter<GH extends BaseGroupHolder, CH extends BaseChildHolder, GD, CD> {
    public abstract GH newGroupHolder();

    public CH newChildHolder() {
        return null;
    }

    public abstract void bindGroupHolder(GH holder, GD data, int groupIndex, boolean expand);

    public void bindChildHolder(CH holder, CD data, int groupIndex, int childIndex) {
    }

    public void onGroupClick(View view, GD data, int groupIndex, boolean expand) {
    }

    public void onChildChick(View view, CD data, int groupIndex, int childIndex) {
    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    public int getOffset() {
        return 0;
    }


    boolean stickyGroup;
    public StickyRecyclerView mStickyRecyclerView;
    private View layoutStickyGroup;
    private BaseGroupHolder stickyGroupHolder;
    private InnerAdapter innerAdapter;

    public StickyRecyclerViewAdapter(StickyRecyclerView stickyRecyclerView) {
        this(stickyRecyclerView, false);
    }

    public int getHeaderTop() {
        return innerAdapter.getHeaderTop();
    }

    public int getFirstVisibleViewHolderType() {
        RecyclerView.ViewHolder holder = mStickyRecyclerView.mRecyclerView.getChildViewHolder(
                mStickyRecyclerView.mRecyclerView.getChildAt(0));
        if (holder instanceof RefreshRecyclerViewRefreshHolder) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_REFRESH;
        } else if (holder instanceof RefreshRecyclerViewLoadMoreHolder) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_LOAD_MORE;
        } else if (holder instanceof BaseGroupHolder) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_GROUP;
        } else if (holder instanceof BaseChildHolder) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_CHILD;
        } else if (holder == innerAdapter.headerHolder) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_HEADER;
        } else if (holder == innerAdapter.footerHolder) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_FOOTER;
        }

        return holder.getItemViewType();
    }

    public StickyRecyclerViewAdapter(StickyRecyclerView stickyRecyclerView, boolean stickyGroup) {
        this.mStickyRecyclerView = stickyRecyclerView;
        this.stickyGroup = stickyGroup;
        innerAdapter = new InnerAdapter(stickyRecyclerView.getContext(), stickyRecyclerView.mRecyclerView, this);

        if (stickyGroup) {
            stickyGroupHolder = newGroupHolder();
            layoutStickyGroup = stickyGroupHolder.itemView;
            layoutStickyGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    innerAdapter.onStickyGroupClick(stickyGroupHolder.holderData.groupIndex, layoutStickyGroup);
                }
            });
        }
    }

    GridLayoutManager mGridLayoutManager;
    int mSpanCount = 1;
    void setAdapter() {
        innerAdapter.setHeader(mStickyRecyclerView.headerView);
        innerAdapter.setFooter(mStickyRecyclerView.footerView);

        if (stickyGroup) {
            mStickyRecyclerView.addStickyGroupView(layoutStickyGroup);
        }

        mStickyRecyclerView.mRecyclerView.setAdapter(innerAdapter);

        mGridLayoutManager = new GridLayoutManager(mStickyRecyclerView.getContext(), mSpanCount);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int groupChildCount = innerAdapter.getGroupAndChildCount();
                int subAdapterCount = innerAdapter.getSubAdapterAllItemCount();
                int allCount = groupChildCount + subAdapterCount
                        + 1 + 1 + 1 + 1;//加上refresh、loadMore、header和footer的个数

                if (position == 0 || position == 1 || position == allCount - 2 || position == allCount - 1) {
                    return mSpanCount;
                } else if (position >= groupChildCount + 1 + 1) {//加上refresh、header
                    int pos = groupChildCount + 1 + 1;
                    for (int i = 0; i < innerAdapter.subAdapterList.size(); i++) {
                        RefreshRecyclerViewBaseSubAdapter subAdapter = (RefreshRecyclerViewBaseSubAdapter) innerAdapter.subAdapterList.get(i);
                        int subCount = subAdapter.getItemCount();
                        int subPosition = position - pos;
                        if (subPosition < subCount) {
                            return (int) (mSpanCount / subAdapter.getColumnCount()
                                    * subAdapter.getColumnWeight(subPosition));
                        } else {
                            pos += subCount;
                        }
                    }
                }

                return mSpanCount;
            }
        });
        mStickyRecyclerView.mRecyclerView.setLayoutManager(mGridLayoutManager);
    }

    public void setData(List<GD> groupDataList,
                                 Map<GD, List<CD>> groupDataListMap) {
        setData(groupDataList, null, groupDataListMap);
    }

    public void setData(List<GD> groupDataList, boolean[] expandState,
                                 Map<GD, List<CD>> groupDataListMap) {
        innerAdapter.setData(groupDataList, expandState, groupDataListMap);
    }

    void onStickyGroupScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (!stickyGroup) {
            return;
        }

        int offset = getOffset();

        HolderData topHolderData = StickyRecyclerViewUtil.getHolderDataByHolderView(recyclerView,
                recyclerView.findChildViewUnder(0, offset + 1));
        if (topHolderData != null && innerAdapter.isGroupExpand(topHolderData.groupIndex)) {
            if (stickyGroupHolder.holderData == null
                    || topHolderData.groupData != stickyGroupHolder.holderData.groupData) {
                innerAdapter.onBindGroupHolder(stickyGroupHolder, topHolderData.groupIndex);
            }

            layoutStickyGroup.setVisibility(View.VISIBLE);

            View lastChildView = null;
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View tmp = recyclerView.getChildAt(i);
                HolderData tmpHolderData = StickyRecyclerViewUtil.getHolderDataByHolderView(recyclerView, tmp);
                int childCount = innerAdapter.getRealChildCount(topHolderData.groupIndex);
                if (tmpHolderData != null && childCount > 0 && tmpHolderData.groupIndex == topHolderData.groupIndex && tmpHolderData.childIndex == childCount - 1) {
                    lastChildView = tmp;
                    break;
                }
            }
            if (lastChildView != null) {
                int deltaY = lastChildView.getTop() + lastChildView.getHeight() - offset - layoutStickyGroup.getMeasuredHeight();
                if (deltaY < 0) {
                    layoutStickyGroup.setTranslationY(deltaY + offset);
                } else {
                    layoutStickyGroup.setTranslationY(offset);
                }
            } else {
                layoutStickyGroup.setTranslationY(offset);
            }

        } else {
            layoutStickyGroup.setVisibility(View.GONE);
        }
    }

    /**
     * 只有当某个组下的子数据发生变化时才能调用；如果是组的数据变化，请不要调用，应该用setData
     *
     * @param resetExpandSate 是否重置每个组的展开状态
     */
    public void notifyChildDataChanged(boolean resetExpandSate) {
        innerAdapter.notifyChildDataChanged(resetExpandSate);
    }

    /**
     * 只有当某个组下的子数据发生变化时才能调用；如果是组的数据变化，请不要调用，应该用setData
     */
    public void notifyChildDataChanged() {
        notifyChildDataChanged(false);
    }

    public List<GD> getGroupList() {
        return innerAdapter.groupDataList;
    }

    public Map<GD, List<CD>> getGroupMap() {
        return innerAdapter.groupDataListMap;
    }

    public List<CD> getChildList(int groupIndex) {
        List<GD> groupList = getGroupList();
        Map<GD, List<CD>> groupMap = getGroupMap();
        if (groupMap != null && groupList != null && groupIndex >= 0 && groupIndex < groupList.size()) {
            return groupMap.get(groupList.get(groupIndex));
        }
        return null;
    }

    public void setChildList(int groupIndex, List<CD> childList) {
        List<GD> groupList = getGroupList();
        Map<GD, List<CD>> groupMap = getGroupMap();
        if (groupMap != null && groupList != null && groupIndex >= 0 && groupIndex < groupList.size()) {
            groupMap.put(groupList.get(groupIndex), childList);
        }
    }

    public void addSubAdapter(RefreshRecyclerViewBaseSubAdapter adapter) {
        innerAdapter.addSubAdapter(adapter);
    }
}
