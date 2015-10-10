package com.badou.mworking.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.badou.mworking.R;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.MultiPhotoView;

import java.io.File;

import uk.co.senab.photoview.PhotoView;

public class MultiPhotoPresenter extends Presenter {

    MultiPhotoView mMultiPhotoView;

    public MultiPhotoPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mMultiPhotoView = (MultiPhotoView) v;
    }

    public void downloadImage(Bitmap bitmap) {
        String fileStr = FileUtils.getSDPath(mContext) + "douxing/";
        File file = new File(fileStr);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!BitmapUtil.isEmpty(bitmap)) {
            String fileName = System.currentTimeMillis() + ".jpg";
            FileUtils.writeBitmap2SDcard(bitmap, fileStr + fileName);
            mMultiPhotoView.showToast("图片已保存至：" + fileStr + fileName);
            // 下载完毕之后，发送广播扫描文件，否则的话，相册里面不能及时看到
            Uri uri = Uri.fromFile(new File(fileStr + fileName));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            mContext.sendBroadcast(localIntent);
        }
    }

    public void openFail() {
        mMultiPhotoView.showToast(R.string.chatter_open_image_fail);
        ((Activity) mContext).finish();
    }
}
