package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 功能描述: 个人中心页面
 */
public class UserCenterActivity extends BaseNoTitleActivity {

    private String mUid = "";
    private String finalImgPath;

    private UserDetail mUserDetail;

    private ImageView ivUserHeadIcon;
    private TextView postsNumTextView;     // 我的圈帖子数量
    private TextView levelTextView;    //等级
    private Bitmap headBmp;
    private String imgCacheUrl = "";
    private TextView chatNumTextView;   //聊天未读数量
    private TextView storeNumTextView;   //收藏数量

    private ImageChooser mImageChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        disableSwipeBack();
        initView();
        initListener();
        initData();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
    }

    private void initView() {
        postsNumTextView = (TextView) findViewById(R.id.tv_user_center_group_post_number);
        chatNumTextView = (TextView) findViewById(R.id.tv_user_center_message_number);

        // 用户头像
        ivUserHeadIcon = (ImageView) findViewById(R.id.iv_user_center_top_head);
        levelTextView = (TextView) findViewById(R.id.tv_user_center_top_level);
        storeNumTextView = (TextView) findViewById(R.id.tv_user_center_store_number);

    }

    private void initListener() {

        mImageChooser = new ImageChooser(mContext, true, true, true);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChose(Bitmap bitmap, int type) {
                getImageToView(bitmap);
            }
        });

        ivUserHeadIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 判断是否联网，如果没有联网的话，不进行拍照和相册选取图片的操作
                if (!NetUtils.isNetConnected(mContext)) {
                    ToastUtil.showToast(mContext, R.string.error_service);
                    return;
                }
                mImageChooser.takeImage(getResources().getString(R.string.uc_dialog_title_settingHead));
            }
        });

        levelTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                checkLevel();
            }
        });

        // actionbar home 操作，返回主界面
        findViewById(R.id.iv_user_center_top_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mContext != null) {
                    finish();
                }
            }
        });

        // 我的学习
        findViewById(R.id.ll_user_center_study_progress)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, UserProgressActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(UserProgressActivity.KEY_USERINFO, mUserDetail);
                        intent.putExtras(bundle);
                        intent.putExtra(UserProgressActivity.KEY_TYPE, Category.CATEGORY_TRAINING);
                        startActivity(intent);
                    }
                });
        // 进入我的圈页面
        findViewById(R.id.ll_user_center_group)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // 缺省UID的情况下，默认进入我的圈
                        Intent intent = new Intent(mContext, ChatterUserActivity.class);
                        intent.putExtra(ChatterUserActivity.KEY_USER_CHATTER, new UserChatterInfo(mUserDetail));
                        intent.putExtra(ChatterUserActivity.KEY_UID, mUid);
                        startActivity(intent);
                    }
                });

        // 进入我的考试
        findViewById(R.id.ll_user_center_exam)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, UserProgressActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(UserProgressActivity.KEY_USERINFO, mUserDetail);
                        intent.putExtras(bundle);
                        intent.putExtra(UserProgressActivity.KEY_TYPE, Category.CATEGORY_EXAM);
                        startActivity(intent);
                    }
                });

        // 进入我的私信
        findViewById(R.id.ll_user_center_message)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity, ChatListActivity.class);
                        intent.putExtra(ChatListActivity.KEY_HEAD_URL, mUserDetail.headimg);
                        startActivity(intent);
                    }
                });
        // 进入我的账号
        findViewById(R.id.ll_user_center_my_account)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity, AccountManageActivity.class));
                    }
                });
        // 进入关于我们
        findViewById(R.id.iv_user_center_top_about).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(mActivity, AboutUsActivity.class));
            }
        });

        findViewById(R.id.ll_user_center_store).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, StoreActivity.class));
            }
        });
    }

    private void initData() {
        // 获取用户的uid
        try {
            mUid = UserInfo.getUserInfo().getUid();
            // 根据uid拿到用户头像的路径
            finalImgPath = mActivity.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + mUid + ".png";
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mUserDetail = getUserCache();
        updateViewValue();
        mProgressDialog.setContent(R.string.user_detail_download_ing);

        mProgressDialog.show();
        updateData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    private void updateData() {
        // 获取用户详情
        ServiceProvider.doOptainUserDetail(mContext, mUid, new VolleyListener(
                mContext) {

            @Override
            public void onCompleted() {
                if (!mActivity.isFinishing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                JSONObject jObject = response.optJSONObject(Net.DATA);
                saveUserCache(jObject);
                mUserDetail = new UserDetail(jObject);
                updateViewValue();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageChooser.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 功能描述: 从文件中获取用户头像
     *
     * @param path
     * @return
     */
    private Bitmap getUserIconFromFile(String path) {
        if (!FileUtils.hasSdcard()) {
            return null;
        }
        int headWidth, headHeight;
        headWidth = getResources().getDimensionPixelSize(
                R.dimen.user_center_image_head_size);
        headHeight = headWidth;
        return BitmapUtil.decodeSampledBitmapFromFile(path, headWidth,
                headHeight);
    }

    /**
     * 功能描述: 设置用户头像
     *
     * @param uid
     * @param imgUrl
     */
    private void setUserIcon(final String uid, final String imgUrl) {
        if (headBmp != null && !headBmp.isRecycled()) {
            headBmp.recycle();
            headBmp = null;
        }
        if (!TextUtils.isEmpty(imgUrl))
            headBmp = BitmapLruCache.getBitmapLruCache()
                    .getCircleBitmap(imgUrl);
        int headWidth, headHeight;
        headWidth = getResources().getDimensionPixelSize(
                R.dimen.user_center_image_head_size);
        headHeight = headWidth;
        headBmp = BitmapUtil.getCirlBitmp(headBmp,
                headWidth, headHeight);
        if (headBmp != null) {
            ivUserHeadIcon.setImageBitmap(headBmp);
            return;
        }
        headBmp = BitmapUtil.getCirlBitmp(getUserIconFromFile(finalImgPath),
                headWidth, headHeight);
        if (headBmp != null) {
            ivUserHeadIcon.setImageBitmap(headBmp);
            BitmapLruCache.getBitmapLruCache().putCircleBitmap(imgCacheUrl, headBmp);
            return;
        } else {
            MyVolley.getImageLoader().get(
                    imgUrl,
                    new CircleImageListener(imgUrl, ivUserHeadIcon, headWidth, headHeight));
        }
    }

    /**
     * 设置分数 学习进度
     */
    private void updateViewValue() {

        if (mUserDetail == null) {
            return;
        }

        // 用户信息
        imgCacheUrl = mUserDetail.headimg;
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

    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(final Bitmap bitmap) {
        if (bitmap != null) {
            mProgressDialog.setTitle(R.string.user_detail_icon_upload_ing);
            mProgressDialog.show();
            ServiceProvider.doUpdateBitmap(mContext, bitmap,
                    Net.getRunHost() + Net.UPDATE_HEAD_ICON(mUid),
                    new VolleyListener(mContext) {

                        @Override
                        public void onCompleted() {
                            if (!mActivity.isFinishing())
                                mProgressDialog.dismiss();
                        }

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            Bitmap headbitmap = BitmapLruCache.getBitmapLruCache().get(imgCacheUrl);
                            if (headbitmap != null && !headbitmap.isRecycled()) {
                                headbitmap.recycle();
                            }
                            FileUtils.writeBitmap2SDcard(bitmap, finalImgPath);
                            bitmap.recycle();
                            setUserIcon(mUid, null);
                            ToastUtil.showToast(mContext, R.string.user_detail_icon_upload_success);
                        }
                    });
        } else {
            ToastUtil.showToast(mContext,
                    R.string.user_detail_icon_upload_failed);
        }
    }

    /**
     * 功能描述: 等级查看
     */
    public void checkLevel() {
        if (levelTextView != null) {
            levelTextView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    String userId = UserInfo.getUserInfo().getUid();
                    Intent intent = new Intent(mContext, BackWebActivity.class);
                    intent.putExtra("title", "等级介绍");
                    intent.putExtra(BackWebActivity.KEY_URL, Constant.LV_URL + userId);
                    startActivity(intent);
                }
            });
        }
    }
}
