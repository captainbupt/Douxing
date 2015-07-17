package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.util.Constant;

public class TrainWebViewActivity extends TrainBaseActivity {

    public static Intent getIntent(Context context, String rid, boolean isTraining) {
        return TrainBaseActivity.getIntent(context, TrainWebViewActivity.class, rid, isTraining);
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail) {
        super.setData(rid, categoryDetail);
        WebViewFragment mWebViewFragment;
        if (categoryDetail.getFmt() == Constant.MWKG_FORAMT_TYPE_PDF) {
            mWebViewFragment = (WebViewFragment) WebViewFragment.getFragment(Constant.TRAIN_IMG_SHOW + rid + Constant.TRAIN_IMG_FORMAT);
        } else {
            mWebViewFragment = (WebViewFragment) WebViewFragment.getFragment(categoryDetail.getUrl());
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, mWebViewFragment);
        transaction.commit();
    }
}
