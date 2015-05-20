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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.net.LVUtil;
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
import com.badou.mworking.widget.SwipeBackLayout;

import org.holoeverywhere.app.AlertDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 类: <code> UserCenterActivity </code> 功能描述: 个人中心页面 创建人: 葛建锋 创建日期: 2014年7月15日
 * 下午3:59:23 开发环境: JDK7.0
 */
public class UserCenterActivity extends BaseNoTitleActivity {

    private String uid = "";
    private String finalImgPath;

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 3;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    public static final String KEY_USERINFO = "UserCenter_INFO";

    private UserDetail mUserDetail;

    private ImageView homeImageView;
    private ImageView settingsImageView;
    private ImageView ivUserHeadIcon;
    private TextView postsNumTextView;     // 我的圈帖子数量
    private TextView levelTextView;    //等级
    private Bitmap headBmp;
    private String imgCacheUrl = "";
    private TextView chatNumTextView;   //聊天未读数量

    // Press the back button in mobile phone
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_from_right,
                R.anim.out_to_left);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.base, null);
        layout.attachToActivity(this);
        setContentView(R.layout.activity_user_center);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        postsNumTextView = (TextView) findViewById(R.id.posts_num_tv);
        chatNumTextView = (TextView) findViewById(R.id.chat_num_tv);
        // actionbar home 操作，返回主界面
        homeImageView = (ImageView) findViewById(R.id.iv_actionbar_left);
        homeImageView.setImageResource(R.drawable.title_bar_back_normal);
        // 个人中心的设置操作
        settingsImageView = (ImageView) findViewById(R.id.iv_actionbar_right);
        settingsImageView.setVisibility(View.GONE);

        // 设置actionbar标题
        ((TextView) findViewById(R.id.txt_actionbar_title)).setText(getIntent().getStringExtra(BaseActionBarActivity.KEY_TITLE));
        // 用户头像
        ivUserHeadIcon = (ImageView) findViewById(R.id.ivUserSecondHeadIcon);
        levelTextView = (TextView) findViewById(R.id.lv_tv);

    }

    private void initListener() {
        homeImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (UserCenterActivity.this != null) {
                    finish();
                    overridePendingTransition(0, R.anim.base_slide_right_out);
                }
            }
        });

        settingsImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity,
                        AccountManageActivity.class);
                mActivity.startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }
        });

        ivUserHeadIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 判断是否联网，如果没有联网的话，不进行拍照和相册选取图片的操作
                if (!NetUtils.isNetConnected(UserCenterActivity.this)) {
                    ToastUtil.showToast(mContext, R.string.error_service);
                    return;
                }
                showDialog();
            }
        });

        levelTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                checkLevel();
            }
        });

        // 我的学习
        ((RelativeLayout) findViewById(R.id.llUserSecondStudyProgress))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity,
                                MyStudyProgressAct.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KEY_USERINFO, mUserDetail);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
        // 进入我的圈页面
        ((LinearLayout) findViewById(R.id.llUserMyGroup))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity,
                                AroundUserActivity.class));
                    }
                });

        // 进入我的考试
        ((RelativeLayout) findViewById(R.id.llUserSecondExam))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mActivity,
                                MyExamAct.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(KEY_USERINFO, mUserDetail);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });

        // 进入我的直通车
        ((LinearLayout) findViewById(R.id.chat_linear))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity,
                                ChattingActivity.class));
                    }
                });
        // 进入我的账号
        ((LinearLayout) findViewById(R.id.llUserMyAccount))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(mActivity,
                                AccountManageActivity.class));
                    }
                });
        // 进入关于我们
        ((ImageView) findViewById(R.id.iv_about)).setOnClickListener(new OnClickListener() {

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
                    .getUserInfo().getUserId();
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
            BitmapLruCache.getBitmapLruCache().putCircleBitmap(imgUrl, headBmp);
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
        imgCacheUrl = mUserDetail.getHeadimg();
        String strScore = mContext.getResources().getString(R.string.text_score);
        String strPingJunFen = mContext.getResources().getString(R.string.ucneter_text_pingJunFen);
        setUserIcon(uid, mUserDetail.getHeadimg());
        if (!TextUtils.isEmpty(mUserDetail.getName())) {
            ((TextView) findViewById(R.id.tvActivityUserSecondName))
                    .setText(mUserDetail.getName());
        }
        if (!TextUtils.isEmpty(mUserDetail.getDpt())) {
            ((TextView) findViewById(R.id.tvActivityUserSecondPart))
                    .setText(mUserDetail.getDpt());
        }
        if (!TextUtils.isEmpty(String.valueOf(mUserDetail.getScore()))) {
            ((TextView) findViewById(R.id.tvUserSecondAvrScoreUser))
                    .setText(strPingJunFen + String.valueOf(mUserDetail.getScore())
                            + strScore);
        }

        // 学习进度
        if (!TextUtils.isEmpty(String.valueOf(mUserDetail.getStudy_total()))
                && !TextUtils.isEmpty(String.valueOf(mUserDetail
                .getTraining_total()))) {
            ((TextView) findViewById(R.id.studyTotalPercent))
                    .setText(String.valueOf(mUserDetail.getStudy_total())
                            + "/"
                            + String.valueOf(mUserDetail.getTraining_total()));
            int study = mUserDetail.getStudy_total();
            int training = mUserDetail.getTraining_total();
            int s = study * 100;
            if (training != 0) {
                int progress = s / training;
                ProgressBar pbTotalBar = (ProgressBar) findViewById(R.id.proBarTotalUser);
                pbTotalBar.setProgress(progress);
            }
        }

        // 同事圈
        postsNumTextView.setText(mUserDetail.getAsk() + getResources().getString(R.string.chatter_num));
        LVUtil.setTextViewBg(levelTextView, mUserDetail.getCircle_lv());
        int nmsg = mUserDetail.getNmsg();
        if (nmsg > 0) {
            chatNumTextView.setVisibility(View.VISIBLE);
            chatNumTextView.setText(mUserDetail.getNmsg() + "");
            // 如果是两位数的话，换一个背景
            if (nmsg > 9) {
                chatNumTextView.setBackgroundResource(R.drawable.icon_chat_unread_long);
            } else {
                chatNumTextView.setBackgroundResource(R.drawable.icon_chat_unread);
            }
        }

    }

    /**
     * 显示选择对话框
     */
    private void showDialog() {

        String[] items = new String[]{mContext.getResources().getString(R.string.choose_sd_Pic),
                mContext.getResources().getString(R.string.choose_camera)};
        new AlertDialog.Builder(this)
                .setTitle(R.string.uc_dialog_title_settingHead)
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // 选择本地图片
                                Intent intentFromGallery = new Intent();
                                intentFromGallery.setType("image/*"); // 设置文件类型

                                intentFromGallery
                                        .setAction(Intent.ACTION_GET_CONTENT);
                                intentFromGallery
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(intentFromGallery,
                                        IMAGE_REQUEST_CODE);
                                overridePendingTransition(R.anim.in_from_right,
                                        R.anim.out_to_left);
                                break;
                            case 1: // 拍照
                                Intent intentFromCapture = new Intent(
                                        MediaStore.ACTION_IMAGE_CAPTURE);
                                // 判断存储卡是否可以用，可用进行存储
                                if (FileUtils.hasSdcard()) {
                                    File file = new File(mActivity
                                            .getExternalCacheDir()
                                            .getAbsolutePath()
                                            + File.separator + "temp.png");
                                    if (file.exists())
                                        file.delete();
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    intentFromCapture.putExtra(
                                            MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(file));
                                }
                                intentFromCapture
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivityForResult(intentFromCapture,
                                        CAMERA_REQUEST_CODE);
                                overridePendingTransition(R.anim.in_from_right,
                                        R.anim.out_to_left);
                                break;
                        }
                    }
                })
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    if (data != null)
                        startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    if (FileUtils.hasSdcard()) {
                        File file = new File(mActivity.getExternalCacheDir()
                                .getAbsolutePath() + File.separator + "temp.png");
                        BitmapFactory.Options option = new BitmapFactory.Options();
                        option.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), option); //根据Path读取资源图片
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        matrix.preRotate(readPictureDegree(file.toString()));
                        Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                                matrix, true);// 从新生成图片
                        FileUtils.writeBitmap2SDcard(bitmap2,
                                file.toString());
                        startPhotoZoom(Uri.fromFile(file));
                    } else {
                        Toast.makeText(UserCenterActivity.this, R.string.save_camera_fail,
                                Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
                case RESULT_CANCELED:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        String filepath = getPath(UserCenterActivity.this, uri);
        Uri newUri = Uri.fromFile(new File(filepath));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(newUri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 3);
        intent.putExtra("aspectY", 3);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            // isExternalStorageDocument
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            // isDownloadsDocument
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            // isMediaDocument
            else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * 保存裁剪之后的图片数据
     */
    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            final Bitmap photo = extras.getParcelable("data");
            if (photo != null) {
                mProgressDialog.setTitle(R.string.user_detail_icon_upload_ing);
                mProgressDialog.show();
                ServiceProvider.doUpdateBitmap(mContext, photo,
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
                                        FileUtils.writeBitmap2SDcard(photo,
                                                finalImgPath);
                                        photo.recycle();
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
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
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
                            .getUserInfo().getUserId();
                    Intent intent = new Intent(mContext, BackWebActivity.class);
                    intent.putExtra("title", "等级介绍");
                    intent.putExtra(BackWebActivity.VALUE_URL, Constant.LV_URL + userId);
                    startActivity(intent);
                }
            });
        }
    }
}
