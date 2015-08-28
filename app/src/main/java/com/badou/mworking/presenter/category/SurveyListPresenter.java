package com.badou.mworking.presenter.category;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.domain.category.CategoryUseCase;
import com.badou.mworking.domain.category.SurveyStatusUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Survey;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.net.BaseSubscriber;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class SurveyListPresenter extends CategoryListPresenter {

    SurveyStatusUseCase mSurveyStatusUseCase;

    public SurveyListPresenter(Context context, int category) {
        super(context, category);
        mSurveyStatusUseCase = new SurveyStatusUseCase();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mClickPosition >= 0 && mClickPosition < mCategoryListView.getDataCount()) {
            final Survey survey = (Survey) mCategoryListView.getItem(mClickPosition);
            mSurveyStatusUseCase.setSurveyIdList(new ArrayList<String>() {{
                add(survey.getSurveyId());
            }});
            mSurveyStatusUseCase.execute(new BaseSubscriber<LinkedTreeMap<String, Integer>>(mContext) {
                @Override
                public void onResponseSuccess(LinkedTreeMap<String, Integer> data) {
                    survey.setRead(data.get(survey.getSurveyId()) > 0);
                    mCategoryListView.setItem(mClickPosition, survey);
                }
            });
        }
    }

    @Override
    protected boolean setData(Object data, int index) {
        CategoryOverall categoryOverall = (CategoryOverall) data;
        List<Category> trainingList = categoryOverall.getCategoryList(mCategoryIndex);
        updateCommentInfo(trainingList, index);
        return super.setData(data, index);
    }

    private void updateCommentInfo(final List<Category> surveyList, final int index) {
        List<String> surveyIds = new ArrayList<>();
        for (Category category : surveyList) {
            surveyIds.add(((Survey) category).getSurveyId());
        }
        mSurveyStatusUseCase.setSurveyIdList(surveyIds);
        mSurveyStatusUseCase.execute(new BaseSubscriber<LinkedTreeMap<String, Integer>>(mContext) {
            @Override
            public void onResponseSuccess(LinkedTreeMap<String, Integer> data) {

                for (String surveyId : data.keySet()) {
                    int status = data.get(surveyId);
                    for (Category survey : surveyList) {
                        if (((Survey) survey).getSurveyId().equals(surveyId)) {
                            survey.setRead(status > 0);
                        }
                    }
                }
                SurveyListPresenter.super.setList(surveyList, index);
            }

            @Override
            public void onCompleted() {
                mBaseListView.refreshComplete();
            }
        });
    }

}
