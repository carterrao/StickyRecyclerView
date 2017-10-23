package com.rzc.stickyrecyclerview.demo.headerfooterlistview;

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
import com.rzc.stickyrecyclerview.demo.ImageActivity;
import com.rzc.stickyrecyclerview.demo.MockData;
import com.rzc.stickyrecyclerview.demo.Province;
import com.rzc.stickyrecyclerview.lib.BaseChildHolder;
import com.rzc.stickyrecyclerview.lib.BaseGroupHolder;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerView;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerViewAdapter;

import java.util.List;

/**
 * Created by rzc on 17/9/30.
 */

public class HeaderFooterListViewActivity extends Activity {
    private ProgressDialog mProgressDialog;
    private StickyRecyclerView mStickyRecyclerView;
    private StickyRecyclerViewAdapter<HeaderFooterListViewActivity.ProvinceHolder, BaseChildHolder, Province, Object> mAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_list_view_activity);

        mStickyRecyclerView = findViewById(R.id.mStickyRecyclerView);

        View header = View.inflate(this, R.layout.header_footer_list_view_header, null);
        ImageView headerPic = header.findViewById(R.id.ivPic);
        Glide.with(this).load("http://211.159.149.56:8080/map/8/4o28b0625501ad13015501ad2bfc0136.jpg")
                .placeholder(R.mipmap.ic_launcher).into(headerPic);

        View footer = View.inflate(this, R.layout.header_footer_list_view_footer, null);
        ImageView footerPic = footer.findViewById(R.id.ivPic);
        Glide.with(this).load("http://img.qqai.net/uploads/i_3_4246494178x537668949_21.jpg")
                .placeholder(R.mipmap.ic_launcher).into(footerPic);

        /** //TODO
         * 注意事项：
         * 1.如果要设置header、footer，setHeaderFooter必须在setAdapter之前调用，因为StickyRecyclerView目前
         *   设计为adapter可以动态切换，而header、footer在切换了adapter时候一直是共用的；如果希望在数据加载完成
         *   后才显示header、footer，可以header.setVisibility(View.GONE)先隐藏，等数据下载完成的回调再调用
         *   header.setVisibility(View.VISIBLE)来设置为显示
         * 2.如果只想要header，调用mStickyRecyclerView.setHeaderFooter(header, null)；同理，只想要footer，
         * 则调用mStickyRecyclerView.setHeaderFooter(null, footer);
         */
        mStickyRecyclerView.setHeaderFooter(header, footer);


        mAdapter = new StickyRecyclerViewAdapter<ProvinceHolder, BaseChildHolder, Province, Object>(mStickyRecyclerView) {
            @Override
            public ProvinceHolder newGroupHolder() {
                return new ProvinceHolder(View.inflate(HeaderFooterListViewActivity.this, R.layout.simple_list_view_list_item, null));
            }

            @Override
            public void bindGroupHolder(ProvinceHolder holder, Province data, int groupIndex, boolean expand) {
                if (!TextUtils.isEmpty(data.picUrl)) {
                    Glide.with(HeaderFooterListViewActivity.this).load(data.picUrl)
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
        };
        mStickyRecyclerView.setAdapter(mAdapter);

        mProgressDialog = ProgressDialog.show(this, "", "正在加载...");

        MockData.queryProvinceList(new MockData.OnProvinceListResponse() {
            @Override
            public void onFailed() {
                mProgressDialog.dismiss();
                toast("数据加载失败");
            }

            @Override
            public void onSuccess(List<Province> data) {
                mProgressDialog.dismiss();
                mAdapter.setData(data, null);
            }
        }, true); //改成false模拟数据请求失败 //TODO
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
