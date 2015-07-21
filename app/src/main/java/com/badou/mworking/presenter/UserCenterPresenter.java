package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;

import com.badou.mworking.AboutUsActivity;
import com.badou.mworking.AccountManageActivity;
import com.badou.mworking.BackWebActivity;
import com.badou.mworking.ChatListActivity;
import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.R;
import com.badou.mworking.StoreActivity;
import com.badou.mworking.UserProgressActivity;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.UserCenterView;
import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class UserCenterPresenter extends Presenter {
    UserDetail mUserDetail;
    Bitmap mHeadBmp;
    String mFinalImgPath;
    String mImgCacheUrl = "";
    UserCenterView mUserCenterView;

    public UserCenterPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mUserCenterView = (UserCenterView) v;
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
        Intent intent = new Intent(mContext, UserProgressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(UserProgressActivity.KEY_USERINFO, mUserDetail);
        intent.putExtras(bundle);
        intent.putExtra(UserProgressActivity.KEY_TYPE, Category.CATEGORY_TRAINING);
        mContext.startActivity(intent);
    }

    public void toMyExam() {
        Intent intent = new Intent(mContext, UserProgressActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(UserProgressActivity.KEY_USERINFO, mUserDetail);
        intent.putExtras(bundle);
        intent.putExtra(UserProgressActivity.KEY_TYPE, Category.CATEGORY_EXAM);
        mContext.startActivity(intent);
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
        intent.putExtra(ChatListActivity.KEY_HEAD_URL, mUserDetail.headimg);
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

    private void initData() {
        // 获取用户的uid
        // 根据uid拿到用户头像的路径
        mFinalImgPath = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + UserInfo.getUserInfo().getUid() + ".png";
        mUserDetail = getUserCache();
        mUserCenterView.showProgressDialog(R.string.user_detail_download_ing);
        updateData();
    }

    @Override
    public void resume() {
        super.resume();
        updateData();
    }

    private void updateData() {
        // 获取用户详情
        ServiceProvider.doOptainUserDetail(mContext, UserInfo.getUserInfo().getUid(), new VolleyListener(
                mContext) {

            @Override
            public void onCompleted() {
                mUserCenterView.hideProgressDialog();
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                JSONObject jObject = response.optJSONObject(Net.DATA);
                saveUserCache(jObject);
                mUserDetail = new UserDetail(jObject);
            }
        });
    }

    private void saveUserCache(JSONObject jsonObject) {
        SP.putStringSP(mContext, SP.DEFAULTCACHE, "userdetail", jsonObject.toString());
    }

    private UserDetail getUserCache() {
        try {
            JSONObject jsonObject = new JSONObject(SP.getStringSP(mContext, SP.DEFAULTCACHE, "userdetail", ""));
            return new UserDetail(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /**
     * 设置分数 学习进度
     */
/*    private void updateViewValue() {

        if (mUserDetail == null) {
            return;
        }
        // 用户信息
        String strScore = mContext.getResources().getString(R.string.text_score);
        String strPingJunFen = mContext.getResources().getString(R.string.user_center_exam_average);
        setUserIcon(mUid, mUserDetail.headimg);
        if (!TextUtils.isEmpty(mUserDetail.name)) {
            ((TextView) findViewById(R.id.tv_user_center_top_name))
                    .setText(mUserDetail.name + "\n" + mUserDetail.dpt);
        }
        if (!TextUtils.isEmpty(String.valueOf(mUserDetail.score))) {
            ((TextView) findViewById(R.id.tv_user_center_exam_score))
                    .setText(strPingJunFen + String.valueOf(mUserDetail.score)
                            + strScore);
        }

        // 学习进度
        if (!TextUtils.isEmpty(String.valueOf(mUserDetail.study_total))
                && !TextUtils.isEmpty(String.valueOf(mUserDetail
                .training_total))) {
            ((TextView) findViewById(R.id.tv_user_center_study_percent))
                    .setText(String.valueOf(mUserDetail.study_total)
                            + "/"
                            + String.valueOf(mUserDetail.training_total));
            int study = mUserDetail.study_total;
            int training = mUserDetail.training_total;
            int s = study * 100;
            if (training != 0) {
                int progress = s / training;
                ProgressBar pbTotalBar = (ProgressBar) findViewById(R.id.pb_user_center_study_progress);
                pbTotalBar.setProgress(progress);
            }
        }

        // 同事圈
        postsNumTextView.setText(mUserDetail.ask + getResources().getString(R.string.chatter_num));
        LVUtil.setTextViewBg(levelTextView, mUserDetail.circle_lv);
        int nmsg = mUserDetail.nmsg;
        if (nmsg > 0) {
            chatNumTextView.setVisibility(View.VISIBLE);
            chatNumTextView.setText(nmsg + "");
        } else {
            chatNumTextView.setVisibility(View.GONE);
        }
        int storeNumber = mUserDetail.store;
        storeNumTextView.setText(storeNumber + getResources().getString(R.string.chatter_num));

    }*/

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(final Bitmap bitmap) {
        if (bitmap != null) {
            mUserCenterView.showProgressDialog(R.string.user_detail_icon_upload_ing);
            ServiceProvider.doUpdateBitmap(mContext, bitmap,
                    Net.getRunHost() + Net.UPDATE_HEAD_ICON(UserInfo.getUserInfo().getUid()),
                    new VolleyListener(mContext) {

                        @Override
                        public void onCompleted() {
                            mUserCenterView.hideProgressDialog();
                        }

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            if (mHeadBmp != null && !mHeadBmp.isRecycled()) {
                                mHeadBmp.recycle();
                            }
                            bitmap.recycle();
                            mUserCenterView.showToast(R.string.user_detail_icon_upload_success);
                        }
                    });
        } else {
            ToastUtil.showToast(mContext,
                    R.string.user_detail_icon_upload_failed);
        }
    }
}
