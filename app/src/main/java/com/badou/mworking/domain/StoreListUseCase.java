package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;

import rx.Observable;

public class StoreListUseCase extends UseCase {

    int mPageNum;

    public void setPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getStoreList(UserInfo.getUserInfo().getUid(), mPageNum, Constant.LIST_ITEM_NUM);
    }
}
