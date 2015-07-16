package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.main.MainBanner;
import com.badou.mworking.net.bitmap.ImageViewLoader;

/**
 * 功能描述: 显示banner 的适配器
 */
public class BannerAdapter extends MyBaseAdapter<MainBanner> {

    public BannerAdapter(Context context) {
        super(context);
    }

    public int getCount() {
        //设置成最大，使用户看不到边界  而不是 imgList.size()， 这里要注意一下
        return Integer.MAX_VALUE;
    }

    @Override
    public MainBanner getItem(int i) {
        if(mItemList == null || mItemList.size() == 0){
            return null;
        }else {
            return mItemList.get(i % mItemList.size());
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            ImageView imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);//不清楚有什么效果
            imageView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ScaleType.FIT_XY);
            convertView = imageView;
        }
        MainBanner mainBanner = getItem(position);
        if (mainBanner == null) {
            ((ImageView) convertView).setImageResource(R.drawable.banner_default);
        }else {
            ImageViewLoader.setImageViewResource((ImageView) convertView, R.drawable.banner_default, mainBanner.getImg());
        }
        return convertView;
    }
}
