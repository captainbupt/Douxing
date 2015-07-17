package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class MarkReadUseCase extends UseCase {

    String mRid;

    public MarkReadUseCase(String rid) {
        this.mRid = rid;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        System.out.println("rid: " + mRid);
        return RestRepository.getInstance().markRead(UserInfo.getUserInfo().getUid(), mRid);
    }
}
