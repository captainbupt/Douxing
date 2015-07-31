package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class CategoryRateUseCase extends UseCase {

    String mRid;
    int mCredit;

    public CategoryRateUseCase(String rid) {
        this.mRid = rid;
    }

    public void setCredit(int credit) {
        this.mCredit = credit;
    }


    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().rateCategory(UserInfo.getUserInfo().getUid(), mRid, mCredit);
    }
}
