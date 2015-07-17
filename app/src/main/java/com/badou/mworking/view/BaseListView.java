package com.badou.mworking.view;

import java.util.List;

public interface BaseListView<T> extends BaseView {
    void showNoneResult();

    void hideNoneResult();

    void disablePullUp();

    void enablePullUp();

    void startRefreshing();

    boolean isRefreshing();

    void refreshComplete();

    void setData(List<T> data);

    void addData(List<T> data);

    int getDataCount();

    void setItem(int index, T item);

    T getItem(int index);

    void removeItem(int index);

    void showProgressBar();

    void hideProgressBar();
}
