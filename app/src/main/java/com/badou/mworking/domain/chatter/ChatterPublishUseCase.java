package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class ChatterPublishUseCase extends UseCase {

    String mContent;
    String mPicture;
    boolean mAnonymous;

    public ChatterPublishUseCase(String mContent, String mPicture, boolean mAnonymous) {
        this.mContent = mContent;
        this.mPicture = mPicture;
        this.mAnonymous = mAnonymous;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().publishChatter(new Body(UserInfo.getUserInfo().getUid(), mContent, mPicture, mAnonymous ? 1 : 0));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("type")
        String type = "share";
        @SerializedName("content")
        String content;
        @SerializedName("picture")
        String picture;
        @SerializedName("anonymous")
        int anonymous;

        public Body(String uid, String content, String picture, int anonymous) {
            this.uid = uid;
            this.content = content;
            this.picture = picture;
            this.anonymous = anonymous;
        }
    }

    public static class Response {
        @SerializedName("qid")
        String qid;

        public String getQid() {
            return qid;
        }
    }
}
