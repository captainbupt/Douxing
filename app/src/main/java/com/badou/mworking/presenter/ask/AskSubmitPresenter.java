package com.badou.mworking.presenter.ask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.badou.mworking.AskActivity;
import com.badou.mworking.R;
import com.badou.mworking.domain.ask.AskPublishUseCase;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.ask.AskSubmitView;

import org.json.JSONObject;

import java.util.List;

public class AskSubmitPresenter extends Presenter {

    AskSubmitView mAskSubmitView;

    public AskSubmitPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mAskSubmitView = (AskSubmitView) v;
    }

    public void takeImage() {
        mAskSubmitView.takeImage();
    }

    public void onImageSelected(Bitmap bitmap) {
        mAskSubmitView.addImage(bitmap);
    }

    public void publishAsk(String subject, String content, List<Bitmap> bitmapList) {
        if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(content)) {
            mAskSubmitView.showToast(R.string.ask_answer_tip_empty);
            return;
        } else if (content.replace(" ", "").length() < 5 || subject.replace(" ", "").length() < 5) {
            mAskSubmitView.showToast(R.string.ask_answer_tip_little);
            return;
        }
        Bitmap bitmap = null;

        if (bitmapList != null && bitmapList.size() >= 1)
            bitmap = bitmapList.get(0);
        new AskPublishUseCase(subject, content, bitmap).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                ((Activity) mContext).setResult(Activity.RESULT_OK);
                ((Activity) mContext).finish();
            }

            @Override
            public void onCompleted() {
                mAskSubmitView.hideProgressDialog();
            }
        });
    }
}
