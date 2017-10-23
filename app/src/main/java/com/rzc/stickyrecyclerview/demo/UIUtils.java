package com.rzc.stickyrecyclerview.demo;

import android.content.Context;

/**
 * Created by rzc on 17/9/30.
 */

public class UIUtils {
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;

        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return statusBarHeight;
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5F);
    }
}
