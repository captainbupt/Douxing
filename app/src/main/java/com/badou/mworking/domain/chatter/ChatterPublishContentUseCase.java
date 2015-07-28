package com.badou.mworking.domain.chatter;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.easemob.chatuidemo.domain.User;

import java.io.File;

import rx.Observable;

public class ChatterPublishContentUseCase extends UseCase {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_URL = 3;

    int mImgIndex;
    int mType;
    String mQid;
    String mUrl;

    public ChatterPublishContentUseCase(int type, String qid, String url) {
        this.mQid = qid;
        this.mType = type;
        this.mUrl = url;
        this.mImgIndex = 1;
    }

    public void next(String filePath) {
        mImgIndex++;
        mUrl = filePath;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        switch (mType) {
            case TYPE_IMAGE:
                return RestRepository.getInstance().publishChatterImage(UserInfo.getUserInfo().getUid(), mQid, mImgIndex, new File(mUrl));
            case TYPE_VIDEO:
                return RestRepository.getInstance().publishChatterVideo(UserInfo.getUserInfo().getUid(), mQid, new File(mUrl));
            case TYPE_URL:
                return RestRepository.getInstance().publicChatterUrl(UserInfo.getUserInfo().getUid(), mQid, mUrl);
        }
        return null;
    }
}
