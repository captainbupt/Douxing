package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class CategoryCommentSendUseCase extends UseCase {

    String mRid;
    String mWhom;
    String mComment;

    public CategoryCommentSendUseCase(String rid) {
        this.mRid = rid;
    }

    public void setData(String comment) {
        setData(comment, null);
    }

    public void setData(String comment, String whom) {
        this.mComment = comment;
        this.mWhom = whom;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().sendCategoryComment(UserInfo.getUserInfo().getUid(), mRid, mWhom, mComment);
    }
}
