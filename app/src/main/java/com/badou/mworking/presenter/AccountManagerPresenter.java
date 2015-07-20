package com.badou.mworking.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.AccountManageActivity;
import com.badou.mworking.LoginActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.MTrainingDBHelper;
import com.badou.mworking.domain.ChangePasswordUseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.view.BaseView;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class AccountManagerPresenter extends Presenter {
    AccountManageActivity accountManageActivity;
    UserInfo userInfo;

    public AccountManagerPresenter(Context context) {
        super(context);
        this.accountManageActivity = (AccountManageActivity) context;
        initialize(accountManageActivity);
    }

    private void initialize(AccountManageActivity accountManageActivity) {
        this.userInfo = UserInfo.getUserInfo();
        accountManageActivity.setAccount(userInfo.getAccount());
        accountManageActivity.setActionbarTitle(accountManageActivity.getResources().getString(R.string.title_name_Myzhanghao));
        if (UserInfo.ANONYMOUS_ACCOUNT.equals(userInfo.getAccount())) {
            accountManageActivity.anonymousMode();
        } else {
            accountManageActivity.normalMode();
        }
    }

    public void passwordModified(String originPassword, String newPassword, String confirmPassword) {
        if (TextUtils.isEmpty(originPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)
                || originPassword.length() < 6 || newPassword.length() < 6 || confirmPassword.length() < 6) {
            accountManageActivity.disableButton();
        } else {
            accountManageActivity.enableButton();
        }
    }

    public void changePassword(String originPassword, String newPassword, String confirmPassword) {
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
            new ChangePasswordUseCase(originPassword, newPassword).execute(new BaseSubscriber<ChangePasswordUseCase.Response>(mContext) {
                @Override
                public void onResponseSuccess(ChangePasswordUseCase.Response data) {
                    changePasswordSuccess(data);
                }

                @Override
                public void onErrorCode(int code) {
                    accountManageActivity.showToast(R.string.change_error_incorrect_password);
                }

                @Override
                public void onCompleted() {
                    accountManageActivity.hideProgressDialog();
                }
            });
        }
    }

    /**
     * 功能描述:  修改密码
     *
     * @param data
     */
    private void changePasswordSuccess(ChangePasswordUseCase.Response data) {
        userInfo.setUid(data.getUid());
        SPHelper.setUserInfo(userInfo);
        MTrainingDBHelper.getMTrainingDBHelper().createUserTable(userInfo.getUid());
        accountManageActivity.showToast(R.string.change_result_change_password_success);
        accountManageActivity.finish();
    }

    public void logout() {
        accountManageActivity.startActivity(LoginActivity.getIntent(mContext));
    }

    public void anonymousClicked() {
        accountManageActivity.showToast(R.string.tip_anonymous_logout);
    }

    @Override
    public void attachView(BaseView v) {

    }
}
