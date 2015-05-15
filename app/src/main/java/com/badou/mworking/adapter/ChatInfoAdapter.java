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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.model.ChatInfo;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.TimeTransfer;

import java.util.ArrayList;

/**
 * 类: <code> ChatInfoAdapter </code> 功能描述: 聊天界面 创建人: 葛建锋 创建日期: 2014年9月18日
 * 下午4:19:19 开发环境: JDK7.0
 */
public class ChatInfoAdapter extends BaseAdapter {

	private static final int splitTime = 10 * 60;// 两条信息的时间间隔

	private LayoutInflater layoutInflater;
	private ArrayList<ChatInfo> mData;
	private Context mContext;
	private String whomOther;
	private String img = "";
	int size = 0;// 设置头像的大小
	private String myHeadImgUrl = "";

	/**
	 * 功能描述:
	 * 
	 * @param context
	 */
	public ChatInfoAdapter(Context context, ArrayList<ChatInfo> ChatInfos,
			String whomOther, String img, String myHeadImgUrl) {
		super();
		this.mContext = context;
		this.layoutInflater = LayoutInflater.from(context);
		this.mData = ChatInfos;
		this.whomOther = whomOther;
		this.img = img;
		this.myHeadImgUrl = myHeadImgUrl;
		size = mContext.getResources().getDimensionPixelSize(
				R.dimen.around_icon_head_size);
	}

	public void setdata(ArrayList<ChatInfo> ChatInfos) {
		if (ChatInfos == null) {
			ChatInfos = new ArrayList<ChatInfo>();
		}
		this.mData = ChatInfos;
	}

	@Override
	public int getCount() {
		return mData.size() > 0 ? mData.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int posion, View convertView, ViewGroup parent) {
		AllViewHolder holder;
		if (convertView != null) {
			holder = (AllViewHolder) convertView.getTag();
		} else {
			convertView = layoutInflater
					.inflate(R.layout.chatinfoadapter, null);
			holder = new AllViewHolder(convertView);
			convertView.setTag(holder);
		}
		ChatInfo chatInfo = mData.get(posion);
		if (whomOther.equals(chatInfo.getOwn())) {
			setOtherChat(holder, chatInfo);
		} else {
			setMyChat(holder, chatInfo);
		}

		/** 设置间隔时间的显示 **/
		long nowTime = mData.get(posion).getTs();
		if (0 == posion && nowTime != 0) {
			holder.tvTs.setVisibility(View.VISIBLE);
			holder.tvTs.setText(TimeTransfer.long2StringDetailDate(mContext,
					nowTime * 1000) + "");
		} else {
			long afterTime = mData.get(posion - 1).getTs();
			long cha = nowTime - afterTime;
			if (cha > splitTime) {
				holder.tvTs.setVisibility(View.VISIBLE);
				holder.tvTs.setText(TimeTransfer.long2StringDetailDate(
						mContext, nowTime * 1000) + "");
			} else {
				holder.tvTs.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	static class AllViewHolder {
		LinearLayout layoutMy;
		ImageView imgMyHead;
		TextView tvMyContent;
		LinearLayout layoutOther;
		ImageView imgOtherHead;
		TextView tvOtherContent;
		TextView tvTs;// 发布时间

		public AllViewHolder(View view) {
			layoutMy = (LinearLayout) view.findViewById(R.id.rl_mychat);
			tvMyContent = (TextView) view.findViewById(R.id.tvMyMSG);
			imgMyHead = (ImageView) view.findViewById(R.id.my_head_img);

			layoutOther = (LinearLayout) view.findViewById(R.id.rl_other_chat);
			imgOtherHead = (ImageView) view.findViewById(R.id.other_head_img);
			tvOtherContent = (TextView) view.findViewById(R.id.tvOtherMSG);
			tvTs = (TextView) view.findViewById(R.id.tv_time);
		}
	}

	private void setMyChat(AllViewHolder vh, ChatInfo chatInfo) {
		vh.layoutMy.setVisibility(View.VISIBLE);
		vh.layoutOther.setVisibility(View.GONE); 
		/** 设置头像 **/
		vh.imgMyHead.setImageResource(R.drawable.user_detail_head_icon);
		if (myHeadImgUrl == null || myHeadImgUrl.equals("")) {
			vh.imgMyHead.setImageResource(R.drawable.user_detail_head_icon);
		} else {
			/** 设置头像 **/
			Bitmap bm = BitmapLruCache.getBitmapLruCache().getCircleBitmap(
					myHeadImgUrl);
			if (bm != null && !bm.isRecycled()) {
				vh.imgMyHead.setImageBitmap(bm);
				bm = null;
			} else {
				MyVolley.getImageLoader().get(
						myHeadImgUrl,
						new CircleImageListener(mContext, myHeadImgUrl,
								vh.imgMyHead, size, size));
			}
		}

		vh.tvMyContent.setText(chatInfo.getContent() + "");
	}

	private void setOtherChat(AllViewHolder vh, ChatInfo chatInfo) {
		vh.layoutMy.setVisibility(View.GONE);
		vh.layoutOther.setVisibility(View.VISIBLE);
		/** 设置头像 **/
		vh.imgOtherHead.setImageResource(R.drawable.user_detail_head_icon);
		/** 设置头像 **/
		Bitmap bm = BitmapLruCache.getBitmapLruCache().getCircleBitmap(img);
		if (bm != null && !bm.isRecycled()) {
			vh.imgOtherHead.setImageBitmap(bm);
			bm = null;
		} else {
			MyVolley.getImageLoader().get(
					img,
					new CircleImageListener(mContext, img, vh.imgOtherHead,
							size, size));
		}
		vh.tvOtherContent.setText(chatInfo.getContent() + "");
	}
}
