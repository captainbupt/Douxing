package com.badou.mworking;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.CoursewareScoreDilog;
import com.badou.mworking.widget.CoursewareScoreDilog.CoursewareScoreDilogListener;
import com.joanzapata.pdfview.PDFView;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 类: <code> BaseViewerActivity </code> 功能描述: pdf 显示页面 创建人: 葛建锋 创建日期: 2014年9月11日
 * 上午11:35:02 开发环境: JDK7.0
 */
public class BaseViewerActivity extends BaseNoTitleActivity implements OnClickListener {

	public static final String VALUE_TRAIN = "train";
	
	private Handler handle;//处理404错误
	private static int URL_NOT_FOUND = 9;

	private Train train;// 实体类
	private LinearLayout ll_dianZan;   //点赞布局
	private LinearLayout ll_comment;   //评论布局
	private TextView tvComment;		//评论数量
	private TextView tvZan;			//点赞数量
	private ProgressDialog mProgressDialog;

	/** action 左侧iv **/
	private ImageView actionbarLeftLv; // 标题栏左边返回图标
	private ImageView actionbarRight; // 标题栏右边图标
	private TextView actionbarTitleTv; // 标题栏标题

	private PDFView pdfView;
	
	/**
	 * Called when the activity is first created.
	 */
	@SuppressLint("HandlerLeak")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.basevieweractivity);
		//页面滑动关闭
		layout.attachToActivity(this);
		try {
			train = (Train) getIntent().getBundleExtra(VALUE_TRAIN)
					.getSerializable(VALUE_TRAIN);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		initAction();
		initView();
		initLisener();
		String webGone = "";
/*		String webGone = getIntent().getStringExtra(
				TrainActivity.KEY_webView_pdf);*/
		if (webGone == null || "".equals(webGone)){// || !webGone.equals(TrainActivity.KEY_webView_pdf)) {
			// 显示pdf
			try {
				String filePath = FileUtils.getTrainCacheDir(mContext) + train.getRid() + ".pdf";
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
		} else {// 显示网页
			final WebView mWebView = new WebView(mContext);
			LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			WebSettings settings = mWebView.getSettings();
			mWebView.setLayoutParams(rl);
			settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
			settings.setJavaScriptEnabled(true);
			settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
			mWebView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					view.loadUrl(url);
					return true;
				}
			});

			boolean netConnet = NetUtils.isNetConnected(mContext);
			try {
				if (!netConnet) {
					finish();
					ToastUtil.showNetExc(mContext);
				} else {
					if (train == null) {
						finish();
					} else {
						String webUrl = train.getRid();
						String company = SP.getStringSP(mContext,SP.DEFAULTCACHE,Constant.COMPANY, "badou");
						final String url = Constant.TRAIN_IMG_SHOW  + company + File.separator + webUrl + Constant.TRAIN_IMG_FORMAT;
						getRespStatus(url);   
						
						handle =  new Handler(){
							@Override
							public void handleMessage(Message msg) {
								super.handleMessage(msg);
								if (msg.what == URL_NOT_FOUND) {
									if (status == 200) {
										mWebView.loadUrl(url);
									}else {
										ToastUtil.showToast(BaseViewerActivity.this, R.string.web_error);
									}
								}
							}
						};
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	int status = 0;
	private void getRespStatus(final String url) {  
		new Thread(new Runnable() {
			@Override
			public void run() {
		        try {  
		        	status = 0;
		                HttpGet head = new HttpGet(url);  
		                HttpClient client = new DefaultHttpClient();  
		                HttpResponse resp = client.execute(head);  
		                status = resp.getStatusLine().getStatusCode();  
		        } catch (IOException e) {
		        	status = 0;
		        	e.printStackTrace();
		        }  
		        handle.obtainMessage(URL_NOT_FOUND).sendToTarget();
			}
		}).start();
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
		if (NetUtils.isNetConnected(BaseViewerActivity.this)) {
			if (mProgressDialog != null) {
				mProgressDialog.show();
			}
			netUpdateNum(train.getRid());
		}
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
		pdfView = (PDFView) this.findViewById(R.id.pdfview);
		ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
		ll_dianZan = (LinearLayout) findViewById(R.id.ll_dianZan);
		tvComment = (TextView) findViewById(R.id.comment_num);
		tvZan = (TextView) findViewById(R.id.Zan_num);
		tvComment.setText(train.getCommentNum() + "");
		tvZan.setText(train.getEcnt() + "");
	}

	private void initLisener() {

		ll_comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// actionBar右侧button 跳转到评论页面
				Intent intent = new Intent(BaseViewerActivity.this,
						CommentActivity.class);
				intent.putExtra(CommentActivity.VALUE_FEEDBACK_COUNT,
						train.getFeedbackCount());
				intent.putExtra(CommentActivity.VALUE_RID, train.getRid());
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});

		ll_dianZan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showPingfenDilog();
			}
		});
	}

	/**
	 * 更新点赞数和评论数
	 * 
	 * @param rid
	 */
	private void netUpdateNum(String rid) {
		String[] rids = { rid };
		// 发起网络请求，获取课件的点赞数
		ServiceProvider.doUpdateFeedbackCount(BaseViewerActivity.this, rids,
				new VolleyListener(BaseViewerActivity.this) {
					@Override
					public void onResponse(Object responseObject) {

						if (mProgressDialog != null) {
							mProgressDialog.dismiss();
						}
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
							for (int i = 0; i < resultArray.length(); i++) {
								JSONObject jsonObject = resultArray
										.optJSONObject(i);
								String rid = jsonObject
										.optString(ResponseParams.RESOURCE_ID);
								int feedbackCount = jsonObject
										.optInt(ResponseParams.ZAN_NUM);
								int comment = jsonObject
										.optInt(ResponseParams.COMMENT_NUM);
								if (train.getRid().equals(rid)) {
									train.setCommentNum(comment);
									train.setFeedbackCount(feedbackCount);
								}
							}

						} catch (Exception e) {
							if (null != mProgressDialog
									&& BaseViewerActivity.this != null
									&& !BaseViewerActivity.this.isFinishing()) {
								mProgressDialog.dismiss();
							}
						} finally {
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						super.onErrorResponse(error);
						if (null != mProgressDialog
								&& BaseViewerActivity.this != null
								&& !BaseViewerActivity.this.isFinishing()) {
							mProgressDialog.dismiss();
						}
					}

				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_right:
			String titleStr = getResources().getString(R.string.statistical_data);
			String uid = ((AppApplication) getApplicationContext()).getUserInfo().getUserId();
			String url = Net.getRunHost(BaseViewerActivity.this)+Net.getTongji(uid,train.getRid());
			Intent intent = new Intent();
			intent.setClass(BaseViewerActivity.this, BackWebActivity.class);
			intent.putExtra(BackWebActivity.VALUE_URL,url);
			intent.putExtra(BackWebActivity.VALUE_TITLE,titleStr);
			startActivity(intent);
			break;
		case R.id.iv_actionbar_left:
			BaseViewerActivity.this.finish();
			break;
		default:
			break;
		}
	}
	
	private void showPingfenDilog(){
		// 课件评分
		if(train!=null){
			String coursewareScore = train.getCoursewareScore();
			new CoursewareScoreDilog(BaseViewerActivity.this,train.getRid(),coursewareScore,new CoursewareScoreDilogListener() {
				
				@Override
				public void positiveListener(int coursewareScore) {
					train.setCoursewareScore(coursewareScore+"");
					tvZan.setText(train.getEcnt() + 1 + "");
					//TrainActivity.pingfen = coursewareScore;
				}
			}).show();
		}
	}
	
	/**
	 * 功能描述: 添加返回按钮，弹出是否退出应用程序对话框
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			BaseViewerActivity.this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * c初始化action 布局
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
		String title = SP.getStringSP(BaseViewerActivity.this, SP.TRAINING, train.getTag()+"", "");
		actionbarTitleTv.setText(title);
		actionbarLeftLv.setImageResource(R.drawable.title_bar_back_normal);
	}
}
