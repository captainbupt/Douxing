package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;

import rx.Observable;

public class ChatterHotListUseCase extends UseCase {

    int mPageNum;

    public void setPageNum(int mPageNum) {
        this.mPageNum = mPageNum;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getChatterHotList(UserInfo.getUserInfo().getUid(), mPageNum, Constant.LIST_ITEM_NUM);
    }
}
