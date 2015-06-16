package com.badou.mworking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.base.BaseStatisticalActionBarActivity;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.bitmap.NormalImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.SPUtil;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import java.io.File;

/**
 * 功能描述:  actionbar为返回的网页展示页面
 */
@SuppressLint("SetJavaScriptEnabled")
public class BackWebActivity extends BaseStatisticalActionBarActivity {

    public static final String KEY_URL = "url";
    public static final String KEY_LOGO_URL = "logo";
    public static final String KEY_SHOW_STATISTICAL = "statistical";
    public static final String KEY_SHOW_RATING = "rating";
    public static final String KEY_SHOW_COMMENT = "comment";

    private String mUrl;
    private String mCameraFilePath; //拍照路径

    private LinearLayout mNetExceptionLinearLayout;
    private TextView mNetExceptionRepeatTextView;
    private ImageView mNetExceptionImageView;

    private WebView mWebView;

    private BottomRatingAndCommentView mBottomView;

    private ValueCallback<Uri> mUploadMessage;

    private float SOldScale = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        initData();
        initWebView();
    }

    private void initView() {
        mWebView = (WebView) findViewById(R.id.wv_activity_web_view);
        mNetExceptionLinearLayout = (LinearLayout) findViewById(R.id.ll_activity_web_view_exception);
        mNetExceptionRepeatTextView = (TextView) findViewById(R.id.tv_activity_web_view_exception_repeat);
        mNetExceptionImageView = (ImageView) findViewById(R.id.iv_activity_web_view_exception);
        mBottomView = (BottomRatingAndCommentView) findViewById(R.id.bracv_activity_web_view);
    }

    private void initData() {
        mUrl = mReceivedIntent.getStringExtra(KEY_URL);
        boolean isShowStatistical = mReceivedIntent.getBooleanExtra(KEY_SHOW_STATISTICAL, false);
        boolean isAdmin = ((AppApplication) getApplicationContext())
                .getUserInfo().isAdmin;
        if (isAdmin && isShowStatistical) {
            setRightImage(R.drawable.button_title_admin_statistical);
        } else {
            mTitleRightImageView.setVisibility(View.GONE);
        }
        boolean showRating = mReceivedIntent.getBooleanExtra(KEY_SHOW_RATING, false);
        boolean showComment = mReceivedIntent.getBooleanExtra(KEY_SHOW_COMMENT, false);
        if (!showRating && !showComment) {
            mBottomView.setVisibility(View.GONE);
        } else {
            mBottomView.setVisibility(View.VISIBLE);
            if (showRating && showComment) {
                mBottomView.setData(mRid, 0, 0, -1);
            } else if (!showRating && showComment) {
                mBottomView.setData(mRid, -1, 0, -1);
            } else if (showRating && !showComment) {
                mBottomView.setData(mRid, 0, -1, -1);
            }
        }
        String logoUrl = mReceivedIntent.getStringExtra(KEY_LOGO_URL);
        if (!TextUtils.isEmpty(logoUrl)) {
            bannerDate(logoUrl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetUtils.isNetConnected(mContext)) {
            mNetExceptionImageView.setVisibility(ImageView.GONE);
            mNetExceptionLinearLayout.setVisibility(LinearLayout.GONE);
            mWebView.setVisibility(WebView.VISIBLE);
            mWebView.setHorizontalScrollBarEnabled(false);
        } else {
            mNetExceptionImageView.setVisibility(ImageView.VISIBLE);
            mNetExceptionLinearLayout.setVisibility(LinearLayout.VISIBLE);
            mNetExceptionImageView.setVisibility(ImageView.VISIBLE);
            mWebView.setVisibility(WebView.GONE);
            mNetExceptionRepeatTextView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    BackWebActivity.this.onResume();
                }
            });
        }
        if (android.os.Build.VERSION.SDK_INT >= 11)
            mWebView.onResume();
    }

    /**
     * 功能描述: banner 跳转进入
     */
    private void bannerDate(String logoUrl) {
        ImageView logoImage = new ImageView(mContext);
        logoImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelOffset(R.dimen.action_bar_img_size)));
        logoImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int padding = getResources().getDimensionPixelOffset(R.dimen.offset_less);
        logoImage.setPadding(padding, padding, padding, padding);
        ImageViewLoader.setImageViewResource(logoImage, R.drawable.logo, logoUrl);
        setTitleCustomView(logoImage);
    }

    /**
     * 功能描述: webview设置，加载内容
     */
    private void initWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);

        //设置加载进来的页面自适应手机屏幕
        // 第一个方法设置webview推荐使用的窗口，设置为true。第二个方法是设置webview加载的页面的模式，也设置为true。
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //缩放开关
        webSettings.setSupportZoom(true);
        //设置是否可缩放
        webSettings.setBuiltInZoomControls(true);
        //无限缩放
        webSettings.setUseWideViewPort(true);
        //隐藏缩放按钮
        if (android.os.Build.VERSION.SDK_INT >= 11)
            webSettings.setDisplayZoomControls(false);

        mWebView.setWebChromeClient(new WebChromeClient() {
            // Android > 4.1.1 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType) {
                if (mUploadMessage != null)
                    return;
                mUploadMessage = uploadMsg;
                //startActivityForResult(createCameraIntent(), 1);
                startActivityForResult(createDefaultOpenableIntent(), 1);
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onScaleChanged(WebView view, float oldScale,
                                       float newScale) {
                if (Math.abs(SOldScale - 0.0) < 0.001) {
                    SOldScale = oldScale;
                }
                if (newScale - SOldScale < 0.2) {
                    mSwipeBackLayout.setEnabled(true);
                } else {
                    mSwipeBackLayout.setEnabled(false);
                }
            }

        });
        mWebView.loadUrl(mUrl);

    }

    private Intent createDefaultOpenableIntent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        Intent chooser = createChooserIntent(createCameraIntent());
        chooser.putExtra(Intent.EXTRA_INTENT, i);
        return chooser;
    }

    private Intent createChooserIntent(Intent... intents) {
        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
        chooser.putExtra(Intent.EXTRA_TITLE, "选择操作");
        return chooser;
    }

    /**
     * 功能描述:调取相机        这里给定的是相册的路径，然后以当前时间命名
     *
     * @return
     */
    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File externalDataDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath());
        if (!cameraDataDir.exists()) {
            cameraDataDir.mkdirs();
        }
        mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator + System.currentTimeMillis() + ".jpg";
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mCameraFilePath)));
        return cameraIntent;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();

            if (result == null && intent == null
                    && resultCode == Activity.RESULT_OK) {
                File cameraFile = new File(mCameraFilePath);

                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile);
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                }
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (android.os.Build.VERSION.SDK_INT >= 11)
            mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SPUtil.setWebViewPosition(mContext, mUrl.trim(), mWebView.getScrollY());
        mWebView.destroy();
    }
}
