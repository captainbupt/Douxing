package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.MainBanner;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;

import java.util.ArrayList;

/**
 * 功能描述: 显示banner 的适配器
 */
public class BannerAdapter extends MyBaseAdapter {

    public BannerAdapter(Context context) {
        super(context);
    }

    public int getCount() {
        //设置成最大，使用户看不到边界  而不是 imgList.size()， 这里要注意一下
        return Integer.MAX_VALUE;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder(mContext);
            convertView = viewHolder.imageView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (mItemList.size() <= 0) {
            viewHolder.imageView.setImageResource(R.drawable.banner_default);
            return convertView;
        }

        String url = ((MainBanner) mItemList.get(position % mItemList.size())).getBannerImgURL();
        Bitmap bm = BitmapLruCache.getBitmapLruCache().getBitmap(url);
        if (bm != null) {
            viewHolder.imageView.setImageBitmap(bm);
        } else {
            MyVolley.getImageLoader().get(url,
                    new PicImageListener(mContext, viewHolder.imageView, url));
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;

        public ViewHolder(Context context) {
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);//不清楚有什么效果
            imageView.setLayoutParams(new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ScaleType.FIT_XY);
        }
    }
}
