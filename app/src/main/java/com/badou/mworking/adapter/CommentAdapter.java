package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.model.Question;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.TimeTransfer;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * 类:  <code> CommentAdapter </code>
 * 功能描述:评论adapter 
 * 创建人:董奇
 * 创建日期: 2014年7月21日 下午7:05:50
 * 开发环境: JDK7.0
 */
public class CommentAdapter extends BaseAdapter {
	
	private List<Question> mData;
	private Context mContext;
	private LayoutInflater mInflater;
	private int AllCount = 0;
	
	public void setDatas(List<Question> mData,int AllCount) {
		this.AllCount = AllCount;
		this.mData = mData;
		notifyDataSetChanged();
	}

	public void addDatas(List<Question> Questions,int AllCount) {
		if (this.mData == null)
			this.mData = Questions;
		else {
			for (Question temp : Questions) {
				this.mData.add(temp);
			}
		}
		this.AllCount = AllCount;
		notifyDataSetChanged();
	}

	public CommentAdapter(Context mContext) {
		super();
		mData = new ArrayList<Question>();
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Question getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
			return getAllView(position, convertView, parent);
	}
	/**
	 * 
	 * 功能描述:
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	private View getAllView(int position, View convertView, ViewGroup parent) {
		AllViewHolder holder;
		/**加载布局**/
		if (convertView != null) {
			holder = (AllViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.adapter_comment,
					parent, false);
			holder = new AllViewHolder(convertView);
			convertView.setTag(holder);
		}
		Question Question = mData.get(position);
		/*获取员工号*/
		String name = Question.getEmployee_id();
		if (!TextUtils.isEmpty(name)) {
			holder.tvQuestionShareName.setText(name);
		}
		/*获取评论内容*/
		String content = Question.getContent();
		if (!TextUtils.isEmpty(content)) {
			holder.tvQuestionShareContent.setText(content);
		}
		/*获取评论时间*/
		String pubTime = TimeTransfer.long2StringDetailDate(mContext,Question
				.getPublish_ts());
		holder.tvQuestionShareDate.setText(pubTime);
		
		/**设置头像**/
		int size = mContext.getResources().getDimensionPixelSize(
				R.dimen.around_icon_head_size);
		Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(
				Question.getImgUrl());
		if (headBmp != null && !headBmp.isRecycled()) {
			holder.headImg.setImageBitmap(headBmp);
		} else {
				MyVolley.getImageLoader().get(
						Question.getImgUrl(),
						new CircleImageListener(mContext, Question.getImgUrl(),holder.headImg, size,
								size), size, size);
			
		}
		
		Bitmap contentBmp = null;
		if (Question.getContentPicUrl()!=null) {
			contentBmp = BitmapLruCache.getBitmapLruCache().get(
					Question.getContentPicUrl());
		}
		if (contentBmp !=null && contentBmp.isRecycled()) {
			holder.imgContentPic.setImageBitmap(contentBmp);
		} else {
			
		}
		
		/*设置楼数*/
		int floorNum =  AllCount - position;
		holder.tvFloorNum.setText(floorNum+mContext.getResources().getString(R.string.floor_num)+"   ·");
		
		return convertView;
	}

	/**
	 * 
	 * 类:  <code> AllViewHolder </code>
	 * 功能描述: 提交评论 
	 * 创建人:董奇
	 * 创建日期: 2014年7月18日 下午8:52:45
	 * 开发环境: JDK7.0
	 */
	static class AllViewHolder {
		ImageView headImg;
		ImageView imgContentPic;
		TextView tvQuestionShareName;
		TextView tvQuestionShareContent;
		TextView tvQuestionShareDate;
		TextView tvFloorNum;

		public AllViewHolder(View view) {
			imgContentPic = (ImageView) view.findViewById(R.id.imgQuestionShare);
			headImg = (ImageView) view
					.findViewById(R.id.ivAdapterQuestionShareIcon);
			tvQuestionShareName = (TextView) view
					.findViewById(R.id.tvQuestionShareName);
			tvQuestionShareContent = (TextView) view
					.findViewById(R.id.tvQuestionShareContent);
			tvQuestionShareDate = (TextView) view
					.findViewById(R.id.tvQuestionShareDate);
			tvFloorNum = (TextView) view.findViewById(R.id.tvFloor);
		}
	}
}
