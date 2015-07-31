package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class ChatterPraiseUseCase extends UseCase {

    String mQid;

    public ChatterPraiseUseCase(String qid) {
        this.mQid = qid;
    }

    public ChatterPraiseUseCase() {
    }

    public void setQid(String qid) {
        this.mQid = qid;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().praiseChatter(UserInfo.getUserInfo().getUid(), mQid);
    }
}
