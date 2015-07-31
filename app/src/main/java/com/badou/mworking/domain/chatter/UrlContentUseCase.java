package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class UrlContentUseCase extends UseCase {

    String mUrl;

    public UrlContentUseCase(String url) {
        this.mUrl = url;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().parseUrlContent(new Body(UserInfo.getUserInfo().getUid(), mUrl), mUrl);
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("url")
        String url;

        public Body(String uid, String url) {
            this.uid = uid;
            this.url = url;
        }
    }
}
