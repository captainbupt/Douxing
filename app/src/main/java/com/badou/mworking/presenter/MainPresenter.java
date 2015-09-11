package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.badou.mworking.AskActivity;
import com.badou.mworking.BackWebActivity;
import com.badou.mworking.ChatterActivity;
import com.badou.mworking.ExperienceInformationActivity;
import com.badou.mworking.LoginActivity;
import com.badou.mworking.R;
import com.badou.mworking.UserCenterActivity;
import com.badou.mworking.CategoryListActivity;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.entity.main.MainBanner;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.entity.main.NewVersion;
import com.badou.mworking.entity.main.Shuffle;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.AppManager;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.DialogUtil;
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

import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainPresenter extends Presenter {

    public static final String ACTION_RECEIVER_MESSAGE = "message";
    private boolean isSearching = false;
    private long mExitTime = 0;
    private String mLogoUrl;
    MainGridView mMainView;
    List<MainIcon> mMainIconList;
    UserInfo mUserInfo;

    public MainPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mMainView = (MainGridView) v;
        initialize();
    }

    private void initialize() {
        if (!AppApplication.isInitialized) {
            AppApplication.initial((AppApplication) mContext.getApplicationContext());
        }
        DensityUtil.init((Activity) mContext);
        mUserInfo = UserInfo.getUserInfo();
        if (mUserInfo == null) {
            mContext.startActivity(LoginActivity.getIntent(mContext));
            return;
        }
        if (SPHelper.getIsMainFirst()) {
            mMainView.showGuideFragment();
            SPHelper.setIsMainFirst(false);
        }
        mLogoUrl = SPHelper.getLogoUrl();
        mMainView.setLogoImage(mLogoUrl);

        updateMainIcon();
        AlarmUtil alarmUtil = new AlarmUtil();
        alarmUtil.OpenTimer(mContext.getApplicationContext());

        JPushInterface.init(mContext.getApplicationContext());
        //push 推送默认开启，如果用户关闭掉推送的话，在这里停掉推送
        if (SPHelper.getClosePushOption()) {
            JPushInterface.stopPush(mContext.getApplicationContext());
        }

        initData();
        registerListener();
    }

    /**
     * 更新显示主界面icon
     */
    private void updateMainIcon() {
        if (mMainIconList == null) {
            mMainIconList = mUserInfo.getShuffle().getMainIconList(mContext, mUserInfo.getAccess());
        }
        for (MainIcon mainIcon : mMainIconList) {
            switch (mainIcon.getKey()) {
                case Shuffle.BUTTON_NOTICE: // 通知公告
                    mainIcon.setUnreadNumber(SPHelper.getUnreadNumber(Category.CATEGORY_NOTICE));
                    break;
                case Shuffle.BUTTON_TRAINING: // 微培训
                    mainIcon.setUnreadNumber(SPHelper.getUnreadNumber(Category.CATEGORY_TRAINING));
                    break;
                case Shuffle.BUTTON_EXAM: // 在线考试
                    mainIcon.setUnreadNumber(SPHelper.getUnreadNumber(Category.CATEGORY_EXAM));
                    break;
                case Shuffle.BUTTON_TASK: // 任务签到
                    mainIcon.setUnreadNumber(SPHelper.getUnreadNumber(Category.CATEGORY_TASK));
                    break;
                case Shuffle.BUTTON_SHELF: //橱窗
                    mainIcon.setUnreadNumber(SPHelper.getUnreadNumber(Category.CATEGORY_SHELF));
                    break;
                case Shuffle.BUTTON_ENTRY: //报名
                    mainIcon.setUnreadNumber(SPHelper.getUnreadNumber(Category.CATEGORY_ENTRY));
                    break;
            }
        }
        mMainView.setMainIconData(mMainIconList);
    }

    private void registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVER_MESSAGE);
        mContext.registerReceiver(mMessageReceiver, filter);
        //有选择性的接收某些类型event事件
        EMChatManager.getInstance().registerEventListener(mEMEventListener, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage});
    }

    private void unregisterListener() {
        mContext.unregisterReceiver(mMessageReceiver);
        EMChatManager.getInstance().unregisterEventListener(mEMEventListener);
    }

    private void initData() {
        mMainView.setBannerData(SPHelper.getMainBanner());
        updateMessageCenter();
        if (SPHelper.isFirstLoginToday()) {
            checkUpdate();
        }
        SPHelper.setIsFirstLoginToday();
        if (!mUserInfo.isAnonymous()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loginEMChat(mUserInfo.getAccount(), mUserInfo.getHxpwd());
                }
            }).start();
        } else {
            mContext.startActivity(new Intent(mContext, ExperienceInformationActivity.class));
        }
    }

    private void updateMessageCenter() {
        mMainView.setMessageCenterStatus(MessageCenterResManager.getAllItem().size() > 0 || getUnreadMsgCountTotal() > 0);
    }

    /**
     * 登录
     */
    public void loginEMChat(final String currentUsername, final String currentPassword) {
        if (TextUtils.isEmpty(currentPassword)) {
            ToastUtil.showToast(mContext, R.string.Login_failed);
            return;
        }
        if (DemoHXSDKHelper.getInstance().isLogined())
            EMChatManager.getInstance().logout();
        // 调用sdk登陆方法登陆聊天服务器
        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                ((Activity) mContext).runOnUiThread(new Runnable() {
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
                            EMChatManager.getInstance().logout();
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
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(mContext, R.string.Login_failed);
                    }
                });
            }
        });
    }


    public void onItemClick(MainIcon mainIcon) {
        Intent intent = new Intent();
        switch (mainIcon.getKey()) {
            case Shuffle.BUTTON_NOTICE: // 通知公告
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_NOTICE, false);
                break;
            case Shuffle.BUTTON_TRAINING: // 微培训
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_TRAINING, false);
                break;
            case Shuffle.BUTTON_EXAM: // 在线考试
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_EXAM, false);
                break;
            case Shuffle.BUTTON_SURVEY: // 培训调研
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_SURVEY, false);
                break;
            case Shuffle.BUTTON_TASK: // 任务签到
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_TASK, false);
                break;
            case Shuffle.BUTTON_CHATTER: // 同事圈
                intent.setClass(mContext, ChatterActivity.class);
                break;
            case Shuffle.BUTTON_ASK: //问答
                intent.setClass(mContext, AskActivity.class);
                break;
            case Shuffle.BUTTON_SHELF: //橱窗
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_SHELF, false);
                break;
            case Shuffle.BUTTON_ENTRY: //报名
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_ENTRY, false);
                break;
            case Shuffle.BUTTON_PLAN: //学习地图
                intent = CategoryListActivity.getIntent(mContext, Category.CATEGORY_PLAN, false);
                break;
        }
        mContext.startActivity(intent);
    }

    public void onBannerClick(AdapterView<?> parent, View view, int position, long id) {
        MainBanner mainBanner = (MainBanner) parent.getAdapter().getItem(position);
        if (mainBanner != null) {
            String url = mainBanner.getUrl();
            if (url.endsWith(".mp4") || url.endsWith(".mp3")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(url);
                if (url.endsWith(".mp4"))
                    intent.setDataAndType(uri, "video/*");
                else
                    intent.setDataAndType(uri, "audio/*");
                mContext.startActivity(intent);
            } else {
                Intent intent = BackWebActivity.getIntentBanner(mContext, mainBanner.getUrl(), TextUtils.isEmpty(mLogoUrl) ? "invalid" : mLogoUrl);
                mContext.startActivity(intent);
            }
        }
    }

    public void onBannerSelected(AdapterView<?> arg0, View arg1, int selIndex, long arg3) {
        mMainView.setIndicator(selIndex);
    }

    public void onUserCenterClick() {
        Intent intent = new Intent(mContext, UserCenterActivity.class);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
    }

    public void onSearchClick() {
        mMainView.showSearchFragment();
        isSearching = true;
    }

    public void onMessageCenterClick() {
        mContext.startActivity(new Intent(mContext, MainActivity.class));
    }

    public boolean onBackPressed() {
        if (isSearching) {
            mMainView.hideSearchFragment();
            isSearching = false;
            return true;
        } else {
            // 应为系统当前的系统毫秒数一定小于2000
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                ToastUtil.showToast(mContext, R.string.main_exit_tips);
                mExitTime = System.currentTimeMillis();
            } else {
                AppManager.getAppManager().AppExit(mContext.getApplicationContext(), false);
            }
        }
        return false;
    }

    /**
     * 功能描述:网络请求更新资源包，         这里是上传MD5来进行匹配，应为本地icon图片已经缓存，每次上传null，会把完整信息请求下来，
     * 如果url匹配，不会再下载图片内容
     */
    private void checkUpdate() {
        CheckUpdateUseCase useCase = new CheckUpdateUseCase();
        useCase.execute(new BaseSubscriber<MainData>(mContext) {
            @Override
            public void onResponseSuccess(MainData data) {
                // 有遮罩则不提示更新
                if (((AppCompatActivity) mContext).getSupportFragmentManager().getFragments() == null) {
                    if (data.getNewVersion() != null && data.getNewVersion().hasNewVersion()) {
                        DialogUtil.apkUpdate(mContext, mMainView, data.getNewVersion());
                    }
                    if (data.getDayAct() > 0) {
                        mMainView.showCreditReward(data.getDayAct());
                    }
                }
                SPHelper.setCheckUpdate(data);
                mLogoUrl = SPHelper.getLogoUrl();
                mMainView.setLogoImage(mLogoUrl);
                mMainView.setBannerData(data.getBanner());
                // 保存banner信息数据到sp
                SPHelper.setMainBanner(data.getBanner());
            }
        });
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
        updateMainIcon();
    }

    @Override
    public void destroy() {
        super.destroy();
        unregisterListener();
    }
}
