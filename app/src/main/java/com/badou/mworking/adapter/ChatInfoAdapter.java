/* 
 * 文件名: ChatInfoAdapter.java
 * 包路径: com.badou.mworking.adapter
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2014年9月18日 下午4:19:19
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2014年9月18日 下午4:19:19 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.ChatInfo;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述: 聊天界面
 */
public class ChatInfoAdapter extends MyBaseAdapter {

	private static final int splitTime = 10 * 60;// 两条信息的时间间隔

	private String mOtherName;
	private String mOtherHeadUrl = "";
	int mHeadSize = 0;// 设置头像的大小
	private String mHeadUrl = "";

	/**
	 * 功能描述:
	 * 
	 * @param context
	 */
	public ChatInfoAdapter(Context context,String whomOther, String img, String myHeadImgUrl) {
		super(context);
		this.mOtherName = whomOther;
		this.mOtherHeadUrl = img;
		this.mHeadUrl = myHeadImgUrl;
		mHeadSize = mContext.getResources().getDimensionPixelSize(
				R.dimen.around_icon_head_size);
	}

	@Override
	public View getView(int posion, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = mInflater
					.inflate(R.layout.adapter_chat_info, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		ChatInfo chatInfo = (ChatInfo) mItemList.get(posion);
		if (mOtherName.equals(chatInfo.getOwn())) {
			setOtherChat(holder, chatInfo);
		} else {
			setMyChat(holder, chatInfo);
		}

		/** 设置间隔时间的显示 **/
		long nowTime = chatInfo.getTs();
		if (0 == posion && nowTime != 0) {
			holder.mTimeTextView.setVisibility(View.VISIBLE);
			holder.mTimeTextView.setText(TimeTransfer.long2StringDetailDate(mContext,
					nowTime * 1000) + "");
		} else {
			long afterTime = ((ChatInfo)mItemList.get(posion-1)).getTs();
			long cha = nowTime - afterTime;
			if (cha > splitTime) {
				holder.mTimeTextView.setVisibility(View.VISIBLE);
				holder.mTimeTextView.setText(TimeTransfer.long2StringDetailDate(
						mContext, nowTime * 1000) + "");
			} else {
				holder.mTimeTextView.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	static class ViewHolder {
		LinearLayout mMyChatLayout;
		ImageView mMyHeadImageView;
		TextView mMyContentTextView;
		LinearLayout mOtherChatLayout;
		ImageView mOtherHeadImageView;
		TextView mOtherContentTextView;
		TextView mTimeTextView;// 发布时间

		public ViewHolder(View view) {
			mMyChatLayout = (LinearLayout) view.findViewById(R.id.rl_mychat);
			mMyContentTextView = (TextView) view.findViewById(R.id.tvMyMSG);
			mMyHeadImageView = (ImageView) view.findViewById(R.id.my_head_img);

			mOtherChatLayout = (LinearLayout) view.findViewById(R.id.rl_other_chat);
			mOtherHeadImageView = (ImageView) view.findViewById(R.id.other_head_img);
			mOtherContentTextView = (TextView) view.findViewById(R.id.tvOtherMSG);
			mTimeTextView = (TextView) view.findViewById(R.id.tv_adapter_chat_list_time);
		}
	}

	private void setMyChat(ViewHolder vh, ChatInfo chatInfo) {
		vh.mMyChatLayout.setVisibility(View.VISIBLE);
		vh.mOtherChatLayout.setVisibility(View.GONE);
		/** 设置头像 **/
		vh.mMyHeadImageView.setImageResource(R.drawable.icon_user_detail_default_head);
		if (mHeadUrl == null || mHeadUrl.equals("")) {
			vh.mMyHeadImageView.setImageResource(R.drawable.icon_user_detail_default_head);
		} else {
			/** 设置头像 **/
			Bitmap bm = BitmapLruCache.getBitmapLruCache().getCircleBitmap(
					mHeadUrl);
			if (bm != null && !bm.isRecycled()) {
				vh.mMyHeadImageView.setImageBitmap(bm);
				bm = null;
			} else {
				MyVolley.getImageLoader().get(
						mHeadUrl,
						new CircleImageListener(mContext, mHeadUrl,
								vh.mMyHeadImageView, mHeadSize, mHeadSize));
			}
		}

		vh.mMyContentTextView.setText(chatInfo.getContent() + "");
	}

	private void setOtherChat(ViewHolder vh, ChatInfo chatInfo) {
		vh.mMyChatLayout.setVisibility(View.GONE);
		vh.mOtherChatLayout.setVisibility(View.VISIBLE);
		/** 设置头像 **/
		vh.mOtherHeadImageView.setImageResource(R.drawable.icon_user_detail_default_head);
		/** 设置头像 **/
		Bitmap bm = BitmapLruCache.getBitmapLruCache().getCircleBitmap(mOtherHeadUrl);
		if (bm != null && !bm.isRecycled()) {
			vh.mOtherHeadImageView.setImageBitmap(bm);
			bm = null;
		} else {
			MyVolley.getImageLoader().get(
					mOtherHeadUrl,
					new CircleImageListener(mContext, mOtherHeadUrl, vh.mOtherHeadImageView,
							mHeadSize, mHeadSize));
		}
		vh.mOtherContentTextView.setText(chatInfo.getContent() + "");
	}
}
