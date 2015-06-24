package com.badou.mworking.presenter;

import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.badou.mworking.AccountManageActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/6/23 0023.
 */
public class AccountManagerPresenter extends Presenter {

    AccountManageActivity accountManageActivity;
    UserInfo userInfo;

    public void setAccountManageActivity(AccountManageActivity accountManageActivity) {
        this.accountManageActivity = accountManageActivity;
    }

    public void initialize() {
        this.userInfo = ((AppApplication) accountManageActivity.getApplication()).getUserInfo();
        accountManageActivity.setAccount(userInfo.account);
        if ("anonymous".equals(userInfo.account)) {
            accountManageActivity.anonymousMode();
        } else {
            accountManageActivity.normalMode();
        }
    }

    public void passwordChanged(String originPassword, String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(originPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)
                || originPassword.length() < 6 || newPassword.length() < 6 || confirmPassword.length() < 6) {
            accountManageActivity.disableButton();
        } else {
            accountManageActivity.enableButton();
        }
    }

    void changePassword(String originPassword, String newPassword, String confirmPassword) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
        boolean a = pattern.matcher(newPassword).matches();

        if (TextUtils.isEmpty(originPassword)) {
            accountManageActivity.showToast(R.string.change_error_empty_password_original);
        } else if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            accountManageActivity.showToast(R.string.change_error_empty_password_new);
        } else if (originPassword.length() < 6) {
            accountManageActivity.showToast(R.string.change_error_short_password_original);
        } else if (newPassword.length() < 6) {
            accountManageActivity.showToast(R.string.change_error_short_password_new);
        } else if (!a) {
            accountManageActivity.showToast(R.string.tips_username_input_New_MiMa);
        } else if (originPassword.equals(newPassword)) {
            accountManageActivity.showToast(R.string.change_error_same_new_original);
        } else if (TextUtils.isEmpty(confirmPassword) || !newPassword.equals(confirmPassword)) {
            accountManageActivity.showToast(R.string.change_error_different_password);
        } else {
            accountManageActivity.showProgressDialog(R.string.change_action_change_passwrod);
            ServiceProvider.doChangePassword(accountManageActivity, originPassword,
                    newPassword, new VolleyListener(accountManageActivity) {

                        @Override
                        public void onErrorCode(int code) {
                            accountManageActivity.showToast(R.string.change_error_incorrect_password);
                        }

                        @Override
                        public void onCompleted() {
                            accountManageActivity.hideProgressDialog();
                        }

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            changePasswordSuccess(response.optJSONObject(Net.DATA));
                        }
                    });
        }
    }

    /**
     * ��������:  �޸�����
     */
    private void changePasswordSuccess(JSONObject data) {
        UserInfo userInfo = ((AppApplication) accountManageActivity.getApplication()).getUserInfo();
        userInfo.userId = data.optString(RequestParameters.USER_ID);
        userInfo.saveUserInfo(accountManageActivity.getApplicationContext());
        MTrainingDBHelper.getMTrainingDBHelper().createUserTable(userInfo.userId);
        accountManageActivity.showToast(R.string.change_result_change_password_success);
        accountManageActivity.finish();
    }
}
