package com.badou.mworking.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.AroundDetailActivity;
import com.badou.mworking.MyGroupActivity;
import com.badou.mworking.PhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.TongSHQVideoPlayActivity;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.TongShQuResManage;
import com.badou.mworking.fragment.TongSHQFragments;
import com.badou.mworking.model.Question;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;

/**
 * 
 * 类: <code> AroundAdapters </code> 功能描述:同事圈adapter 创建人:董奇 创建日期: 2014年7月23日
 * 下午7:02:55 开发环境: JDK7.0
 */
public class TongShiQuanAdapter extends BaseAdapter {

	private List<Question> mData = new ArrayList<Question>();
	private Context mContext;
	private TongSHQFragments mFragment;
	private LayoutInflater mInflater;

	public void setDatas(List<Question> mData) {
		this.mData.clear();
		this.mData.addAll(mData);
		notifyDataSetChanged();
	}

	public void addDatas(List<Question> Questions) {
		if (this.mData == null)
			this.mData = Questions;
		else {
			for (Question temp : Questions) {
				this.mData.add(temp);
			}
		}
		notifyDataSetChanged();
	}

	public TongShiQuanAdapter(Context mContext, TongSHQFragments mFragment ) {
		super();
		mData = new ArrayList<Question>();
		this.mContext = mContext;
		this.mFragment = mFragment;
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
	 * 功能描述:
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	private View getAllView(final int position, View convertView,
			ViewGroup parent) {
		final AllViewHolder holder;
		if (convertView != null) {
			holder = (AllViewHolder) convertView.getTag();
		} else {
			convertView = mInflater.inflate(R.layout.adapter_arount_item,
					parent, false);
			holder = new AllViewHolder(convertView);
			convertView.setTag(holder);
		}
		final Question question = mData.get(position);

		String name = question.getEmployee_id();
		if (!TextUtils.isEmpty(name)) {
			holder.tvQuestionShareName.setText(name);
		}
		final String content = question.getContent();
		if (!TextUtils.isEmpty(content)) {
			if (content.length() > 100) {
				holder.showAllContentTv.setVisibility(View.VISIBLE);
				holder.tvQuestionShareContent.setText(content.substring(0, 100)
						+ "...");
			} else {
				holder.showAllContentTv.setVisibility(View.GONE);
				holder.tvQuestionShareContent.setText(content);
			}
		}
		String pubTime = TimeTransfer.long2StringDetailDate(mContext,
				question.getPublish_ts());
		holder.tvQuestionShareDate.setText(pubTime);
		holder.tvQuestionShareNums.setText("" + question.getReply_no());
		int size = mContext.getResources().getDimensionPixelSize(
				R.dimen.around_icon_head_size);
		Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(
				question.getImgUrl());
		holder.headImg.setTag(question.getImgUrl());
		if (question.getImgUrl().equals(holder.headImg.getTag())) {
			if (headBmp != null && !headBmp.isRecycled()) {
				holder.headImg.setImageBitmap(headBmp);
				headBmp = null;
			} else {
				MyVolley.getImageLoader().get(
						question.getImgUrl(),
						new CircleImageListener(mContext, question.getImgUrl(),
								holder.headImg, size, size), size, size);
			}
		}
		// 评论中添加的图片
		boolean isWifi = NetUtils.isWifiConnected(mContext);
		// 逻辑1：只有在wifi状态下显示图片
		String imgUrl = question.getContentPicUrl();
		Bitmap contentBmp = BitmapLruCache.getBitmapLruCache().get(imgUrl);
		holder.imgContentPic.setImageBitmap(null);
		// 判断缓存中是否有该图片
		if (contentBmp != null && !contentBmp.isRecycled()) {
			holder.imgContentPic.setVisibility(View.VISIBLE);
			holder.imgContentPic.setImageBitmap(contentBmp);
			contentBmp = null;
			isShowShipingSign(question, holder);
		} else {
			// 没有的话，判断是否是wifi网络
			if (isWifi) {
				MyVolley.getImageLoader().get(
						imgUrl,
						new PicImageListener(mContext, holder.imgContentPic,
								imgUrl));
				isShowShipingSign(question, holder);
			} else {
				// 判断是否在2G/3G下显示图片
				boolean isShowImg = SP.getBooleanSP(mContext, SP.DEFAULTCACHE,
						"pic_show", false);
				if (!isShowImg) {
					MyVolley.getImageLoader().get(
							imgUrl,
							new PicImageListener(mContext,
									holder.imgContentPic, imgUrl));
					isShowShipingSign(question, holder);
				} else {
					if (!TextUtils.isEmpty(imgUrl)) {
						holder.shenliuliangTv.setVisibility(View.VISIBLE);
					} else {
						holder.shenliuliangTv.setVisibility(View.GONE);
					}
					holder.imgContentPic.setVisibility(View.GONE);
				}
			}
		}
		holder.imgContentPic
				.setOnClickListener(new ImageClickListener(question));
		holder.headImg.setOnClickListener(new ImageClickListener(question));
		/*** 评论弹出dialog监听 **/
		holder.llComment.setOnClickListener(new CommentOnClick(position,
				question));
		/** 设置点赞数和监听 **/
		int credit_num = question.getCredit_no();
		holder.tvChkNum.setText(credit_num + "");
		// 设置显示级别
		LVUtil.setTextViewBg(holder.tvLv, question.getCircle_lv());
		/** 设置点赞的check **/
		if (TongShQuResManage.isSelect(mContext, question.getQid())) {
			holder.chk.setChecked(true);
		} else {
			holder.chk.setChecked(false);
		}
		holder.layoutCredit.setOnClickListener(new CreditListener(credit_num,
				question, holder.chk, holder.tvChkNum));
		holder.showAllContentTv
				.setOnClickListener(new ShowAllContent(question));

		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

				builder.setTitle("请选择操作")
						.setItems(new String[] { "复制" },
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ClipboardManager clip = (ClipboardManager) mContext
												.getSystemService(Context.CLIPBOARD_SERVICE);
										clip.setText(content); // 复制
										ToastUtil.showToast(mContext,
												"内容已复制到剪切板");
									}
								}).show();
				return true;
			}
		});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (mFragment.lvIsEnable) {
					mFragment.lvIsEnable = false;
					Constant.is_refresh = false;
					TongSHQFragments.clickPostion = position;
					// 跳转到单条的Item的页面，并传递数据
					Question question = getItem(position);
					Intent intent = new Intent(mContext,
							AroundDetailActivity.class);
					intent.putExtra(AroundDetailActivity.VALUE_QUESTION,
							question);
					mFragment.getActivity().startActivityForResult(intent, TongSHQFragments.requestCode);
					// 设置切换动画，从右边进入，左边退出
					mFragment.getActivity().overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
				}
			}
		});

		return convertView;
	}

	/**
	 * 功能描述:是否显示视屏标志
	 */
	private void isShowShipingSign(Question question, AllViewHolder holder) {
		String videoUrl = question.getVideourl();
		if (TextUtils.isEmpty(videoUrl)) {
			holder.tongshiquanShipingImg.setVisibility(View.GONE);
			return;
		} else {
			holder.tongshiquanShipingImg.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 类: <code> AllViewHolder </code> 功能描述: 提交评论 创建人:董奇 创建日期: 2014年7月18日
	 * 下午8:52:45 开发环境: JDK7.0
	 */
	static class AllViewHolder {

		ImageView headImg;// 头像
		ImageView imgContentPic;// 图片
		ImageView tongshiquanShipingImg; // 同事圈视屏标志图片

		TextView tvQuestionShareName;// 用户名称
		TextView tvQuestionShareContent;// 评论的内容
		TextView tvQuestionShareDate;// 下方日期时间
		TextView tvQuestionShareNums;// 评论的数量
		TextView tvChkNum;// 点赞数
		TextView tvLv; // 等级
		TextView showAllContentTv; // 显示全文
		TextView shenliuliangTv; // 省流量模式

		LinearLayout llComment;
		LinearLayout layoutCredit;// 点赞区域

		CheckBox chk;// 点赞chk

		public AllViewHolder(View view) {
			llComment = (LinearLayout) view.findViewById(R.id.ll_comment);
			imgContentPic = (ImageView) view
					.findViewById(R.id.imgQuestionShare);
			headImg = (ImageView) view
					.findViewById(R.id.ivAdapterQuestionShareIcon);
			tongshiquanShipingImg = (ImageView) view
					.findViewById(R.id.tongshiquan_shiping_img);
			tvQuestionShareName = (TextView) view
					.findViewById(R.id.tvQuestionShareName);
			tvQuestionShareContent = (TextView) view
					.findViewById(R.id.tvQuestionShareContent);
			tvQuestionShareDate = (TextView) view
					.findViewById(R.id.tvQuestionShareDate);
			tvQuestionShareNums = (TextView) view
					.findViewById(R.id.tvQuestionShareNums);
			chk = (CheckBox) view.findViewById(R.id.chb_credit);
			tvChkNum = (TextView) view.findViewById(R.id.tv_credit_num);
			layoutCredit = (LinearLayout) view.findViewById(R.id.layout_credit);
			tvLv = (TextView) view.findViewById(R.id.tv_user_center_top_level);
			showAllContentTv = (TextView) view
					.findViewById(R.id.show_all_content_tv);
			shenliuliangTv = (TextView) view.findViewById(R.id.shenliuliang_tv);
		}
	}

	class ImageClickListener implements OnClickListener {
		private Question question;

		public ImageClickListener(Question question) {
			this.question = question;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			// 点击内容图片或者视屏
			case R.id.imgQuestionShare:
				String videoUrl = question.getVideourl();
				// 判断视屏URL是否为空， 为空的话，点击则放大图片，否则跳到视屏播放页面
				if (TextUtils.isEmpty(videoUrl)) {
					Intent intent = new Intent(mContext, PhotoActivity.class);
					intent.putExtra(PhotoActivity.MODE_PICZOMM,
							question.getContentPicUrl());
					((Activity) mContext).startActivity(intent);
				} else {
					Intent intent = new Intent(mContext,
							TongSHQVideoPlayActivity.class);
					intent.putExtra(TongSHQVideoPlayActivity.VIDEOURL,
							question.getVideourl());
					intent.putExtra(TongSHQVideoPlayActivity.QID,
							question.getQid());
					((Activity) mContext).startActivity(intent);
				}
				break;
			// 点击头像，显示该人我的圈列表
			case R.id.ivAdapterQuestionShareIcon:
				String uid = question.getUid();
				if (TextUtils.isEmpty(uid)) {
					return;
				}
				Intent intent = new Intent(mContext, MyGroupActivity.class);
				intent.putExtra("uid", uid);
				((Activity) mContext).startActivity(intent);
				break;
			default:
				break;
			}
		}
	}

	class CreditListener implements OnClickListener {
		int creditNum = 0;
		private Question question;
		CheckBox checkBox;
		TextView tvNum;

		public CreditListener(int creditNum, Question question,
				CheckBox checkBox, TextView tvNum) {
			this.creditNum = creditNum;
			this.question = question;
			this.checkBox = checkBox;
			this.tvNum = tvNum;
		}

		@Override
		public void onClick(View arg0) {
			if (checkBox.isChecked()) {
				return;
			} else {

				question.setCredit_no(creditNum + 1);
				checkBox.setChecked(true);
				tvNum.setText(question.getCredit_no() + "");

				/** 调用同事圈点赞接口 提交点赞 **/
				ServiceProvider.doSetCredit(mContext, question.getQid(),
						new VolleyListener(mContext) {

							@Override
							public void onResponse(Object responseObject) {
								JSONObject respon = (JSONObject) responseObject;
								try {
									int code = respon.optInt(Net.CODE);
									if (code == Net.LOGOUT) {
										AppApplication.logoutShow(mContext);
										return;
									}
									if (Net.SUCCESS != code) {
										ToastUtil.showToast(mContext,
												R.string.credit_fail);
										return;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								TongShQuResManage
										.insertItem(mContext, question);
							}

							@Override
							public void onErrorResponse(VolleyError error) {
								super.onErrorResponse(error);
							}
						});
			}
		}
	}

	class CommentOnClick implements OnClickListener {
		int position = -1;
		Question question;

		public CommentOnClick(int position, Question question) {
			this.position = position;
			this.question = question;
		}

		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(mContext, AroundDetailActivity.class);
			intent.putExtra(AroundDetailActivity.VALUE_QUESTION, question);
			TongSHQFragments.clickPostion = position;
			((Activity) mContext).startActivityForResult(intent,
					TongSHQFragments.requestCode);

		}
	}

	/**
	 * 显示全文的监听
	 * */
	class ShowAllContent implements OnClickListener {

		Question question;

		public ShowAllContent(Question question) {
			this.question = question;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, AroundDetailActivity.class);
			intent.putExtra(AroundDetailActivity.VALUE_QUESTION, question);
			mContext.startActivity(intent);
		}
	}
}
