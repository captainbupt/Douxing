package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.model.ContanctsList;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.TimeTransfer;
import com.swipe.delete.SwipeLayout;
import com.swipe.delete.adapters.BaseSwipeAdapter;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChatAdapter extends BaseSwipeAdapter{

	private Context mContext;
	private ArrayList<ContanctsList> mData;
	private ContanctsList chat = null;
	
	public ChatAdapter(Context mContext,ArrayList<ContanctsList> mData) {
		this.mContext = mContext;
		this.mData = mData;
	}
	
	public void setData(ArrayList<ContanctsList> mData) {
		this.mData = mData;
	}
	
	@Override
	public int getCount() {
		return mData.size() >0 ? mData.size() : 0;
	}

	@Override
	public Object getItem(int arg0) {
		return mData.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	class ViewHolder{
		private ImageView imgHead;
		private TextView tvUnreadNum;
		private TextView tvTime;
		private TextView tvName;
		private TextView tvContent;
		
		public ViewHolder(View view) {
			imgHead = (ImageView) view.findViewById(R.id.img_head);
			tvName = (TextView) view.findViewById(R.id.tv_name);
			tvTime = (TextView) view.findViewById(R.id.tv_time);
			tvUnreadNum = (TextView) view.findViewById(R.id.tv_unreadNum);
			tvContent = (TextView) view.findViewById(R.id.tv_content);
		}
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_chat_list, null);
		return view;
	}

	@Override
	public void fillValues(final int position, View convertView) {
		ViewHolder vh = new ViewHolder(convertView);
		chat = mData.get(position);
		/**设置头像**/
		vh.imgHead.setImageResource(R.drawable.user_detail_head_icon);
		/**设置头像**/
		int size = mContext.getResources().getDimensionPixelSize(
				R.dimen.around_icon_head_size);
		if (null != chat && !"".equals(chat.getImg())) {
			Bitmap bm = BitmapLruCache.getBitmapLruCache().getCircleBitmap(chat.getImg());
			if (bm != null && !bm.isRecycled()) {
				vh.imgHead.setImageBitmap(bm);
				bm = null;
			} else {
				MyVolley.getImageLoader().get(chat.getImg(), new CircleImageListener(mContext,
						chat.getImg(), vh.imgHead, size,size));
			}
		}
		
		/**设置名字**/
		vh.tvName.setText(chat.getName()+"");
		/**设置发布时间**/
		if(chat.getTs()==0){
			vh.tvTime.setText("");
		}else{
			vh.tvTime.setText(TimeTransfer.long2StringDetailDate(mContext,chat.getTs()*1000));
		}
		
		/**设置发布内容**/
		vh.tvContent.setText(chat.getContent());
		
		/**设置未读数**/
		if (chat.getMsgcnt()>0) {
			vh.tvUnreadNum.setVisibility(View.VISIBLE);
			vh.tvUnreadNum.setText(chat.getMsgcnt()+"");
		}else {
			vh.tvUnreadNum.setVisibility(View.INVISIBLE);
		}
		if (chat.getMsgcnt()>9) {
			vh.tvUnreadNum.setBackgroundResource(R.drawable.icon_chat_unread_long);
		} else {
			vh.tvUnreadNum.setBackgroundResource(R.drawable.icon_chat_unread);
		}
		final SwipeLayout swipeLayout = (SwipeLayout) convertView
				.findViewById(getSwipeLayoutResourceId(position));
		// 添加删除布局的点击事件
		convertView.findViewById(R.id.ll_menu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deleteChat(position);				
				// 点击完成之后，关闭删除menu
				swipeLayout.close();
			}
		});
	}
	
	/**
	 *  删除会话
	 */
	private void deleteChat(final int position){
		String whom = mData.get(position).getWhom();
		mData.remove(position);
		notifyDataSetChanged();
		ServiceProvider.delChat(mContext, whom, new VolleyListener(mContext) {
			
			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (code != Net.SUCCESS) {
					return;
				}
			}
		});
	}
}
