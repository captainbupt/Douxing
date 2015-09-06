/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.badou.mworking.R;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.task.LoadLocalBigImgTask;
import com.easemob.chatuidemo.utils.ImageCache;
import com.easemob.chatuidemo.widget.photoview.PhotoView;
import com.easemob.util.EMLog;
import com.easemob.util.ImageUtils;
import com.easemob.util.PathUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载显示大图
 */
public class ShowBigImage extends Activity {
    private static final String TAG = "ShowBigImage";
    private ProgressDialog pd;
    private PhotoView image;
    private ImageView downloadImageView;
    private int default_res = R.drawable.default_image;
    private String localFilePath;
    private Bitmap bitmap;
    private boolean isDownloaded;
    private ProgressBar loadLocalPb;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (PhotoView) findViewById(R.id.image);
        loadLocalPb = (ProgressBar) findViewById(R.id.pb_load_local);
        downloadImageView = (ImageView) findViewById(R.id.download_image_view);
        default_res = getIntent().getIntExtra("default_image", R.drawable.icon_emchat_single);
        Uri uri = getIntent().getParcelableExtra("uri");
        String remotepath = getIntent().getExtras().getString("remotepath");
        String secret = getIntent().getExtras().getString("secret");
        EMLog.d(TAG, "show big image uri:" + uri + " remotepath:" + remotepath);

        //本地存在，直接显示本地的图片
        if (uri != null && new File(uri.getPath()).exists()) {
            EMLog.d(TAG, "showbigimage file exists. directly show it");
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            // int screenWidth = metrics.widthPixels;
            // int screenHeight =metrics.heightPixels;
            bitmap = ImageCache.getInstance().get(uri.getPath());
            if (bitmap == null) {
                bitmap = BitmapUtil.decodeSampledBitmapFromFile(uri.getPath(), ImageUtils.SCALE_IMAGE_WIDTH, ImageUtils.SCALE_IMAGE_HEIGHT);
                ImageCache.getInstance().put(uri.getPath(), bitmap);
            }
            image.setImageBitmap(bitmap);
            downloadImageView.setVisibility(View.VISIBLE);
        } else if (remotepath != null) { //去服务器下载图片
            EMLog.d(TAG, "download remote image");
            Map<String, String> maps = new HashMap<String, String>();
            if (!TextUtils.isEmpty(secret)) {
                maps.put("share-secret", secret);
            }
            downloadImage(remotepath, maps);
        } else {
            image.setImageResource(default_res);
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        downloadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BitmapUtil.isEmpty(bitmap))
                    downloadImage(bitmap);
            }
        });
    }

    public void downloadImage(Bitmap bitmap) {
        String fileStr = FileUtils.getSDPath(this) + "douxing/";
        File file = new File(fileStr);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!BitmapUtil.isEmpty(bitmap)) {
            String fileName = System.currentTimeMillis() + ".jpg";
            FileUtils.writeBitmap2SDcard(bitmap, fileStr + fileName);
            ToastUtil.showToast(this, "图片下载成功");
            // 下载完毕之后，发送广播扫描文件，否则的话，相册里面不能及时看到
            Uri uri = Uri.fromFile(new File(fileStr + fileName));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            sendBroadcast(localIntent);
        }
    }

    /**
     * 通过远程URL，确定下本地下载后的localurl
     *
     * @param remoteUrl
     * @return
     */
    public String getLocalFilePath(String remoteUrl) {
        String localPath;
        if (remoteUrl.contains("/")) {
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/"
                    + remoteUrl.substring(remoteUrl.lastIndexOf("/") + 1);
        } else {
            localPath = PathUtil.getInstance().getImagePath().getAbsolutePath() + "/" + remoteUrl;
        }
        return localPath;
    }

    /**
     * 下载图片
     *
     * @param remoteFilePath
     */
    private void downloadImage(final String remoteFilePath, final Map<String, String> headers) {
        String str1 = getResources().getString(R.string.Download_the_pictures);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(str1);
        pd.show();
        localFilePath = getLocalFilePath(remoteFilePath);
        final EMCallBack callback = new EMCallBack() {
            public void onSuccess() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        int screenWidth = metrics.widthPixels;
                        int screenHeight = metrics.heightPixels;

                        bitmap = ImageUtils.decodeScaleImage(localFilePath, screenWidth, screenHeight);
                        if (bitmap == null) {
                            image.setImageResource(default_res);
                        } else {
                            image.setImageBitmap(bitmap);
                            downloadImageView.setVisibility(View.VISIBLE);
                            ImageCache.getInstance().put(localFilePath, bitmap);
                            isDownloaded = true;
                        }
                        if (pd != null) {
                            pd.dismiss();
                        }
                    }
                });
            }

            public void onError(int error, String msg) {
                EMLog.e(TAG, "offline file transfer error:" + msg);
                File file = new File(localFilePath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        image.setImageResource(default_res);
                    }
                });
            }

            public void onProgress(final int progress, String status) {
                EMLog.d(TAG, "Progress: " + progress);
                final String str2 = getResources().getString(R.string.Download_the_pictures_new);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setMessage(str2 + progress + "%");
                    }
                });
            }
        };

        EMChatManager.getInstance().downloadFile(remoteFilePath, localFilePath, headers, callback);

    }

    @Override
    public void onBackPressed() {
        if (isDownloaded)
            setResult(RESULT_OK);
        finish();
    }
}
