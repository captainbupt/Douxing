package com.badou.mworking;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.ResponseError;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.MD5;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.InputMethodRelativeLayout;
import com.badou.mworking.widget.InputMethodRelativeLayout.OnSizeChangedListenner;
import com.badou.mworking.widget.LoginErrorDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * 功能描述: 登录页面
 */
public class LoginActivity extends BaseNoTitleActivity implements
        OnSizeChangedListenner, OnClickListener, BDLocationListener {

    private InputMethodRelativeLayout layout;
    private ViewGroup boot;
    private ViewGroup login_logo_layout_h;
    private ViewGroup login_logo_layout_v;
    private EditText mUsernameEditText; // 账号输入框
    private EditText mPasswordEdiText; // 密码输入框
    private Button mLoginButton; // 登录按钮
    private TextView mForgetTextView; // 忘记密码textview
    private TextView mExperienceTextView;// 快速体验
    public static final String KEY_ACCOUNT = "user_account";
    public static final String SHUFFLE = "shuffle";


    private String userName = "";  // 用户名
    private String passWord = "";  //　密码

    /**
     * 百度定位 *
     */
    public LocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
        initData();
        initLocation();
    }

    /**
     * 功能描述: 初始化定位数据
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(this);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    /**
     * 功能描述: 添加返回按钮，弹出是否退出应用程序对话框
     */
    @Override
    public void finish() {
        super.finish();
        AppManager.getAppManager().AppExit(this, false);
    }

    protected void initView() {
        // 实例化控件
        mUsernameEditText = (EditText) findViewById(R.id.et_login_username);
        mPasswordEdiText = (EditText) findViewById(R.id.et_login_password);
        mLoginButton = (Button) findViewById(R.id.btn_login_sign_in);
        mForgetTextView = (TextView) findViewById(R.id.tv_login_forget_password);
        mExperienceTextView = (TextView) this.findViewById(R.id.btn_login_experience);
        // 取得InputMethodRelativeLayout组件
        layout = (InputMethodRelativeLayout) this.findViewById(R.id.loginpage);
        // 设置监听事件
        layout.setOnSizeChangedListenner(this);
        // 取得大LOGO布局
        login_logo_layout_v = (ViewGroup) this
                .findViewById(R.id.login_logo_layout_v);
        // 取得小LOGO布局
        login_logo_layout_h = (ViewGroup) this
                .findViewById(R.id.login_logo_layout_h);

        // 取得找回密码和新注册布局
        boot = (ViewGroup) this
                .findViewById(R.id.reg_and_forget_password_layout);
        /**
         * 隐藏键盘
         */
        InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mUsernameEditText.getWindowToken(), 0);


    }

    protected void initListener() {
        /**
         * Username 添加文本改变监听
         */
        mUsernameEditText.addTextChangedListener(new TextChangeListener(mUsernameEditText,
                mPasswordEdiText));
        mPasswordEdiText.addTextChangedListener(new TextChangeListener(mUsernameEditText,
                mPasswordEdiText));

        // 忘记密码
        mForgetTextView.setOnClickListener(this);
        mExperienceTextView.setOnClickListener(this);
        // 登录button
        mLoginButton.setOnClickListener(this);

    }

    protected void initData() {
        mUsernameEditText.setText(SP.getStringSP(mContext, SP.DEFAULTCACHE, KEY_ACCOUNT, ""));
    }

    /**
     * 功能描述:用户密码格式正确时,发起网络请求传递信息
     */
    private void verify(final String username, final String password,
                        JSONObject localtion) {
        // 发起网络请求
        ServiceProvider.doLogin(mContext, username, password, localtion,
                new VolleyListener(mContext) {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onResponse(Object responseObject) {
                        // 收到响应时调用
                        JSONObject response = (JSONObject) responseObject;
                        if (null != mProgressDialog && mContext != null
                                && !mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                        try {
                            // 验证返回码是否正确
                            int code = response.optInt(Net.CODE);
                            if (code == Net.LOGOUT) {
                                AppApplication.logoutShow(mContext);
                                return;
                            }
                            if (code != Net.SUCCESS) {
                                showErrorDialog(R.string.login_error_incorrect_username_password);
                                return;
                            }
                            // 返回码正确时 调用
                            loginSuccess(username,
                                    response.optJSONObject(Net.DATA));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 响应错误
                        if (!mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                        if (error instanceof ResponseError) {
                            showErrorDialog(error.getMessage());
                            return;
                        }
                        showErrorDialog(R.string.error_service);
                    }
                });
    }

    /**
     * 功能描述:登录失败 清空edittext并提示信息
     *
     * @param tips 提示信息的String
     */
    private void showErrorDialog(String tips) {
        LoginErrorDialog dialog = new LoginErrorDialog(mContext, tips);
        dialog.show();
        mPasswordEdiText.setText("");
    }

    /**
     * 功能描述:提示错误信息
     *
     * @param resId 提示信息的resId
     */
    private void showErrorDialog(int resId) {
        String tips = getResources().getString(resId);
        showErrorDialog(tips);
    }

    /**
     * 登录成功 保存信息
     *
     * @param jsonObject 登录成功返回的json
     */
    private void loginSuccess(String username, JSONObject jsonObject) {
        String shuffleStr = jsonObject.optJSONObject("shuffle").toString();
        SP.putStringSP(LoginActivity.this, SP.DEFAULTCACHE, LoginActivity.SHUFFLE, shuffleStr);
        UserInfo userInfo = new UserInfo();
        /*** 保存没MD5的用户账户 **/
        SP.putStringSP(mContext, SP.DEFAULTCACHE, KEY_ACCOUNT, username + "");
        userInfo.setUserInfo(new MD5().getMD5ofStr(username), jsonObject);
        String lang = jsonObject.optString("lang");
        String company = jsonObject.optString("company");
        SP.putStringSP(this, SP.DEFAULTCACHE, Constant.COMPANY, company); //保存公司的名称
        SP.putStringSP(mContext, SP.DEFAULTCACHE, "host", jsonObject.optString("host"));
        // en为英文版，取值zh为中文版。
        AppApplication.changeAppLanguage(getResources(), lang);
        try {
            SP.putStringSP(mContext, SP.DEFAULTCACHE, "host", jsonObject.optString("host"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        // 保存用户登录成功返回的信息 到sharePreferncers
        ((AppApplication) getApplicationContext()).setUserInfo(userInfo);
        goMainGrid();
    }

    /**
     * 功能描述:跳转到主页
     */
    private void goMainGrid() {
        Intent intent = new Intent(mContext, MainGridActivity.class);
        startActivity(intent);
        super.finish();
    }

    /**
     * 功能描述: 用户名 密码输入框 文本改变监听
     */
    class TextChangeListener implements TextWatcher {
        private EditText editUser, editPass;

        public TextChangeListener(EditText editUser, EditText editPass) {
            this.editUser = editUser;
            this.editPass = editPass;
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {

        }

        // 变化之前的内容
        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            Resources res = mContext.getResources();
            if (editUser.getText().length() == 0
                    || editPass.getText().length() == 0) {
                mLoginButton.setEnabled(false);
                mLoginButton.setTextColor(res
                        .getColor(R.color.color_black));
                mLoginButton.setBackgroundResource(R.drawable.background_button_disable);
            } else {
                mLoginButton.setEnabled(true);
                mLoginButton.setTextColor(getResources().getColorStateList(R.color.color_button_text_blue));
                mLoginButton.setBackgroundResource(R.drawable.background_button_enable_blue);
            }
        }
    }

    /**
     * 在Activity中实现OnSizeChangedListener，原理是设置该布局的paddingTop属性来控制子View的偏移
     */
    @Override
    public void onSizeChange(boolean flag, int w, int h) {
        int padding = getResources().getDimensionPixelOffset(R.dimen.login_margin);
        if (flag) {// 键盘弹出时
            layout.setPadding(padding, padding, padding, padding);
            boot.setVisibility(View.GONE);
            login_logo_layout_v.setVisibility(View.GONE);
            login_logo_layout_h.setVisibility(View.VISIBLE);
        } else { // 键盘隐藏时
            layout.setPadding(padding, padding, padding, padding);
            boot.setVisibility(View.VISIBLE);
            login_logo_layout_v.setVisibility(View.VISIBLE);
            login_logo_layout_h.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btn_login_sign_in:
                userName = mUsernameEditText.getText().toString();
                passWord = mPasswordEdiText.getText().toString();
                Pattern pattern = Pattern.compile("^[A-Za-z0-9@\\_\\-\\.]+$");
                boolean a = pattern.matcher(userName).matches(); // true 为格式正确
                boolean b = pattern.matcher(passWord).matches(); // true 为格式正确
                if (!a) {
                    ToastUtil.showToast(mContext, R.string.tips_username_input_invalid);
                } else if (!b) {
                    ToastUtil.showToast(mContext, R.string.tips_password_input_invalid);
                } else {
                    mLocationClient.start();
                    //发起请求时调用 显示ProgressDialog
                    if (mActivity.isFinishing()) {
                        mProgressDialog.setContent(R.string.login_action_login_ing);
                        mProgressDialog.show();
                    }
                }
                break;
            case R.id.tv_login_forget_password:
                if (!NetUtils.isNetConnected(mContext)) {
                    ToastUtil.showNetExc(mContext);
                    return;
                }
                startActivity(new Intent(mContext, ForgetPassWordActivity.class));
                break;
            case R.id.btn_login_experience:
                if (!NetUtils.isNetConnected(mContext)) {
                    ToastUtil.showNetExc(mContext);
                    return;
                }
                userName = "anonymous";
                passWord = "anonymous";
                //快速体验使用默认的账号和密码直接登录进入，不在采集用户名和公司等信息
                mLocationClient.start();
                //发起请求时调用 显示ProgressDialog
                if (!mActivity.isFinishing()) {
                    mProgressDialog.show();
                }
                break;
        }
    }

    /**
     * 获取到地理位置后，开始登录
     */
    @Override
    public void onReceiveLocation(BDLocation location) {
        mLocationClient.stop();
        JSONObject locationJsonObject = new JSONObject();
        if (location == null
                || String.valueOf(location.getLatitude()).equals(4.9E-324)
                || String.valueOf(location.getLongitude()).equals(4.9E-324)) {
            try {
                locationJsonObject.put(RequestParams.LOCATION_LATITUDE, 0d);
                locationJsonObject.put(RequestParams.LOCATION_LONGITUDE, 0d);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                locationJsonObject.put(RequestParams.LOCATION_LATITUDE,
                        location.getLatitude());
                locationJsonObject.put(RequestParams.LOCATION_LONGITUDE,
                        location.getLongitude());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 登录
        verify(userName, passWord, locationJsonObject);
    }

    @Override
    public void onReceivePoi(BDLocation arg0) {

    }

}