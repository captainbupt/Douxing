package com.badou.mworking;

import android.os.Bundle;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EMChatActivity extends BaseBackActionBarActivity {

    @InjectView(R.id.content_list_view)
    PullToRefreshListView mContentListView;
    @InjectView(R.id.none_result_view)
    NoneResultView mNoneResultView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emchat);
        setActionbarTitle(R.string.title_name_emchat);
        ButterKnife.inject(this);
    }

}
