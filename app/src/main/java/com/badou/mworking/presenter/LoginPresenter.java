package com.badou.mworking.presenter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.badou.mworking.ForgetPassWordActivity;
import com.badou.mworking.MainGridActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.EncryptionByMD5;
import com.badou.mworking.util.GsonUtil;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPUtil;
import com.badou.mworking.view.LoginView;
import com.badou.mworking.view.BaseView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONObject;

import java.util.regex.Pattern;

public class LoginPresenter extends Presenter implements BDLocationListener {

    LoginView mLoginView;

    private String userName = "";  // 用户名
    private String passWord = "";  //　密码
    public LocationClient mLocationClient;  // 百度定位

    public LoginPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mLoginView = (LoginView) v;
        mLoginView.showNormalLayout();
        mLoginView.setAccount(SPUtil.getUserAccount());
        initLocation();
    }

    /**
     * 功能描述: 初始化定位数据
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(this);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    public void onTextChanged(String username, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            mLoginView.disableLoginButton();
        } else {
            mLoginView.enableLoginButton();
        }
    }

    /**
     * 功能描述:用户密码格式正确时,发起网络请求传递信息
     */
    public void verify(final String account, final String password, double latitude, double longitude) {
        LoginUseCase loginUseCase = new LoginUseCase(account, EncryptionByMD5.getMD5(password.getBytes()), latitude + "", longitude + "");
        loginUseCase.execute(new BaseSubscriber(mContext, mLoginView) {
            @Override
            public void onCompleted() {
                mLoginView.hideProgressDialog();
            }

            @Override
            public void onErrorCode(int code) {
                mLoginView.showErrorDialog();
            }

            @Override
            public void onResponseSuccess(Object data) {
                loginSuccess(account, (UserInfo) data);
            }
        });
    }

    // 登录成功 保存信息
    private void loginSuccess(String account, UserInfo userInfo) {
        mLoginView.hideProgressDialog();
        Intent intent = MainGridActivity.getIntent(mContext);
        /*** 保存没MD5的用户账户 **/
        UserInfo.setUserInfo((AppApplication) mContext.getApplicationContext(), account, userInfo);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    public void onKeyboardStateChanged(boolean isShow) {
        if (isShow) {
            mLoginView.showSmallLayout();
        } else {
            mLoginView.showNormalLayout();
        }
    }

    public void login(String username, String password) {
        this.userName = username;
        this.passWord = password;
        Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
        boolean a = pattern.matcher(userName).matches(); // true 为格式正确
        boolean b = pattern.matcher(passWord).matches(); // true 为格式正确
        if (!a) {
            mLoginView.showToast(R.string.tips_username_input_invalid);
        } else if (!b) {
            mLoginView.showToast(R.string.tips_password_input_invalid);
        } else {
            mLocationClient.start();
            //发起请求时调用 显示ProgressDialog
            mLoginView.showProgressDialog(R.string.progress_tips_login_ing);
        }
    }

    public void forgetPassword() {
        if (!NetUtils.isNetConnected(mContext)) {
            mLoginView.showToast(R.string.error_service);
            return;
        }
        mActivity.startActivity(ForgetPassWordActivity.getIntent(mContext));
    }

    public void experience() {
        login(UserInfo.ANONYMOUS_ACCOUNT, UserInfo.ANONYMOUS_PASSWORD);
    }

    /**
     * 获取到地理位置后，开始登录
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
        mLocationClient.stop();
        if (location == null || String.valueOf(location.getLatitude()).equals(4.9E-324)
                || String.valueOf(location.getLongitude()).equals(4.9E-324)) {
            verify(userName, passWord, 0d, 0d);
        } else {

            verify(userName, passWord, location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onReceivePoi(BDLocation arg0) {

    }

}
