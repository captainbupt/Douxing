package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class CategoryDetailUseCase extends UseCase {

    String mRid;

    public CategoryDetailUseCase(String rid) {
        this.mRid = rid;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getCategoryDetail(new Body(UserInfo.getUserInfo().getUid(), mRid));
    }

    public static class Body {

        @SerializedName("uid")
        String uid;
        @SerializedName("rid")
        String rid;

        public Body(String uid, String rid) {
            this.uid = uid;
            this.rid = rid;
        }
    }
}
