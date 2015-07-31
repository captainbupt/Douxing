package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class ChatterReplyDeleteUseCase extends UseCase {

    String mQid;
    int mFloor;

    public ChatterReplyDeleteUseCase(String qid, int floor) {
        this.mQid = qid;
        this.mFloor = floor;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().deleteChatterReply(new Body(UserInfo.getUserInfo().getUid(), mQid, mFloor));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("qid")
        String qid;
        @SerializedName("no")
        int floor;

        public Body(String uid, String qid, int floor) {
            this.uid = uid;
            this.qid = qid;
            this.floor = floor;
        }
    }
}
