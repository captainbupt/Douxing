package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseProgressListActivity;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.HorizontalProgressDialog;

import org.holoeverywhere.app.Activity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * @author gejianfeng
 *         微培训页面
 */
public class TrainActivity extends BaseProgressListActivity {

    public static final int PROGRESS_CHANGE = 10;
    public static final int PROGRESS_MAX = 11;
    public static final int PROGRESS_FINISH =12;
    public static final String KEY_RATING = "rating";
    public static final String KEY_RID = "rid";

    private static Handler handler;

    private HorizontalProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Train.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Train.CATEGORY_KEY_UNREAD_NUM;
        super.onCreate(savedInstanceState);
        dialog = new HorizontalProgressDialog(mContext);
        if (handler == null) {
            handler = new DownloadHandler(mContext);
        }
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new TrainAdapter(mContext, false);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Train(jsonObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int rating = data.getIntExtra(KEY_RATING, 0);
            String rid = data.getStringExtra(KEY_RID);
            ((TrainAdapter) mCategoryAdapter).updateRating(rid, rating);
        }
    }

    @Override
    public void onItemClick(int position) {
        Train train = (Train) mCategoryAdapter.getItem(position - 1);
        int subtype = train.subtype;
        if (NetUtils.isNetConnected(mContext)) {
            // 向服务提交课件信息
            ((TrainAdapter) mCategoryAdapter).read(position - 1);
            ServiceProvider.doMarkRead(mContext, train.rid);
        }

        if (Constant.MWKG_FORAMT_TYPE_PDF == subtype) { //返回PDF格式
            goPDFAndWeb(train);
        } else if (Constant.MWKG_FORAMT_TYPE_MPEG == subtype) { // 返回MP4格式
            goVedio(train);
        } else if (Constant.MWKG_FORAMT_TYPE_HTML == subtype) { // 返回html格式
            goHTML(train);
        } else if (Constant.MWKG_FORAMT_TYPE_MP3 == subtype) { // 返回MP3格式
            goAudio(train);
        } else {
            ToastUtil.showToast(mContext, R.string.train_unsupport_type);
            return;
        }
        mCategoryAdapter.notifyDataSetChanged();
    }


    private void goVedio(Train train) {
        Intent intentToMusic = new Intent(mContext, TrainVideoPlayerAct.class);
        Bundle bu = new Bundle();
        bu.putSerializable(TrainVideoPlayerAct.KEY_CATEGORY_VALUE, train);
        intentToMusic.putExtra(TrainVideoPlayerAct.KEY_CATEGORY_VALUE, bu);
        startActivity(intentToMusic);
    }

    private void goHTML(Train train) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("train", train);
        intent.putExtra("train", bundle);
        String url = train.url + "&uid="
                + ((AppApplication) getApplicationContext())
                .getUserInfo().getUserId();
        intent.putExtra(BackWebActivity.ISSHOWTONGJI, true);
        intent.putExtra(BackWebActivity.VALUE_URL, url);
        // 获取分类名
        String title = SP.getStringSP(mContext, SP.TRAINING, train.tag + "", "");
        intent.putExtra(BackWebActivity.VALUE_TITLE, title);
        intent.setClass(mContext, BackWebActivity.class);
        BackWebActivity.PAGEFLAG = BackWebActivity.TRAINING;   // 设置是通过微培训跳转过去的
        startActivity(intent);
    }

    public void goAudio(Train train) {
        Intent intentToMusic = new Intent(mContext, TrainMusicActivity.class);
        Bundle bu = new Bundle();
        bu.putSerializable(TrainMusicActivity.KEY_CATEGORY_VALUE, train);
        intentToMusic.putExtra(TrainMusicActivity.KEY_CATEGORY_VALUE, bu);
        startActivity(intentToMusic);
    }

    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     */
    private void goPdfView(Train train) {
        if (!((Activity) mContext).isFinishing()) {
            // 系统版本>=11 使用第三方的pdf阅读
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(PDFViewerActivity.KEY_CATEGORY_VALUE, train);
                intent.putExtra(PDFViewerActivity.KEY_CATEGORY_VALUE, bundle);
                intent.setClass(mContext, PDFViewerActivity.class);
                startActivity(intent);
            }
        }
    }

    private void goPDFAndWeb(Train train) {
        /**
         * 判断api,太小用web
         * 判断是pdf还是web
         */
        if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
            // 声明pdf文件要保存的路径
            if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
                ToastUtil.showToast(mContext, R.string.train_sd_size_);
                return;
            }
            String path = FileUtils.getTrainCacheDir(mContext) + train.rid + ".pdf";
            File file = new File(path);
            // pdf文件不存在
            if (!file.exists() || !file.isFile() || file.isDirectory()
                    || file.length() == 0) {
                file.delete();
                // 显示对话框
                dialog.show();
                if (NetUtils.isNetConnected(mContext)) {
                    // 开启线程
                    new DownloadThread(train).start();
                } else {
                    if (dialog != null && dialog.isShowing()
                            && !((Activity) mContext).isFinishing()) {
                        // 关闭进度条对话框
                        dialog.dismiss();
                    }
                    ToastUtil.showToast(mContext, R.string.error_service);
                }
            } else {
                // pdf文件已存在 调用
                goPdfView(train);
            }
        } else {// web
            Intent intent = new Intent(mContext, PDFViewerActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(PDFViewerActivity.KEY_CATEGORY_VALUE, train);
            intent.putExtra(PDFViewerActivity.KEY_CATEGORY_VALUE, bundle);
            intent.putExtra(PDFViewerActivity.KEY_WEBVIEW_PDF, false);
            startActivity(intent);
        }
    }

    private class DownloadHandler extends Handler {

        private Context mContext;

        public DownloadHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 如果activity关闭，则不作任何处理
            if (((Activity) mContext).isFinishing()) {
                return;
            }
            // 接受线程中传递的消息
            if (msg.what == PROGRESS_MAX) {
                // 设置进度条最大值
                dialog.setProgressMax((int) msg.obj);
            } else if (msg.what == PROGRESS_CHANGE) {
                // 设置进度条改变
                dialog.setProgress((int) msg.obj);
            } else {
                if (dialog != null && dialog.isShowing()
                        && !mActivity.isFinishing()) {
                    // 关闭进度条对话框
                    dialog.dismiss();
                }
                downloadFinish(msg);
            }
        }

        private void downloadFinish(Message msg) {
            int status = msg.what;
            Bundle bundle = msg.getData();
            Train train = (Train) bundle.getSerializable("train");
            String path = "";
            // 声明文件保存路径 用rid命名
            if (train != null) {
                if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                    path = FileUtils.getTrainCacheDir(mContext) + train.rid + ".pdf";
                } else if (Constant.MWKG_FORAMT_TYPE_MPEG == train.subtype) {
                    path = FileUtils.getTrainCacheDir(mContext) + train.rid + ".mp4";
                }
                if (TextUtils.isEmpty(path)) {
                    return;
                }
                File file = new File(path);
                if (status == HttpDownloader.STATU_FAIL || !file.exists() || file.length() == 0) {
                    // 文件下载失败 提示
                    ToastUtil.showToast(mContext,
                            R.string.train_result_download_fail);
                } else {
                    if (status != HttpDownloader.STATU_SUCCESS) {
                        ToastUtil.showToast(mContext,
                                R.string.train_result_download_exist);
                    }
                    if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                        // 下载完成 调用
                        goPdfView(train);
                    }
                }
            }
        }
    }

    /**
     * 下载pdf文件的线程
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
            if (Constant.MWKG_FORAMT_TYPE_PDF == train.subtype) {
                path = FileUtils.getTrainCacheDir(mContext) + train.rid + ".pdf";
            }
            if (path == null || path.equals("")) {
                return;
            }
            // 通过url下载pdf文件
            int statu = HttpDownloader.downFile(train.url
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
            rids[i] = ((Train) mCategoryAdapter.getItem(i)).rid;
        }
        // 获取资源的点赞数／评论数／评分
        ServiceProvider.doUpdateFeedbackCount(mContext, rids, new VolleyListener(
                mContext) {

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
                        String rid = jsonObject.optString(ResponseParams.CATEGORY_RID);
/*                        int feedbackCount = jsonObject
                                .optInt(ResponseParams.RATING_NUM);
                        int comment = jsonObject
                                .optInt(ResponseParams.COMMENT_NUM);*/
                        int ecnt = jsonObject
                                .optInt(ResponseParams.ECNT); //评分人数
                        int eval = jsonObject
                                .optInt(ResponseParams.EVAL); //评分总分
                        for (int j = 0; j < mCategoryAdapter.getCount(); j++) {
                            Train t = (Train) mCategoryAdapter.getItem(j);
                            if (rid.equals(t.rid)) {
/*                                t.commentNum = comment;
                                t.feedbackCount = feedbackCount;*/
                                t.ecnt = ecnt;
                                t.eval = eval;
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
