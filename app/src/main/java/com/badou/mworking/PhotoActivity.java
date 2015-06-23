package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * 功能描述: 同事圈图片点击之后法放大查看页面
 */
public class PhotoActivity extends BaseNoTitleActivity {

    public static final String KEY_URL = "url";
    public static final String KEY_PATH = "path";
    private PhotoView photoView;
    private ImageView downImg; //图片下载

    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        photoView = (PhotoView) findViewById(R.id.photoView);
        downImg = (ImageView) findViewById(R.id.img_down);
        String imgUrl = mReceivedIntent.getStringExtra(KEY_URL);
        String imgpath = mReceivedIntent.getStringExtra(KEY_PATH);
        if (!TextUtils.isEmpty(imgUrl)) {
            MyVolley.getImageLoader().get(imgUrl,
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            bitmap = imageContainer.getBitmap();
                            //if (bitmap != null) {  // 此逻辑不可用，不知道为什么
                                photoView.setImageBitmap(bitmap);
                           /* } else {
                                System.out.println("null");
                                ToastUtil.showToast(mContext, R.string.chatter_open_image_fail);
                                finish();
                            }*/
                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            ToastUtil.showToast(mContext, R.string.chatter_open_image_fail);
                            finish();
                        }
                    });
        } else if (!TextUtils.isEmpty(imgpath)) {
/*            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(imgpath, option);*/
            bitmap = BitmapFactory.decodeFile(imgpath);
            photoView.setImageBitmap(bitmap);
        } else {
            ToastUtil.showToast(mContext, R.string.chatter_open_image_fail);
            finish();
        }

        //设置点击监听
        photoView.setOnViewTapListener(
                new OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        if (PhotoActivity.this != null) {
                            PhotoActivity.this.finish();
                        }
                    }
                }
        );

        // 下载图片
        downImg.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String fileStr = FileUtils.getSDPath(mContext) + "douxing/";
                        File file = new File(fileStr);
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        if (bitmap != null) {
                            String fileName = System.currentTimeMillis() + ".png";
                            FileUtils.writeBitmap2SDcard(bitmap, fileStr + fileName);
                            ToastUtil.showToast(PhotoActivity.this, "图片下载成功");
                            // 下载完毕之后，发送广播扫描文件，否则的话，相册里面不能及时看到
                            Uri uri = Uri.fromFile(new File(fileStr + fileName));
                            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
                            PhotoActivity.this.sendBroadcast(localIntent);
                        }
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
// 导致SwipeBack无效
/*        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }*/
        super.onDestroy();
    }
}
