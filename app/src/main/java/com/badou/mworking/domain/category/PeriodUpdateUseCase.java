package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class PeriodUpdateUseCase extends UseCase {

    String mRid;
    int mTime;

    public PeriodUpdateUseCase(String rid) {
        mRid = rid;
    }

    public void setTime(int time) {
        mTime = time;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().updatePeriod(new Body(UserInfo.getUserInfo().getUid(), mRid, mTime));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("rid")
        String rid;
        @SerializedName("ts")
        int time;

        public Body(String uid, String rid, int time) {
            this.uid = uid;
            this.rid = rid;
            this.time = time;
        }
    }
}
