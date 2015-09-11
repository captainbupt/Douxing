package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AuditSetUseCase extends UseCase {

    String mWhom;
    boolean isIn;

    public AuditSetUseCase(String whom, boolean isIn) {
        mWhom = whom;
        this.isIn = isIn;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().setAudit(new Body(UserInfo.getUserInfo().getUid(), mWhom, isIn));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("whom")
        String whom;
        @SerializedName("op")
        String option;

        public Body(String uid, String whom, boolean isIn) {
            this.uid = uid;
            this.whom = whom;
            this.option = isIn ? "in" : "out";
        }
    }
}
