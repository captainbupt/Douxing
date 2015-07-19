package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

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
        String uid;
        String rid;
        String op;

        public Body(String uid, String rid, String op) {
            this.uid = uid;
            this.rid = rid;
            this.op = op;
        }
    }
}
