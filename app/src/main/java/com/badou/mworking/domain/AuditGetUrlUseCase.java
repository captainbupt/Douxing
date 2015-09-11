package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AuditGetUrlUseCase extends UseCase {
    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getAuditUrl(new Body(UserInfo.getUserInfo().getUid()));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;

        public Body(String uid) {
            this.uid = uid;
        }
    }

    public static class Response {
        @SerializedName("url")
        String url;

        public String getUrl() {
            return url;
        }
    }
}
