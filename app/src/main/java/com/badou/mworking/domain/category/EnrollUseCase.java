package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class EnrollUseCase extends UseCase {

    String mRid;
    boolean isEnroll;

    public EnrollUseCase(String rid) {
        this.mRid = rid;
    }

    public void setIsEnroll(boolean isEnroll) {
        this.isEnroll = isEnroll;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().enroll(new Body(UserInfo.getUserInfo().getUid(), mRid, isEnroll ? "in" : "out"));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("rid")
        String rid;
        @SerializedName("op")
        String op;

        public Body(String uid, String rid, String op) {
            this.uid = uid;
            this.rid = rid;
            this.op = op;
        }
    }
}
