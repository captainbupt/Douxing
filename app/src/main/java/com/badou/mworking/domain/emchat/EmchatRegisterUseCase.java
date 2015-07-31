package com.badou.mworking.domain.emchat;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class EmchatRegisterUseCase extends UseCase {

    String mUsername;

    public EmchatRegisterUseCase(String username) {
        this.mUsername = username;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().registerEmchat(new Body(mUsername));
    }

    public static class Body {
        @SerializedName("hxusr")
        String username;

        public Body(String username) {
            this.username = username;
        }
    }

    public static class Response {
        @SerializedName("hxpwd")
        String pwd;

        public String getPwd() {
            return pwd;
        }
    }
}
