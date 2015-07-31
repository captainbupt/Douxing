package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class ChatterDeleteUseCase extends UseCase {

    String mQid;

    public ChatterDeleteUseCase(String qid) {
        this.mQid = qid;
    }


    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().deleteChatter(UserInfo.getUserInfo().getUid(), mQid);
    }
}
