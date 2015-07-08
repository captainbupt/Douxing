package com.badou.mworking.presenter;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.badou.mworking.AskActivity;
import com.badou.mworking.BackWebActivity;
import com.badou.mworking.ChatterActivity;
import com.badou.mworking.ExamActivity;
import com.badou.mworking.MainGridActivity;
import com.badou.mworking.MessageCenterActivity;
import com.badou.mworking.NoticeActivity;
import com.badou.mworking.R;
import com.badou.mworking.TaskActivity;
import com.badou.mworking.TrainActivity;
import com.badou.mworking.UserCenterActivity;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.entity.main.MainBanner;
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.entity.main.NewVersion;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.fragment.MainSearchFragment;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.MainGridView;
import com.easemob.EMCallBack;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.MainActivity;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainPresenter extends Presenter {

    public static final String ACTION_RECEIVER_MESSAGE = "message";

    private Intent mReceivedIntent;
    private boolean isSearching = false;
    private long mExitTime = 0;
    private String mLogoUrl;
    MainGridView mMainView;

    public MainPresenter(Context context, Intent intent) {
        super(context);
        mReceivedIntent = intent;
    }

    @Override
    public void attachView(BaseView v) {
        mMainView = (MainGridView) v;
        initialize();
    }

    private void initialize() {
        if (UserInfo.ANONYMOUS_ACCOUNT.equals(UserInfo.getUserInfo().getAccount())) {
            mMainView.showExperienceDialog();
        }
        mLogoUrl = SPHelper.getLogoUrl();
        mMainView.setLogoImage(mLogoUrl);

        AlarmUtil alarmUtil = new AlarmUtil();
        alarmUtil.OpenTimer(mContext.getApplicationContext());

        JPushInterface.init(mContext.getApplicationContext());
        //push 推送默认开启，如果用户关闭掉推送的话，在这里停掉推送
        if (SPHelper.getClosePushOption()) {
            JPushInterface.stopPush(mContext.getApplicationContext());
        }

        if (mReceivedIntent.getBooleanExtra(MainGridActivity.KEY_MESSAGE_CENTER, false)) {
            mActivity.startActivity(new Intent(mContext, MessageCenterActivity.class));
        }
        initData();
        registerListener();
        mMainView.getSearchFragment().setOnHideListener(new MainSearchFragment.OnHideListener() {
            @Override
            public void onHide() {
                isSearching = false;
            }
        });
    }

    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVER_MESSAGE);
        mActivity.registerReceiver(mMessageReceiver, filter);
        //有选择性的接收某些类型event事件
        EMChatManager.getInstance().registerEventListener(mEMEventListener, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage});
    }

    private void unregisterListener() {
        mActivity.unregisterReceiver(mMessageReceiver);
        EMChatManager.getInstance().unregisterEventListener(mEMEventListener);
    }

    private void initData() {
        mMainView.setBannerData(SPHelper.getMainBanner());
        updateMessageCenter();
        //mScrollView.scrollTo(0, 0);
        UserInfo userInfo = UserInfo.getUserInfo();
        updateMessageCenter();
        loginEMChat(userInfo.getAccount(), userInfo.getHxpwd());
        checkUpdate();
    }

    private void updateMessageCenter() {
        mMainView.setMessageCenterStatus(MessageCenterResManager.getAllItem(mContext).size() > 0 || getUnreadMsgCountTotal() > 0);
    }

    /**
     * 登录
     */
    public void loginEMChat(final String currentUsername, final String currentPassword) {
        if (TextUtils.isEmpty(currentPassword)) {
            ToastUtil.showToast(mContext, R.string.Login_failed);
            return;
        }
        if (!DemoHXSDKHelper.getInstance().isLogined()) {
            // 调用sdk登陆方法登陆聊天服务器
            EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

                @Override
                public void onSuccess() {
                    mActivity.runOnUiThread(new Runnable() {
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
                                // 处理好友和群组
                            } catch (Exception e) {
                                e.printStackTrace();
                                // 取好友或者群聊失败，不让进入主页面
                                EMChatEntity.getInstance().logout(null);
                                Toast.makeText(mContext, R.string.login_failure_failed, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                            boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                                    EMChatEntity.getInstance().currentUserNick.trim());
                            if (!updatenick) {
                                Log.e("LoginActivity", "update current user nick fail");
                            }
                        }
                    });
                }

                @Override
                public void onProgress(int progress, String status) {
                }

                @Override
                public void onError(final int code, final String message) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(mContext, R.string.Login_failed);
                        }
                    });
                }
            });
        }
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent();
        MainIcon mainIcon = (MainIcon) arg0.getAdapter().getItem(arg2);
        switch (mainIcon.getKey()) {
            case RequestParameters.CHK_UPDATA_PIC_NOTICE: // 通知公告
                intent.setClass(mContext, NoticeActivity.class);
                break;
            case RequestParameters.CHK_UPDATA_PIC_TRAINING: // 微培训
                intent.setClass(mContext, TrainActivity.class);
                intent.putExtra(TrainActivity.KEY_IS_TRAINING, true);
                break;
            case RequestParameters.CHK_UPDATA_PIC_EXAM: // 在线考试
                intent.setClass(mContext, ExamActivity.class);
                break;
            case RequestParameters.CHK_UPDATA_PIC_SURVEY: // 培训调研
                String uid = UserInfo.getUserInfo().getUid();
                String url = Net.getWeiDiaoYanURl() + uid;
                intent.setClass(mContext, BackWebActivity.class);
                intent.putExtra(BackWebActivity.KEY_URL, url);
                break;
            case RequestParameters.CHK_UPDATA_PIC_TASK: // 任务签到
                intent.setClass(mContext, TaskActivity.class);
                break;
            case RequestParameters.CHK_UPDATA_PIC_CHATTER: // 同事圈
                intent.setClass(mContext, ChatterActivity.class);
                break;
            case RequestParameters.CHK_UPDATA_PIC_ASK: //问答
                intent.setClass(mContext, AskActivity.class);
                break;
            case RequestParameters.CHK_UPDATA_PIC_SHELF: //橱窗
                intent.setClass(mContext, TrainActivity.class);
                intent.putExtra(TrainActivity.KEY_IS_TRAINING, false);
                break;
        }
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, mainIcon.getName());
        mActivity.startActivity(intent);
    }

    public void onBannerClick(AdapterView<?> parent, View view, int position, long id) {
        MainBanner mainBanner = (MainBanner) parent.getAdapter().getItem(position);
        if (mainBanner != null) {
            Intent intent = new Intent(mContext, BackWebActivity.class);
            intent.putExtra(BackWebActivity.KEY_URL, mainBanner.getUrl());
            if (TextUtils.isEmpty(mLogoUrl)) {
                intent.putExtra(BackWebActivity.KEY_LOGO_URL, "invalid"); // 非法值
            } else {
                intent.putExtra(BackWebActivity.KEY_LOGO_URL, mLogoUrl);
            }
            mActivity.startActivity(intent);
        }
    }

    public void onBannerSelected(AdapterView<?> arg0, View arg1, int selIndex, long arg3) {
        mMainView.setIndicator(selIndex);
    }

    public void onUserCenterClick() {
        Intent intent = new Intent(mContext, UserCenterActivity.class);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
    }

    public void onSearchClick() {
        mMainView.showSearchFragment();
        isSearching = true;
    }

    public void onMessageCenterClick() {
        mActivity.startActivity(new Intent(mContext, MainActivity.class));
    }

    public void onBackPressed() {
        if (isSearching) {
            mMainView.getSearchFragment().backPressed();
        } else {
            // 应为系统当前的系统毫秒数一定小于2000
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showToast(mContext, R.string.main_exit_tips);
                mExitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().AppExit(mContext.getApplicationContext(), false);
            }
        }
    }

    /**
     * 功能描述:网络请求更新资源包，         这里是上传MD5来进行匹配，应为本地icon图片已经缓存，每次上传null，会把完整信息请求下来，
     * 如果url匹配，不会再下载图片内容
     */
    private void checkUpdate() {
        CheckUpdateUseCase useCase = new CheckUpdateUseCase(mContext);
        useCase.execute(new BaseSubscriber<MainData>(mContext, mMainView) {
            @Override
            public void onResponseSuccess(MainData data) {
                if (data.getNewVersion().hasNewVersion()) {
                    apkUpdate(data.getNewVersion());
                }
                String logoUrl = data.getButton_vlogo().getUrl();
                if (data.getButton_vlogo().hasNewVersion()) {
                    SPHelper.setLogoUrl(logoUrl);
                    mMainView.setLogoImage(logoUrl);
                    mLogoUrl = logoUrl;
                }
                mMainView.setBannerData(data.getBanner());
                // 保存banner信息数据到sp
                SPHelper.setMainBanner(data.getBanner());
            }
        });
    }

    /**
     * 在主页验证是否有软件更新
     *
     * @param newVersion
     */
    private void apkUpdate(final NewVersion newVersion) {
        if (newVersion.hasNewVersion()) {
            new AlertDialog.Builder(mContext)
                    .setTitle(R.string.main_tips_update_title)
                    .setMessage(newVersion.getDescription())
                    .setPositiveButton(R.string.main_tips_update_btn_ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mMainView.showProgressDialog(R.string.action_update_download_ing);
                                    ServiceProvider.doUpdateMTraning(mActivity, newVersion.getUrl(), new RangeFileAsyncHttpResponseHandler(new File("update.apk")) { // 仅仅是借用该接口

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, File file) {
                                            mMainView.hideProgressDialog();
                                            Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            mActivity.startActivity(intent);
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                                            mMainView.showToast(R.string.error_service);
                                            mMainView.hideProgressDialog();
                                        }
                                    });
                                }
                            }).setNegativeButton(R.string.text_cancel, null)
                    .create().show();
        }
    }

    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    private EMEventListener mEMEventListener = new EMEventListener() {
        @Override
        public void onEvent(EMNotifierEvent event) {
            updateMessageCenter();
        }
    };
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(ACTION_RECEIVER_MESSAGE)) {
                updateMessageCenter();
            }
        }
    };

    @Override
    public void resume() {
        super.resume();
        updateMessageCenter();
        mMainView.updateUnreadNumber();
    }

    @Override
    public void destroy() {
        super.destroy();
        unregisterListener();
    }
}
