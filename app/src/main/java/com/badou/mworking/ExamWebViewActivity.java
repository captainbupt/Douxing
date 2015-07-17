package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.net.Net;

public class ExamWebViewActivity extends ExamBaseActivity {

    public static Intent getIntent(Context context, String rid) {
        return CategoryBaseActivity.getIntent(context, ExamWebViewActivity.class, rid);
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail) {
        super.setData(rid, categoryDetail);
        WebViewFragment mWebViewFragment = (WebViewFragment) WebViewFragment.getFragment(Net.getRunHost() + Net.EXAM_ITEM(UserInfo.getUserInfo().getUid(), rid));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, mWebViewFragment);
        transaction.commit();
    }
}
