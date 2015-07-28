package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class ChatterPraiseUseCase extends UseCase {

    String mQid;

    public ChatterPraiseUseCase(String qid) {
        this.mQid = qid;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().praiseChatter(UserInfo.getUserInfo().getUid(), mQid);
    }
}
