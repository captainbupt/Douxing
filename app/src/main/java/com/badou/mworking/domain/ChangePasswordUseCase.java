package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import rx.Observable;

public class ChangePasswordUseCase extends UseCase {

    String mOldPassword;
    String mNewPassword;

    public ChangePasswordUseCase(String oldPassword, String newPassword) {
        this.mOldPassword = oldPassword;
        this.mNewPassword = newPassword;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().changePassword(new Body(UserInfo.getUserInfo().getUid(), mOldPassword, mNewPassword));
    }

    public static class Body {
        String uid;
        String oldpwd;
        String newpwd;

        public Body(String uid, String oldpwd, String newpwd) {
            this.uid = uid;
            this.oldpwd = oldpwd;
            this.newpwd = newpwd;
        }
    }

    public static class Response{
        String uid;

        public String getUid() {
            return uid;
        }
    }
}
