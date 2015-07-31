package com.badou.mworking.domain;

import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class ResetPasswordUseCase extends UseCase {

    String mPhone;
    String mCode;
    String mPassword;

    public ResetPasswordUseCase(String phone, String code, String password) {
        this.mPhone = phone;
        this.mCode = code;
        this.mPassword = password;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().resetPassword(new Body(mPhone, mCode, mPassword));
    }

    public static class Body {
        @SerializedName("serial")
        String serial;
        @SerializedName("vcode")
        String vcode;
        @SerializedName("newpwd")
        String pwd;

        public Body(String serial, String vcode, String pwd) {
            this.serial = serial;
            this.vcode = vcode;
            this.pwd = pwd;
        }
    }
}
