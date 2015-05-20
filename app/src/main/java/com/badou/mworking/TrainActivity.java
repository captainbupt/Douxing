package com.badou.mworking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.SearchMainAdapter;
import com.badou.mworking.adapter.SearchMoreAdapter;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.base.BaseProgressListActivity;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Classification;
import com.badou.mworking.model.Notice;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.CoursewareScoreDilog;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gejianfeng
 *         微培训页面
 */
public class TrainActivity extends BaseProgressListActivity {

    public static final int PROGRESS_CHANGE = 0x1;
    public static final int PROGRESS_FINISH = 0x2;
    public static final int PROGRESS_MAX = 0x3;
    public static final int REFRESH_EXAM_LV = 0x004;
    public static String KEY_webView_pdf = "webPDF";

    private HorizontalProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_SP_KEY = SP.TRAINING;
        CATEGORY_NAME = Train.CATEGORY_TRAIN;
        CATEGORY_UNREAD_NUM = Train.UNREAD_NUM_TRAIN;
        super.onCreate(savedInstanceState);
        dialog = new HorizontalProgressDialog(mContext);
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new TrainAdapter(mContext, false);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Train(jsonObject);
    }

    private static Train train;
    private static int pingfen = 0;

    /**
     * 不工作？
     */
    @Override
    public void onResume() {
        super.onResume();
        //点赞刷新
        if (TrainActivity.pingfen != 0 && train != null) {
            train.setEcnt(train.getEcnt() + 1);
            train.setCoursewareScore(TrainActivity.pingfen + "");
            TrainActivity.pingfen = 0;
            TrainActivity.train = null;
        }
        //评分刷新
        if (CoursewareScoreDilog.ISPINGFEN && train != null) {
            train.setCoursewareScore(CoursewareScoreDilog.SCORE + "");
            CoursewareScoreDilog.ISPINGFEN = false;
            CoursewareScoreDilog.SCORE = 0;
            TrainActivity.train = null;
        }
    }

    @Override
    public void onItemClick(int position) {
        train = (Train) mCategoryAdapter.getItem(position - 1);
        int subtype = train.getSubtype();
        if (NetUtils.isNetConnected(TrainActivity.this)) {
            // 向服务提交课件信息
            ((TrainAdapter) mCategoryAdapter).read(position - 1);
            ServiceProvider.doMarkRead(TrainActivity.this, train.getRid());
        }
        //返回PDF格式
        if (Constant.MWKG_FORAMT_TYPE_PDF == subtype) {
            toPDFAndWeb(train);
            // 返回MP4格式
        } else if (Constant.MWKG_FORAMT_TYPE_MPEG == subtype) {
            Intent intentToMusic = new Intent(TrainActivity.this, TrainVideoPlayerAct.class);
            Bundle bu = new Bundle();
            bu.putSerializable("train", train);
            intentToMusic.putExtra("train", bu);
            startActivity(intentToMusic);
            // 返回html格式
        } else if (Constant.MWKG_FORAMT_TYPE_HTML == subtype) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("train", train);
            intent.putExtra("train", bundle);
            String url = train.getUrl() + "&uid="
                    + ((AppApplication) getApplicationContext())
                    .getUserInfo().getUserId();
            intent.putExtra(BackWebActivity.ISSHOWTONGJI, true);
            intent.putExtra(BackWebActivity.VALUE_URL, url);
            // 获取分类名
            String title = SP.getStringSP(TrainActivity.this, SP.TRAINING, train.getTag() + "", "");
            intent.putExtra(BackWebActivity.VALUE_TITLE, title);
            intent.setClass(TrainActivity.this, BackWebActivity.class);
            BackWebActivity.PAGEFLAG = BackWebActivity.TRAINING;   // 设置是通过微培训跳转过去的
            TrainActivity.this.startActivity(intent);
        } else if (Constant.MWKG_FORAMT_TYPE_MP3 == subtype) {
            Intent intentToMusic = new Intent(TrainActivity.this, TrainMusicActivity.class);
            Bundle bu = new Bundle();
            bu.putSerializable("train", train);
            intentToMusic.putExtra("train", bu);
            startActivity(intentToMusic);
        } else {
            return;
        }
        mCategoryAdapter.notifyDataSetChanged();
    }

    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     *
     * @param train
     */
    private void toPdfViewer(Train train) {
        if (!((Activity) TrainActivity.this).isFinishing()) {
            // 系统版本>=11 使用第三方的pdf阅读
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("train", train);
                intent.putExtra("train", bundle);
                intent.setClass(TrainActivity.this, BaseViewerActivity.class);
                startActivity(intent);
                // 设置切换动画，从右边进入，左边退出
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        }
    }

    private void toPDFAndWeb(Train train) {
        /**
         * 判断api,太小用web
         * 判断是pdf还是web
         */
        if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
            // 声明pdf文件要保存的路径
            if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
                ToastUtil.showToast(TrainActivity.this, R.string.train_sd_size_);
                return;
            }
            String path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".pdf";
            File file = new File(path);
            // pdf文件不存在
            if (!file.exists() || !file.isFile() || file.isDirectory()
                    || file.length() == 0) {
                file.delete();
                // 显示对话框
                dialog.show();
                if (NetUtils.isNetConnected(TrainActivity.this)) {
                    // 开启线程
                    new DownloadThread(train).start();
                } else {
                    if (dialog != null && dialog.isShowing()
                            && !((Activity) TrainActivity.this).isFinishing()) {
                        // 关闭进度条对话框
                        dialog.dismiss();
                    }
                    ToastUtil.showToast(TrainActivity.this, R.string.error_service);
                }
            } else {
                // pdf文件已存在 调用
                toPdfViewer(train);
            }
        } else {// web
            Intent intent = new Intent(TrainActivity.this, BaseViewerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("train", train);
            intent.putExtra("train", bundle);
            intent.putExtra(KEY_webView_pdf, KEY_webView_pdf);
            startActivity(intent);
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 接受线程中传递的消息
            int statu = msg.what;
            Bundle bundle = msg.getData();
            Train train = (Train) bundle.getSerializable("train");
            String path = "";
            // 声明文件保存路径 用rid命名
            if (train != null) {
                if (Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()) {
                    path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".pdf";
                } else if (Constant.MWKG_FORAMT_TYPE_MPEG == train.getSubtype()) {
                    path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".mp4";
                }
                if (path == null || path.equals("")) {
                    return;
                }
                File file = new File(path);
                if (statu == -1 || !file.exists() || file.length() == 0) {
                    // 文件下载失败 提示
                    ToastUtil.showToast(TrainActivity.this,
                            R.string.train_result_download_fail);
                } else {
                    if (statu != 0) {
                        ToastUtil.showToast(TrainActivity.this,
                                R.string.train_result_download_exist);
                    }
                    if (Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()) {
                        // 下载完成 调用
                        toPdfViewer(train);
                    }
                }
            }
            switch (msg.what) {
                case REFRESH_EXAM_LV:
                    if (Constant.setAdapterRefresh) {
                        // 刷新 listview
                        updataListView(0);
                        Constant.setAdapterRefresh = false;
                    }
                    break;
                case PROGRESS_MAX:
                    dialog.setProgressMax((int) msg.obj);
                    break;
                case PROGRESS_CHANGE:
                    // 设置进度条改变
                    dialog.setProgress((int) msg.obj);
                    break;
                case PROGRESS_FINISH:
                    if (dialog != null && dialog.isShowing()
                            && !mActivity.isFinishing()) {
                        // 关闭进度条对话框
                        dialog.dismiss();
                    }
                    break;

                default:
                    break;
            }

        }
    };

    /**
     * 下载pdf文件的线程 创建人
     */
    class DownloadThread extends Thread {
        private Train train;
        private String path;

        public DownloadThread(Train train) {
            super();
            this.train = train;
        }

        @Override
        public void run() {
            super.run();
            if (Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()) {
                path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".pdf";
            }
            if (path == null || path.equals("")) {
                return;
            }
            // 通过url下载pdf文件
            int statu = HttpDownloader.downFile(train.getUrl()
                            + "&uid="
                            + ((AppApplication) getApplicationContext())
                            .getUserInfo().getUserId(), path,
                    new DownloadListener() {

                        @Override
                        public void onDownloadSizeChange(int downloadSize) {
                            // 已下载的大小
                            Message.obtain(handler, PROGRESS_CHANGE,
                                    downloadSize).sendToTarget();
                        }

                        @Override
                        public void onDownloadFinish(String filePath) {
                            // 下载完成
                            Message.obtain(handler, PROGRESS_FINISH, "")
                                    .sendToTarget();
                        }

                        @Override
                        public void onGetTotalSize(int totalSize) {
                            // 文件大小
                            Message.obtain(handler, PROGRESS_MAX, totalSize)
                                    .sendToTarget();
                        }
                    });
            // 下载成功,向handler传递消息
            Message msg = new Message();
            msg.what = statu;
            Bundle bundle = new Bundle();
            bundle.putSerializable("train", train);
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    protected void updateCompleted() {
        // 通过网络获取课件点赞数量的list
        String[] rids = new String[mCategoryAdapter.getCount()];
        for (int i = 0; i < mCategoryAdapter.getCount(); i++) {
            rids[i] = ((Train)mCategoryAdapter.getItem(i)).getRid();
        }
        // 获取资源的点赞数／评论数／评分
        ServiceProvider.doUpdateFeedbackCount(TrainActivity.this, rids, new VolleyListener(
                TrainActivity.this) {

            @Override
            public void onResponse(Object responseObject) {
                JSONObject response = (JSONObject) responseObject;
                try {
                    int code = response.optInt(Net.CODE);
                    if (code != Net.SUCCESS) {
                        return;
                    }
                    JSONArray resultArray = response
                            .optJSONArray(Net.DATA);
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject jsonObject = resultArray.optJSONObject(i);
                        String rid = jsonObject.optString(ResponseParams.RESOURCE_ID);
                        int feedbackCount = jsonObject
                                .optInt(ResponseParams.ZAN_NUM);
                        int comment = jsonObject
                                .optInt(ResponseParams.COMMENT_NUM);
                        int ecnt = jsonObject
                                .optInt(ResponseParams.ECNT); //评分人数
                        int eval = jsonObject
                                .optInt(ResponseParams.EVAL); //评分总分
                        for (int j = 0; j < mCategoryAdapter.getCount(); j++) {
                            Train t = (Train) mCategoryAdapter.getItem(j);
                            if (rid.equals(t.getRid())) {
                                t.setCommentNum(comment);
                                t.setFeedbackCount(feedbackCount);
                                t.setEcnt(ecnt);
                                t.setEval(eval);
                            }
                        }
                    }
                    mCategoryAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    pullToRefreshListView.onRefreshComplete();
                    hideProgressBar();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                pullToRefreshListView.onRefreshComplete();
                hideProgressBar();
            }

        });
    }
}
