package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.badou.mworking.R;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseListView;
import com.badou.mworking.view.BaseView;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ListPresenter<T> extends Presenter {

    protected int mCurrentIndex = 1;
    protected int mClickPosition = -1;

    protected static final String RESULT_DATA = "data";
    protected static final String RESULT_DELETED = "deleted";
    protected static final int REQUEST_DETAIL = 5;

    BaseListView<T> mBaseListView;

    Handler handler;

    public ListPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mBaseListView = (BaseListView<T>) v;
        initialize();
    }

    protected void initialize() {
        if (!TextUtils.isEmpty(getCacheKey())) {
            List<T> cache = SPHelper.getList(getCacheKey(), getType());
            ListPresenter.this.setList(cache, 1);
        }
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBaseListView.startRefreshing();
            }
        }, 200);
    }

    protected abstract Type getType();

    protected abstract String getCacheKey();

    protected abstract UseCase getRefreshUseCase(int pageIndex);

    public void refresh() {
        mBaseListView.hideNoneResult();
        mBaseListView.showProgressBar();
        getRefreshUseCase(1).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                if (setData(data, 1)) {
                    mCurrentIndex = 1;
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mBaseListView.refreshComplete();
            }
        });
    }

    public void loadMore() {
        mBaseListView.showProgressBar();
        getRefreshUseCase(mCurrentIndex + 1).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                if (setData(data, mCurrentIndex + 1)) {
                    mCurrentIndex++;
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mBaseListView.refreshComplete();
            }
        });
    }

    protected boolean setData(Object data, int index) {
        if (data instanceof List) {
            return setList((List<T>) data, index);
        }
        return false;
    }

    protected boolean setList(List<T> data, int index) {
        mBaseListView.refreshComplete();
        System.out.println("size: " + data.size());
        if (index == 1) {
            if (data == null || data.size() == 0) {
                mBaseListView.showNoneResult();
                mBaseListView.disablePullUp();
                mBaseListView.setData(null);
            } else {
                mBaseListView.setData(data);
                mBaseListView.enablePullUp();
            }
            if (!TextUtils.isEmpty(getCacheKey())) {
                SPHelper.setList(getCacheKey(), data, getType());
            }
            return true;
        } else {
            if (data == null || data.size() == 0) {
                mBaseListView.showToast(R.string.no_more);
                return false;
            } else { // 避免重复加载，根据index来判断，只修改对应位置的值。如没有，则添加到末尾
                int begin = (index - 1) * Constant.LIST_ITEM_NUM;
                if (mBaseListView.getDataCount() <= begin) {
                    mBaseListView.addData(data);
                } else {
                    List<T> tmp = new ArrayList<>();
                    for (int ii = 0; ii < data.size(); ii++) {
                        if (ii < mBaseListView.getDataCount() - begin) {
                            mBaseListView.setItem(ii + begin, data.get(ii));
                        } else {
                            tmp.add(data.get(ii));
                        }
                    }
                    if (tmp.size() > 0) {
                        mBaseListView.addData(tmp);
                    }
                }
                return true;
            }
        }
    }

    public void onItemClick(T data, int position) {
        if (!NetUtils.isNetConnected(mContext)) {
            ToastUtil.showToast(mContext, R.string.error_service);
            return;
        }
        mClickPosition = position;
        toDetailPage(data);
    }

    abstract public void toDetailPage(T data);

    @Override
    public void destroy() {
        if (handler != null)
            handler.removeCallbacksAndMessages(null);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DETAIL && resultCode == Activity.RESULT_OK && mClickPosition >= 0 && mClickPosition < mBaseListView.getDataCount()) {
            if (data.getBooleanExtra(RESULT_DELETED, false)) {
                mBaseListView.removeItem(mClickPosition);
            } else {
                Serializable item = data.getSerializableExtra(RESULT_DATA);
                if (item != null) {
                    onResponseItem(mClickPosition, item);
                }
            }
        }
    }

    public void onResponseItem(int position, Serializable item) {
        mBaseListView.setItem(position, (T) item);
    }

    public static Intent getResultIntent(Serializable data) {
        return getResultIntent(data, false);
    }

    public static Intent getResultIntent(Serializable data, boolean isDeleted) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_DATA, data);
        intent.putExtra(RESULT_DELETED, isDeleted);
        return intent;
    }
}
