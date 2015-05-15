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
import com.badou.mworking.model.MainBanner;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;

import java.util.ArrayList;

/**
 * 
 * 类:  <code> BannerAdapter </code>
 * 功能描述: 显示banner 的适配器
 * 创建日期: 2015年1月8日 下午12:22:28
 * 开发环境: JDK6.0
 */
public class BannerAdapter extends BaseAdapter {
	
	private Context context;
	private ArrayList<MainBanner> imgList; 
	
	public BannerAdapter(Context context,ArrayList<MainBanner> imgList ) {
		this.context = context;
		this.imgList=imgList;
	}

	public int getCount() {
		//设置成最大，使用户看不到边界  而不是 imgList.size()， 这里要注意一下
		return Integer.MAX_VALUE;
	}

	public Object getItem(int position) {

		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder(context);
			convertView = viewHolder.imageView;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (imgList.size() <= 0) {
			viewHolder.imageView.setImageResource(R.drawable.banner_default);
			return convertView;
		}
		
		String url = imgList.get(position % imgList.size()).getBannerImgURL();
		Bitmap bm = BitmapLruCache.getBitmapLruCache().getBitmap(url);
		if ( bm != null ) {
			viewHolder.imageView.setImageBitmap(bm);
		}else{
			MyVolley.getImageLoader().get(url,
					new PicImageListener(context, viewHolder.imageView, url));
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
