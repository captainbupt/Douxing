package com.badou.mworking.presenter.ask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.badou.mworking.R;
import com.badou.mworking.domain.ask.AskReplySendUseCase;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.ask.AskReplyView;

import java.util.List;

public class AskReplyPresenter extends Presenter {

    AskReplyView mAskReplyView;
    String mAid;

    public AskReplyPresenter(Context context, String aid) {
        super(context);
        this.mAid = aid;
    }

    @Override
    public void attachView(BaseView v) {
        mAskReplyView = (AskReplyView) v;
    }

    public void takeImage() {
        mAskReplyView.takeImage();
    }

    public void onImageSelected(Bitmap bitmap) {
        mAskReplyView.addImage(bitmap);
    }

    public void sendReply(String content, List<Bitmap> bitmaps) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(mContext, R.string.ask_answer_tip_empty);
            return;
        } else if (content.replace(" ", "").length() < 5) {
            ToastUtil.showToast(mContext, R.string.ask_answer_tip_little);
            return;
        }
        mAskReplyView.showProgressDialog(R.string.progress_tips_submit_ing);
        Bitmap bitmap = null;
        if (bitmaps != null && bitmaps.size() >= 1)
            bitmap = bitmaps.get(0);
        new AskReplySendUseCase(mAid, content, bitmap).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                ((Activity) mContext).setResult(Activity.RESULT_OK);
                ((Activity) mContext).finish();
            }

            @Override
            public void onCompleted() {
                mAskReplyView.hideProgressDialog();
            }
        });
    }
}
