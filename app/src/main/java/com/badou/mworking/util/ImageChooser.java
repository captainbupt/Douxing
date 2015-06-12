package com.badou.mworking.util;

import android.annotation.TargetApi;
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

import com.badou.mworking.R;
import com.badou.mworking.VideoTakeActivity;

import android.app.Activity;
import android.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/4.
 */
public class ImageChooser {

    private List<String> mItemList;

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;

    /* 请求码 */
    private static final int IMAGE_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int ZOOM_REQUEST_CODE = 3;
    private static final int VIDEO_REQUEST_CODE = 4;

    private Context mContext;
    private boolean isChoose;
    private boolean isPhoto;
    private boolean isZoom;
    private boolean isVideo;

    public void setOnImageChosenListener(OnImageChosenListener onImageChosenListener) {
        this.mOnImageChosenListener = onImageChosenListener;
    }

    private OnImageChosenListener mOnImageChosenListener;

    public interface OnImageChosenListener {
        void onImageChose(Bitmap bitmap, int type);
    }

    public ImageChooser(Context context, boolean isChoose, boolean isPhoto, boolean isZoom, boolean isVideo) {
        this.mContext = context;
        this.isChoose = isChoose;
        this.isPhoto = isPhoto;
        this.isZoom = isZoom;
        this.isVideo = isVideo;
        mItemList = new ArrayList<>();
    }

    public ImageChooser(Context context, boolean isChoose, boolean isPhoto, boolean isZoom) {
        this(context, isChoose, isPhoto, isZoom, false);
    }

    public void takeImage(String title) {
        mItemList.clear();
        if (isChoose) {
            mItemList.add(mContext.getResources().getString(R.string.choose_sd_Pic));
        }
        if (isPhoto) {
            mItemList.add(mContext.getResources().getString(R.string.choose_camera));
        }
        if (isVideo) {
            mItemList.add(mContext.getResources().getString(R.string.choose_video));
        }
        showDialog(title, mItemList.toArray(new String[mItemList.size()]));
    }

    /**
     * 显示选择对话框
     */
    private void showDialog(String title, String[] items) {

        new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mItemList.get(which).equals(mContext.getResources().getString(R.string.choose_sd_Pic))) {
                            choosePhoto();// 选择本地图片
                        } else if (mItemList.get(which).equals(mContext.getResources().getString(R.string.choose_camera))) {
                            takePhoto();// 拍照
                        } else if (mItemList.get(which).equals(mContext.getResources().getString(R.string.choose_video))) {
                            takeVideo();
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

    private void choosePhoto() {
        Intent intentFromGallery = new Intent();
        intentFromGallery.setType("image/*"); // 设置文件类型

        intentFromGallery
                .setAction(Intent.ACTION_GET_CONTENT);
        intentFromGallery
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity) mContext).startActivityForResult(intentFromGallery,
                IMAGE_REQUEST_CODE);
    }

    private void takePhoto() {
        Intent intentFromCapture = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断存储卡是否可以用，可用进行存储
        if (FileUtils.hasSdcard()) {
            File file = new File(mContext
                    .getExternalCacheDir()
                    .getAbsolutePath()
                    + File.separator + "temp.jpg");
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
        ((Activity) mContext).startActivityForResult(intentFromCapture,
                CAMERA_REQUEST_CODE);
    }

    /**
     * 功能描述: 摄像
     */
    private void takeVideo() {
        Intent intent = new Intent();
        intent.setClass(mContext, VideoTakeActivity.class);
        ((Activity) mContext).startActivityForResult(intent, VIDEO_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    if (data != null) {
                        if (isZoom)
                            startPhotoZoom(data.getData());
                        else {
                            getResult(data);
                        }
                    }
                    break;
                case CAMERA_REQUEST_CODE:
                    if (FileUtils.hasSdcard()) {
                        File file = new File(mContext.getExternalCacheDir()
                                .getAbsolutePath() + File.separator + "temp.jpg");
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
                        if (bitmap != bitmap2 && !bitmap.isRecycled())
                            bitmap.recycle();
                        if (isZoom) {
                            startPhotoZoom(Uri.fromFile(file));
                        } else {
                            if (mOnImageChosenListener != null)
                                mOnImageChosenListener.onImageChose(bitmap2, TYPE_IMAGE);
                        }
                    } else {
                        ToastUtil.showToast(mContext, R.string.save_camera_fail);
                    }
                    break;
                case ZOOM_REQUEST_CODE:
                    if (data != null) {
                        getResult(data);
                    }
                    break;
                case VIDEO_REQUEST_CODE:
                    // 捕获返回的拍照路径  (注意： 这里还不能简单的用qid，来命名文件，因为你得先上传图片，然后采用qid，你只能把拍好的视屏改名)
                    String filePath = FileUtils.getChatterVideoDir(mContext);
                    File file = new File(filePath);
                    if (!TextUtils.isEmpty(filePath) && file.exists()) {
                        int size = mContext.getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge);
                        Bitmap videoBitmap = VideoImageThumbnail.getVideoThumbnail(FileUtils.getChatterVideoDir(mContext), size, size,
                                MediaStore.Images.Thumbnails.MICRO_KIND);
                        if (mOnImageChosenListener != null) {
                            mOnImageChosenListener.onImageChose(videoBitmap, TYPE_VIDEO);
                        }
                    }
                    break;
            }
        }
    }

    private void getResult(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            final Bitmap bitmap = extras.getParcelable("data");
            if (mOnImageChosenListener != null)
                mOnImageChosenListener.onImageChose(bitmap, TYPE_IMAGE);
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
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        String filepath = getPath(mContext, uri);
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
        ((Activity) mContext).startActivityForResult(intent, ZOOM_REQUEST_CODE);
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
    @TargetApi(Build.VERSION_CODES.KITKAT)
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

}
