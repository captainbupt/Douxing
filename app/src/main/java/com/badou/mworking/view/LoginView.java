package com.badou.mworking.view;

public interface LoginView extends BaseView {
    void setAccount(String account);
    void enableLoginButton();
    void disableLoginButton();
    void showNormalLayout();
    void showSmallLayout();
    void showErrorDialog();
}
