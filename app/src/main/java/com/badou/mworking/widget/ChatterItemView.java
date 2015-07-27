package com.badou.mworking.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.ChatterResManager;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.listener.TopicClickableSpan;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.TimeTransfer;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatterItemView extends LinearLayout {

    @Bind(R.id.head_image_view)
    ImageView mHeadImageView;
    @Bind(R.id.name_text_view)
    TextView mNameTextView;
    @Bind(R.id.level_text_view)
    LevelTextView mLevelTextView;
    @Bind(R.id.content_text_view)
    TextViewFixTouchConsume mContentTextView;
    @Bind(R.id.full_content_text_view)
    TextView mFullContentTextView;
    @Bind(R.id.video_image_view)
    VideoImageView mVideoImageView;
    @Bind(R.id.image_grid_view)
    MultiImageShowGridView mImageGridView;
    @Bind(R.id.url_content_view)
    ChatterUrlView mUrlContentView;
    @Bind(R.id.save_internet_text_view)
    TextView mSaveInternetTextView;
    @Bind(R.id.time_text_view)
    TextView mTimeTextView;
    @Bind(R.id.praise_image_view)
    ImageView mPraiseImageView;
    @Bind(R.id.praise_text_view)
    TextView mPraiseTextView;
    @Bind(R.id.reply_text_view)
    TextView mReplyTextView;

    private Context mContext;
    HeadClickListener mHeadClickListener;
    PraiseClickListener mPraiseClickListener;

    public ChatterItemView(Context context) {
        super(context);
        initialize(context);
    }

    public ChatterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public void initialize(Context context) {
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.view_chatter_item, this, true);
        ButterKnife.bind(this, this);
    }

    public void setData(Chatter chatter, boolean isHeadClickable) {
        mNameTextView.setText(chatter.getName());
        String content = chatter.getContent();
        TopicClickableSpan.setClickTopic(mContext, mContentTextView, content, 100);
        if (mContentTextView.getText().length() > 100) {
            mFullContentTextView.setVisibility(View.VISIBLE);
        } else {
            mFullContentTextView.setVisibility(View.GONE);
        }
        mTimeTextView.setText(TimeTransfer.long2ChatterDetailData(mContext, chatter.getPublishTime()));
        mReplyTextView.setText("" + chatter.getReplyNumber());
        ImageViewLoader.setCircleImageViewResource(mHeadImageView, chatter.getHeadUrl(), mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

        // 有Url则直接显示url
        System.out.println("url content: " + (chatter.getUrlContent() != null));
        if (chatter.getUrlContent() != null)
            System.out.println("url: " + chatter.getUrlContent().getUrl());
        if (chatter.getUrlContent() != null && !TextUtils.isEmpty(chatter.getUrlContent().getUrl())) {
            mUrlContentView.setVisibility(View.VISIBLE);
            mVideoImageView.setVisibility(View.GONE);
            mImageGridView.setVisibility(View.GONE);
            mSaveInternetTextView.setVisibility(View.GONE);
            mUrlContentView.setData(chatter.getUrlContent());
        } else { // 无url则判断是否省流量
            mUrlContentView.setVisibility(View.GONE);
            if (NetUtils.isWifiConnected(mContext) || !SPHelper.getSaveInternetOption()) {
                mSaveInternetTextView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(chatter.getVideoUrl())) {
                    mVideoImageView.setVisibility(View.VISIBLE);
                    mVideoImageView.setData(chatter.getImgUrl(), chatter.getVideoUrl(), chatter.getQid());
                    mImageGridView.setVisibility(View.GONE);
                } else if (chatter.hasImageList()) {
                    mImageGridView.setVisibility(View.VISIBLE);
                    mImageGridView.setList(chatter.getPhotoUrls());
                    mVideoImageView.setVisibility(View.GONE);
                } else {
                    mImageGridView.setVisibility(View.GONE);
                    mVideoImageView.setVisibility(View.GONE);
                }
            } else { // 省流量则在有图片时显示提示
                mVideoImageView.setVisibility(View.GONE);
                mImageGridView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(chatter.getVideoUrl()) || chatter.hasImageList()) {
                    mSaveInternetTextView.setVisibility(View.VISIBLE);
                } else {
                    mSaveInternetTextView.setVisibility(View.GONE);
                }
            }
        }

        /** 设置点赞数和监听 **/
        mPraiseTextView.setText(chatter.getPraiseNumber() + "");
        // 设置显示级别
        if (chatter.getName().equals("神秘的TA")) {
            mLevelTextView.setVisibility(View.GONE);
        } else {
            mLevelTextView.setVisibility(View.VISIBLE);
            mLevelTextView.setLevel(chatter.getLevel());
        }
        if (isHeadClickable) { // 设置头像点击事件
            if (mHeadClickListener == null)
                mHeadClickListener = new HeadClickListener();
            mHeadImageView.setOnClickListener(mHeadClickListener);
            mHeadClickListener.chatter = chatter;
        }

        /** 设置点赞的check **/
        if (ChatterResManager.isSelect(mContext, chatter.getQid())) {
            mPraiseImageView.setImageResource(R.drawable.icon_praise_checked);
            mPraiseImageView.setEnabled(false);
            mPraiseTextView.setEnabled(false);
        } else {
            mPraiseImageView.setImageResource(R.drawable.icon_praise_unchecked);
            // 设置点赞点击事件
            mPraiseClickListener = new PraiseClickListener();
            mPraiseImageView.setOnClickListener(mPraiseClickListener);
            mPraiseTextView.setOnClickListener(mPraiseClickListener);
            mPraiseClickListener.chatter = chatter;
            mPraiseClickListener.praiseImageView = mPraiseImageView;
            mPraiseClickListener.numberTextView = mPraiseTextView;
            mPraiseImageView.setEnabled(true);
            mPraiseTextView.setEnabled(true);
        }
    }

    class HeadClickListener implements OnClickListener {
        public Chatter chatter;

        @Override
        public void onClick(View v) {
            UserChatterInfo userChatterInfo = new UserChatterInfo(chatter);
            if (userChatterInfo.name.equals("神秘的TA")) {
                return;
            }
            Intent intent = new Intent(mContext, ChatterUserActivity.class);
            intent.putExtra(ChatterUserActivity.KEY_UID, chatter.getUid());
            intent.putExtra(ChatterUserActivity.KEY_USER_CHATTER, userChatterInfo);
            mContext.startActivity(intent);
        }
    }

    class PraiseClickListener implements OnClickListener {
        public Chatter chatter;
        public ImageView praiseImageView;
        public TextView numberTextView;

        @Override
        public void onClick(View arg0) {
            chatter.increasePraise();
            praiseImageView.setImageResource(R.drawable.icon_praise_checked);
            numberTextView.setText(chatter.getPraiseNumber() + "");

            /** 调用同事圈点赞接口 提交点赞 **/
            ServiceProvider.doSetCredit(mContext, chatter.getQid(), new VolleyListener(mContext) {

                @Override
                public void onErrorCode(int code) {
                    chatter.decreasePraise();
                    praiseImageView.setImageResource(R.drawable.icon_praise_unchecked);
                    numberTextView.setText(chatter.getPraiseNumber() + "");
                    mPraiseImageView.setEnabled(true);
                    mPraiseTextView.setEnabled(true);
                }

                @Override
                public void onResponseSuccess(JSONObject response) {
                    ChatterResManager.insertItem(mContext, chatter);
                    mPraiseImageView.setEnabled(false);
                    mPraiseTextView.setEnabled(false);
                }
            });
        }
    }
}
