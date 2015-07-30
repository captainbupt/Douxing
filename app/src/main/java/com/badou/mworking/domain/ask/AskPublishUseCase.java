package com.badou.mworking.domain.ask;

import android.graphics.Bitmap;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.BitmapUtil;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AskPublishUseCase extends UseCase {

    String mSubject;
    String mContent;
    Bitmap mBitmap;

    public AskPublishUseCase(String subject, String content, Bitmap bitmap) {
        this.mSubject = subject;
        this.mContent = content;
        this.mBitmap = bitmap;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().publishAsk(new Body(UserInfo.getUserInfo().getUid(), mSubject, mContent, mBitmap == null ? null : BitmapUtil.bitmapToBase64(mBitmap)));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("subject")
        String subject;
        @SerializedName("content")
        String content;
        @SerializedName("picture")
        String picture;

        public Body(String uid, String subject, String content, String picture) {
            this.uid = uid;
            this.subject = subject;
            this.content = content;
            this.picture = picture;
        }
    }

    public static class Response {
        @SerializedName("aid")
        String aid;

        public String getAid() {
            return aid;
        }
    }
}
