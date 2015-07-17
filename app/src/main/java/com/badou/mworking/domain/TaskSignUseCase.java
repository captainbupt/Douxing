package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.baidu.location.BDLocation;

import java.io.File;

import rx.Observable;

public class TaskSignUseCase extends UseCase {

    String mRid;
    BDLocation mLocation;
    File mFile;

    public TaskSignUseCase(String rid, BDLocation location, File file) {
        this.mRid = rid;
        this.mLocation = location;
        this.mFile = file;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().taskSign(UserInfo.getUserInfo().getUid(), mRid, mLocation.getLatitude(), mLocation.getLongitude(), mFile);
    }
}
