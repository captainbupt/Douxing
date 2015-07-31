package com.badou.mworking.domain;

import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class VerificationMessageUseCase extends UseCase {

    String mPhone;

    public VerificationMessageUseCase(String phone) {
        this.mPhone = phone;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().requestVerificationCode(new Body(mPhone));
    }

    public static class Body {
        @SerializedName("serial")
        String serial;

        public Body(String serial) {
            this.serial = serial;
        }
    }
}
