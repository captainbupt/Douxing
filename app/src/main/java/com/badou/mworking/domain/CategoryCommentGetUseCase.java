package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class CategoryCommentGetUseCase extends UseCase {

    String mRid;
    int mPageNum;

    public CategoryCommentGetUseCase(String rid) {
        this.mRid = rid;
        mPageNum = 1;
    }

    public void setPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getCategoryComment(new Body(mPageNum, Constant.LIST_ITEM_NUM, UserInfo.getUserInfo().getUid(), mRid));
    }

    public static class Body {
        @SerializedName("page_no")
        int page_no;
        @SerializedName("item_per_page")
        int item_per_page;
        @SerializedName("uid")
        String uid;
        @SerializedName("rid")
        String rid;

        public Body(int page_no, int item_per_page, String uid, String rid) {
            this.page_no = page_no;
            this.item_per_page = item_per_page;
            this.uid = uid;
            this.rid = rid;
        }
    }

}
