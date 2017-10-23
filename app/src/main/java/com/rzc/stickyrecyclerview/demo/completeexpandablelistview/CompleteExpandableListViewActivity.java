package com.rzc.stickyrecyclerview.demo.completeexpandablelistview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rzc.stickyrecyclerview.R;
import com.rzc.stickyrecyclerview.demo.City;
import com.rzc.stickyrecyclerview.demo.ImageActivity;
import com.rzc.stickyrecyclerview.demo.MockData;
import com.rzc.stickyrecyclerview.demo.Province;
import com.rzc.stickyrecyclerview.demo.UIUtils;
import com.rzc.stickyrecyclerview.demo.WebActivity;
import com.rzc.stickyrecyclerview.lib.BaseChildHolder;
import com.rzc.stickyrecyclerview.lib.BaseGroupHolder;
import com.rzc.stickyrecyclerview.lib.RefreshLoadMoreListener;
import com.rzc.stickyrecyclerview.lib.RefreshRecyclerViewBaseSubAdapter;
import com.rzc.stickyrecyclerview.lib.RefreshRecyclerViewBaseSubHolder;
import com.rzc.stickyrecyclerview.lib.RefreshRecyclerViewSingleViewAdapter;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerView;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerViewAdapter;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerViewHolderType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompleteExpandableListViewActivity extends Activity implements View.OnClickListener {
    private ProgressDialog mProgressDialog;
    private StickyRecyclerView mStickyRecyclerView;
    private StickyRecyclerViewAdapter<ProvinceHolder, CityHolder, Province, City> twoLevelAdapter;
    private StickyRecyclerViewAdapter<ProvinceHolder, BaseChildHolder, Province, Object> oneLevelAdapter;

    private View headerView, footerView;
    private View tvTitle;
    private View layoutStickyDateCategory;
    private View layoutHeaderDateCategory;

    private View tabTwoLevel, tabOneLevel, tabTwoLevelBg, tabOneLevelBg;

    private boolean moreCityShowedArr[];

    static class ProvinceHolder extends BaseGroupHolder {
        ImageView ivPic;
        TextView tvName;
        TextView tvContent1;
        TextView tvContent2;

        public ProvinceHolder(final View itemView) {
            super(itemView);
            ivPic = itemView.findViewById(R.id.ivPic);
            ivPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object province = getGroupData();
                    if (province instanceof Province) {
                        String url = ((Province) province).picUrl;
                        ImageActivity.showPic(itemView.getContext(), url);
                    }
                }
            });
            tvName = itemView.findViewById(R.id.tvName);
            tvContent1 = itemView.findViewById(R.id.tvContent1);
            tvContent2 = itemView.findViewById(R.id.tvContent2);
        }
    }

    class CityHolder extends BaseChildHolder {
        TextView tvName;
        TextView tvContent1;
        TextView tvContent2;
        View layoutContent;
        View verLine;
        View layoutViewMore;

        public CityHolder(final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvContent1 = itemView.findViewById(R.id.tvContent1);
            tvContent2 = itemView.findViewById(R.id.tvContent2);
            layoutContent = itemView.findViewById(R.id.layoutContent);
            verLine = itemView.findViewById(R.id.verLine);
            layoutViewMore = itemView.findViewById(R.id.layoutViewMore);
            itemView.findViewById(R.id.btnView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Object city = getChildData();
                    if (city instanceof City) {
                        String url = "https://baike.baidu.com/item/" + ((City) city).name;
                        WebActivity.showWeb(itemView.getContext(), url);
                    }
                }
            });
            layoutViewMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mProgressDialog.show();
                    Object provinceData = getGroupData();
                    String province = provinceData instanceof Province ? ((Province)provinceData).name : "省错误";
                    MockData.getMoreCityList(province, new MockData.OnMoreCityResponse() {
                        @Override
                        public void onFailed() {
                            mProgressDialog.dismiss();
                            toast("数据加载失败");
                        }

                        @Override
                        public void onSuccess(List<City> moreCity) {
                            moreCityShowedArr[getGroupIndex()] = true;
                            mProgressDialog.dismiss();
                            twoLevelAdapter.getChildList(getGroupIndex()).addAll(moreCity);
                            twoLevelAdapter.notifyChildDataChanged();
                        }
                    }, true);
                }
            });
        }
    }

    private int loadMoreCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complete_expandable_list_view_activity);

        tvTitle = findViewById(R.id.tvTitle);
        layoutStickyDateCategory = findViewById(R.id.layoutStickyDateCategory);

        mStickyRecyclerView = findViewById(R.id.mStickyRecyclerView);
        mStickyRecyclerView.setPullRefreshEnable(true);
