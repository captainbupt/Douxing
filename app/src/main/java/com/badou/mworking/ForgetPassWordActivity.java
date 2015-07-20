package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.ChatServiceActivity;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends BaseBackActionBarActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, ForgetPasswordActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        setActionbarTitle(R.string.title_name_forget_password);
    }

    @OnClick(R.id.phone_layout)
    void toPhoneActivity() {
        startActivity(new Intent(this, ForgetPasswordPhoneActivity.class));
    }

    @OnClick(R.id.service_layout)
    void toServiceActivity() {
        mProgressDialog.show();
        final String imei = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        ServiceProvider.registerAccount(mContext, imei, new VolleyListener(mContext) {
            @Override
            public void onResponseSuccess(JSONObject response) {
                final String password = response.optJSONObject(Net.DATA).optString("hxpwd");
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        loginEMChat(imei, password);
                    }
                }.start();
            }

            @Override
            public void onErrorCode(int code) {
                super.onErrorCode(code);
                mProgressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    /**
     * 登录
     */
    public void loginEMChat(final String currentUsername, final String currentPassword) {
        if (TextUtils.isEmpty(currentPassword)) {
            ToastUtil.showToast(mContext, R.string.Login_failed);
            return;
        }
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            EMChatManager.getInstance().logout();
        }
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 登陆成功，保存用户名密码
                        EMChatEntity.getInstance().setUserName(currentUsername);
                        EMChatEntity.getInstance().setPassword(currentPassword);

                        try {
                            // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                            // ** manually load all local groups and
                            EMGroupManager.getInstance().loadAllGroups();
                            EMChatManager.getInstance().loadAllConversations();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // 取好友或者群聊失败，不让进入主页面
                            EMChatEntity.getInstance().logout(null);
                            Toast.makeText(mContext, R.string.login_failure_failed, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mProgressDialog.dismiss();
                        startActivity(new Intent(mContext, ChatServiceActivity.class));
                        //startActivity(ChatActivity.getServiceIntent(mContext));
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        ToastUtil.showToast(mContext, R.string.Login_failed);
                    }
                });
            }
        });
    }

    @OnClick(R.id.call_text_view)
    void call() {
        Intent phoneIntent = new Intent("android.intent.action.CALL",
                Uri.parse("tel:" + "4008233773"));
        startActivity(phoneIntent);
    }
}