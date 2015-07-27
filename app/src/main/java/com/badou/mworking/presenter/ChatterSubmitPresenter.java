package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.Gravity;

import com.badou.mworking.R;
import com.badou.mworking.domain.ChatterPublishContentUseCase;
import com.badou.mworking.domain.ChatterTopicUseCase;
import com.badou.mworking.domain.ChatterPublishUseCase;
import com.badou.mworking.entity.ChatterTopic;
import com.badou.mworking.net.BaseListSubscriber;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.ChatterSubmitView;
import com.badou.mworking.widget.ChatterUrlTipPopupWindow;

import org.json.JSONObject;

import java.util.List;

public class ChatterSubmitPresenter extends Presenter {

    boolean isTopicShow = false;
    boolean isAnonymous = false;
    boolean isVideo = false;
    ChatterSubmitView mChatterSubmitView;
    ChatterPublishContentUseCase mContentUseCase;
    ChatterUrlTipPopupWindow mPopupWindow;

    public ChatterSubmitPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mChatterSubmitView = (ChatterSubmitView) v;
        getTopicList();
        isTopicShow = false;
        mChatterSubmitView.setTopicListVisibility(false);
    }

    public void showTopic() {
        isTopicShow = !isTopicShow;
        mChatterSubmitView.setTopicListVisibility(isTopicShow);
    }

    public void setAnonymous() {
        isAnonymous = !isAnonymous;
        mChatterSubmitView.setAnonymousCheckBox(isAnonymous);
    }

    public void showUrlTip() {
        if (mPopupWindow == null) {
            mPopupWindow = new ChatterUrlTipPopupWindow(mContext);
        }
        mPopupWindow.showAtLocation(((Activity) mContext).getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }

    public void takeImage() {
        isTopicShow = false;
        mChatterSubmitView.setTopicListVisibility(false);
        mChatterSubmitView.takeImage();
    }

    public void onImageSelected(Bitmap bitmap, boolean isVideo) {
        this.isVideo = isVideo;
        mChatterSubmitView.setImageMode(isVideo);
        if (!isVideo) {
            List<Bitmap> bitmapList = mChatterSubmitView.getCurrentBitmap();
            if (bitmapList != null && bitmapList.size() >= mChatterSubmitView.getMaxImageCount()) {
                mChatterSubmitView.showToast(R.string.chatter_submit_max_image);
                BitmapUtil.recycleBitmap(bitmap);
            } else {
                mChatterSubmitView.addImage(bitmap);
            }
        } else {
            mChatterSubmitView.addVideo(bitmap, FileUtils.getChatterVideoDir(mContext));
        }
    }

    public void onTopicConfirmed(String content) {
        mChatterSubmitView.setTopicListVisibility(false);
        String topic = content.replace("#", "").replace(" ", "").trim();
        mChatterSubmitView.onTopicSelected(topic);
    }

    public void onTopicClicked(ChatterTopic chatterTopic) {
        mChatterSubmitView.onTopicSelected(chatterTopic.getKey());
    }

    public void onVideoDeleted() {
        isVideo = false;
        mChatterSubmitView.clearBitmap();
        mChatterSubmitView.setImageMode(false);
    }

    @Override
    public boolean onBackPressed() {
        if (isTopicShow) {
            mChatterSubmitView.setTopicListVisibility(false);
            isTopicShow = false;
            return true;
        }
        return false;
    }

    public void send(String content) {
        // 断网判断
        if (!NetUtils.isNetConnected(mContext)) {
            ToastUtil.showToast(mContext, R.string.error_service);
            return;
        }
        content = content.replaceAll("\\n", "").trim();

        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(mContext, R.string.question_content_null);
            return;
        }
        if (content.length() < 5) {
            ToastUtil.showToast(mContext, R.string.comment_tips_length);
            return;
        }
        List<Bitmap> bitmapList = mChatterSubmitView.getCurrentBitmap();
        if (!isVideo && bitmapList != null && bitmapList.size() >= 1) {
            publishQuestionShare(content, bitmapList, isVideo);
        } else if (isVideo) {
            publishQuestionShare(content, bitmapList, isVideo);
        } else {
            publishQuestionShare(content, null, isVideo);
        }
    }

    /**
     * 发布问题/分享 内容
     *
     * @param content
     */
    private void publishQuestionShare(String content, final List<Bitmap> bitmapList, final boolean isVideo) {
        mChatterSubmitView.showProgressDialog(R.string.progress_tips_send_ing);
        ChatterPublishUseCase publishChatterUseCase;
        if (isVideo && bitmapList != null && bitmapList.size() == 1) {
            publishChatterUseCase = new ChatterPublishUseCase(content, BitmapUtil.bitmapToBase64(bitmapList.get(0)), isAnonymous);
        } else {
            publishChatterUseCase = new ChatterPublishUseCase(content, null, isAnonymous);
        }
        publishChatterUseCase.execute(new BaseSubscriber<ChatterPublishUseCase.Response>(mContext) {
            @Override
            public void onResponseSuccess(ChatterPublishUseCase.Response data) {
                String qid = data.getQid();
                if (isVideo) {
                    publishVideo(qid);
                } else if (bitmapList != null && bitmapList.size() > 0) {
                    publicImage(qid, 1, bitmapList);
                } else {
                    mChatterSubmitView.hideProgressDialog();
                    toChatterActivity();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mChatterSubmitView.hideProgressDialog();
            }
        });
    }

    private void publicImage(final String qid, final int index, final List<Bitmap> bitmapList) {
        if (bitmapList == null || index - 1 >= bitmapList.size()) {
            mChatterSubmitView.hideProgressDialog();
            mChatterSubmitView.showToast(R.string.tongShiQuan_submit_success);
            toChatterActivity();
            return;
        }
        String filePath = FileUtils.getTrainCacheDir(mContext) + "tmp.jpg";
        BitmapUtil.saveBitmap(bitmapList.get(index - 1), filePath);
        if (index == 1) {
            mContentUseCase = new ChatterPublishContentUseCase(ChatterPublishContentUseCase.TYPE_IMAGE, qid, filePath);
        } else {
            mContentUseCase.next(filePath);
        }
        mContentUseCase.execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                if (index >= bitmapList.size()) {
                    mChatterSubmitView.hideProgressDialog();
                    mChatterSubmitView.showToast(R.string.tongShiQuan_submit_success);
                    toChatterActivity();
                } else {
                    publicImage(qid, index + 1, bitmapList);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mChatterSubmitView.hideProgressDialog();
            }
        });
    }

    /**
     * 功能描述: 上传视屏
     */
    private void publishVideo(String qid) {
        mContentUseCase = new ChatterPublishContentUseCase(ChatterPublishContentUseCase.TYPE_VIDEO, qid, FileUtils.getChatterVideoDir(mContext));
        mContentUseCase.execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                toChatterActivity();
            }

            @Override
            public void onCompleted() {
                mChatterSubmitView.hideProgressDialog();
            }
        });
    }

    private void toChatterActivity() {
        mChatterSubmitView.clearBitmap();
        ((Activity) mContext).setResult(Activity.RESULT_OK);
        ((Activity) mContext).finish();
    }

    private void getTopicList() {
        new ChatterTopicUseCase().execute(new BaseListSubscriber<ChatterTopic>(mContext) {

            @Override
            public void onResponseSuccess(List<ChatterTopic> data) {
                mChatterSubmitView.onTopicSynchronized(data);
            }
        });
    }

    @Override
    public void destroy() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        super.destroy();
    }
}
