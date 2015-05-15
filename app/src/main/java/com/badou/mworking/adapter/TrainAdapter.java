package com.badou.mworking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.IconLoadListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;

import java.util.ArrayList;

/**
 * 类: <code> TrainAdapter </code> 功能描述: 微培训adapter 创建人: 葛建锋 创建日期: 2014年7月17日
 * 下午5:07:48 开发环境: JDK7.0
 */
public class TrainAdapter extends BaseAdapter {

	private ArrayList<Train> mData;
	private LayoutInflater mInflater;
	private Context mContext;
	public static final String VALUE_ACT_TRAIN = "train";
	public static final String VALUE_ACT_STUDY = "study";
	private String act_name;// 判断是在 我的学习/微培训 页面显示的(act_name=VALUE_ACT_TRAIN
							// 表示要显示微培训.)

	/**
	 * 微培训/我的学习
	 * @param mContext
	 * @param mData
	 * @param act_name
	 *            判断是在 我的学习/微培训 页面显示的(act_name=VALUE_ACT_TRAIN 表示要显示微培训.)
	 */
	public TrainAdapter(Context mContext, ArrayList<Train> mData,
			String act_name) {
		this.mData = null == mData ? new ArrayList<Train>() : mData;
		this.mInflater = LayoutInflater.from(mContext);
		this.mContext = mContext;
		this.act_name = act_name;
	}

	public void setData(ArrayList<Train> mData) {
		this.mData = null == mData ? new ArrayList<Train>() : mData;
	} 

	public ArrayList<Train> getmData() {
		if (this.mData == null) {
			mData = new ArrayList<Train>();
		}
		return mData;
	}
	
	public void addData(ArrayList<Train> list) {
		if (this.mData == null) {
			this.mData = new ArrayList<Train>();
		}
		this.mData.addAll(list);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Train getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final Train train = mData.get(position);
		if (VALUE_ACT_STUDY.equals(act_name)) {
			/** 学习进度显示的布局 **/
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.adapter_item_study_progress, null);
			}
			TextView tvSubject = (TextView) convertView
					.findViewById(R.id.lv_item_study_subject);
			TextView tvDpt = (TextView) convertView
					.findViewById(R.id.lv_item_study_dpt_time);

			tvSubject.setText(train.getSubject() + "");
			tvDpt.setText(TimeTransfer.long2StringDetailDate(mContext,train.getTime()));
			return convertView;
		} else {
			/** 微培训列表页显示的布局 **/
			if (convertView != null) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				convertView = mInflater.inflate(R.layout.adapter_train_item,
						parent, false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
		}
		if (train.getImgUrl() == null || train.getImgUrl().equals("")) {
			holder.logoImage.setImageResource(R.drawable.pic_train_item);
		} else {
			holder.logoImage.setTag(train.getImgUrl());
			/** 加载图片 **/
			Bitmap bm = BitmapLruCache.getBitmapLruCache().getBitmap(
					train.getImgUrl());
			if (bm != null
					&& holder.logoImage.getTag().equals(train.getImgUrl())) {
				holder.logoImage.setImageBitmap(bm);
				bm = null;
			} else {
				/** 设置默认图在IconLoadListener 中 **/
				MyVolley.getImageLoader().get(
						train.getImgUrl(),
						new IconLoadListener(mContext, holder.logoImage, train
								.getImgUrl(), R.drawable.pic_train_item));
			}
		}
		
		// 显示标题 
		holder.subject.setText(train.getSubject());
		// 显示时间和部门
		holder.bumenAndDateTv.setText(TimeTransfer.long2StringDetailDate(mContext,train.getTime()));
		// 显示评分人数
		holder.pingfenrenshuTv.setText(" ("+train.getEcnt()+")");
		// 显示评分星星
		if(train.getEcnt()!=0){
			holder.pingfenRatingbar.setRating((float)train.getEval()/train.getEcnt());
		}
		// 该课件是否已读
		if (train.getIsRead() == Constant.READ_YES) {
			holder.rl_bg.setBackgroundResource(R.drawable.icon_read_);
		} else {
			holder.rl_bg.setBackgroundResource(R.drawable.icon_unread_orange);
		}
		/** 显示是否置顶 **/
		if (train.getTop()== Constant.TOP_YES) {
			holder.top.setVisibility(View.VISIBLE);
		} else {
			holder.top.setVisibility(View.GONE);
		}
		return convertView;
	}

	/**
	 * 
	 * 功能描述:设置是否已读,并更新sp
	 * @param position
	 */
	public void read(int position) {
		String userNum = ((AppApplication) mContext.getApplicationContext())
				.getUserInfo().getUserNumber();
		if (this.mData.get(position).getIsRead() == Constant.READ_NO) {
			mData.get(position).setIsRead(Constant.READ_YES);
			this.notifyDataSetChanged();
			int unreadNum = SP.getIntSP(mContext, SP.DEFAULTCACHE,userNum+Train.UNREAD_NUM_TRAIN, 0);
			if (unreadNum > 0 ) {
				SP.putIntSP(mContext, SP.DEFAULTCACHE,userNum+Train.UNREAD_NUM_TRAIN, unreadNum - 1);
			}
		}
	}
	
	static class ViewHolder {
		
		TextView subject;
		TextView bumenAndDateTv;   //显示部门和时间
		TextView pingfenrenshuTv;  //显示评分人数
		ImageView top;
		ImageView logoImage;
		RatingBar pingfenRatingbar; // 星星显示
		RelativeLayout rl_bg;

		public ViewHolder(View view) {
			rl_bg = (RelativeLayout) view.findViewById(R.id.rl_item_bg_isread);
			top = (ImageView) view.findViewById(R.id.iv_adapter_base_item_top);
			logoImage = (ImageView) view.findViewById(R.id.img_train_pic);
			subject = (TextView) view
					.findViewById(R.id.tv_adapter_base_item_subject);
			bumenAndDateTv = (TextView) view.findViewById(R.id.bumen_and_date_tv);
			pingfenrenshuTv = (TextView) view.findViewById(R.id.pingfenrenshu_tv);
			pingfenRatingbar = (RatingBar) view.findViewById(R.id.pingfen_ratingbar);
		}
	}
}
