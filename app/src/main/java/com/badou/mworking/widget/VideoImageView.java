package com.badou.mworking.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.VideoPlayActivity;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;

public class VideoImageView extends FrameLayout {
    private Context mContext;
    private String mLocalPath;
    private String mUrl;
    private String mQid;
    private SimpleDraweeView mContentImageView;
    private ImageView mShowImageView;
    private ImageView mDeleteImageView;
    private Bitmap mBitmap;

    public VideoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_video_image, this);
        mContentImageView = (SimpleDraweeView) findViewById(R.id.iv_view_video_image_content);
        mDeleteImageView = (ImageView) findViewById(R.id.iv_view_video_image_delete);
        mShowImageView = (ImageView) findViewById(R.id.iv_view_video_image_show);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toVideoActivity();
            }
        });
        mDeleteImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                if (mOnImageDeleteListener != null)
                    mOnImageDeleteListener.onDelete();
            }
        });
    }

    public interface OnImageDeleteListener {
        void onDelete();
    }

    OnImageDeleteListener mOnImageDeleteListener;

    public void setOnImageDeleteListener(OnImageDeleteListener onImageDeleteListener) {
        mDeleteImageView.setVisibility(View.VISIBLE);
        this.mOnImageDeleteListener = onImageDeleteListener;
        int size = getResources().getDimensionPixelSize(R.dimen.image_size_content);
        int margin = getResources().getDimensionPixelOffset(R.dimen.offset_small);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(size, size);
        layoutParams.setMargins(margin, margin, 0, 0);
        mContentImageView.setLayoutParams(layoutParams);
        mShowImageView.setLayoutParams(layoutParams);
    }

    public void setData(Bitmap bitmap, String localPath) {
        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
        mBitmap = bitmap;
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setPlaceholderImage(new BitmapDrawable(mContext.getResources(), bitmap), ScalingUtils.ScaleType.CENTER_CROP)
                .build();
        mContentImageView.setHierarchy(hierarchy);
        mLocalPath = localPath;
        mQid = null;
    }

    public void setData(String imgUrl, String videoUrl, String qid) {
        if (mBitmap != null && !mBitmap.isRecycled())
            mBitmap.recycle();
        mContentImageView.setImageURI(UriUtil.getHttpUri(videoUrl));
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
        BitmapUtil.recycleBitmap(mBitmap);
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
            intent.putExtra(VideoPlayActivity.KEY_VIDEOPATH, mLocalPath);
            //intent.putExtra(VideoPlayActivity.KEY_VIDEOPATH, FileUtils.getChatterVideoDir(mContext));
            mContext.startActivity(intent);
        }
    }
}
