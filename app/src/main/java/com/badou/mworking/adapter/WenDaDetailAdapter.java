package com.badou.mworking.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.android.volley.VolleyError;
import com.badou.mworking.PhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.WenDaManage;
import com.badou.mworking.model.WenDaAnswer;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;

/**
 * @author 葛建锋
 * 问答详情页面
 */
public class WenDaDetailAdapter extends BaseAdapter{
	
	private ArrayList<WenDaAnswer> wenDaAnswers;
	private Context context;
	private int count = 0 ;
	private LayoutInflater mInflater;
	private WaitProgressDialog mProgressDialog;
	private String qid;
	
	public WenDaDetailAdapter(Context context,ArrayList<WenDaAnswer> wenDaAnswers,String qid) {
		super();
		this.qid = qid;
		this.context = context;
		this.wenDaAnswers = wenDaAnswers;
		this.mInflater = LayoutInflater.from(context);
		mProgressDialog = new WaitProgressDialog(context,"删除中，请稍后...");
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
		
		final int floorNum = count - position;
		holder.tvFloor.setText(floorNum+ context.getResources().getString(R.string.floor_num)+"   ·");
		
		if (((AppApplication) context.getApplicationContext()).getUserInfo()
				.isAdmin || name.equals("我")) {
			holder.tvQuestionShareDelete
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							new AlertDialog.Builder(context)
									.setTitle(R.string.myQuan_dialog_title_tips)
									.setMessage(
											context.getResources().getString(
													R.string.tip_delete_confirmation))
									.setPositiveButton(
											R.string.text_ok,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													deleteReply(floorNum);
												}

											})
									.setNegativeButton(R.string.text_cancel,
											null).show();

						}
					});
		} else {
			holder.tvQuestionShareDelete.setVisibility(View.GONE);
		}
		
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
		TextView tvQuestionShareDelete;

		public AllViewHolder(View view) {
			imgContentPic = (ImageView) view.findViewById(R.id.imgQuestionShare);
			headImg = (ImageView) view
					.findViewById(R.id.iv_adapter_comment_head);
			tvQuestionShareName = (TextView) view
					.findViewById(R.id.tv_adapter_comment_name);
			tvQuestionShareContent = (TextView) view
					.findViewById(R.id.tv_adapter_comment_content);
			tvQuestionShareDate = (TextView) view
					.findViewById(R.id.tv_adapter_comment_date);
			tvFloor = (TextView) view.findViewById(R.id.tv_floor);
			zanNum = (TextView) view.findViewById(R.id.zan_num);
			zanChk = (CheckBox) view.findViewById(R.id.zan_chk);
			tvQuestionShareDelete = (TextView) view
					.findViewById(R.id.tvQuestionShareDelete);
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
	
	private void deleteReply(final int floor){
		ServiceProvider.deleteReplyComment(context,qid,floor,new VolleyListener(context) {
			
			@Override
			public void onResponse(Object responseObject) {
				if (null != mProgressDialog && context != null) {
					mProgressDialog.dismiss();
				}
				JSONObject response = (JSONObject) responseObject;
				if (responseObject == null) {
					ToastUtil.showNetExc(context);
					return;
				}
				int code = response.optInt(Net.CODE);
				if (code==Net.LOGOUT) {
					AppApplication.logoutShow(context);
					return;
				}
				if (Net.SUCCESS != code) {
					ToastUtil.showNetExc(context);
					return;
				}
				ToastUtil.showToast(context, "删除评论成功！");
				int position = count-floor;
				wenDaAnswers.remove(position);
				notifyDataSetChanged();
			}

			@Override
			public void onErrorResponse(VolleyError arg0) {
				super.onErrorResponse(arg0);
				if (null != mProgressDialog && context != null) {
					mProgressDialog.dismiss();
				}
			}
		});
	}
	
}
	