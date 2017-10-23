package com.rzc.stickyrecyclerview.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.rzc.stickyrecyclerview.R;

public class ImageActivity extends Activity {
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mProgressDialog = ProgressDialog.show(this, "", "正在加载图片...");
        ImageView ivPic = findViewById(R.id.ivPic);
        ivPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Glide.with(this).load(getIntent().getStringExtra("url"))
                .placeholder(R.mipmap.ic_launcher).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                toast("图片加载失败");
                mProgressDialog.dismiss();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mProgressDialog.dismiss();
                return false;
            }
        }).into(ivPic);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showPic(Context context, String url) {
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }
}
