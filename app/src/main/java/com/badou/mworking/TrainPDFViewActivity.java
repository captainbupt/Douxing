package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.Train;
import com.badou.mworking.fragment.PDFViewFragment;

public class TrainPDFViewActivity extends TrainBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFViewFragment pdfViewFragment = new PDFViewFragment();
        pdfViewFragment.setArguments(PDFViewFragment.getArgument(mTrain.getRid(), mTrain.getUrl()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, pdfViewFragment);
        transaction.commit();
    }

    public static Intent getIntent(Context context, Train train) {
        Intent intent = new Intent(context, TrainPDFViewActivity.class);
        intent.putExtra(KEY_TRAINING, train);
        return intent;
    }
}