//        mStickyRecyclerView.setPullLoadEnable(true); //设置上拉加载更多，应该在有数据并且数据充满屏幕后设置
        mStickyRecyclerView.setRefreshLoadMoreListener(new RefreshLoadMoreListener() {
            @Override
            public void onRefresh() {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mStickyRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                mStickyRecyclerView.loadMoreResetState();
                                mStickyRecyclerView.stopRefresh();
                            }
                        });
                    }
                }.start();
            }

            @Override
            public void onLoadMore() {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        mStickyRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                mStickyRecyclerView.loadMoreCurrOver();

                                if (loadMoreCount++ > 2) {
                                    mStickyRecyclerView.loadMoreNoMoreState();
//                                    mRecyclerView.setPullLoadEnable(false);
                                }

                            }
                        });
                    }
                }.start();
            }
        });

        headerView = View.inflate(this, R.layout.complete_expandable_list_view_header, null);
        initTab(headerView);
        final ImageView headerPic = headerView.findViewById(R.id.ivPic);
        Glide.with(this).load("http://211.159.149.56:8080/map/8/4o28b0625501ad13015501ad2bfc0136.jpg")
                .placeholder(R.mipmap.ic_launcher).into(headerPic);
        layoutHeaderDateCategory = headerView.findViewById(R.id.layoutHeaderDateCategory);

        footerView = View.inflate(this, R.layout.complete_expandable_list_view_footer, null);
        ImageView footerPic = footerView.findViewById(R.id.ivPic);
        Glide.with(this).load("http://img.qqai.net/uploads/i_3_4246494178x537668949_21.jpg")
                .placeholder(R.mipmap.ic_launcher).into(footerPic);

        headerView.setVisibility(View.GONE);
        footerView.setVisibility(View.GONE);
        mStickyRecyclerView.setHeaderFooter(headerView, footerView);
        twoLevelAdapter = new StickyRecyclerViewAdapter<ProvinceHolder, CityHolder, Province, City>(mStickyRecyclerView, true) {
            @Override
            public ProvinceHolder newGroupHolder() {
                return new ProvinceHolder(View.inflate(CompleteExpandableListViewActivity.this, R.layout.complete_expandable_list_view_group_item, null));
            }

            @Override
            public void bindGroupHolder(ProvinceHolder holder, Province data, int groupIndex, boolean expand) {
                if (!TextUtils.isEmpty(data.picUrl)) {
                    Glide.with(CompleteExpandableListViewActivity.this).load(data.picUrl)
                            .placeholder(R.mipmap.ic_launcher).into(holder.ivPic);
                    holder.ivPic.setVisibility(View.VISIBLE);
                } else {
                    holder.ivPic.setVisibility(View.GONE);
                }
                holder.tvName.setText(data.name);

                if (TextUtils.isEmpty(data.content1)) {
                    holder.tvContent1.setVisibility(View.GONE);
                } else {
                    holder.tvContent1.setText(data.content1);
                    holder.tvContent1.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(data.content2)) {
                    holder.tvContent2.setVisibility(View.GONE);
                } else {
                    holder.tvContent2.setText(data.content2);
                    holder.tvContent2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public CityHolder newChildHolder() {
                return new CityHolder(View.inflate(CompleteExpandableListViewActivity.this, R.layout.complete_expandable_list_view_child_item, null));
            }

            @Override
            public void bindChildHolder(final CityHolder holder, City data, int groupIndex, int childIndex) {
                holder.tvName.setText(data.name);

                if (TextUtils.isEmpty(data.content1)) {
                    holder.tvContent1.setVisibility(View.GONE);
                } else {
                    holder.tvContent1.setText(data.content1);
                    holder.tvContent1.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(data.content2)) {
                    holder.tvContent2.setVisibility(View.GONE);
                } else {
                    holder.tvContent2.setText(data.content2);
                    holder.tvContent2.setVisibility(View.VISIBLE);
                }

                if (!moreCityShowedArr[groupIndex] && childIndex == getChildList(groupIndex).size() - 1) {
                    holder.layoutViewMore.setVisibility(View.VISIBLE);
                } else {
                    holder.layoutViewMore.setVisibility(View.GONE);
                }

                //动态调整竖向分隔线高度
                holder.layoutContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int newHeight = holder.layoutContent.getHeight();
                        ViewGroup.LayoutParams lp = holder.verLine.getLayoutParams();
                        if(lp.height != newHeight) {
                            lp.height = newHeight;
                            holder.verLine.setLayoutParams(lp);
                        }
                    }
                });
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int headerXY[] = new int[2];
                headerView.getLocationOnScreen(headerXY);
                boolean headerVisible = getFirstVisibleViewHolderType() < StickyRecyclerViewHolderType.HOLDER_TYPE_GROUP;
                if (!headerVisible || -(headerXY[1] - UIUtils.getStatusBarHeight(CompleteExpandableListViewActivity.this))
                        > UIUtils.dp2px(CompleteExpandableListViewActivity.this, 100)) {
                    tvTitle.setVisibility(View.VISIBLE);
                    int dateCategoryXY[] = new int[2];
                    layoutHeaderDateCategory.getLocationOnScreen(dateCategoryXY);
                    if (!headerVisible || dateCategoryXY[1] <= tvTitle.getHeight() + UIUtils.getStatusBarHeight(CompleteExpandableListViewActivity.this)) {
                        layoutStickyDateCategory.setVisibility(View.VISIBLE);
                    } else {
                        layoutStickyDateCategory.setVisibility(View.GONE);
                    }
                } else {
                    tvTitle.setVisibility(View.GONE);
                    layoutStickyDateCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public int getOffset() {
                int titleHeight = tvTitle.getMeasuredHeight();
                int dateHeight = layoutHeaderDateCategory.getMeasuredHeight();
                int offset = titleHeight + dateHeight;
                return offset;
            }
        };
        mStickyRecyclerView.setAdapter(twoLevelAdapter);

        //这里给出例子动态切换adapter为另一种风格，为了方便，这里还是复用的上面的ProvinceHolder及Province数据，
        //是可以改成别的任意的StickyRecyclerViewAdapter类型的
        oneLevelAdapter = new StickyRecyclerViewAdapter<ProvinceHolder, BaseChildHolder, Province, Object>(mStickyRecyclerView) {
            @Override
            public ProvinceHolder newGroupHolder() {
                return new ProvinceHolder(View.inflate(CompleteExpandableListViewActivity.this, R.layout.complete_expandable_list_view_group_item, null));
            }

            @Override
            public void bindGroupHolder(ProvinceHolder holder, Province data, int groupIndex, boolean expand) {
                if (!TextUtils.isEmpty(data.picUrl)) {
                    Glide.with(CompleteExpandableListViewActivity.this).load(data.picUrl)
                            .placeholder(R.mipmap.ic_launcher).into(holder.ivPic);
                    holder.ivPic.setVisibility(View.VISIBLE);
                } else {
                    holder.ivPic.setVisibility(View.GONE);
                }
                holder.tvName.setText(data.name);

                if (TextUtils.isEmpty(data.content1)) {
                    holder.tvContent1.setVisibility(View.GONE);
                } else {
                    holder.tvContent1.setText(data.content1);
                    holder.tvContent1.setVisibility(View.VISIBLE);
                }

                if (TextUtils.isEmpty(data.content2)) {
                    holder.tvContent2.setVisibility(View.GONE);
                } else {
                    holder.tvContent2.setText(data.content2);
                    holder.tvContent2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int headerXY[] = new int[2];
                headerView.getLocationOnScreen(headerXY);
                boolean headerVisible = getFirstVisibleViewHolderType() < StickyRecyclerViewHolderType.HOLDER_TYPE_GROUP;
                if (!headerVisible || -(headerXY[1] - UIUtils.getStatusBarHeight(CompleteExpandableListViewActivity.this))
                        > UIUtils.dp2px(CompleteExpandableListViewActivity.this, 100)) {
                    tvTitle.setVisibility(View.VISIBLE);
                    int dateCategoryXY[] = new int[2];
                    layoutHeaderDateCategory.getLocationOnScreen(dateCategoryXY);
                    if (!headerVisible || dateCategoryXY[1] <= tvTitle.getHeight() + UIUtils.getStatusBarHeight(CompleteExpandableListViewActivity.this)) {
                        layoutStickyDateCategory.setVisibility(View.VISIBLE);
                    } else {
                        layoutStickyDateCategory.setVisibility(View.GONE);
                    }
                } else {
                    tvTitle.setVisibility(View.GONE);
                    layoutStickyDateCategory.setVisibility(View.GONE);
                }
            }
        };

        mProgressDialog = ProgressDialog.show(this, "", "正在加载...");

        MockData.queryProvinceCityMap(new MockData.OnProvinceCityMapResponse() {
            @Override
            public void onFailed() {
                mProgressDialog.dismiss();
                toast("数据加载失败");
            }

            @Override
            public void onSuccess(List<Province> provinces, Map<Province, List<City>> map) {
                mProgressDialog.dismiss();

                mStickyRecyclerView.setPullLoadEnable(true);//设置上拉下载更多功能

                headerView.setVisibility(View.VISIBLE);
                footerView.setVisibility(View.VISIBLE);

                moreCityShowedArr = new boolean[provinces.size()];

                //假设要默认展开第一组 //TODO
                boolean expandState[] = new boolean[provinces.size()];
                expandState[0] = true;
                twoLevelAdapter.setData(provinces, expandState, map);
                //如果不需要默认展开，就不需要上面三行代码，直接mAdapter.setData(provinces, map);即可

                oneLevelAdapter.setData(provinces, null);

                addSubRecyclerViewAdapter();
            }
        }, true); //改成false模拟数据请求失败 //TODO
    }

    private void initTab(View root) {
        tabTwoLevel = root.findViewById(R.id.tabTwoLevel);
        tabOneLevel = root.findViewById(R.id.tabOneLevel);
        tabTwoLevelBg = root.findViewById(R.id.tabTwoLevelBg);
        tabOneLevelBg = root.findViewById(R.id.tabOneLevelBg);
        tabTwoLevel.setOnClickListener(this);
        tabOneLevel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == tabTwoLevel && tabTwoLevelBg.getVisibility() != View.VISIBLE) {
            tabTwoLevelBg.setVisibility(View.VISIBLE);
            tabOneLevelBg.setVisibility(View.GONE);
            mStickyRecyclerView.setAdapter(twoLevelAdapter);
        } else if (view == tabOneLevel && tabOneLevelBg.getVisibility() != View.VISIBLE) {
            tabOneLevelBg.setVisibility(View.VISIBLE);
            tabTwoLevelBg.setVisibility(View.GONE);
            mStickyRecyclerView.setAdapter(oneLevelAdapter);
        }
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    static class TestSubHolder extends RefreshRecyclerViewBaseSubHolder {
        TextView textView;

        public TestSubHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    //测试下组和child后再添加别的RecyclerView Adapter数据，并且指定每行为2列
    private void addSubRecyclerViewAdapter() {
        twoLevelAdapter.addSubAdapter(new RefreshRecyclerViewSingleViewAdapter(
                StickyRecyclerViewHolderType.HOLDER_TYPE_SUB_BASE + 1,
                View.inflate(this, R.layout.complete_expandable_list_view_sub_list_header, null)));

        final List<String> subList = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            subList.add("子item内容" + i);
        }
        twoLevelAdapter.addSubAdapter(new RefreshRecyclerViewBaseSubAdapter<TestSubHolder>() {

            @Override
            public TestSubHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                TestSubHolder holder = new TestSubHolder(View.inflate(CompleteExpandableListViewActivity.this,
                        R.layout.complete_expandable_list_view_sub_list_item, null));
                return holder;
            }

            @Override
            public void onBindViewHolder(TestSubHolder holder, int position) {
                if (position < subList.size()) {
                    holder.textView.setText(subList.get(position));
                    holder.itemView.setVisibility(View.VISIBLE);
                } else {//因为后面是补齐的空列，所以需要隐藏
                    holder.itemView.setVisibility(View.GONE);
                }
            }

            /**
             * 返回值必须大于等于StickyRecyclerViewHolderType.HOLDER_TYPE_SUB_BASE，
             * 并且所有的subAdapter返回值不能相同
             */
            @Override
            public int getItemViewType(int position) {
                return StickyRecyclerViewHolderType.HOLDER_TYPE_SUB_BASE + 2;
            }

            @Override
            public int getItemCount() {
                if (subList == null || subList.size() == 0) {
                    return 0;
                }

                int count = subList.size();
                int mod = (count + 2) % 3;//因为getColumnWeight设置了有2个列占2倍列宽，所以后面加2，如果有n列列宽比较多，后面加的数是n % getColumnCount
                if (mod == 0) {
                    return count;
                } else {//由于这里是一行3列，不足时3列时补齐3列
                    return count + 3 - mod;
                }
            }

            @Override
            public int getColumnCount() {
                return 3;
            }

            @Override
            public float getColumnWeight(int position) {
                if (position == 0 || position == 3) {
                    return 2;
                }
                return super.getColumnWeight(position);
            }

            @Override
            public void onItemClick(int position, int itemViewType) {
                toast(subList.get(position));
            }
        });
    }
}
