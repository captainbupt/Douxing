package com.badou.mworking.domain.ask;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class AskReplyGetUseCase extends UseCase {

    String mAid;
    int mPageNum;

    public AskReplyGetUseCase(String aid) {
        this.mAid = aid;
    }

    public void setPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getAskReply(new Body(UserInfo.getUserInfo().getUid(), mAid, mPageNum, Constant.LIST_ITEM_NUM));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("aid")
        String aid;
        @SerializedName("page_no")
        int pageNum;
        @SerializedName("item_per_page")
        int itemNum;

        public Body(String uid, String aid, int pageNum, int itemNum) {
            this.uid = uid;
            this.aid = aid;
            this.pageNum = pageNum;
            this.itemNum = itemNum;
        }
    }
}
