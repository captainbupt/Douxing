package com.badou.mworking.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.TongSHQVideoPlayActivity;
import com.badou.mworking.net.bitmap.ImageViewLoader;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;

/**
 * Created by Administrator on 2015/6/10.
 */
public class VideoImageView extends FrameLayout {
    private Context mContext;
    private String mUrl;
    private String mQid;
    private ImageView mContentImageView;

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
        mContentImageView.setImageBitmap(bitmap);
        mUrl = videoUrl;
        mQid = qid;
    }

    public void setData(String imgUrl, String videoUrl, String qid) {
        ImageViewLoader.setSquareImageViewResource(mContext, mContentImageView, imgUrl, mContext.getResources().getDimensionPixelSize(R.dimen.image_size_content));
        mUrl = videoUrl;
        mQid = qid;
    }

    private void toVideoActivity() {
        Intent intent = new Intent(mContext, TongSHQVideoPlayActivity.class);
        intent.putExtra(TongSHQVideoPlayActivity.VIDEOURL, mUrl);
        intent.putExtra(TongSHQVideoPlayActivity.QID, mQid);
        mContext.startActivity(intent);
    }
}
