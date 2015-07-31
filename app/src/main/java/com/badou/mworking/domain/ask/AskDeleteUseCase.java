package com.badou.mworking.domain.ask;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AskDeleteUseCase extends UseCase {

    String mAid;

    public AskDeleteUseCase(String aid) {
        this.mAid = aid;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().deleteAsk(new Body(UserInfo.getUserInfo().getUid(), mAid));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("aid")
        String aid;

        public Body(String uid, String aid) {
            this.uid = uid;
            this.aid = aid;
        }
    }
}
