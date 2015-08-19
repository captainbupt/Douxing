package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import java.util.List;

import rx.Observable;

public class CategoryBaseUseCase extends UseCase{

    List<String> mRidList;

    public CategoryBaseUseCase(List<String> ridList) {
        mRidList = ridList;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getCategoryBase(UserInfo.getUserInfo().getUid(), mRidList);
    }
}
