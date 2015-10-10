package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.badou.mworking.R;
import com.badou.mworking.database.ChatterResManager;
import com.badou.mworking.domain.StoreListUseCase;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.domain.chatter.ChatterPraiseUseCase;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.StoreItem;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.factory.ResourceIntentFactory;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.StoreListView;
import com.google.gson.reflect.TypeToken;

import org.jivesoftware.smack.Chat;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.List;

public class StoreListPresenter extends ListPresenter<Store> {

    StoreListView mStoreListView;
    StoreListUseCase mStoreListUseCase;
    StoreUseCase mStoreUseCase;
    ChatterPraiseUseCase mPraiseUseCase;

    public StoreListPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mStoreListView = (StoreListView) v;
        super.attachView(v);
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<Store>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return SP.STORE + UserInfo.getUserInfo().getUid();
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mStoreListUseCase == null)
            mStoreListUseCase = new StoreListUseCase();
        mStoreListUseCase.setPageNum(pageIndex);
        return mStoreListUseCase;
    }

    @Override
    public void toDetailPage(Store data) {
        Intent intent = ResourceIntentFactory.getIntentFromStore(mContext, data);
        if (intent == null) {
            ToastUtil.showToast(mContext, R.string.category_unsupport_type);
        } else {
            ((Activity) mContext).startActivityForResult(intent, REQUEST_DETAIL);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DETAIL && resultCode == Activity.RESULT_OK && mClickPosition >= 0 && mClickPosition < mBaseListView.getDataCount()) {
            if (data.getBooleanExtra(RESULT_DELETED, false)) {
                Store store = mBaseListView.getItem(mClickPosition);
                store.setDeleted();
                mBaseListView.setItem(mClickPosition, store);
            } else {
                Serializable item = data.getSerializableExtra(RESULT_DATA);
                if (item != null) {
                    onResponseItem(mClickPosition, item);
                }
            }
        }
    }

    @Override
    public void onResponseItem(int position, Serializable item) {
        if (item instanceof StoreItem) {
            if (!((StoreItem) item).isStore()) {
                mBaseListView.removeItem(position);
            }
        }
        if (item instanceof String) {
            Chatter chatter = GsonUtil.fromJson((String) item, Chatter.class);
            if (!chatter.isStore()) {
                mBaseListView.removeItem(position);
            } else {
                Store store = mBaseListView.getItem(position);
                store.getChatter().setReplyNumber(chatter.getReplyNumber());
                mStoreListView.setItem(position, store);
            }
        }
    }

    public void deleteStore(Store store, final int position) {
        if (mStoreUseCase == null) {
            mStoreUseCase = new StoreUseCase();
        }
        mStoreUseCase.setData(store);

        mStoreUseCase.execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                mBaseListView.removeItem(position);
            }
        });
    }

    public void praiseStore(final Store store, final int position) {
        if (mPraiseUseCase == null)
            mPraiseUseCase = new ChatterPraiseUseCase();
        mPraiseUseCase.setQid(store.getChatter().getQid());
        mBaseListView.showProgressDialog(R.string.progress_tips_praise_ing);
        mPraiseUseCase.execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                ChatterResManager.insertItem(mContext, store.getChatter());
                store.getChatter().increasePraise();
                mBaseListView.setItem(position, store);
            }

            @Override
            public void onCompleted() {
                mBaseListView.hideProgressDialog();
            }
        });
    }
}
