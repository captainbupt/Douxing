package com.badou.mworking.domain.ask;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AskListUseCase extends UseCase {

    int mPageNum;

    public void setPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getAskList(new Body(UserInfo.getUserInfo().getUid(), mPageNum, Constant.LIST_ITEM_NUM));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("page_no")
        int pageNum;
        @SerializedName("item_per_page")
        int itemNum;
        @SerializedName("key")
        String key = "";

        public Body(String uid, int pageNum, int itemNum) {
            this.uid = uid;
            this.pageNum = pageNum;
            this.itemNum = itemNum;
        }
    }
}
