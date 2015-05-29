package com.badou.mworking.base;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.util.NetUtils;

/**
 * webview 基础页面
 */
public abstract class BaseBackWebViewActivity extends BaseBackActionBarActivity implements OnClickListener{
	
	protected LinearLayout llNetException;
	protected LinearLayout ll_dianZan;   //点赞布局
	protected LinearLayout ll_comment;   //评论布局
	protected LinearLayout commentRelat;
	protected LinearLayout weipeixuncommentRelat;
	protected RelativeLayout rlComment;

	protected TextView tvComment;		//评论数量
	protected TextView tvZan;			//点赞数量
	protected TextView tvCommentNum;
	protected TextView tvBadouNetExceptionRepeat;
	
	protected ImageView ivNetException;

	protected WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawable(null);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.view_web);

		mWebView = (WebView) findViewById(R.id.wv_web_view);
		llNetException = (LinearLayout) findViewById(R.id.llNetException);
		tvBadouNetExceptionRepeat = (TextView) findViewById(R.id.tvBadouNetExceptionRepeat);
		ivNetException = (ImageView) findViewById(R.id.ivNetException);
		tvCommentNum = (TextView) findViewById(R.id.click_num);
		commentRelat = (LinearLayout) findViewById(R.id.tv_user_progress_bottom);
		weipeixuncommentRelat = (LinearLayout) findViewById(R.id.weipeixuncomment_relat);
		rlComment = (RelativeLayout) findViewById(R.id.rl_comment);
		
		ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
		ll_dianZan = (LinearLayout) findViewById(R.id.ll_dianZan);
		tvComment = (TextView) findViewById(R.id.comment_num);
		tvZan = (TextView) findViewById(R.id.Zan_num);
		
		ll_comment.setOnClickListener(this);
		ll_dianZan.setOnClickListener(this);
		commentRelat.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean flag = NetUtils.isNetConnected(getApplicationContext());
		if (flag) {
			ivNetException.setVisibility(ImageView.GONE);
			llNetException.setVisibility(LinearLayout.GONE);
			mWebView.setVisibility(WebView.VISIBLE);
			mWebView.setHorizontalScrollBarEnabled(false);
		} else {
			ivNetException.setVisibility(ImageView.VISIBLE);
			llNetException.setVisibility(LinearLayout.VISIBLE);
			ivNetException.setVisibility(ImageView.VISIBLE);
			mWebView.setVisibility(WebView.GONE);
			tvBadouNetExceptionRepeat.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onResume();
				}
			});
		}
	}

	@Override
	public void finish() {
		super.finish();

	}
}
