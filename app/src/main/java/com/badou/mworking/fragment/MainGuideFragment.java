package com.badou.mworking.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainGuideFragment extends BaseFragment {

    @Bind(R.id.message_layout)
    LinearLayout mMessageLayout;
    @Bind(R.id.complete_layout)
    LinearLayout mCompleteLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_guide, container, false);
        ButterKnife.bind(this, view);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return view;
    }

    @OnClick(R.id.next)
    void next() {
        mMessageLayout.setVisibility(View.GONE);
        mCompleteLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.finish)
    void finish() {
        ((ActionBarActivity) mActivity).getSupportFragmentManager().beginTransaction().hide(this).commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
