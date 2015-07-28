package com.badou.mworking.presenter.chatter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.RadioGroup;

import com.badou.mworking.ChatterSubmitActivity;
import com.badou.mworking.R;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.entity.Store;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.chatter.ChatterView;

public class ChatterPresenter extends Presenter{

    private static final int REQUEST_CHATTER_SUBMIT = 1;

    ChatterView mChatterView;

    public ChatterPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mChatterView = (ChatterView) v;
        mChatterView.setChatterPage();
    }

    public void onPageSelected(int position){
        if (position == 0)
            mChatterView.setChatterPage();
        else
            mChatterView.setHotPage();
    }

    public void publishChatter(){
        Intent intent = ChatterSubmitActivity.getIntent(mContext, null);
        ((Activity)mContext).startActivityForResult(intent, REQUEST_CHATTER_SUBMIT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CHATTER_SUBMIT && resultCode == Activity.RESULT_OK){
            mChatterView.refresh();
        }
    }
}
