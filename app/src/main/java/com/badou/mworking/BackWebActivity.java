package com.badou.mworking;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackWebViewActivity;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.IconLoadListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.CoursewareScoreDilog;
import com.badou.mworking.widget.CoursewareScoreDilog.CoursewareScoreDilogListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * 功能描述:  actionbar为返回的网页展示页面
 */
@SuppressLint("SetJavaScriptEnabled")
public class BackWebActivity extends BaseBackWebViewActivity {

    public static final String VALUE_URL = "url";
    public static final String VALUE_TITLE = "title";
    public static final String VALUE_RID = "rid";
    public static final String ISSHOWTONGJI = "isShowTongji";

    public static int PAGEFLAG = BackWebActivity.GENERAL;  // 默认为普通页面   在onDestroy（）方法中还原
    public static final int GENERAL = 0;    //普通页面跳转进入
    public static final int TRAINING = 1;    //微培训页面跳转进入
    public static final int NOTICE = 2;    //通知公告页面跳转进入
    public static final int BANNER = 3;    //banner 跳转
    public static final int EXAM = 4;    //在线考试跳转进入

    private String url = "";
    private String title;
    private String rid;
    private String mCameraFilePath; //拍照路径
    private String uid = "";  // 用户id
    private boolean isAdmin = false; // 是否是管理员
    private boolean isShowTongji = false; // 是否显示统计的按钮

    private Train train = null;

    private ProgressDialog mProgressDialog;
    private ValueCallback<Uri> mUploadMessage;

    public static boolean ISSLIDEABLE = true;
    private float SOldScale = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAdmin = ((AppApplication) getApplicationContext())
                .getUserInfo().isAdmin;
        uid = ((AppApplication) getApplicationContext()).getUserInfo().userId;
        try {
            if (BackWebActivity.PAGEFLAG != BackWebActivity.EXAM) {
                layout.attachToActivity(this);
            }
            Intent intent = getIntent();
            if (BackWebActivity.PAGEFLAG == BackWebActivity.TRAINING) {
                weiPeiXunDate(intent);
            } else if (BackWebActivity.PAGEFLAG == BackWebActivity.NOTICE) {
                noticeDate(intent);
            } else if (BackWebActivity.PAGEFLAG == BackWebActivity.BANNER) {
                bannerDate();
            }
            title = intent.getStringExtra(VALUE_TITLE);
            url = intent.getStringExtra(VALUE_URL);
            if (title != null && !title.equals("")) {
                setActionbarTitle(title);
            } else {
                setActionbarTitle("");
            }
            isShowTongji = intent.getBooleanExtra(ISSHOWTONGJI, false);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        init();
        initLisener();
        setBottom();
    }

    /**
     * 功能描述: 微培训跳转进入
     */
    private void weiPeiXunDate(Intent intent) {
        train = (Train) intent.getBundleExtra("train").getSerializable("train");
        if (train != null) {
            rid = train.rid;
            url = train.url + "&uid="
                    + ((AppApplication) this.getApplicationContext())
                    .getUserInfo().userId;
        }
    }

    /**
     * 功能描述: banner 跳转进入
     */
    private void bannerDate() {
        setActionbarTitle("");
        getSupportActionBar().setCustomView(R.layout.activity_main_title_bar);
        ViewGroup layout_action = (ViewGroup) getSupportActionBar().getCustomView().findViewById(R.id.logo_bg);
        ImageView logoImg = (ImageView) layout_action.findViewById(R.id.iv_actionbar_logo);
        // 调用缓存中的企业logoUrl图片，这样断网的情况也会显示出来了，如果本地没有的话，网络获取
        String logoUrl = SP.getStringSP(this, SP.DEFAULTCACHE, "logoUrl", "");
        Bitmap logBmp = BitmapLruCache.getBitmapLruCache().get(logoUrl);
        if (logBmp != null && logBmp.isRecycled()) {
            logoImg.setImageBitmap(logBmp);
        } else {
            MyVolley.getImageLoader().get(
                    logoUrl,
                    new IconLoadListener(mContext, logoImg, logoUrl,
                            R.drawable.logo));
        }
    }

