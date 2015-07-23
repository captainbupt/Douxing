package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.badou.mworking.AboutUsActivity;
import com.badou.mworking.AccountManageActivity;
import com.badou.mworking.BackWebActivity;
import com.badou.mworking.ChatListActivity;
import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.MyExamActivity;
import com.badou.mworking.MyStudyActivity;
import com.badou.mworking.R;
import com.badou.mworking.StoreActivity;
import com.badou.mworking.domain.SetHeadUseCase;
import com.badou.mworking.domain.UserDetailUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.UserCenterView;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.ChatActivity;

import java.io.File;

public class UserCenterPresenter extends Presenter {
    UserDetail mUserDetail;
    String mFinalImgPath;
    String mImgCacheUrl = "";
    UserCenterView mUserCenterView;
    UserDetailUseCase mUserDetailUseCase;

    public UserCenterPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mUserCenterView = (UserCenterView) v;
        initData();
    }

    private void initData() {
        // 根据uid拿到用户头像的路径
        mFinalImgPath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + UserInfo.getUserInfo().getUid() + ".png";
        UserDetail userDetail = SPHelper.getUserDetail();
        if (userDetail != null) {
            mImgCacheUrl = userDetail.getHeadimg();
            mUserCenterView.setData(userDetail);
        }
        mUserCenterView.showProgressDialog(R.string.user_detail_download_ing);
        updateData();
    }

    // 获取用户详情
    private void updateData() {
        if (mUserDetailUseCase == null)
            mUserDetailUseCase = new UserDetailUseCase();
        mUserDetailUseCase.execute(new BaseSubscriber<UserDetail>(mContext) {
            @Override
            public void onResponseSuccess(UserDetail data) {
                mUserDetail = data;
                mImgCacheUrl = data.getHeadimg();
                SPHelper.setUserDetail(data);
                mUserCenterView.setData(data);
            }

            @Override
            public void onCompleted() {
                mUserCenterView.hideProgressDialog();
            }
        });
    }

    /**
     * 保存裁剪之后的图片数据
     */
    public void onImageSelected(final Bitmap bitmap) {
        if (bitmap != null) {
            mUserCenterView.showProgressDialog(R.string.user_detail_icon_upload_ing);
            new SetHeadUseCase(FileUtils.writeBitmap2TmpFile(mContext, bitmap)).execute(new BaseSubscriber(mContext) {
                @Override
                public void onResponseSuccess(Object data) {
                    int size = mContext.getResources().getDimensionPixelSize(R.dimen.user_center_image_head_size);
                    Bitmap headBmp = BitmapUtil.getCirlBitmp(bitmap, size, size);
                    if (TextUtils.isEmpty(mImgCacheUrl)) {
                        mUserCenterView.setHeadImage(headBmp);
                    } else {
                        BitmapLruCache.getBitmapLruCache().putBitmap(mImgCacheUrl, bitmap);
                        BitmapLruCache.getBitmapLruCache().putCircleBitmap(mImgCacheUrl, headBmp);
                        mUserCenterView.setHeadImage(mImgCacheUrl);
                    }
                    mUserCenterView.showToast(R.string.user_detail_icon_upload_success);
                }

                @Override
                public void onErrorCode(int code) {
                    super.onErrorCode(code);
                }

                @Override
                public void onCompleted() {
                    mUserCenterView.hideProgressDialog();
                }
            });
        } else {
            ToastUtil.showToast(mContext,
                    R.string.user_detail_icon_upload_failed);
        }
    }

    public void changeUserHead() {
        // 判断是否联网，如果没有联网的话，不进行拍照和相册选取图片的操作
        if (!NetUtils.isNetConnected(mContext)) {
            mUserCenterView.showToast(R.string.error_service);
            return;
        }
        mUserCenterView.takeImage();
    }

    public void checkLevel() {
        String userId = UserInfo.getUserInfo().getUid();
        Intent intent = new Intent(mContext, BackWebActivity.class);
        intent.putExtra("title", "等级介绍");
        intent.putExtra(BackWebActivity.KEY_URL, Constant.LV_URL + userId);
        mContext.startActivity(intent);
    }

    public void toMyStudy() {
        mContext.startActivity(MyStudyActivity.getIntent(mContext, mUserDetail));
    }

    public void toMyExam() {
        mContext.startActivity(MyExamActivity.getIntent(mContext, mUserDetail));
    }

    public void toMyChatter() {
        // 缺省UID的情况下，默认进入我的圈
        Intent intent = new Intent(mContext, ChatterUserActivity.class);
        intent.putExtra(ChatterUserActivity.KEY_USER_CHATTER, new UserChatterInfo(mUserDetail));
        intent.putExtra(ChatterUserActivity.KEY_UID, UserInfo.getUserInfo().getUid());
        mContext.startActivity(intent);

    }

    public void toMyChat() {
        Intent intent = new Intent(mContext, ChatListActivity.class);
        intent.putExtra(ChatListActivity.KEY_HEAD_URL, mUserDetail.getHeadimg());
        mContext.startActivity(intent);
    }

    public void toMyAccount() {
        mContext.startActivity(new Intent(mContext, AccountManageActivity.class));
    }

    public void toAboutUs() {
        mContext.startActivity(new Intent(mContext, AboutUsActivity.class));
    }

    public void toMyStore() {
        mContext.startActivity(new Intent(mContext, StoreActivity.class));
    }

    public void toService() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            mContext.startActivity(ChatActivity.getServiceIntent(mContext));
        } else {
            mUserCenterView.showProgressDialog();
            ((DemoHXSDKHelper) DemoHXSDKHelper.getInstance()).loginAnonymous((Activity) mContext, new EMCallBack() {
                @Override
                public void onSuccess() {
                    mUserCenterView.hideProgressDialog();
                    mContext.startActivity(ChatActivity.getServiceIntent(mContext));
                }

                @Override
                public void onError(int i, String s) {
                    mUserCenterView.hideProgressDialog();
                    mUserCenterView.showToast(R.string.Login_failed);
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }

    @Override
    public void resume() {
        updateData();
    }
}
