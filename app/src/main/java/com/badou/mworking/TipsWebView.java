package com.badou.mworking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseFragmentActivity;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.util.MD5;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;

import org.json.JSONException;
import org.json.JSONObject;

public class TipsWebView extends BaseFragmentActivity {

	public static final String KEY_TipsWebView = "key_Tips";
	public static final String VALUE_TipsWebView_JSON = "key_json";
	public static final String KEY_TipsWebView_PHONE = "key_phone";
	public static final int ACT_TipsWebView = 1;
	public static final int ACT_Login = 2;
	private LinearLayout llNetException;
	private TextView tvBadouNetExceptionRepeat;
	private ImageView ivNetException;

	private TextView tvbottomBtn;

	protected static final String URL = "url";

	protected WebView mWebView;

	protected String mCurrentUrl;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		layout.attachToActivity(this);
		setContentView(R.layout.act_tiyan_web);
		mContext = TipsWebView.this;
		mWebView = (WebView) findViewById(R.id.webView1);
		tvbottomBtn = (TextView) this.findViewById(R.id.tv_bottom);
		llNetException = (LinearLayout) findViewById(R.id.llNetException);
		tvBadouNetExceptionRepeat = (TextView) findViewById(R.id.tvBadouNetExceptionRepeat);
		ivNetException = (ImageView) findViewById(R.id.ivNetException);
		// intent.putExtra(BackWebActivity.VALUE_URL,
		// "http://mworking.cn/badou/verify-help.html");
		// intent.putExtra("tiyan", 1);

		TextView titleTv = (TextView) initAction();
		final int tag = getIntent().getIntExtra(KEY_TipsWebView, 0);
		try {
			mCurrentUrl = getIntent().getStringExtra(BackWebActivity.VALUE_URL);
			
			if (ACT_TipsWebView == tag) {
				titleTv.setText(R.string.notGetMsg);
				tvbottomBtn.setText(R.string.toPhone);
				tvbottomBtn.setVisibility(View.GONE);
				mWebView.loadUrl(mCurrentUrl);
			} else if (ACT_Login == tag) {
				titleTv.setText(R.string.title_name_WenXin);
				tvbottomBtn.setVisibility(View.VISIBLE);
				tvbottomBtn.setText(R.string.tips_web_btn_toMain);
				mWebView.loadUrl(mCurrentUrl);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();

		}
		tvbottomBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (ACT_Login == tag) {
					try {
						loginSuccess(new JSONObject(getIntent().getStringExtra(VALUE_TipsWebView_JSON)));
						finish();
					} catch (JSONException e) {
						e.printStackTrace();
						finish();
					} catch (Exception e) {
						e.printStackTrace();
						finish();
					}
				} else if (ACT_TipsWebView == tag) {
					Intent phoneIntent = new Intent("android.intent.action.CALL",
							Uri.parse("tel:" + "4008233773"));
					startActivity(phoneIntent);
				}
				
			}
		});

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
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.setWebChromeClient(new InternalWebChromeClient());
			mWebView.setDownloadListener(new InternalDownloadListener());
		} else {
			ivNetException.setVisibility(ImageView.VISIBLE);
			llNetException.setVisibility(LinearLayout.VISIBLE);
			ivNetException.setVisibility(ImageView.VISIBLE);
			tvBadouNetExceptionRepeat.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onResume();
				}
			});
		}
	}

	/**
	 * c初始化action 布局
	 * 
	 * @param onclick
	 */
	private View initAction() {
		ImageView ivLeft = (ImageView) this
				.findViewById(R.id.iv_actionbar_left);
		TextView tvTitle = (TextView) this
				.findViewById(R.id.txt_actionbar_title);
		ImageView ivRight = (ImageView) this
				.findViewById(R.id.iv_actionbar_right);
		ivLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		ivRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
		ivRight.setVisibility(View.GONE);
		return tvTitle;
	}

	/**
	 * 登录成功 保存信息
	 * 
	 * @param username
	 * @param password
	 * @param jsonObject
	 *            登录成功返回的json
	 */
	private void loginSuccess(JSONObject jsonObject) {
		String shuffleStr = jsonObject.optJSONObject("shuffle").toString();
		SP.putStringSP(TipsWebView.this, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, shuffleStr);
		// 验证成功 跳转到ExperienceDetailAct
		String acount = getIntent().getStringExtra(KEY_TipsWebView_PHONE);
		UserInfo userInfo = new UserInfo();
		/***保存没MD5的用户账户 **/
		SP.putStringSP(mContext,SP.DEFAULTCACHE, LoginActivity.KEY_ACCOUNT, acount+"");
		userInfo.setUserInfo(new MD5().getMD5ofStr(acount), jsonObject);
		// 保存用户登录成功返回的信息 到sharePreferncers
		((AppApplication) getApplicationContext()).setUserInfo(userInfo);
		Intent intent = new Intent(mContext, MainGridActivity.class);
		startActivity(intent);
	}
	
	protected String wrapUrl(String url) {
		if (TextUtils.isEmpty(url)) {
			return "";
		}
		Uri uri = Uri.parse(url);
		Uri.Builder uriBuilder = uri.buildUpon();
		return uriBuilder.toString();

	}

	protected void loadUrl(final String url) {
		mWebView.post(new Runnable() {
			@Override
			public void run() {
				if (mWebView != null) {
					mWebView.loadUrl(wrapUrl(url));
				}
			}
		});
	}

	protected void postUrl(final String url, final byte[] data) {
		mWebView.post(new Runnable() {
			@Override
			public void run() {
				if (mWebView != null) {
					mWebView.postUrl(wrapUrl(url), data);
				}
			}
		});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(URL, mCurrentUrl);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		loadUrl(savedInstanceState.getString("url"));
	}

	private class InternalDownloadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	private class InternalWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());
			builder.setTitle(R.string.message_tips);
			builder.setMessage(message);
			builder.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					});
			builder.setCancelable(false);
			builder.create();
			try {
				builder.show();
			} catch (Exception ex) {
				Log.d("exception", ex.getLocalizedMessage());
			}
			return true;
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				final JsResult result) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());
			builder.setTitle(R.string.message_tips);
			builder.setMessage(message);
			builder.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					});
			builder.setNeutralButton(android.R.string.cancel,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					});
			builder.setCancelable(false);
			builder.create();
			try {
				builder.show();
			} catch (Exception ex) {
				Log.d("exception", ex.getLocalizedMessage());
			}
			return true;

		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, final JsPromptResult result) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					view.getContext());
			builder.setTitle(R.string.message_tips);
			builder.setMessage(message);
			final EditText editText = new EditText(view.getContext());
			editText.setText(defaultValue);
			builder.setView(editText);
			builder.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.confirm(editText.getText().toString());
						}
					});
			builder.setNeutralButton(android.R.string.cancel,
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							result.cancel();
						}
					});
			builder.setCancelable(false);
			builder.create();
			try {
				builder.show();
			} catch (Exception ex) {
				Log.d("exception", ex.getLocalizedMessage());
			}
			return true;
		}
	}

}
