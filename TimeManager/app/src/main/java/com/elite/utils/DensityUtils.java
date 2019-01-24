package com.elite.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by zwb
 * Description 屏幕尺寸工具类
 * Date 16/8/3.
 */
public class DensityUtils {

    public static DisplayMetrics getDisplayMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕的宽
     * @param context
     * @return
     */
    public static int getW(Context context){
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕的高
     * @return
     */
    public static int getH(Context context){
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取密度系数
     * @param context
     * @return
     */
    public static float getDensity(Context context){
        return getDisplayMetrics(context).density;
    }

    /**
     * 获取密度
     * @param context
     * @return
     */
    public static int getDensityDpi(Context context){
        return getDisplayMetrics(context).densityDpi;
    }
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * getDensity(context) + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * getDensity(context) + 0.5f);
    }

}
