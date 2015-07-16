package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class SearchUseCase extends UseCase {

    String mKey;

    public void setKey(String key) {
        this.mKey = key;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getSearchResult(UserInfo.getUserInfo().getUid(), mKey);
    }
}
