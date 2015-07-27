package com.badou.mworking;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.fragment.ChatterListFragment;

public class ChatterTopicActivity extends BaseBackActionBarActivity {
    public static final String KEY_TOPIC = "topic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_topic);
        ChatterListFragment fragment = ChatterListFragment.getFragment(mReceivedIntent.getStringExtra(KEY_TOPIC));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_activity_chatter_topic_container, fragment);
        transaction.commit();
    }
}
