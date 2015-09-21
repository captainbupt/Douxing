package com.badou.mworking.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery.LayoutParams;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.main.MainBanner;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 显示banner 的适配器
 */
public class BannerAdapter extends MyBaseAdapter<MainBanner> {

    ViewGroup.LayoutParams mLayoutParams;

    public BannerAdapter(Context context) {
        super(context);
        mLayoutParams = new LayoutParams(DensityUtil.getWidthInPx((Activity) mContext), DensityUtil.getWidthInPx((Activity) mContext) * 340 / 720);
    }

    public int getCount() {
        //设置成最大，使用户看不到边界  而不是 imgList.size()， 这里要注意一下
        return Integer.MAX_VALUE;
    }

    @Override
    public MainBanner getItem(int i) {
        if (mItemList == null || mItemList.size() == 0) {
            return null;
        } else {
            return mItemList.get(i % mItemList.size());
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            SimpleDraweeView imageView = new SimpleDraweeView(mContext);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(mContext.getResources());
            GenericDraweeHierarchy hierarchy = builder
                    .setPlaceholderImage(ContextCompat.getDrawable(mContext, R.drawable.banner_default), ScalingUtils.ScaleType.CENTER_CROP)
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .build();
            imageView.setLayoutParams(mLayoutParams);
            imageView.setHierarchy(hierarchy);
            convertView = imageView;
        }
        MainBanner mainBanner = getItem(position);
        if (mainBanner != null) {
            ((SimpleDraweeView) convertView).setImageURI(UriUtil.getHttpUri(mainBanner.getImg()));
        }
        return convertView;
    }
}
