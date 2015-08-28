package com.badou.mworking.presenter.category;

import android.content.Context;

import com.badou.mworking.domain.category.TrainingCommentInfoUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.net.BaseSubscriber;

import java.util.ArrayList;
import java.util.List;

public class TrainingListPresenter extends CategoryListPresenter {

    public static final String RESPONSE_RATING_NUMBER = "ratingNumber";
    public static final String RESPONSE_COMMENT_NUMBER = "commentNumber";

    TrainingCommentInfoUseCase commentInfoUseCase = new TrainingCommentInfoUseCase();

    public TrainingListPresenter(Context context, int category) {
        super(context, category);
    }

    @Override
    protected boolean setData(Object data, int index) {
        CategoryOverall categoryOverall = (CategoryOverall) data;
        List<Category> trainingList = categoryOverall.getCategoryList(mCategoryIndex);
        updateCommentInfo(trainingList, index);
        return super.setData(data, index);
    }

    private void updateCommentInfo(final List<Category> trainingList, final int index) {
        List<String> rids = new ArrayList<>();
        for (Category category : trainingList) {
            rids.add(category.getRid());
        }
        commentInfoUseCase.setRids(rids);
        commentInfoUseCase.execute(new BaseSubscriber<List<Train.TrainingCommentInfo>>(mContext) {
            @Override
            public void onResponseSuccess(List<Train.TrainingCommentInfo> data) {
                for (Train.TrainingCommentInfo commentInfo : data) {
                    for (Category category : trainingList) {
                        if (category.getRid().equals(commentInfo.getRid())) {
                            ((Train) category).setCommentInfo(commentInfo);
                        }
                    }
                }
                TrainingListPresenter.super.setList(trainingList, index);
            }

            @Override
            public void onCompleted() {
                mBaseListView.refreshComplete();
            }
        });
    }
}
