package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import java.io.File;

import rx.Observable;

public class SetHeadUseCase extends UseCase {

    File headFile;

    public SetHeadUseCase(File headFile) {
        this.headFile = headFile;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().setUserHead(UserInfo.getUserInfo().getUid(), headFile);
    }
}
