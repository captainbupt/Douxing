package com.badou.mworking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.PhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.WenDaManage;
import com.badou.mworking.model.WenDaAnswer;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.TimeTransfer;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author 葛建锋
 * 问答详情页面
 */
public class WenDaDetailAdapter extends BaseAdapter{
	
	private ArrayList<WenDaAnswer> wenDaAnswers;
	private Context context;
	private int count = 0 ;
	private LayoutInflater mInflater;
	
	public WenDaDetailAdapter(Context context,ArrayList<WenDaAnswer> wenDaAnswers) {
		super();
		this.context = context;
		this.wenDaAnswers = wenDaAnswers;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return wenDaAnswers.size();
	}

	@Override
	public WenDaAnswer getItem(int position) {
		return wenDaAnswers.get(position);
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
		if (convertView != null) {
			holder = (AllViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.wendadetailadapter,
					parent, false);
			holder = new AllViewHolder(convertView);
			convertView.setTag(holder);
		}
		final WenDaAnswer wenDaAnswer = wenDaAnswers.get(position);

		String name = wenDaAnswer.getEid();
		if (!TextUtils.isEmpty(name)) {
			holder.tvQuestionShareName.setText(name+"");
		}
		String content = wenDaAnswer.getContent();
		if (!TextUtils.isEmpty(content)) {
			holder.tvQuestionShareContent.setText(content);
		}
		String pubTime = TimeTransfer.long2StringDetailDate(context,wenDaAnswer.getCreate_ts()*1000);
		holder.tvQuestionShareDate.setText(pubTime);

		int size = context.getResources().getDimensionPixelSize(
				R.dimen.around_icon_head_size);
		String headUrl = wenDaAnswer.getImgurl();
		Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(headUrl);
		if (headBmp != null && !headBmp.isRecycled()) {
			holder.headImg.setImageBitmap(headBmp);
		} else {
				MyVolley.getImageLoader().get(headUrl,
						new CircleImageListener(context,headUrl,holder.headImg, size,
								size), size, size);
		}
		
		String picUrl = wenDaAnswer.getPicurl();
		//评论中添加的图片
		boolean isWifi = NetUtils.isWifiConnected(context);
		Bitmap contentBmp = null;
		if (TextUtils.isEmpty(picUrl)) {
			contentBmp = BitmapLruCache.getBitmapLruCache().get(picUrl);
		}
		if (contentBmp !=null && contentBmp.isRecycled()) {
			holder.imgContentPic.setImageBitmap(contentBmp);
		} else {
			if (isWifi) {
				holder.imgContentPic.setVisibility(View.VISIBLE);
				if (!TextUtils.isEmpty(picUrl)) {
					MyVolley.getImageLoader().get(picUrl,
							new PicImageListener(context, holder.imgContentPic,picUrl));
					holder.imgContentPic.setOnClickListener(new ViewClickListener(wenDaAnswer,position));
				}else {
					holder.imgContentPic.setVisibility(View.GONE);
				}
			}
		}
		/** 设置点赞的check **/
		if (WenDaManage.isSelect(context, wenDaAnswer.getAid(),wenDaAnswer.getCreate_ts()+"")) {
			holder.zanChk.setChecked(true);
			holder.zanChk.setEnabled(false);
		} else {
			holder.zanChk.setChecked(false);
			holder.zanChk.setEnabled(true);
		}
		holder.zanNum.setText(wenDaAnswer.getCount()+"");
		
		holder.zanChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				dianZan(wenDaAnswer);
			}
		});
		
		int floorNum = count - position;
		holder.tvFloor.setText(floorNum+ context.getResources().getString(R.string.floor_num)+"   ·");
		return convertView;
	}

	public void setAnswerConut(int i){
		count = i;
	}
	

	static class AllViewHolder {
		
		ImageView headImg;
		ImageView imgContentPic;
		TextView tvQuestionShareName;
		TextView tvQuestionShareContent;
		TextView tvQuestionShareDate;
		TextView tvFloor;
		TextView zanNum; 	//点赞数量 
		CheckBox zanChk;   //点赞checkbox

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
			tvFloor = (TextView) view.findViewById(R.id.tv_floor);
			zanNum = (TextView) view.findViewById(R.id.zan_num);
			zanChk = (CheckBox) view.findViewById(R.id.zan_chk);
		}
	}
	
	class ViewClickListener implements OnClickListener{

		private WenDaAnswer wenDaAnswer;
		private int position = 0;
		
		public ViewClickListener(WenDaAnswer wenDaAnswer,int position) {
			this.wenDaAnswer = wenDaAnswer;
			this.position = position;
		}
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.imgQuestionShare:
				Constant.is_refresh = false;
				Intent intent = new Intent(context, PhotoActivity.class);
				intent.putExtra(PhotoActivity.MODE_PICZOMM, wenDaAnswer.getPicurl());
				((Activity)context).startActivity(intent);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 回复点赞
	 * @param wenDaAnswer
	 */
	private void dianZan( final WenDaAnswer wenDaAnswer){
		final int count = wenDaAnswer.getCount();
		wenDaAnswer.setCount(count+1);
		WenDaManage.insertItem(context, wenDaAnswer);
		WenDaDetailAdapter.this.notifyDataSetChanged();
		ServiceProvider.pollAnswer(context,wenDaAnswer.getAid(), wenDaAnswer.getCreate_ts()+"", new VolleyListener(context) {
			
			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				return;
//				int errcode = response.optInt(Net.CODE);
//				if(errcode == Net.SUCCESS){
//				}
			}
		});
	}
	
}
	