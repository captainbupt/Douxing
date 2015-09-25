package com.badou.mworking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.R;
import com.badou.mworking.entity.chatter.UrlContent;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatterUrlView extends LinearLayout {
    @Bind(R.id.content_image_view)
    SimpleDraweeView mContentImageView;
    @Bind(R.id.content_text_view)
    EllipsizeTextView mContentTextView;

    String mUrl;
    Context mContext;

    public ChatterUrlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_chatter_url, this, true);
        ButterKnife.bind(this, this);
        mContentTextView.setMaxLines(2);
    }

    public void setData(UrlContent urlContent) {
        if (urlContent != null) {
            this.mUrl = urlContent.getUrl();
            mContentTextView.setText(urlContent.getDescription());
            mContentImageView.setImageURI(UriUtil.getHttpUri(urlContent.getImg()));
            setEnabled(true);
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(BackWebActivity.getIntent(mContext, mContext.getString(R.string.chatter_submit_title_url_detail), mUrl));
                }
            });
        } else {
            mContentTextView.setText(R.string.chatter_parse_url_fail);
            mContentImageView.setImageResource(R.drawable.icon_chatter_url_undetectable);
            setEnabled(false);
        }
    }
}
