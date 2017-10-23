package com.rzc.stickyrecyclerview.lib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by rzc on 17/9/7.
 */

class InnerAdapter<GD, CD, GH extends BaseGroupHolder, CH extends BaseChildHolder> extends RecyclerView.Adapter implements View.OnClickListener {
    private static final int FIRST_GROUP_POSITION = 2;//refreshView位置0，header的位置是1，所以group的第一个位置为2

    private RefreshRecyclerView recyclerView;
    List<GD> groupDataList;
    Map<GD, List<CD>> groupDataListMap;

    private GroupState groupStateArr[];

    HeaderFooterHolder headerHolder;
    HeaderFooterHolder footerHolder;

    private StickyRecyclerViewAdapter stickyRecyclerViewAdapter;

    List<RefreshRecyclerViewBaseSubAdapter> subAdapterList = new ArrayList<RefreshRecyclerViewBaseSubAdapter>();

    public InnerAdapter(Context context, RefreshRecyclerView recyclerView, StickyRecyclerViewAdapter stickyRecyclerViewAdapter) {
        this.stickyRecyclerViewAdapter = stickyRecyclerViewAdapter;
        this.recyclerView = recyclerView;

        headerHolder = new HeaderFooterHolder(new FrameLayout(context));
        footerHolder = new HeaderFooterHolder(new FrameLayout(context));
    }

    void setData(List<GD> groupDataList, boolean[] expandState,
                 Map<GD, List<CD>> groupDataListMap) {
        this.groupDataList = groupDataList;
        if (groupDataList != null) {
            groupStateArr = new GroupState[groupDataList.size()];
            this.groupDataListMap = groupDataListMap;
            for (int i = 0; i < groupDataList.size(); i++) {
                GroupState groupState = new GroupState();
                groupState.index = i;
                if (groupDataListMap != null) {
                    groupState.childCount = groupDataListMap.get(groupDataList.get(i)).size();
                }
                if (expandState != null && expandState.length == groupDataList.size()) {
                    groupState.expand = expandState[i];
                }
                groupStateArr[i] = groupState;
            }
        }
        notifyDataSetChanged();
    }

