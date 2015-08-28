package com.badou.mworking.entity.category;

import com.google.gson.annotations.SerializedName;

public class Survey extends Category {

    @SerializedName("survey_id")
    String surveyId;

    public String getSurveyId() {
        return surveyId;
    }

    @Override
    public int getCategoryType() {
        return CATEGORY_SURVEY;
    }

    @Override
    public void updateData(CategoryDetail categoryDetail) {

    }
}