    /**
     * 功能描述: 通知公告跳转进入
     */
    private void noticeDate(Intent intent) {
        if (intent != null) {
            try {
                rid = intent.getStringExtra(VALUE_RID);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 功能描述: webview设置，加载内容
     */
    private void init() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        /**
         * //设置加载进来的页面自适应手机屏幕
         * settings.setUseWideViewPort(true);
         * ettings.setLoadWithOverviewMode(true);
         * 第一个方法设置webview推荐使用的窗口，设置为true。第二个方法是设置webview加载的页面的模式，也设置为true。
         * */
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
                startActivityForResult(createCameraIntent(), 1);
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
                    BackWebActivity.ISSLIDEABLE = true;
                } else {
                    BackWebActivity.ISSLIDEABLE = false;
                }
            }

        });

        mWebView.loadUrl(url);

    }

//	private Intent createDefaultOpenableIntent() {
//		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
//		i.addCategory(Intent.CATEGORY_OPENABLE);
//		i.setType("*/*");
//		Intent chooser = createChooserIntent(createCameraIntent());
//		chooser.putExtra(Intent.EXTRA_INTENT, i);
//		return chooser;
//	}
//
//	private Intent createChooserIntent(Intent... intents) {
//		Intent chooser = new Intent(Intent.ACTION_CHOOSER);
//		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents);
//		chooser.putExtra(Intent.EXTRA_TITLE, "选择操作");
//		return chooser;
//	}

    /**
     * 功能描述:调取相机        这里给定的是相册的路径，然后以当前时间命名
     *
     * @return
     */
    private Intent createCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File externalDataDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        System.out.println("externalDataDir:" + externalDataDir);
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
    @TargetApi(11)
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        mWebView.onResume();

        if (isAdmin) {
            String str = getResources().getString(R.string.statistical_data);
            if (!TextUtils.isEmpty(title) && str.equals(title)) {
                super.rlComment.setVisibility(View.GONE);
                super.weipeixuncommentRelat.setVisibility(View.GONE);
            } else {
                if (isShowTongji) {
                    mImgRight.setBackgroundResource(R.drawable.admin_tongji);
                    mImgRight.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 控制底部哪些内容显示，哪些内容不显示
     */
    private void setBottom() {
        boolean flag = NetUtils.isNetConnected(getApplicationContext());
        if (flag) {
            // 通知公告
            if (BackWebActivity.PAGEFLAG == BackWebActivity.NOTICE) {
                netUpdateNum(rid);
                super.rlComment.setVisibility(View.VISIBLE);
                super.weipeixuncommentRelat.setVisibility(View.GONE);
                // 微培训
            } else if (BackWebActivity.PAGEFLAG == BackWebActivity.TRAINING) {
                netUpdateNum(train.rid);
                super.rlComment.setVisibility(View.GONE);
                super.weipeixuncommentRelat.setVisibility(View.VISIBLE);
                // 普通webview
            } else {
                super.rlComment.setVisibility(View.GONE);
                super.weipeixuncommentRelat.setVisibility(View.GONE);
            }
        } else {
            super.rlComment.setVisibility(View.GONE);
            super.weipeixuncommentRelat.setVisibility(View.GONE);
            ToastUtil.showNetExc(mContext);
        }
    }

    @Override
    @TargetApi(11)
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
        BackWebActivity.PAGEFLAG = BackWebActivity.GENERAL;
        BackWebActivity.ISSLIDEABLE = true;
    }

    /**
     * 更新点赞数和评论数
     *
     * @param rid
     */
    private void netUpdateNum(final String rid) {
        if (null != mProgressDialog && mContext != null
                && !mActivity.isFinishing()) {
            mProgressDialog.show();
        }
        String[] rids = {rid};
        // 发起网络请求，获取课件的点赞数
        ServiceProvider.doUpdateFeedbackCount(mContext, rids,
                new VolleyListener(mContext) {
                    @Override
                    public void onResponse(Object responseObject) {

                        int comment = 0;
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                        }
                        JSONObject response = (JSONObject) responseObject;
                        try {
                            int code = response.optInt(Net.CODE);
                            if (code == Net.LOGOUT) {
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
                            if (BackWebActivity.PAGEFLAG == BackWebActivity.TRAINING) {
                                for (int i = 0; i < resultArray.length(); i++) {
                                    JSONObject jsonObjectl = resultArray
                                            .optJSONObject(i);
                                    String rid = jsonObject
                                            .optString(ResponseParams.CATEGORY_RID);
                                    int feedbackCount = jsonObject
                                            .optInt(ResponseParams.RATING_NUM);
                                    int commentNum = jsonObject
                                            .optInt(ResponseParams.COMMENT_NUM);
                                        /*if (train.rid.equals(rid)) {
											train.setCommentNum(commentNum);
											train.setFeedbackCount(feedbackCount);
										}*/
                                }
                            } else if (BackWebActivity.PAGEFLAG == BackWebActivity.NOTICE) {
                                comment = jsonObject
                                        .optInt(ResponseParams.COMMENT_NUM);
                            } else {

                            }

                        } catch (Exception e) {
                            ToastUtil.showNetExc(mContext);
                            if (null != mProgressDialog && mContext != null
                                    && !mActivity.isFinishing()) {
                                mProgressDialog.dismiss();
                            }
                        } finally {
                            if (BackWebActivity.PAGEFLAG == BackWebActivity.NOTICE) {
                                tvCommentNum.setText(comment + getResources().getString(R.string.taolun));
                            } else {
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (null != mProgressDialog && mContext != null
                                && !mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 点击左上角
     */
    @Override
    public void clickLeft() {
        BackWebActivity.this.finish();
    }

    private void showPingfenDilog() {
        // 判断如果是微培训跳入
        if (BackWebActivity.PAGEFLAG == BackWebActivity.TRAINING) {
            if (train != null) {
                String coursewareScore = train.coursewareScore;
                new CoursewareScoreDilog(mContext, train.rid, coursewareScore, new CoursewareScoreDilogListener() {

                    @Override
                    public void positiveListener(int coursewareScore) {
                        train.coursewareScore = coursewareScore + "";
                        tvZan.setText(train.ecnt + 1 + "");
                        Intent intent = new Intent();
                        intent.putExtra(TrainActivity.KEY_RATING, coursewareScore);
                        intent.putExtra(TrainActivity.KEY_RID, train.rid);
                        setResult(RESULT_OK, intent);
                    }
                }).show();
            }
        }
    }

    /**
     * 功能描述: 添加返回按钮，弹出是否退出应用程序对话框
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BackWebActivity.this.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 点击actionbar 右上角操作，进入到评论页面
     */
    @Override
    public void clickRight() {
        super.clickRight();
        BackWebActivity.PAGEFLAG = BackWebActivity.GENERAL;
        String titleStr = getResources().getString(R.string.statistical_data);
        String url = Net.getRunHost(BackWebActivity.this) + Net.getTongji(uid, rid);
        Intent intent = new Intent();
        intent.setClass(BackWebActivity.this, BackWebActivity.class);
        intent.putExtra(BackWebActivity.VALUE_URL, url);
        intent.putExtra(BackWebActivity.VALUE_TITLE, titleStr);
        BackWebActivity.this.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_progress_bottom:
                Intent intent = new Intent();
                intent.setClass(BackWebActivity.this, CommentActivity.class);
                intent.putExtra(CommentActivity.VALUE_RID, rid);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initLisener() {

        ll_comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(mContext, CommentActivity.class);
                if (BackWebActivity.PAGEFLAG == BackWebActivity.NOTICE) {
                    intent.putExtra(CommentActivity.VALUE_RID, rid);
                } else if (BackWebActivity.PAGEFLAG == BackWebActivity.TRAINING) {
                    intent.putExtra(CommentActivity.VALUE_RID, train.rid);
                } else {

                }
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        // 点赞布局
        ll_dianZan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showPingfenDilog();

            }
        });
    }
}
