package com.badou.mworking.domain;

import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rx.Observable;

public class ChatterListUseCase extends UseCase {

    private int mPageNum;
    private String mTopic;

    public void setPageNum(int pageNum) {
        this.mPageNum = pageNum;
    }

    public ChatterListUseCase(String topic){
        this.mTopic = topic;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getChatterList(new Body(UserInfo.getUserInfo().getUid(), "share", mPageNum, Constant.LIST_ITEM_NUM), mTopic);
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
