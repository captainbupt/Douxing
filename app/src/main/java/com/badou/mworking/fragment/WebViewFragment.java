package com.badou.mworking.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewFragment extends BaseFragment {

    public static final String KEY_URL = "url";
    @Bind(R.id.web_view)
    WebView mWebView;
    @Bind(R.id.net_exception_image_view)
    ImageView mNetExceptionImageView;
    @Bind(R.id.net_exception_repeat_text_view)
    TextView mNetExceptionRepeatTextView;
    @Bind(R.id.net_exception_linear_layout)
    LinearLayout mNetExceptionLinearLayout;

    private String mUrl;
    private String mCameraFilePath; //拍照路径

    private ValueCallback<Uri> mUploadMessage;

    public static BaseFragment getFragment(String url) {
        WebViewFragment webViewFragment = new WebViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_view, container, false);
        ButterKnife.bind(this, view);
        mUrl = ((Bundle) getArguments()).getString(KEY_URL);
        initWebView();
        return view;
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
        if (Build.VERSION.SDK_INT >= 11)
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
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                if (onScaleChangedListener != null) {
                    onScaleChangedListener.onScaleChanged(view, oldScale, newScale);
                }
            }

        });
        mWebView.loadUrl(mUrl);

    }

    private OnScaleChangedListener onScaleChangedListener;

    public void setOnScaleChangedListener(OnScaleChangedListener onScaleChangedListener) {
        this.onScaleChangedListener = onScaleChangedListener;
    }

    public interface OnScaleChangedListener {
        void onScaleChanged(WebView view, float oldScale, float newScale);
    }

    // 必须在reset之前做操作，否则会空指针
    @Override
    public void onDestroyView() {
        SPHelper.setWebViewPosition(mUrl.trim(), mWebView.getScrollY());
        mWebView.destroy();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.net_exception_repeat_text_view)
    void retry() {
        onResume();
    }

    @Override
    public void onResume() {
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
        }
        if (Build.VERSION.SDK_INT >= 11)
            mWebView.onResume();
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
     */
    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File externalDataDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
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
            Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
            if (result == null && intent == null && resultCode == Activity.RESULT_OK) {
                File cameraFile = new File(mCameraFilePath);

                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile);
                    mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result));
                }
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= 11)
            mWebView.onPause();
    }
}