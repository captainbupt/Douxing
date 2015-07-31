package com.badou.mworking.domain.ask;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AskReplyPraiseUseCase extends UseCase {

    String mAid;
    long mTs;

    public AskReplyPraiseUseCase(String aid) {
        this.mAid = aid;
    }

    public void setTs(long ts) {
        this.mTs = ts / 1000l;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().praiseAskReply(new Body(UserInfo.getUserInfo().getUid(), mAid, mTs));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("aid")
        String aid;
        @SerializedName("ts")
        long ts;

        public Body(String uid, String aid, long ts) {
            this.uid = uid;
            this.aid = aid;
            this.ts = ts;
        }
    }
}
