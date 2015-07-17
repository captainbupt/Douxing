package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.fragment.PDFViewFragment;

public class TrainPDFViewActivity extends TrainBaseActivity {

    @Override
    public void setData(String rid, CategoryDetail categoryDetail) {
        super.setData(rid, categoryDetail);
        PDFViewFragment pdfViewFragment = new PDFViewFragment();
        pdfViewFragment.setArguments(PDFViewFragment.getArgument(rid, categoryDetail.getUrl()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, pdfViewFragment);
        transaction.commit();
    }

    public static Intent getIntent(Context context, String rid, boolean isTraining) {
        return TrainBaseActivity.getIntent(context, TrainPDFViewActivity.class, rid, isTraining);
    }
}
