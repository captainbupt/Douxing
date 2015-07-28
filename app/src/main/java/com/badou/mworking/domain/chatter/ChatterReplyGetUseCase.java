package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class ChatterReplyGetUseCase extends UseCase {

    int mPageNum;
    String mQid;

    public ChatterReplyGetUseCase(String qid) {
        this.mQid = qid;
    }

    public void setPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getChatterReply(new Body(UserInfo.getUserInfo().getUid(), mQid, mPageNum, Constant.LIST_ITEM_NUM));
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("qid")
        String qid;
        @SerializedName("page_no")
        int pageNum;
        @SerializedName("item_per_page")
        int itemNum;

        public Body(String uid, String qid, int pageNum, int itemNum) {
            this.uid = uid;
            this.qid = qid;
            this.pageNum = pageNum;
            this.itemNum = itemNum;
        }
    }
}
