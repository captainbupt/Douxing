package com.badou.mworking.view;

public interface StoreItemView {
    void showProgressDialog(int resId);

    void setStore(boolean isStore);

    void showToast(int resId);

    void hideProgressDialog();

}