    void notifyChildDataChanged(boolean resetExpandSate) {
        if (groupDataListMap != null && groupDataList != null && groupStateArr != null
                && groupDataList.size() == groupStateArr.length) {
            for (int i = 0; i < groupDataList.size(); i++) {
                GroupState groupState = groupStateArr[i];
                groupState.childCount = groupDataListMap.get(groupDataList.get(i)).size();
                if (resetExpandSate) {
                    groupState.expand = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    int getHeaderTop() {
        return headerHolder.itemView.getTop();
    }

    public void setHeader(View header) {
        if (header != null) {
            ViewParent parent = header.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(header);
            }
            headerHolder.container.removeAllViews();
            headerHolder.container.addView(header);
        }
    }

    public void setFooter(View footer) {
        if (footer != null) {
            ViewParent parent = footer.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(footer);
            }
            footerHolder.container.removeAllViews();
            footerHolder.container.addView(footer);
        }
    }

    private int getGroupCount() {
        return groupStateArr != null ? groupStateArr.length : 0;
    }

    private int getChildCount(int groupIndex) {
        if (groupStateArr != null && groupIndex >= 0 && groupIndex < groupStateArr.length) {
            if (groupStateArr[groupIndex].expand) {
                return groupStateArr[groupIndex].childCount;
            } else {
                return 0;
            }
        }
        return 0;
    }

    int getRealChildCount(int groupIndex) {
        if (groupStateArr != null && groupIndex >= 0 && groupIndex < groupStateArr.length) {
            return groupStateArr[groupIndex].childCount;
        }
        return 0;
    }

    private int getGroupIndex(int position) {
        if (groupStateArr != null) {
            int pos = FIRST_GROUP_POSITION;
            for (int i = 0; i < groupStateArr.length; i++) {
                int childCount = getChildCount(i);
                if (position <= pos + childCount) {
                    return i;
                }
                pos += childCount + 1;
            }
        }
        return position;
    }

    private int[] getGroupChildIndex(int position) {
        int[] groupChild = new int[2];
        int pos = FIRST_GROUP_POSITION;
        for (int i = 0; i < groupStateArr.length; i++) {
            int childCount = getChildCount(i);
            if (position <= pos + childCount) {
                groupChild[0] = i;
                groupChild[1] = position - pos - 1;
                break;
            } else {
                pos += childCount + 1;
            }
        }
        return groupChild;
    }

    public boolean isGroupExpand(int groupIndex) {
        return groupStateArr[groupIndex].expand;
    }

    public void expandGroup(int groupIndex) {
        groupStateArr[groupIndex].expand = true;
        notifyDataSetChanged();
    }

    public void collapseGroup(int groupIndex) {
        groupStateArr[groupIndex].expand = false;
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        HolderData holderData = StickyRecyclerViewUtil.getHolderDataByHolderView(recyclerView, view);
        if (holderData != null) {
            if (holderData.holderType == StickyRecyclerViewHolderType.HOLDER_TYPE_GROUP) {
                stickyRecyclerViewAdapter.onGroupClick(view, holderData.groupData,
                        holderData.groupIndex, isGroupExpand(holderData.groupIndex));

                if (groupDataListMap != null) {
                    int position = recyclerView.getChildAdapterPosition(view);
                    int groupIndex = getGroupIndex(position);
                    if (isGroupExpand(groupIndex)) {
                        collapseGroup(groupIndex);
                    } else {
                        expandGroup(groupIndex);
                        if (stickyRecyclerViewAdapter.stickyGroup) {
                            int dy = view.getTop();
                            if (dy == 0) {
                                dy = (int) view.getTranslationY();
                            }
                            if (dy != 0) {
                                recyclerView.smoothScrollBy(0, dy - stickyRecyclerViewAdapter.getOffset());
                            }
                        }
                    }
                }
            } else if (holderData.holderType == StickyRecyclerViewHolderType.HOLDER_TYPE_CHILD) {
                stickyRecyclerViewAdapter.onChildChick(view, holderData.childData,
                        holderData.groupIndex, holderData.childIndex);
            }
        }
    }

    void onStickyGroupClick(int groupIndex, final View view) {
        boolean expand = isGroupExpand(groupIndex);
        stickyRecyclerViewAdapter.onGroupClick(view, groupDataList.get(groupIndex), groupIndex, expand);

        if (expand) {
            collapseGroup(groupIndex);
            recyclerView.smoothScrollBy(0, 1);
            recyclerView.smoothScrollBy(0, -1);
        } else {
            expandGroup(groupIndex);
            int dy = view.getTop();
            if (dy == 0) {
                dy = (int) view.getTranslationY();
            }
            if (dy != 0) {
                recyclerView.smoothScrollBy(0, dy - stickyRecyclerViewAdapter.getOffset());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        int groupChildCount = getGroupAndChildCount();
        int subAdapterCount = getSubAdapterAllItemCount();
        int allCount = groupChildCount + subAdapterCount
                + 1 + 1 + 1 + 1;//加上refresh、loadMore、header和footer的个数

        if (position == 0) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_REFRESH;
        } else if (position == 1) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_HEADER;
        } else if (position == allCount - 2) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_FOOTER;
        } else if (position == allCount - 1) {
            return StickyRecyclerViewHolderType.HOLDER_TYPE_LOAD_MORE;
        } else if (position >= groupChildCount + 1 + 1) {//加上refresh、header
            int pos = groupChildCount + 1 + 1;
            for (int i = 0; i < subAdapterList.size(); i++) {
                RefreshRecyclerViewBaseSubAdapter subAdapter = subAdapterList.get(i);
                int subCount = subAdapter.getItemCount();
                if (position - pos < subCount) {
                    int viewType = subAdapter.getItemViewType(position - pos);
                    subAdapter.viewTypeSet.add(viewType);
                    return viewType;
                } else {
                    pos += subCount;
                }
            }
        }
        int pos = FIRST_GROUP_POSITION;
        for (int i = 0; i < groupStateArr.length; i++) {
            if (pos == position) {
                return StickyRecyclerViewHolderType.HOLDER_TYPE_GROUP;
            } else if (pos > position) {
                break;
            }
            pos += getChildCount(i) + 1;
        }
        return StickyRecyclerViewHolderType.HOLDER_TYPE_CHILD;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == StickyRecyclerViewHolderType.HOLDER_TYPE_REFRESH) {
            return recyclerView.refreshHolder;
        } else if (viewType == StickyRecyclerViewHolderType.HOLDER_TYPE_HEADER) {
            return headerHolder;
        } else if (viewType == StickyRecyclerViewHolderType.HOLDER_TYPE_FOOTER) {
            return footerHolder;
        } else if (viewType == StickyRecyclerViewHolderType.HOLDER_TYPE_LOAD_MORE) {
            return recyclerView.loadMoreHolder;
        } else if (viewType == StickyRecyclerViewHolderType.HOLDER_TYPE_GROUP) {
            BaseGroupHolder groupViewHolder = stickyRecyclerViewAdapter.newGroupHolder();
            return groupViewHolder;
        } else if (viewType == StickyRecyclerViewHolderType.HOLDER_TYPE_CHILD) {
            RecyclerView.ViewHolder holder = stickyRecyclerViewAdapter.newChildHolder();
            return holder;
        } else {
            for (RefreshRecyclerViewBaseSubAdapter subAdapter : subAdapterList) {
                if (subAdapter.viewTypeSet.contains(viewType)) {
                    return subAdapter.onCreateViewHolder(parent, viewType);
                }
            }
            return null;
        }
    }

