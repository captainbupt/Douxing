package com.badou.mworking.domain;

import android.text.TextUtils;

import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rx.Observable;

public class ChatterListUseCase extends UseCase {

    int mPageNum;
    String mTopic;
    String mUid;

    public void setPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    public ChatterListUseCase(String topic) {
        this.mTopic = topic;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (TextUtils.isEmpty(mUid)) {
            return RestRepository.getInstance().getChatterList(new Body(UserInfo.getUserInfo().getUid(), "share", mPageNum, Constant.LIST_ITEM_NUM), mTopic, false);
        } else {
            return RestRepository.getInstance().getChatterList(new Body(mUid, "share", mPageNum, Constant.LIST_ITEM_NUM), mTopic, true);
        }
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("type")
        String type;
        @SerializedName("page_no")
        int pageNum;
        @SerializedName("item_per_page")
        int itemNum;

        public Body(String uid, String type, int pageNum, int itemNum) {
            this.uid = uid;
            this.type = type;
            this.pageNum = pageNum;
            this.itemNum = itemNum;
        }

        public String getUid() {
            return uid;
        }

        public String getType() {
            return type;
        }

        public int getPageNum() {
            return pageNum;
        }

        public int getItemNum() {
            return itemNum;
        }
    }

    public static class Response {
        @SerializedName("ttlcnt")
        int totalCount;
        @SerializedName("result")
        List<Chatter> chatterList;

        public int getTotalCount() {
            return totalCount;
        }

        public List<Chatter> getChatterList() {
            return chatterList;
        }
    }
}
