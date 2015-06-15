package com.badou.mworking.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.VideoPlayActivity;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.FileUtils;

import java.io.File;

/**
 * Created by Administrator on 2015/6/10.
 */
public class VideoImageView extends FrameLayout {
    private Context mContext;
    private String mUrl;
    private String mQid;
    private ImageView mContentImageView;
    private Bitmap mBitmap;

    public VideoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_video_image, this);
        mContentImageView = (ImageView) findViewById(R.id.iv_view_video_image_content);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toVideoActivity();
            }
        });
    }

    public void setData(Bitmap bitmap, String videoUrl, String qid) {
        mBitmap = bitmap;
        mContentImageView.setImageBitmap(bitmap);
        mUrl = videoUrl;
        mQid = qid;
    }

    public void setData(String imgUrl, String videoUrl, String qid) {
        ImageViewLoader.setSquareImageViewResource(mContentImageView, R.drawable.icon_image_default, imgUrl, mContext.getResources().getDimensionPixelSize(R.dimen.image_size_content));
        mUrl = videoUrl;
        mQid = qid;
    }

    // 只有编辑时才会有bitmap实例
    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void clear() {
        File file = new File(FileUtils.getChatterVideoDir(mContext));
        if (file.exists()) {
            file.delete();
        }
        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
    }

    private void toVideoActivity() {
        if (!TextUtils.isEmpty(mQid)) {
            String path = FileUtils.getChatterDir(mContext) + mQid + ".mp4";
            Intent intent = new Intent(mContext, VideoPlayActivity.class);
            intent.putExtra(VideoPlayActivity.KEY_VIDEOURL, mUrl);
            intent.putExtra(VideoPlayActivity.KEY_VIDEOPATH, path);
            mContext.startActivity(intent);
        } else {
            // 还未上传，直接打开本地文件
            Intent intent = new Intent(mContext, VideoPlayActivity.class);
            intent.putExtra(VideoPlayActivity.KEY_VIDEOURL, mUrl);
            intent.putExtra(VideoPlayActivity.KEY_VIDEOPATH, FileUtils.getChatterVideoDir(mContext));
            mContext.startActivity(intent);
        }
    }
}
