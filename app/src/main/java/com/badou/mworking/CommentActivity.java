package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.fragment.CommentFragment;
import com.badou.mworking.presenter.CommentPresenter;

/**
 * 功能描述: 评论页面
 */
public class CommentActivity extends BaseBackActionBarActivity {

    public static final String KEY_RID = "rid";
    public static final String RESPONSE_COUNT = "count";

    public static Intent getIntent(Context context, String rid) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(KEY_RID, rid);
        return intent;
    }

    CommentFragment commentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        setActionbarTitle(R.string.title_name_comment);
        commentFragment = CommentFragment.getFragment(mReceivedIntent.getStringExtra(KEY_RID));
        getSupportFragmentManager().beginTransaction().replace(R.id.container, commentFragment).commit();
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra(RESPONSE_COUNT, commentFragment.getCommentPresenter().getCommentCount());
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (!commentFragment.onBackPressed())
            super.onBackPressed();
    }
}
