package com.badou.mworking.view;

import java.util.List;

public interface BaseListView<T> extends BaseView {
    void showNoneResult();

    void hideNoneResult();

    void disablePullUp();

    void enablePullUp();

    void setRefreshing();

    boolean isRefreshing();

    void refreshComplete();

    void setData(List<T> data);

    void addData(List<T> data);

    void showProgressBar();

    void hideProgressBar();
}
