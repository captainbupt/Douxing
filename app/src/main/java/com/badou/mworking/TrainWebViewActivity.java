package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.Train;
import com.badou.mworking.fragment.WebViewFragment;

public class TrainWebViewActivity extends TrainBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebViewFragment mWebViewFragment = new WebViewFragment();
        mWebViewFragment.setArguments(WebViewFragment.getArgument(mTrain.getUrl()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, mWebViewFragment);
        transaction.commit();
    }

    public static Intent getIntent(Context context, Train train) {
        Intent intent = new Intent(context, TrainWebViewActivity.class);
        intent.putExtra(KEY_TRAINING, train);
        return intent;
    }
}
