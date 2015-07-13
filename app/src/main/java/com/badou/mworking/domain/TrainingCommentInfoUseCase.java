package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class TrainingCommentInfoUseCase extends UseCase {

    List<String> rids = new ArrayList<>();

    public void setRids(List<String> rids) {
        this.rids.clear();
        this.rids.addAll(rids);
    }

    public void setRid(String rid){
        rids.clear();
        rids.add(rid);
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getTrainCommentInfo(UserInfo.getUserInfo().getUid(), rids);
    }
}
