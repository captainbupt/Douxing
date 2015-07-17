package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.fragment.WebViewFragment;

public class ExamWebViewActivity extends ExamBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebViewFragment mWebViewFragment = (WebViewFragment) WebViewFragment.getFragment(mExam.getUrl());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, mWebViewFragment);
        transaction.commit();
    }

    public static Intent getIntent(Context context, Exam exam) {
        Intent intent = new Intent(context, ExamWebViewActivity.class);
        intent.putExtra(KEY_EXAM, exam);
        return intent;
    }
}
