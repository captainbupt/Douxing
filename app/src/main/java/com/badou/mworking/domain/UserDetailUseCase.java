package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class UserDetailUseCase extends UseCase {

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getUserDetail(UserInfo.getUserInfo().getUid());
    }
}
