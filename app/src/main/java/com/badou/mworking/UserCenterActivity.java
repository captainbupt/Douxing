package com.badou.mworking;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.ProgressBar;
import org.holoeverywhere.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 功能描述: 个人中心页面
 */
public class UserCenterActivity extends BaseNoTitleActivity {

    private String uid = "";
    private String finalImgPath;

    private UserDetail mUserDetail;

    private ImageView ivUserHeadIcon;
    private TextView postsNumTextView;     // 我的圈帖子数量
    private TextView levelTextView;    //等级
    private Bitmap headBmp;
    private String imgCacheUrl = "";
    private TextView chatNumTextView;   //聊天未读数量

    private ImageChooser mImageChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        postsNumTextView = (TextView) findViewById(R.id.tv_user_center_group_post_number);
        chatNumTextView = (TextView) findViewById(R.id.tv_user_center_message_number);

        // 用户头像
        ivUserHeadIcon = (ImageView) findViewById(R.id.iv_user_center_top_head);
        levelTextView = (TextView) findViewById(R.id.tv_user_center_top_level);

    }

    private void initListener() {

        mImageChooser = new ImageChooser(mContext, true, true, true);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChose(Bitmap bitmap) {
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
                        Intent intent = new Intent(mActivity,
                                UserProgressActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(UserProgressActivity.KEY_USERINFO, mUserDetail);
                        intent.putExtras(bundle);
                        intent.putExtra(UserProgressActivity.KEY_TYPE, Category.CATEGORY_TRAIN);
                        startActivity(intent);
                    }
                });
        // 进入我的圈页面
        findViewById(R.id.ll_user_center_group)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity,
                                MyGroupActivity.class));
                    }
                });

        // 进入我的考试
        findViewById(R.id.ll_user_center_exam)
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity,
                                UserProgressActivity.class);
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
                        Intent intent = new Intent(mActivity,
                                ChatListActivity.class);
                        intent.putExtra(ChatListActivity.KEY_HEAD_URL, mUserDetail.headimg);
                        startActivity(intent);
                    }
                });
        // 进入我的账号
        findViewById(R.id.ll_user_center_my_account)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity,
                                AccountManageActivity.class));
                    }
                });
        // 进入关于我们
        findViewById(R.id.iv_user_center_top_about).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(mActivity,
                        AboutUsActivity.class));
            }
        });
    }

    private void initData() {
        // 获取用户的uid
        try {
            uid = ((AppApplication) mContext.getApplicationContext())
                    .getUserInfo().userId;
            // 根据uid拿到用户头像的路径
            finalImgPath = mActivity.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES).getAbsolutePath()
                    + File.separator + uid + ".png";
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mProgressDialog.setContent(R.string.user_detail_download_ing);

        mProgressDialog.show();
        // 获取用户详情
        ServiceProvider.doOptainUserDetail(mContext, uid, new VolleyListener(
                mContext) {
            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (null != mProgressDialog && mContext != null
                        && !mActivity.isFinishing())
                    mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(Object arg0) {
                if (null != mProgressDialog && mContext != null
                        && !mActivity.isFinishing())
                    mProgressDialog.dismiss();
                JSONObject jsonObject = (JSONObject) arg0;
                int code = jsonObject.optInt(Net.CODE);
                if (code == Net.LOGOUT) {
                    AppApplication.logoutShow(mContext);
                    return;
                }
                if (code != Net.SUCCESS) {
                    return;
                }
                JSONObject jObject = null;
                try {
                    jObject = new JSONObject(jsonObject.optString(Net.DATA));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jObject == null) {
                    return;
                }

                mUserDetail = new UserDetail(jObject);
                updateViewValue();
            }

        });
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
                    new CircleImageListener(mContext, imgUrl, ivUserHeadIcon,
                            headWidth, headHeight));
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
        setUserIcon(uid, mUserDetail.headimg);
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
            // 如果是两位数的话，换一个背景
            if (nmsg > 9) {
                chatNumTextView.setBackgroundResource(R.drawable.icon_chat_unread_long);
            } else {
                chatNumTextView.setBackgroundResource(R.drawable.icon_chat_unread);
            }
        }

    }

    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(final Bitmap bitmap) {
        if (bitmap != null) {
            mProgressDialog.setTitle(R.string.user_detail_icon_upload_ing);
            mProgressDialog.show();
            ServiceProvider.doUpdateBitmap(mContext, bitmap,
                    Net.getRunHost(mContext) + Net.UPDATE_HEAD_ICON(uid),
                    new VolleyListener(mContext) {

                        @Override
                        public void onResponse(Object responseObject) {
                            Bitmap headbitmap = BitmapLruCache.getBitmapLruCache().get(imgCacheUrl);
                            if (headbitmap != null && !headbitmap.isRecycled()) {
                                headbitmap.recycle();
                            }
                            if (null != mProgressDialog && mContext != null
                                    && !mActivity.isFinishing())
                                mProgressDialog.dismiss();
                            try {
                                int code = ((JSONObject) responseObject)
                                        .optInt(Net.CODE);
                                if (code == Net.LOGOUT) {
                                    AppApplication.logoutShow(mContext);
                                    return;
                                }
                                if (code != Net.SUCCESS) {
                                    ToastUtil.showToast(
                                            mContext,
                                            mActivity
                                                    .getString(R.string.error_service));
                                    return;
                                } else {
                                    FileUtils.writeBitmap2SDcard(bitmap,
                                            finalImgPath);
                                    bitmap.recycle();
                                    setUserIcon(uid, null);
                                    ToastUtil
                                            .showToast(
                                                    mContext,
                                                    R.string.user_detail_icon_upload_success);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                    String userId = ((AppApplication) mContext.getApplicationContext())
                            .getUserInfo().userId;
                    Intent intent = new Intent(mContext, BackWebActivity.class);
                    intent.putExtra("title", "等级介绍");
                    intent.putExtra(BackWebActivity.KEY_URL, Constant.LV_URL + userId);
                    startActivity(intent);
                }
            });
        }
    }
}
