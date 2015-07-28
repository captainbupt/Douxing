package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class ChatterUseCase extends UseCase {

    String mQid;

    public ChatterUseCase(String qid) {
        this.mQid = qid;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getChatter(UserInfo.getUserInfo().getUid(), mQid);
    }
}
