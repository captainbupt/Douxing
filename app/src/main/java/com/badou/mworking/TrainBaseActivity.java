package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.Net;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.CoursewareScoreDilog;
import com.badou.mworking.widget.CoursewareScoreDilog.CoursewareScoreDilogListener;

import java.io.File;

/**
 * 
 * 类: <code> TrainBaseActivity </code> 功能描述: 微培训父类,用于显示action 和 bottom的布局 创建人:
 * yunhen 创建日期: 2015年1月7日 下午2:44:10 开发环境: JDK6.0
 */
public abstract class TrainBaseActivity extends BaseNoTitleActivity {

	/** action 左侧iv **/
	public ImageView ivLeft;
	/** action 右侧 iv **/
	public ImageView ivRight;
	/** action 中间tv **/
	public TextView tvTitle;
	
	/**可点击的布局(点赞)**/
	public LinearLayout llZanBtn;
	/**显示点赞数量的 tv**/
	public TextView tvZanNum;
	/**可点击的布局(评论)**/
	public LinearLayout llCommBtn;
	/**显示评论数量**/
	public TextView tvCommNum;
	
	private Train train;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		if (getTrain() != null) {
			train = getTrain();
		}else {
			Log.w("badou", "实体类是空");
		}
		initAction();
		initView();
		initLisener();
	}

	/** 获取xml布局 */
	public abstract int getLayoutId();

	public abstract void setRightClick();
	
	public abstract Train getTrain();

	
	protected void initView(){
		/**可点击的布局(点赞)**/
		llZanBtn = (LinearLayout) this.findViewById(R.id.ll_dianZan);
		/**显示点赞数量的 tv**/
		tvZanNum = (TextView) this.findViewById(R.id.Zan_num);
		/**可点击的布局(评论)**/
		llCommBtn = (LinearLayout) this.findViewById(R.id.ll_comment);
		/**显示评论数量**/
		tvCommNum = (TextView) this.findViewById(R.id.comment_num);
	}
	
	/**
	 * c初始化action 布局
	 *
	 */
	private void initAction() {
		ivLeft = (ImageView) this.findViewById(R.id.iv_actionbar_left);
		tvTitle = (TextView) this.findViewById(R.id.txt_actionbar_title);
		ivRight = (ImageView) this.findViewById(R.id.iv_actionbar_right);
		boolean isAdmin = ((AppApplication) getApplicationContext())
				.getUserInfo().isAdmin();
		if(isAdmin){
			ivRight.setBackgroundResource(R.drawable.admin_tongji);
			ivRight.setVisibility(View.VISIBLE);
			ivRight.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					String titleStr = getResources().getString(R.string.statistical_data);
					String uid = ((AppApplication) getApplicationContext()).getUserInfo().getUserId();
					String url = Net.getRunHost(TrainBaseActivity.this)+Net.getTongji(uid,train.rid);
					Intent intent = new Intent();
					intent.setClass(TrainBaseActivity.this, BackWebActivity.class);
					intent.putExtra(BackWebActivity.VALUE_URL,url);
					intent.putExtra(BackWebActivity.VALUE_TITLE,titleStr);
					startActivity(intent);
				}
			});
		}
		ivLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				TrainBaseActivity.this.finish();
			}
		});
		// 获取分类名
		String title = SP.getStringSP(TrainBaseActivity.this, SP.TRAINING, train.tag+"", "");
		tvTitle.setText(title);
		ivLeft.setImageResource(R.drawable.title_bar_back_normal);
	}
	
	/**
	 * 功能描述: 显示评分对话框
	 */
	public void showPingfenDilog(){
		// 课件评分
		if(train!=null){
			String coursewareScore = train.coursewareScore;
			String saveFilePath = FileUtils.getTrainCacheDir(TrainBaseActivity.this);
			String mp3fileStr = saveFilePath + train.rid+".mp3";
			String mp4fileStr = saveFilePath + train.rid+".mp4";
			File mp3file = new File(mp3fileStr);
			File mp4file = new File(mp4fileStr);
			// 如果文件存在则显示评分，或者显示了多少分，否则不让评分并作提醒
			if(mp3file.exists()||mp4file.exists()){
				new CoursewareScoreDilog(mContext, train.rid, coursewareScore, new CoursewareScoreDilogListener() {

					@Override
					public void positiveListener(int coursewareScore) {
						train.coursewareScore = coursewareScore + "";
						tvZanNum.setText(train.ecnt + 1 + "");
						Intent intent = new Intent();
						intent.putExtra(TrainActivity.KEY_RATING, coursewareScore);
						intent.putExtra(TrainActivity.KEY_RID, train.rid);
						setResult(RESULT_OK, intent);
					}
				}).show();
			}else{
				ToastUtil.showToast(TrainBaseActivity.this, getResources().getString(R.string.kejian_tips));
			}
		}
	}
	
	/**
	 * 功能描述: 添加返回按钮，弹出是否退出应用程序对话框
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			TrainBaseActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(CommentActivity.success){
			tvCommNum.setText(CommentActivity.commentnum+"");
			CommentActivity.success = false;
		}
	}
	
	private void initLisener() {

		llCommBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setRightClick(); 
			}
		});

		llZanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPingfenDilog();
			}
		});
	}
}
