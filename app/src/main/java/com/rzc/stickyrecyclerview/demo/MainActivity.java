package com.rzc.stickyrecyclerview.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rzc.stickyrecyclerview.R;
import com.rzc.stickyrecyclerview.demo.completeexpandablelistview.CompleteExpandableListViewActivity;
import com.rzc.stickyrecyclerview.demo.expandablelistview.ExpandableListViewActivity;
import com.rzc.stickyrecyclerview.demo.headerfooterlistview.HeaderFooterListViewActivity;
import com.rzc.stickyrecyclerview.demo.scrollheaderfooterlistview.ScrollHeaderFooterListViewActivity;
import com.rzc.stickyrecyclerview.demo.simplelistview.SimpleListViewActivity;
import com.rzc.stickyrecyclerview.lib.BaseChildHolder;
import com.rzc.stickyrecyclerview.lib.BaseGroupHolder;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerView;
import com.rzc.stickyrecyclerview.lib.StickyRecyclerViewAdapter;

import java.util.Arrays;

/**
 * 功能测试列表，这里先演示最简单的用法，StickyRecyclerView本来是封装用来
 * 实现类似ExpandableListView的效果，不过也支持ListView这种形式
 */
public class MainActivity extends Activity {
    private String[] funcArr = new String[] {
            "最普通的ListView效果",
            "带header和footer的ListView",
            "带滑动监听、header、footer的ListView",
            "最普通的类似ExpandableListView",
            "一个比较完整类似ExpandableListView例子",
    };
    private StickyRecyclerView mStickyRecyclerView;

    static class MyHolder extends BaseGroupHolder {
        TextView textView;

        public MyHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStickyRecyclerView = findViewById(R.id.mStickyRecyclerView);
        StickyRecyclerViewAdapter adapter
                = new StickyRecyclerViewAdapter<MyHolder, BaseChildHolder, String, Object>(mStickyRecyclerView) {
            @Override
            public MyHolder newGroupHolder() {
                return new MyHolder(View.inflate(MainActivity.this, R.layout.main_list_item, null));
            }

            @Override
            public void bindGroupHolder(MyHolder holder, String data, int groupIndex, boolean expand) {
                holder.textView.setText(data);
            }

            @Override
            public void onGroupClick(View view, String data, int groupIndex, boolean expand) {
                if (groupIndex == 0) {
                    startActivity(new Intent(MainActivity.this, SimpleListViewActivity.class));
                } else if (groupIndex == 1) {
                    startActivity(new Intent(MainActivity.this, HeaderFooterListViewActivity.class));
                } else if (groupIndex == 2) {
                    startActivity(new Intent(MainActivity.this, ScrollHeaderFooterListViewActivity.class));
                } else if (groupIndex == 3) {
                    startActivity(new Intent(MainActivity.this, ExpandableListViewActivity.class));
                } else if (groupIndex == 4) {
                    startActivity(new Intent(MainActivity.this, CompleteExpandableListViewActivity.class));
                }
            }
        };
        mStickyRecyclerView.setAdapter(adapter);
        adapter.setData(Arrays.asList(funcArr), null);
    }
}
