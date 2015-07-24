package com.badou.mworking.presenter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.VolleyError;
import com.badou.mworking.R;
import com.badou.mworking.adapter.ChatterTopicAdapter;
import com.badou.mworking.domain.PublishChatterUseCase;
import com.badou.mworking.entity.ChatterTopic;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.ChatterSubmitView;
import com.badou.mworking.widget.NoScrollListView;
import com.badou.mworking.widget.VideoImageView;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatterSubmitPresenter extends Presenter {

    boolean isTopicShow = false;
    boolean isAnonymous = false;
    boolean isVideo = false;
    ChatterSubmitView mChatterSubmitView;

    public ChatterSubmitPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mChatterSubmitView = (ChatterSubmitView) v;
    }

    public void showTopic() {
        isTopicShow = !isTopicShow;
        mChatterSubmitView.setTopicListVisibility(isTopicShow);
    }

    public void setAnonymous() {
        isAnonymous = !isAnonymous;
        mChatterSubmitView.setAnonymousCheckBox(isAnonymous);
    }

    public void takeImage() {
        isTopicShow = false;
        mChatterSubmitView.setTopicListVisibility(false);
        if (!isVideo && mBitmapList.size() >= mChatterSubmitView.getMaxImageCount()) {
            mChatterSubmitView.showToast(R.string.chatter_submit_max_image);
            return;
        }
        mChatterSubmitView.takeImage();
    }

    public void onImageSelected(Bitmap bitmap, boolean isVideo) {
        if (mBitmapList == null)
            mBitmapList = new ArrayList<>();
        if (isVideo) {
            for (Bitmap tmp : mBitmapList) {
                if (tmp != null && !tmp.isRecycled())
                    tmp.recycle();
            }
            mBitmapList.clear();
            mBitmapList.add(bitmap);
        } else {
            mBitmapList.add(bitmap);
        }
        this.isVideo = isVideo;
        mChatterSubmitView.setImageMode(isVideo);
        mChatterSubmitView.addImage(bitmap);
        mChatterSubmitView.addVideo(bitmap, FileUtils.getChatterVideoDir(mContext));
    }

    public void onTopicConfirmed(String content) {
        String topic = content.replace("#", "").replace(" ", "").trim();
        mChatterSubmitView.onTopicSelected(topic);
    }

    public void onTopicClicked(ChatterTopic chatterTopic) {
        mChatterSubmitView.onTopicSelected(chatterTopic.getKey());
    }

    public void onVideoDeleted() {
        isVideo = false;
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
        mChatterSubmitView.showToast(R.string.progress_tips_send_ing);
        PublishChatterUseCase publishChatterUseCase;
        if (isVideo && bitmapList != null && bitmapList.size() == 1) {
            publishChatterUseCase = new PublishChatterUseCase(content, BitmapUtil.bitmapToBase64(bitmapList.get(0)), isAnonymous);
        }else{
            publishChatterUseCase = new PublishChatterUseCase(content, null, isAnonymous);
        }
        publishChatterUseCase.execute(new BaseSubscriber<PublishChatterUseCase.Response>(mContext) {
            @Override
            public void onResponseSuccess(PublishChatterUseCase.Response data) {
                String qid = data.getQid();
                if(!isVideo){

                }
            }

            @Override
            public void onCompleted() {
                mChatterSubmitView.hideProgressDialog();
            }
        });
        ServiceProvider.doPublishQuestionShare(mContext, "share", content, bitmap, mAnonymousCheckBox.isChecked() ? 1 : 0,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        // 获取questionid
                        String qid = response.optJSONObject(Net.DATA).optString("qid");
                        if (TextUtils.isEmpty(qid)) {
                            ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                            return;
                        }
                        if (type == ImageChooser.TYPE_VIDEO) {
                            publishVideo(qid);
                        } else if (type == ImageChooser.TYPE_IMAGE) {
                            publicImage(qid, 0, bitmapList);
                        } else {
                            ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                            toChatterActivity();
                        }
                    }

                    @Override
                    public void onErrorCode(int code) {
                        ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                    }
                });
    }

    private void publicImage(final String qid, final int index, final List<Object> bitmapList) {
        ServiceProvider.doUploadImage(mContext, qid, index + 1, (Bitmap) bitmapList.get(index), new VolleyListener(mContext) {

            @Override
            public void onResponseSuccess(JSONObject response) {
                if (index == bitmapList.size() - 1) {
                    mProgressDialog.dismiss();
                    ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                    toChatterActivity();
                } else {
                    publicImage(qid, index + 1, bitmapList);
                }
            }

            @Override
            public void onErrorCode(int code) {
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
            }
        });
    }

    /**
     * 功能描述: 上传视屏
     */
    private void publishVideo(String qid) {

        ServiceProvider.doUploadVideo(mContext, qid, FileUtils.getChatterVideoDir(mContext), new VolleyListener(mContext) {
            @Override
            public void onResponseSuccess(JSONObject response) {
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                toChatterActivity();
            }

            @Override
            public void onErrorCode(int code) {
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
            }

            @Override
            public void onCompleted() {
                mProgressDialog.dismiss();
            }
        });

    }

    private void toChatterActivity() {
        setResult(RESULT_OK);
        finish();
    }

    private void getTopicList() {
        ServiceProvider.doGetTopicList(mContext, new VolleyListener(mContext) {

            @Override
            public void onResponseSuccess(JSONObject response) {
                JSONObject datas = response.optJSONObject(Net.DATA);
                if (datas == null) {
                    return;
                }
                List<Object> topicList = new ArrayList<>();
                Iterator it = datas.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = datas.optString(key);
                    topicList.add(new ChatterTopic(key, Long.parseLong(value) * 1000));
                }
                mTopicAdapter.setList(topicList);
            }
        });
    }
}
