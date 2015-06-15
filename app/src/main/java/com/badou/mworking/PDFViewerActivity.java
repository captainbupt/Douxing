package com.badou.mworking;

import android.os.Bundle;

import com.badou.mworking.base.BaseStatisticalActionBarActivity;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.BottomRatingAndCommentView;
import com.joanzapata.pdfview.PDFView;

import java.io.File;

/**
 * 功能描述: pdf 显示页面
 */
public class PDFViewerActivity extends BaseStatisticalActionBarActivity {

    public static final String KEY_RID = "rid";
    public static final String KEY_SHOW_RATING = "rating";
    public static final String KEY_SHOW_COMMENT = "comment";
    public static final String KEY_STATISTICAL = "statistical";

    private PDFView mPdfView;
    private BottomRatingAndCommentView mBottomView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        initView();
        initData();

    }

    protected void initView() {
        mPdfView = (PDFView) this.findViewById(R.id.pdfview_activity_pdf_view);
        mBottomView = (BottomRatingAndCommentView) findViewById(R.id.bracv_activity_pdf_view);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        boolean showRating = mReceivedIntent.getBooleanExtra(KEY_SHOW_RATING, false);
        if (showRating) {
            mBottomView.setData(mRid, 0, 0, -1);
        } else {// 设置-1，用于隐藏评分选项
            mBottomView.setData(mRid, -1, 0, -1);
        }
        mBottomView.updateData();
        showPdf();
    }

    private void showPdf() {
        // 显示pdf
        File file = null;
        try {
            String filePath = FileUtils.getTrainCacheDir(mContext) + mRid + ".pdf";
            file = new File(filePath);
            if (file.exists() && file.length() > 0) {
                //加载pdf文件
                mPdfView.fromFile(file).showMinimap(false).enableSwipe(true)
                        .load();
            } else {
                file.delete();
                finish();
                ToastUtil.showToast(mContext, R.string.tips_pdf_view_open_error);
            }
            final File finalFile = file;
            /*mPdfView.setOnOpenPDFFailedListener(new PDFView.OnOpenPDFFailedListener() {
                @Override
                public void onOpenFailed() {
                    if (finalFile != null) {
                        finalFile.delete();
                    }
                    ToastUtil.showToast(mContext, R.string.tips_pdf_view_open_error);
                }
            });*/
        } catch (Exception e) {
            // 捕获打开pdf异常
            e.printStackTrace();
            if (file != null) {
                file.delete();
            }
            ToastUtil.showToast(mContext, R.string.tips_pdf_view_open_error);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBottomView.updateData();
    }
}
