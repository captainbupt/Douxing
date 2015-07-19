package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.presenter.CategoryBasePresenter;
import com.badou.mworking.presenter.ListPresenter;
import com.badou.mworking.view.CategoryBaseView;

import org.json.JSONObject;

public abstract class CategoryBaseActivity extends BaseBackActionBarActivity implements CategoryBaseView {

    protected static final String KEY_RID = "rid";

    CategoryBasePresenter mPresenter;
    ImageView mStoreImageView;

    public static Intent getIntent(Context context, Class clz, String rid) {
        Intent intent = new Intent(context, clz);
        intent.putExtra(KEY_RID, rid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = (CategoryBasePresenter) getPresenter();
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail) {
        mStoreImageView = getDefaultImageView(mContext, categoryDetail.isStore() ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
        addTitleRightView(mStoreImageView, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.onStoreClicked();
            }
        });
        if (UserInfo.getUserInfo().isAdmin()) {
            addTitleRightView(getDefaultImageView(mContext, R.drawable.button_title_admin_statistical), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.onStatisticalClicked();
                }
            });
        }
        setCommentNumber(categoryDetail.getCcnt());
        setRatingNumber(categoryDetail.getEcnt());
    }

    @Override
    public void setStore(boolean isStore) {
        mStoreImageView.setImageResource(isStore ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        CategoryDetail categoryDetail = mPresenter.getData();
        setResult(RESULT_OK, ListPresenter.getResultIntent(categoryDetail));
        super.finish();
    }
}
