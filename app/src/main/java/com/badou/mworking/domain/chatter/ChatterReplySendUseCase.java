package com.badou.mworking.domain.chatter;

import android.text.TextUtils;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class ChatterReplySendUseCase extends UseCase {

    String mQid;
    String mContent;
    String mWhom;

    public ChatterReplySendUseCase(String qid) {
        this.mQid = qid;
    }

    public void setData(String content, String whom) {
        this.mContent = content;
        this.mWhom = whom;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (TextUtils.isEmpty(mWhom)) {
            return RestRepository.getInstance().sendChatterReply(new Body(UserInfo.getUserInfo().getUid(), mQid, mContent));
        } else {
            return RestRepository.getInstance().sendChatterReplyAt(new Body(UserInfo.getUserInfo().getUid(), mQid, mContent, mWhom));
        }
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("qid")
        String qid;
        @SerializedName("content")
        String content;
        @SerializedName("whom")
        String whom;

        public Body(String uid, String qid, String content) {
            this.uid = uid;
            this.qid = qid;
            this.content = content;
        }

        public Body(String uid, String qid, String content, String whom) {
            this.uid = uid;
            this.qid = qid;
            this.content = content;
            this.whom = whom;
        }
    }
}