    void onBindGroupHolder(GH holder, int groupIndex) {
        GD groupData = groupDataList.get(groupIndex);
        holder.holderData = new HolderData(groupData, null, groupIndex, -1,
                StickyRecyclerViewHolderType.HOLDER_TYPE_GROUP);
        stickyRecyclerViewAdapter.bindGroupHolder(holder, groupData,
                groupIndex, isGroupExpand(groupIndex));
    }

    void onBindChildHolder(CH holder, int groupIndex, int childIndex) {
        GD groupData = groupDataList.get(groupIndex);
        CD childData = groupDataListMap.get(groupData).get(childIndex);
        holder.holderData = new HolderData(groupData, childData, groupIndex, childIndex,
                StickyRecyclerViewHolderType.HOLDER_TYPE_CHILD);
        stickyRecyclerViewAdapter.bindChildHolder((CH) holder, childData, groupIndex, childIndex);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaseGroupHolder) {
            final int groupIndex = getGroupIndex(position);
            onBindGroupHolder((GH) holder, groupIndex);

        } else if (holder instanceof BaseChildHolder) {
            int[] groupChild = getGroupChildIndex(position);
            onBindChildHolder((CH) holder, groupChild[0], groupChild[1]);
        }

        if (holder instanceof BaseGroupHolder || holder instanceof BaseChildHolder) {
            holder.itemView.setOnClickListener(this);
        }

        if (holder instanceof RefreshRecyclerViewBaseSubHolder) {
            int pos = getGroupAndChildCount() + 1 + 1;//subAdapter添加在组和child之后，另外算上refresh和header个1个
            for (int i = 0; i < subAdapterList.size(); i++) {
                RefreshRecyclerViewBaseSubAdapter subAdapter = subAdapterList.get(i);
                int subCount = subAdapter.getItemCount();
                if (position - pos < subCount) {
                    RefreshRecyclerViewBaseSubHolder subHolder = (RefreshRecyclerViewBaseSubHolder) holder;
                    subHolder.subAdapter = subAdapter;
                    subHolder.subPosition = position - pos;
                    subAdapter.onBindViewHolder(subHolder, position - pos);
                    return;
                } else {
                    pos += subCount;
                }
            }
        }
    }

    int getGroupAndChildCount() {
        int count = getGroupCount();
        if (groupStateArr != null) {
            for (int i = 0; i < groupStateArr.length; i++) {
                count += getChildCount(i);
            }
        }
        return count;
    }

    int getSubAdapterAllItemCount() {
        int count = 0;
        for (RefreshRecyclerViewBaseSubAdapter subAdapter : subAdapterList) {
            count += subAdapter.getItemCount();
        }
        return count;
    }

    @Override
    public int getItemCount() {
        int allCount = getGroupAndChildCount() + getSubAdapterAllItemCount()
                + 1 + 1 + 1 + 1;//加上refresh、loadMore、header和footer的个数
        return allCount;
    }

    void addSubAdapter(RefreshRecyclerViewBaseSubAdapter adapter) {
        if (!subAdapterList.contains(adapter)) {
            subAdapterList.add(adapter);
            adapter.setRefreshRecyclerViewAdapter(this);

            stickyRecyclerViewAdapter.mSpanCount = leastCommonMultiple(stickyRecyclerViewAdapter.mSpanCount, adapter.getColumnCount());
            stickyRecyclerViewAdapter.mGridLayoutManager.setSpanCount(stickyRecyclerViewAdapter.mSpanCount);
            notifyDataSetChanged();
        }
    }

    //最大公约数
    private static int greatestCommonDivisor(int a, int b) {
        if (b == 0) {
            return a;
        }
        return greatestCommonDivisor(b, a % b);
    }

    //最小公倍数
    private static int leastCommonMultiple(int a, int b) {
        return a * b / greatestCommonDivisor(a, b);
    }
}
