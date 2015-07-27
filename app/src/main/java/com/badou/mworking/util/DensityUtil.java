package com.badou.mworking.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.badou.mworking.R;

public class DensityUtil {

    private int offsetMicro;
    private int offsetSmall;
    private int offsetLess;
    private int offsetLless;
    private int offsetMedium;
    private int offsetLarge;
    private int offsetXlarge;
    private int offsetXxlarge;
    private int textSizeSmall;
    private int textSizeLess;
    private int textSizeMedium;
    private int textSizeLarge;
    private int textSizeXlarge;
    private int textSizeXxlarge;
    private int iconSizeTiny;
    private int iconSizeSmall;
    private int iconSizeMedium;
    private int iconSizeLarger;
    private int iconSizeLarge;
    private int iconSizeXlarge;
    private int screenHeight;
    private int screenWidth;

    static DensityUtil densityUtil;

    public static void init(Activity activity) {
        Resources resources = activity.getResources();
        if (densityUtil == null)
            densityUtil = new DensityUtil();
        densityUtil.offsetMicro = resources.getDimensionPixelOffset(R.dimen.offset_micro);
        densityUtil.offsetSmall = resources.getDimensionPixelOffset(R.dimen.offset_small);
        densityUtil.offsetLess = resources.getDimensionPixelOffset(R.dimen.offset_less);
        densityUtil.offsetLless = resources.getDimensionPixelOffset(R.dimen.offset_lless);
        densityUtil.offsetMedium = resources.getDimensionPixelOffset(R.dimen.offset_medium);
        densityUtil.offsetLarge = resources.getDimensionPixelOffset(R.dimen.offset_large);
        densityUtil.offsetXlarge = resources.getDimensionPixelOffset(R.dimen.offset_xlarge);
        densityUtil.offsetXxlarge = resources.getDimensionPixelOffset(R.dimen.offset_xxlarge);
        densityUtil.textSizeSmall = resources.getDimensionPixelSize(R.dimen.text_size_small);
        densityUtil.textSizeLess = resources.getDimensionPixelSize(R.dimen.text_size_less);
        densityUtil.textSizeMedium = resources.getDimensionPixelSize(R.dimen.text_size_medium);
        densityUtil.textSizeLarge = resources.getDimensionPixelSize(R.dimen.text_size_large);
        densityUtil.textSizeXlarge = resources.getDimensionPixelSize(R.dimen.text_size_xlarge);
        densityUtil.textSizeXxlarge = resources.getDimensionPixelSize(R.dimen.text_size_xxlarge);
        densityUtil.iconSizeTiny = resources.getDimensionPixelSize(R.dimen.icon_size_tiny);
        densityUtil.iconSizeSmall = resources.getDimensionPixelSize(R.dimen.icon_size_small);
        densityUtil.iconSizeMedium = resources.getDimensionPixelSize(R.dimen.icon_size_medium);
        densityUtil.iconSizeLarger = resources.getDimensionPixelSize(R.dimen.icon_size_larger);
        densityUtil.iconSizeLarge = resources.getDimensionPixelSize(R.dimen.icon_size_large);
        densityUtil.iconSizeXlarge = resources.getDimensionPixelSize(R.dimen.icon_size_xlarge);
        densityUtil.screenWidth = getWidthInPx(activity);
        densityUtil.screenHeight = getHeightInPx(activity);
    }

    public int getIconSizeTiny() {
        return iconSizeTiny;
    }

    public int getIconSizeSmall() {
        return iconSizeSmall;
    }

    public int getIconSizeMedium() {
        return iconSizeMedium;
    }

    public int getIconSizeLarger() {
        return iconSizeLarger;
    }

    public int getIconSizeLarge() {
        return iconSizeLarge;
    }

    public int getIconSizeXlarge() {
        return iconSizeXlarge;
    }

    public int getOffsetMicro() {
        return offsetMicro;
    }

    public int getOffsetSmall() {
        return offsetSmall;
    }

    public int getOffsetLess() {
        return offsetLess;
    }

    public int getOffsetLless() {
        return offsetLless;
    }

    public int getOffsetMedium() {
        return offsetMedium;
    }

    public int getOffsetLarge() {
        return offsetLarge;
    }

    public int getOffsetXlarge() {
        return offsetXlarge;
    }

    public int getOffsetXxlarge() {
        return offsetXxlarge;
    }

    public int getTextSizeSmall() {
        return textSizeSmall;
    }

    public int getTextSizeLess() {
        return textSizeLess;
    }

    public int getTextSizeMedium() {
        return textSizeMedium;
    }

    public int getTextSizeLarge() {
        return textSizeLarge;
    }

    public int getTextSizeXlarge() {
        return textSizeXlarge;
    }

    public int getTextSizeXxlarge() {
        return textSizeXxlarge;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public static DensityUtil getInstance() {
        if (densityUtil != null)
            return densityUtil;
        else
            throw new IllegalStateException("DensityUtil not initialized");
    }

    public static final int getHeightInPx(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static final int getWidthInPx(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }
}
