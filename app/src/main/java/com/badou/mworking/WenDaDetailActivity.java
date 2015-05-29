package com.badou.mworking;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.badou.mworking.adapter.WenDaDetailAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Ask;
import com.badou.mworking.model.WenDaAnswer;
import com.badou.mworking.net.LVUtil;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoScrollListView;
import com.badou.mworking.widget.SwipeBackLayout;
import com.badou.mworking.widget.WaitProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.umeng.analytics.MobclickAgent;

/**
 * 问答详情页面
 */
public class WenDaDetailActivity extends BaseNoTitleActivity implements
		OnClickListener {
	
	private Ask ask;

	public static final String KEY_RELAY_NO = "KEY_RELAY_NO";
	private WenDaDetailAdapter wenDaDetailAdapter;

	private TextView delAsk;    //删除问答
	private WaitProgressDialog mProgressDialog;
	private ImageView imgTitlePic;// title显示的图片
	private NoScrollListView lvQuestion;

	private ArrayList<WenDaAnswer> wenDaAnswers = new ArrayList<WenDaAnswer>();

	private ImageView actionbarLeftImg;
	private TextView actionbarTitleTv;
	private TextView commentTv; // 我要回答
	private TextView lvTv;  //等级
	private TextView sixinTv; // 私信
	private TextView replayNum;  //回复数

	private PullToRefreshScrollView pullToRefreshScrollView;
	
	private ImageView wendaNoanswearRelay;  // 没有回答时显示的布局

	private int beginIndex = 1;
	
	public static Boolean ISREFRESHWENDAPINGLUN = false; //是否刷新问答评论
	
	public static int ANSWEARCOUNT = 0;
	
	private SwipeBackLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wendadetailactivity);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		WenDaDetailActivity.ANSWEARCOUNT = 0;   //清空数据
		init();
		setViewValue(ask);
		initListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if(WenDaDetailActivity.ISREFRESHWENDAPINGLUN){
			ANSWEARCOUNT = ANSWEARCOUNT + 1;
			wenDaDetailAdapter.setAnswerConut(ANSWEARCOUNT);
			replayNum.setText(ANSWEARCOUNT+"答复");
			WenDaDetailActivity.ISREFRESHWENDAPINGLUN = false;
			beginIndex = 1; 
			wenDaAnswers.clear();
			updateReply(beginIndex);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 初始化
	 */
	private void init() {
		actionbarLeftImg = (ImageView) findViewById(R.id.iv_actionbar_left);
		actionbarTitleTv = (TextView) findViewById(R.id.txt_actionbar_title);
		wendaNoanswearRelay = (ImageView) findViewById(R.id.wenda_noanswear_img);
		sixinTv = (TextView) findViewById(R.id.sixin_tv);
		lvTv = (TextView) findViewById(R.id.tv_user_center_top_level);
		replayNum = (TextView) findViewById(R.id.replay_num);
		actionbarLeftImg.setOnClickListener(this);
		actionbarTitleTv.setText("问答详情");
		commentTv = (TextView) findViewById(R.id.tv_user_progress_bottom);
		commentTv.setText("我要回答");
		commentTv.setOnClickListener(this);
		sixinTv.setOnClickListener(this);

		ask = (Ask) getIntent().getSerializableExtra("ask");
		LVUtil.setTextViewBg(lvTv, ask.getCircle_lv());
		replayNum.setText(ask.getCount()+"答复");
		
		// 自定义LinearLayout
		lvQuestion = (NoScrollListView) findViewById(R.id.lvQuestionAnswers);
		delAsk = (TextView) findViewById(R.id.tv_delete_comment);
		delAsk.setOnClickListener(this);
		mProgressDialog = new WaitProgressDialog(WenDaDetailActivity.this,
				R.string.message_wait);
		imgTitlePic = (ImageView) findViewById(R.id.imgTitleAroundDetail);
		String picUrl = ask.getPicurl();
		Bitmap titleBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(picUrl);
		if (titleBmp != null && !titleBmp.isRecycled()) {
			imgTitlePic.setImageBitmap(titleBmp);
		} else {
			MyVolley.getImageLoader().get(picUrl,
					new PicImageListener(WenDaDetailActivity.this, imgTitlePic, picUrl));
		}
		
		wenDaDetailAdapter = new WenDaDetailAdapter(WenDaDetailActivity.this,wenDaAnswers,ask.getAid());
		ANSWEARCOUNT = ask.getCount();
		wenDaDetailAdapter.setAnswerConut(ANSWEARCOUNT);
		lvQuestion.setAdapter(wenDaDetailAdapter);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_left:
			finish();
			break;
		case R.id.tv_user_progress_bottom:
			Intent intent = new Intent(this, WenDaAnswerActivity.class);
			intent.putExtra("aid", ask.getAid());
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.tv_delete_comment:
			new AlertDialog.Builder(mContext)
			.setTitle(R.string.myQuan_dialog_title_tips)
			.setMessage(
					mContext.getResources().getString(
							R.string.my_group_tishi))
			.setPositiveButton(R.string.text_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0,
								int arg1) {
							if (null != mProgressDialog && mContext != null) {
								mProgressDialog.show();
							}
							deleteASK();
						}
					}).setNegativeButton(R.string.text_cancel, null).show();
			break;
		case R.id.sixin_tv:
			Intent intent2 = new Intent(WenDaDetailActivity.this, ChatInfoActivity.class);
			intent2.putExtra(ChatInfoActivity.KEY_NAME, ask.getEid());
			intent2.putExtra(ChatInfoActivity.KEY_whom, ask.getWhom());
			intent2.putExtra(ChatInfoActivity.KEY_img, ask.getImgurl());
			startActivity(intent2);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 
	 * 功能描述:设置蓝色title显示内容
	 * 
	 * @param ask
	 */
	private void setViewValue(Ask ask) {
		if (ask != null) {
			TextView tvQuestionContent = (TextView) findViewById(R.id.tvQuestionContent);
			TextView tvQuestionName = (TextView) findViewById(R.id.tvQuestionName);
			TextView tvTiem = (TextView) this.findViewById(R.id.tv_time);// 时间
			ImageView ivHeadimg = (ImageView) this
					.findViewById(R.id.iv_user_head_icon);

			String content = ask.getContent();
			if (!TextUtils.isEmpty(content)) {
				tvQuestionContent.setText(content);
			}
				
			/**删除和私信逻辑 */
			String userUid = ((AppApplication) this.getApplicationContext())
					.getUserInfo().userId;
			String currentUid = ask.getUid();   
			int isGuanliYuan = ask.getDelop();      
			// 点击进入是自己
			if(userUid.equals(currentUid)){
				sixinTv.setVisibility(View.GONE);
				delAsk.setVisibility(View.VISIBLE);
			// 点击进入不是自己
			}else{
				// 是管理员
				if(isGuanliYuan == 1 ){
					sixinTv.setVisibility(View.VISIBLE);
					delAsk.setVisibility(View.VISIBLE);
				// 不是管理员	
				}else{
					sixinTv.setVisibility(View.VISIBLE);
					delAsk.setVisibility(View.GONE);
				}
			}
				
			tvQuestionName.setText(ask.getEid());
			tvTiem.setText(TimeTransfer.long2StringDetailDate(WenDaDetailActivity.this,
					ask.getCreate_ts()*1000) + "");
			String headImgUrl = ask.getImgurl();
			Bitmap userHeadBm = BitmapLruCache.getBitmapLruCache()
					.getCircleBitmap(headImgUrl);
			if (userHeadBm != null) {
				ivHeadimg.setImageBitmap(userHeadBm);
				userHeadBm = null;
			} else {
				/** 设置头像 **/
				int size = WenDaDetailActivity.this.getResources().getDimensionPixelSize(
						R.dimen.around_icon_head_size);
				MyVolley.getImageLoader().get(
						headImgUrl,
						new CircleImageListener(WenDaDetailActivity.this,headImgUrl,
								ivHeadimg, size, size));
			}
		}
	}

	/**
	 * 功能描述:删除我的圈中的item
	 *
	 */
	private void deleteASK() {
		ServiceProvider.deleteAsk(WenDaDetailActivity.this, ask.getAid(), new VolleyListener(WenDaDetailActivity.this) {
			
			@Override
			public void onResponse(Object responseObject) {
				if (null != mProgressDialog && WenDaDetailActivity.this != null) {
					mProgressDialog.dismiss();
				}
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (responseObject == null) {
					ToastUtil.showNetExc(WenDaDetailActivity.this);
					return;
				}
				if (code == Net.LOGOUT) {
					AppApplication.logoutShow(WenDaDetailActivity.this);
					return;
				}
				if (Net.SUCCESS != code) {
					ToastUtil.showNetExc(WenDaDetailActivity.this);
					return;
				}
				WenDActivity.ISDELETE = true;
				finish();
			}
		});
	}

	/**
	 * 功能描述:发送回复TextView设置监听,pullToRefreshScrollView设置下拉刷新监听
	 */
	private void initListener() {

		// 点击图片放大显示
		imgTitlePic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 如果视屏url为空则点击的时候显示图片，如果url不为空，点击的时候显示视屏
				Intent goToPhotoAct = new Intent(WenDaDetailActivity.this, PhotoActivity.class);
				goToPhotoAct.putExtra(PhotoActivity.MODE_PICZOMM,
						ask.getPicurl());
				startActivity(goToPhotoAct);
			}
		});

		pullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.ptrs_activity_around_detail);
		pullToRefreshScrollView
				.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ScrollView> refreshView) {
						updateReply(beginIndex);
					}
				});
		updateReply(1);
	}

	/**
	 * 功能描述:获取回答列表
	 */
	private void updateReply(int beginNum) {
		mProgressDialog.show();
		// 获取最新内容
		ServiceProvider.updateAnswerList(WenDaDetailActivity.this, beginNum,Constant.LIST_ITEM_NUM, ask.getAid(), new VolleyListener(WenDaDetailActivity.this) {
			
			@Override
			public void onResponse(Object responseObject) {
				if (null != mProgressDialog && WenDaDetailActivity.this != null
						&& !mActivity.isFinishing())
					mProgressDialog.dismiss();
				if (pullToRefreshScrollView.isRefreshing()) {
					pullToRefreshScrollView.onRefreshComplete();
				}
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (code == Net.LOGOUT) {
					AppApplication.logoutShow(mContext);
					return;
				}
				if (Net.SUCCESS != code) {
					ToastUtil.showNetExc(WenDaDetailActivity.this);
					return;
				}
				ArrayList<WenDaAnswer> wendaAnswersTemp = new ArrayList<WenDaAnswer>();
				JSONArray jsonArray = response.optJSONArray("data");
				if(jsonArray==null){
					return;
				}
				int length = jsonArray.length();
				if(length==0){
					if(beginIndex==1){
						lvQuestion.setVisibility(View.GONE);
						wendaNoanswearRelay.setVisibility(View.VISIBLE);
					}else{
						wendaNoanswearRelay.setVisibility(View.GONE);
						ToastUtil.showToast(WenDaDetailActivity.this, "没有更多了");
					}
					return;
				}
				wendaNoanswearRelay.setVisibility(View.GONE);
				lvQuestion.setVisibility(View.VISIBLE);
				for (int i = 0; i < length; i++) {
					JSONObject jsonObject = jsonArray.optJSONObject(i);
					WenDaAnswer wenDaAnswer = new WenDaAnswer(jsonObject);
					wendaAnswersTemp.add(wenDaAnswer);
				}
				beginIndex++;
				lvQuestion.setVisibility(View.VISIBLE);
				wenDaAnswers.addAll(wendaAnswersTemp);
				wenDaDetailAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	protected void onDestroy() {
		if (null != mProgressDialog && WenDaDetailActivity.this != null) {
			mProgressDialog.dismiss();
		}
		super.onDestroy();
	}
}
