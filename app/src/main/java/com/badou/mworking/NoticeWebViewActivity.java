package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.fragment.WebViewFragment;

public class NoticeWebViewActivity extends NoticeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebViewFragment mWebViewFragment = new WebViewFragment();
        mWebViewFragment.setArguments(WebViewFragment.getArgument(mNotice.url));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, mWebViewFragment);
        transaction.commit();
    }

    public static Intent getIntent(Context context, Notice notice) {
        Intent intent = new Intent(context, NoticeWebViewActivity.class);
        intent.putExtra(KEY_NOTICE, notice);
        return intent;
    }
}
