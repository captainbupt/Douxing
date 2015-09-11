package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AuditGetUseCase extends UseCase {

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getAuditList(new Body(UserInfo.getUserInfo().getUid()));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;

        public Body(String uid) {
            this.uid = uid;
        }
    }
}
