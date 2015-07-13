package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.fragment.PDFViewFragment;

public class NoticePDFViewActivity extends NoticeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFViewFragment pdfViewFragment = new PDFViewFragment();
        pdfViewFragment.setArguments(PDFViewFragment.getArgument(mNotice.getRid(), mNotice.getUrl()));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_container, pdfViewFragment);
        transaction.commit();
    }

    public static Intent getIntent(Context context, Notice notice) {
        Intent intent = new Intent(context, NoticePDFViewActivity.class);
        intent.putExtra(KEY_NOTICE, notice);
        return intent;
    }
}
