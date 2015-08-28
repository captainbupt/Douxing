package com.badou.mworking.domain.category;

import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import rx.Observable;

public class SurveyStatusUseCase extends UseCase {

    List<String> mSurveyIdList;

    public void setSurveyIdList(List<String> surveyIdList) {
        mSurveyIdList = surveyIdList;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getSurveyStatus(new Body(mSurveyIdList, UserInfo.getUserInfo().getUid()));
    }

    public static class Body {
        @SerializedName("op")
        String operation = "answer_status";
        @SerializedName("list")
        List<String> surveyIds;
        @SerializedName("uid")
        String uid;

        public Body(List<String> surveyIds, String uid) {
            this.surveyIds = surveyIds;
            this.uid = uid;
        }
    }
}
