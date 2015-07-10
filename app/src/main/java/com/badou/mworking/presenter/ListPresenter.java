package com.badou.mworking.presenter;

import android.content.Context;

import com.badou.mworking.R;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseListView;
import com.badou.mworking.view.BaseView;

import java.util.List;

public abstract class ListPresenter<T> extends Presenter {

    protected int mCurrentIndex = 1;
    protected int mClickPosition = -1;

    BaseListView<T> mBaseListView;

    public ListPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mBaseListView = (BaseListView<T>) v;
    }

    protected abstract String getCacheKey();

    protected abstract UseCase getLoadMoreUseCase();

    protected abstract UseCase getRefreshUseCase();

    public void refresh() {
        mBaseListView.hideNoneResult();
        mBaseListView.setRefreshing();
        getRefreshUseCase().execute(new BaseSubscriber<List<T>>(mContext) {
            @Override
            public void onResponseSuccess(List<T> data) {
                mCurrentIndex = 1;
                setList(data);
            }

            @Override
            public void onCompleted() {
                mBaseListView.refreshComplete();
            }
        });
    }

    public void loadMore() {
        mBaseListView.setRefreshing();
        getLoadMoreUseCase().execute(new BaseSubscriber<List<T>>(mContext) {
            @Override
            public void onResponseSuccess(List<T> data) {
                mCurrentIndex++;
                setList(data);
            }

            @Override
            public void onCompleted() {
                mBaseListView.refreshComplete();
            }
        });
    }

    public void setList(List<T> data) {
        if (mCurrentIndex == 1) {
            if (data == null || data.size() == 0) {
                mBaseListView.showNoneResult();
                mBaseListView.disablePullUp();
                mBaseListView.setData(null);
            } else {
                mBaseListView.setData(data);
                updateCompleted();
            }
            SPHelper.setList(getCacheKey(), data);
        } else {
            if (data == null || data.size() == 0) {
                mBaseListView.showToast(R.string.no_more);
            } else {
                mBaseListView.addData(data);
                mCurrentIndex++;
                updateCompleted();
            }
        }
    }

    protected void onItemClick(T data, int position) {
        // ¿¼ÊÔÃ»ÓÐÁªÍø
        if (!NetUtils.isNetConnected(mContext)) {
            ToastUtil.showToast(mContext, R.string.error_service);
            return;
        }
        mClickPosition = position;
        toDetailPage(data);
    }

    abstract protected void toDetailPage(T data);

    protected void updateCompleted() {
    }
}
