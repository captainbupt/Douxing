package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class StoreUseCase extends UseCase {

    private boolean isAdd;
    private String sid;
    private String type;

    public StoreUseCase(String sid, String type) {
        this.sid = sid;
        this.type = type;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().modifyStore(new Body(UserInfo.getUserInfo().getUid(), sid, type), isAdd);
    }

    public static class Body {
        String uid;
        String sid;
        String type;

        public Body(String uid, String sid, String type) {
            this.uid = uid;
            this.sid = sid;
            this.type = type;
        }
    }
}
