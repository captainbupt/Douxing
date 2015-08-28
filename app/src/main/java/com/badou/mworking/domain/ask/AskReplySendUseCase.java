package com.badou.mworking.domain.ask;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.BitmapUtil;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AskReplySendUseCase extends UseCase {

    String mAid;
    String mContent;
    Bitmap mBitmap;
    String mWhom;

    public AskReplySendUseCase(String aid, String content, Bitmap bitmap, String whom) {
        this.mAid = aid;
        this.mContent = content;
        this.mBitmap = bitmap;
        this.mWhom = whom;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().sendAskReply(new Body(UserInfo.getUserInfo().getUid(), mAid, mContent, mBitmap == null ? null : BitmapUtil.bitmapToBase64(mBitmap), mWhom));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("aid")
        String aid;
        @SerializedName("content")
        String content;
        @SerializedName("picture")
        String picture;
        @SerializedName("whom")
        String whom;

        public Body(String uid, String aid, String content, String picture, String whom) {
            this.uid = uid;
            this.aid = aid;
            this.content = content;
            this.picture = picture;
            this.whom = whom;
        }
    }
}
