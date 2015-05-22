package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseProgressListActivity;
import com.badou.mworking.model.Notice;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.HorizontalProgressDialog;

import org.json.JSONObject;

import java.io.File;

/**
 * 功能描述: 通知公告页面
 */
public class NoticeActivity extends BaseProgressListActivity {

    private HorizontalProgressDialog dialog;
    private static DownloadHandler mDownloadHandler;

    public static final int PROGRESS_CHANGE = 0x1;
    public static final int PROGRESS_FINISH = 0x2;
    public static final int PROGRESS_MAX = 0x3;

    @Override
    protected void onCreate(Bundle arg0) {
        CATEGORY_NAME = Notice.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Notice.CATEGORY_KEY_UNREAD_NUM;
        super.onCreate(arg0);
        dialog = new HorizontalProgressDialog(mContext);
        if (mDownloadHandler == null) {
            mDownloadHandler = new DownloadHandler();
        }
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new NoticeAdapter(mContext);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Notice(jsonObject);
    }

    @Override
    protected void onItemClick(int position) {
        Notice notice = (Notice) mCategoryAdapter.getItem(position - 1);
        int type = notice.subtype;
        ((NoticeAdapter) mCategoryAdapter).read(position - 1);
        ServiceProvider.doMarkRead(mContext, notice.rid);
        mCategoryAdapter.notifyDataSetChanged();
        // 返回类型是 html
        if (Constant.MWKG_FORAMT_TYPE_HTML == type) {
            // 因为是html，所以只有联网才可以点击查看
            if (NetUtils.isNetConnected(mContext)) {
                showDetail(notice);
            } else {
                ToastUtil.showNetExc(mContext);
            }
            // 返回类型是pdf
        } else if (Constant.MWKG_FORAMT_TYPE_PDF == type) {
            toPDFAndWeb(notice);
        } else {
            return;
        }
    }

    private void toPDFAndWeb(Notice notice) {
        // 声明pdf文件要保存的路径
        if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
            ToastUtil.showToast(NoticeActivity.this, R.string.train_sd_size_);
            return;
        }
        String path = FileUtils.getTrainCacheDir(NoticeActivity.this) + notice.rid + ".pdf";
        File file = new File(path);
        // pdf文件不存在
        if (!file.exists() || !file.isFile() || file.isDirectory()
                || file.length() == 0) {
            file.delete();
            // 显示对话框
            dialog.show();
            if (NetUtils.isNetConnected(NoticeActivity.this)) {
                // 开启线程
                new DownloadThread(notice).start();
            } else {
                if (dialog != null && dialog.isShowing()
                        && !(NoticeActivity.this).isFinishing()) {
                    // 关闭进度条对话框
                    dialog.dismiss();
                }
                ToastUtil.showToast(NoticeActivity.this, R.string.error_service);
            }
        } else {
            // pdf文件已存在 调用
            toPdfViewer(notice);
        }
    }

    /**
     * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
     */
    private void toPdfViewer(Notice notice) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PDFViewerActivity.KEY_CATEGORY_VALUE, notice);
        intent.putExtra(PDFViewerActivity.KEY_CATEGORY_VALUE, bundle);
        intent.putExtra(PDFViewerActivity.KEY_WEBVIEW_PDF, true);
        intent.setClass(NoticeActivity.this, PDFViewerActivity.class);
        startActivity(intent);
    }


    /**
     * 功能描述: 下载pdf文件的线程
     */
    class DownloadThread extends Thread {
        private Notice notice;
        private String path;

        public DownloadThread(Notice notice) {
            super();
            this.notice = notice;
        }

        @Override
        public void run() {
            super.run();
            if (Constant.MWKG_FORAMT_TYPE_PDF == notice.subtype) {
                path = FileUtils.getTrainCacheDir(NoticeActivity.this) + notice.rid + ".pdf";
            }
            if (TextUtils.isEmpty(path)) {
                return;
            }
            // 通过url下载pdf文件
            int status = HttpDownloader.downFile(notice.url
                            + "&uid="
                            + ((AppApplication) getApplicationContext())
                            .getUserInfo().getUserId(), path,
                    new DownloadListener() {

                        @Override
                        public void onDownloadSizeChange(int downloadSize) {
                            // 已下载的大小
                            Message.obtain(mDownloadHandler, PROGRESS_CHANGE,
                                    downloadSize).sendToTarget();
                        }

                        @Override
                        public void onGetTotalSize(int totalSize) {
                            // 文件大小
                            Message.obtain(mDownloadHandler, PROGRESS_MAX, totalSize)
                                    .sendToTarget();
                        }
                    });
            // 下载成功,向handler传递消息
            Message msg = new Message();
            msg.what = status;
            Bundle bundle = new Bundle();
            bundle.putSerializable("notice", notice);
            msg.setData(bundle);
            mDownloadHandler.sendMessage(msg);
        }
    }

    class DownloadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 接受线程中传递的消息
            int statu = msg.what;
            Bundle bundle = msg.getData();
            Notice notice = (Notice) bundle.getSerializable("notice");
            String path = "";
            // 声明文件保存路径 用rid命名
            if (notice != null) {
                path = FileUtils.getTrainCacheDir(NoticeActivity.this) + notice.rid + ".pdf";
                if (path == null || path.equals("")) {
                    return;
                }
                File file = new File(path);
                if (statu == -1 || !file.exists() || file.length() == 0) {
                    // 文件下载失败 提示
                    ToastUtil.showToast(NoticeActivity.this,
                            R.string.train_result_download_fail);
                } else {
                    if (statu != 0) {
                        ToastUtil.showToast(NoticeActivity.this,
                                R.string.train_result_download_exist);
                    }
                    if (Constant.MWKG_FORAMT_TYPE_PDF == notice.subtype) {
                        // 下载完成 调用
                        toPdfViewer(notice);
                    }
                }
            }
            switch (msg.what) {
                case PROGRESS_MAX:
                    dialog.setProgressMax((int) msg.obj);
                    break;
                case PROGRESS_CHANGE:
                    // 设置进度条改变
                    dialog.setProgress((int) msg.obj);
                    break;
                case PROGRESS_FINISH:
                    if (dialog != null && dialog.isShowing() && !NoticeActivity.this.isFinishing()) {
                        // 关闭进度条对话框
                        dialog.dismiss();
                    }
                    break;

                default:
                    break;
            }

        }
    }

    ;

    /**
     * 功能描述: 显示通知的详细内容
     *
     * @param notice
     */
    private void showDetail(Notice notice) {
        int subtype = notice.subtype;
        if (Constant.MWKG_FORAMT_TYPE_HTML != subtype) {
            return;
        }
        Intent intent = new Intent();
        intent.setClass(NoticeActivity.this, BackWebActivity.class);
        intent.putExtra(BackWebActivity.VALUE_URL, notice.url
                + "&uid="
                + ((AppApplication) getApplicationContext())
                .getUserInfo().getUserId());
        // 获取分类名
        String title = SP.getStringSP(NoticeActivity.this, SP.NOTICE, notice.tag + "", "");
        intent.putExtra(BackWebActivity.VALUE_TITLE, title);
        intent.putExtra(BackWebActivity.VALUE_RID, notice.rid);
        intent.putExtra(BackWebActivity.ISSHOWTONGJI, true);
        BackWebActivity.PAGEFLAG = BackWebActivity.NOTICE;    //设置跳转是通知公告
        NoticeActivity.this.startActivity(intent);
        // 设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
