package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class ChatterTopicUseCase extends UseCase {

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getTopicList(UserInfo.getUserInfo().getUid());
    }
}
