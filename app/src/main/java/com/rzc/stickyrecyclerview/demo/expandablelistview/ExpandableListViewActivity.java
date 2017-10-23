package com.rzc.stickyrecyclerview.demo.expandablelistview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rzc.stickyrecyclerview.R;
import com.rzc.stickyrecyclerview.demo.City;
import com.rzc.stickyrecyclerview.demo.ImageActivity;
import com.rzc.stickyrecyclerview.demo.MockData;
import com.rzc.stickyrecyclerview.demo.Province;
import com.rzc.stickyrecyclerview.demo.WebActivity;
import com.rzc.stickyrecyclerview.lib.BaseChildHolder;
import com.rzc.stickyrecyclerview.lib.BaseGroupHolder;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerView;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerViewAdapter;

import java.util.List;
import java.util.Map;

public class ExpandableListViewActivity extends Activity {
    private ProgressDialog mProgressDialog;
    private StickyRecyclerView mStickyRecyclerView;
    private StickyRecyclerViewAdapter<ProvinceHolder, CityHolder, Province, City> mAdapter;

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

    static class CityHolder extends BaseChildHolder {
        TextView tvName;
        TextView tvContent1;
        TextView tvContent2;

        public CityHolder(final View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvContent1 = itemView.findViewById(R.id.tvContent1);
            tvContent2 = itemView.findViewById(R.id.tvContent2);
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_list_view_activity);

        mStickyRecyclerView = findViewById(R.id.mStickyRecyclerView);
        //TODO 这里增加了一个bool值参数，是为了在滑动时顶部当前组有吸顶效果，如果不需要此效果，去掉此参数或改为false即可
        mAdapter = new StickyRecyclerViewAdapter<ProvinceHolder, CityHolder, Province, City>(mStickyRecyclerView, true) {
            @Override
            public ProvinceHolder newGroupHolder() {
                return new ProvinceHolder(View.inflate(ExpandableListViewActivity.this, R.layout.expandable_list_view_group_item, null));
            }

            @Override
            public void bindGroupHolder(ProvinceHolder holder, Province data, int groupIndex, boolean expand) {
                if (!TextUtils.isEmpty(data.picUrl)) {
                    Glide.with(ExpandableListViewActivity.this).load(data.picUrl)
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
                return new CityHolder(View.inflate(ExpandableListViewActivity.this, R.layout.expandable_list_view_child_item, null));
            }

            @Override
            public void bindChildHolder(CityHolder holder, City data, int groupIndex, int childIndex) {
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
        };
        mStickyRecyclerView.setAdapter(mAdapter);

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

                //假设要默认展开第一组 //TODO
                boolean expandState[] = new boolean[provinces.size()];
                expandState[0] = true;
                mAdapter.setData(provinces, expandState, map);

                //如果不需要默认展开，就不需要上面三行代码，直接mAdapter.setData(provinces, map);即可
            }
        }, true); //改成false模拟数据请求失败 //TODO
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
