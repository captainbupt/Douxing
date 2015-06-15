package com.badou.mworking;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.fragment.ChatterListFragment;

/**
 * Created by Administrator on 2015/6/11.
 */
public class ChatterTopicActivity extends BaseBackActionBarActivity {
    public static final String KEY_TOPIC = "topic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_topic);
        ChatterListFragment fragment = new ChatterListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ChatterListFragment.KEY_ARGUMENT_TOPIC, mReceivedIntent.getStringExtra(KEY_TOPIC));
        fragment.setArguments(arguments);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fl_activity_chatter_topic_container, fragment);
        transaction.commit();
    }
}
