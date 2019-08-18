package com.example.comment.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.comment.annotation.Comment;


/**
 * 页面相关
 *
 * @author wangjiang wangjiang7747@gmail.com
 * @version V1.0
 */
@Comment
public final class DisplayUtil {

    private DisplayUtil() {
        throw new UnsupportedOperationException("不能创建此对象");
    }

    /**
     * 获得StatusBar的高度
     *
     * @param context 上下文对象
     * @return 状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen",
                "android");
        int statusBarHeight = resources.getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    /**
     * 获得NavigationBar的高度
     *
     * @param context 上下文对象
     * @return 底部导航栏的高度，有些设备有底部导航栏，有些设备没有底部导航栏
     */
    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        int navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        return navigationBarHeight;
    }

    /**
     * 获得ActionBar的高度，注：在配置文件中获得ActionBar高度可通过：?attr/actionBarSize
     *
     * @param activity 当前的Activity对象
     * @return ActionBar的高度
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize,
                tv, true)) {// 如果资源是存在的、有效的
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    activity.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * 获得页面高度
     *
     * @param activity 当前的Activity对象
     * @return 获得内容的高度
     */
    public static int getContentHeight(Activity activity) {
        return getScreenHeight(activity) - getStatusBarHeight(activity)
                - getActionBarHeight(activity);
    }

    /**
     * 将px转换为dp
     *
     * @param context 上下文对象
     * @param pxValue 像素值
     * @return 转换后的dp值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dp转换为px
     *
     * @param context 上下文对象
     * @param dpValue dp值
     * @return 转换后的px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获得屏幕宽度
     *
     * @param context 上下文对象
     * @return 屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context 上下文对象
     * @return 屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}