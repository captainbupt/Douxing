package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Notice;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.SP;
import com.joanzapata.pdfview.PDFView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * 类: <code> BaseViewerActivity </code> 功能描述: pdf 显示页面 创建人: 葛建锋 创建日期: 2014年9月11日
 * 上午11:35:02 开发环境: JDK7.0
 */
public class NoticePdfViewerActivity extends BaseNoTitleActivity implements OnClickListener {
	
	public static final String VALUE_NOTICE = "notice";
	
	private Notice notice;// 实体类

	/** action 左侧iv **/
	private ImageView actionbarLeftLv; // 标题栏左边返回图标
	private ImageView actionbarRight; // 标题栏右边图标
	private TextView actionbarTitleTv; // 标题栏标题

	private PDFView pdfView;
	
	private LinearLayout commentRelat;
	private TextView commentNum;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticepdfvieweractivity);
		//页面滑动关闭
		layout.attachToActivity(this);
		try {
			notice = (Notice) getIntent().getBundleExtra(VALUE_NOTICE)
					.getSerializable(VALUE_NOTICE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initAction();
		initView();
			// 显示pdf
		try {
			String filePath = FileUtils.getTrainCacheDir(mContext) + notice.getRid() + ".pdf";
			File file = new File(filePath);
			if (file.exists()) {
				//加载pdf文件
				pdfView.fromFile(file).showMinimap(false).enableSwipe(true)
						.load();
			}else {
				Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// 捕获打开pdf异常
			e.printStackTrace();
		}
	}
	
	public void decodingProgressChanged(final int currentlyDecoding) {
		runOnUiThread(new Runnable() {
			public void run() {
				getWindow().setFeatureInt(
						Window.FEATURE_INDETERMINATE_PROGRESS,
						currentlyDecoding == 0 ? 10000 : currentlyDecoding);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		netUpdateNum(notice.getRid());
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void initView() {
		pdfView = (PDFView) findViewById(R.id.pdfview);
		commentRelat = (LinearLayout) findViewById(R.id.comment_relat);
		commentNum = (TextView) findViewById(R.id.click_num);
		commentRelat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(NoticePdfViewerActivity.this,CommentActivity.class);
				intent.putExtra(CommentActivity.VALUE_RID, notice.getRid());
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_right:
			String titleStr = getResources().getString(R.string.statistical_data);
			String uid = ((AppApplication) getApplicationContext()).getUserInfo().getUserId();
			String url = Net.getRunHost(NoticePdfViewerActivity.this)+Net.getTongji(uid,notice.getRid());
			Intent intent = new Intent();
			intent.setClass(NoticePdfViewerActivity.this, BackWebActivity.class);
			intent.putExtra(BackWebActivity.VALUE_URL,url);
			intent.putExtra(BackWebActivity.VALUE_TITLE,titleStr);
			startActivity(intent);
			break;
		case R.id.iv_actionbar_left:
			NoticePdfViewerActivity.this.finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			NoticePdfViewerActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * c初始化action 布局
	 * @param onclick
	 */
	private void initAction() {
		actionbarLeftLv = (ImageView) this.findViewById(R.id.iv_actionbar_left);
		actionbarTitleTv = (TextView) this.findViewById(R.id.txt_actionbar_title);
		actionbarRight = (ImageView) this.findViewById(R.id.iv_actionbar_right);
		boolean isAdmin = ((AppApplication) getApplicationContext())
				.getUserInfo().isAdmin();
		if(isAdmin){
			actionbarRight.setBackgroundResource(R.drawable.admin_tongji);
			actionbarRight.setVisibility(View.VISIBLE);
			actionbarRight.setOnClickListener(this);
		}
		actionbarLeftLv.setOnClickListener(this);
		// 获取分类名
		String title = SP.getStringSP(NoticePdfViewerActivity.this, SP.NOTICE, notice.getTag()+"", "");
		actionbarTitleTv.setText(title);
		actionbarLeftLv.setImageResource(R.drawable.title_bar_back_normal);
	}
	
	/**
	 * 更新点赞数和评论数
	 * 
	 * @param rid
	 */
	private void netUpdateNum(final String rid) {
		String[] rids = { rid };
		// 发起网络请求，获取课件的点赞数
		ServiceProvider.doUpdateFeedbackCount(mContext, rids,
				new VolleyListener(mContext) {
					@Override
					public void onResponse(Object responseObject) {
						int comment = 0;
						JSONObject response = (JSONObject) responseObject;
						try {
							int code = response.optInt(Net.CODE);
							if (code==Net.LOGOUT) {
								AppApplication.logoutShow(mContext);
								return;
							}
							if (code != Net.SUCCESS) {
								return;
							}
							JSONArray resultArray = response
									.optJSONArray(Net.DATA);
								JSONObject jsonObject = resultArray
										.optJSONObject(0);
									comment = jsonObject
											.optInt(ResponseParams.COMMENT_NUM);
									commentNum.setText(comment + getResources().getString(R.string.taolun));
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				});
	}
}
