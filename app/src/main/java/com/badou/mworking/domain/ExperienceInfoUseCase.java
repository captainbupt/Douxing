package com.badou.mworking.domain;

import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class ExperienceInfoUseCase extends UseCase {

    String name;
    String phone;
    String company;
    String title;

    public ExperienceInfoUseCase(String name, String phone, String company, String title) {
        this.name = name;
        this.phone = phone;
        this.company = company;
        this.title = title;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().sendExperienceInfo(new Body(name, phone, company, title));
    }

    public static class Body {
        @SerializedName("op")
        String operation = "add";
        @SerializedName("name")
        String name;
        @SerializedName("tel")
        String phone;
        @SerializedName("company")
        String company;
        @SerializedName("title")
        String title;
        @SerializedName("sys")
        String system = "android";

        public Body(String name, String phone, String company, String title) {
            this.name = name;
            this.phone = phone;
            this.company = company;
            this.title = title;
        }
    }
}
