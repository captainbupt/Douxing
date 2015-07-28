package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class ClassificationUseCase extends UseCase {

    private String type;

    public ClassificationUseCase(String type) {
        this.type = type;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getClassification(UserInfo.getUserInfo().getUid(), type);
    }
}
