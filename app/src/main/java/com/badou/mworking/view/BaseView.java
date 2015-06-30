package com.badou.mworking.view;

public interface BaseView {
    void showProgressDialog(String msg);
    void showProgressDialog(int resId);
    void hideProgressDialog();
    void showToast(int resId);
    void showToast(String msg);
}
