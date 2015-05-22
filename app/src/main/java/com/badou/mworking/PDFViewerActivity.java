package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.Category;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 功能描述: pdf 显示页面
 */
public class PDFViewerActivity extends BaseBackActionBarActivity {

    public static final String KEY_CATEGORY_VALUE = "content";
    public static final String KEY_WEBVIEW_PDF = "webview_pdf"; // True 使用pdf， false使用web

    private Handler mWebVieHandler;

    private Category mCategoryEntity;// 实体类

    private LinearLayout mRatingLayout;   //评分布局
    private LinearLayout mCommentLayout;   //评论布局
    private TextView mCommentTextView;        //评论数量
    private TextView mRatingTextView;            //评分数量

    private PDFView mPdfView;
    private WebView mWebView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basevieweractivity);
        initView();
        initLisener();
        initData();

        boolean isPdf = mReceivedIntent.getBooleanExtra(
                KEY_WEBVIEW_PDF, false);
        if (isPdf) { // 显示pdfview
            showPdf();
        } else {// 显示网页
            showWeb();
        }
    }

    protected void initView() {
        mPdfView = (PDFView) this.findViewById(R.id.pdfview);
        mCommentLayout = (LinearLayout) findViewById(R.id.ll_comment);
        mRatingLayout = (LinearLayout) findViewById(R.id.ll_dianZan);
        mCommentTextView = (TextView) findViewById(R.id.comment_num);
        mRatingTextView = (TextView) findViewById(R.id.Zan_num);
    }

    private void initLisener() {

        mCommentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                goComment();
            }
        });

        mRatingLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showPingfenDilog();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        boolean isAdmin = ((AppApplication) getApplicationContext())
                .getUserInfo().isAdmin();
        mCategoryEntity = (Category) mReceivedIntent.getBundleExtra(KEY_CATEGORY_VALUE)
                .getSerializable(KEY_CATEGORY_VALUE);
        if (isAdmin) {
            setRightImage(R.drawable.admin_tongji);
        }
        // 获取分类名
        String title = SP.getStringSP(PDFViewerActivity.this, mCategoryEntity.getCategoryKeyName(), mCategoryEntity.tag + "", "");
        setActionbarTitle(title);
    }

    private void showPdf() {
        // 显示pdf
        try {
            String filePath = FileUtils.getTrainCacheDir(mContext) + mCategoryEntity.rid + ".pdf";
            File file = new File(filePath);
            if (file.exists()) {
                //加载pdf文件
                mPdfView.fromFile(file).showMinimap(false).enableSwipe(true)
                        .load();
            } else {
                Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // 捕获打开pdf异常
            e.printStackTrace();
        }
    }

    private void showWeb() {
        mWebView = new WebView(mContext);
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
                if (mCategoryEntity == null) {
                    finish();
                } else {
                    String webUrl = mCategoryEntity.rid;
                    String company = SP.getStringSP(mContext, SP.DEFAULTCACHE, Constant.COMPANY, "badou");
                    final String url = Constant.TRAIN_IMG_SHOW + company + File.separator + webUrl + Constant.TRAIN_IMG_FORMAT;
                    getRespStatus(url);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getRespStatus(final String url) {
        if (mWebVieHandler == null) {
            mWebVieHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (mActivity.isFinishing())
                        return;
                    int status = msg.what;
                    if (status == 200) {
                        mWebView.loadUrl(url);
                    } else {
                        ToastUtil.showToast(PDFViewerActivity.this, R.string.web_error);
                    }
                }
            };
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int status = 0;
                try {
                    HttpGet head = new HttpGet(url);
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse resp = client.execute(head);
                    status = resp.getStatusLine().getStatusCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mWebVieHandler.obtainMessage(status).sendToTarget();
            }
        }).start();
    }

    @Override
    public void clickRight() {
        super.clickRight();
        String titleStr = getResources().getString(R.string.statistical_data);
        String uid = ((AppApplication) getApplicationContext()).getUserInfo().getUserId();
        String url = Net.getRunHost(PDFViewerActivity.this) + Net.getTongji(uid, mCategoryEntity.rid);
        Intent intent = new Intent();
        intent.setClass(PDFViewerActivity.this, BackWebActivity.class);
        intent.putExtra(BackWebActivity.VALUE_URL, url);
        intent.putExtra(BackWebActivity.VALUE_TITLE, titleStr);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetUtils.isNetConnected(mContext)) {
            if (mProgressDialog != null) {
                mProgressDialog.show();
            }
            updateCommentRatingNum();
        }
    }

    /**
     * 更新点赞数和评论数
     */
    private void updateCommentRatingNum() {
        // 发起网络请求，获取课件的点赞数
        ServiceProvider.doUpdateFeedbackCount(PDFViewerActivity.this, new String[]{mCategoryEntity.rid},
                new VolleyListener(PDFViewerActivity.this) {
                    @Override
                    public void onResponse(Object responseObject) {
                        JSONObject response = (JSONObject) responseObject;
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
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject = resultArray
                                    .optJSONObject(i);
                            String rid = jsonObject
                                    .optString(ResponseParams.CATEGORY_RID);
                            int ratingNumber = 0;
                            int commentNumber = 0;
                            try {
                                ratingNumber = jsonObject
                                        .optInt(ResponseParams.RATING_NUM);
                                commentNumber = jsonObject
                                        .optInt(ResponseParams.COMMENT_NUM);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (mCategoryEntity.rid.equals(rid)) {
                                if (mCategoryEntity.getCategoryType() == Category.CATEGORY_TRAIN) {
                                    mCommentTextView.setVisibility(View.VISIBLE);
                                    mCommentTextView.setText(commentNumber + "");
                                    mRatingTextView.setVisibility(View.VISIBLE);
                                    mRatingTextView.setText(ratingNumber + "");
                                } else if (mCategoryEntity.getCategoryType() == Category.CATEGORY_NOTICE) {
                                    mCommentTextView.setVisibility(View.VISIBLE);
                                    mCommentTextView.setText(commentNumber + "");
                                }
                                break;
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (null != mProgressDialog
                                && !mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                    }

                });

    }

    private void showPingfenDilog() {
        // 课件评分
        final Train train = (Train) mCategoryEntity;
        if (train != null) {
            String coursewareScore = train.coursewareScore;
            new CoursewareScoreDilog(PDFViewerActivity.this, train.rid, coursewareScore, new CoursewareScoreDilogListener() {

                @Override
                public void positiveListener(int coursewareScore) {
                    train.coursewareScore = coursewareScore + "";
                    mRatingTextView.setText(train.ecnt + 1 + "");
                    Intent intent = new Intent();
                    intent.putExtra(TrainActivity.KEY_RATING, coursewareScore);
                    intent.putExtra(TrainActivity.KEY_RID, mCategoryEntity.rid);
                    setResult(RESULT_OK,intent);
                }
            }).show();
        }
    }

    private void goComment() {
        // actionBar右侧button 跳转到评论页面
        Intent intent = new Intent(PDFViewerActivity.this,
                CommentActivity.class);
        intent.putExtra(CommentActivity.VALUE_RID, mCategoryEntity.rid);
        startActivity(intent);
    }
}
