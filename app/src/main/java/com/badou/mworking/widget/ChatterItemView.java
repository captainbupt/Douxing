package com.badou.mworking.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.database.ChatterResManager;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.TimeTransfer;

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
    @Bind(R.id.reply_image_view)
    ImageView mReplyImageView;
    @Bind(R.id.message_text_view)
    TextView mMessageTextView;
    @Bind(R.id.delete_text_view)
    TextView mDeleteTextView;

    Context mContext;
    OnClickListener mDeletedClickListener;
    OnClickListener mPraiseClickListener;
    OnClickListener mHeadClickListener;
    OnClickListener mMessageClickListener;

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

    public void setDeleteListener(OnClickListener deletedClickListener) {
        mDeletedClickListener = deletedClickListener;
        mDeleteTextView.setOnClickListener(deletedClickListener);
    }

    public void setPraiseListener(OnClickListener praiseClickListener) {
        mPraiseClickListener = praiseClickListener;
        mPraiseImageView.setOnClickListener(mPraiseClickListener);
        mPraiseTextView.setOnClickListener(mPraiseClickListener);
    }

    public void setHeadListener(OnClickListener headClickListener) {
        mHeadClickListener = headClickListener;
        mHeadImageView.setOnClickListener(mHeadClickListener);
    }

    public void setMessageListener(OnClickListener messageClickListener) {
        mMessageClickListener = messageClickListener;
        mMessageTextView.setOnClickListener(mMessageClickListener);
    }

    public void setData(final Chatter chatter, boolean isDetail, int position) {
        mNameTextView.setText(chatter.getName());
        String content = chatter.getContent();
        TopicClickableSpan.setClickTopic(mContext, mContentTextView, content, isDetail ? Integer.MAX_VALUE : 100);
        if (!isDetail && mContentTextView.getText().length() > 100) {
            mFullContentTextView.setVisibility(View.VISIBLE);
        } else {
            mFullContentTextView.setVisibility(View.GONE);
        }
        mTimeTextView.setText(TimeTransfer.long2ChatterDetailData(mContext, chatter.getPublishTime()));
        ImageViewLoader.setCircleImageViewResource(mHeadImageView, chatter.getHeadUrl(), mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

        // 有Url则直接显示url
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

        // 设置显示级别
        if (chatter.getName().equals("神秘的TA")) {
            mLevelTextView.setVisibility(View.GONE);
        } else {
            mLevelTextView.setVisibility(View.VISIBLE);
            mLevelTextView.setLevel(chatter.getLevel());
        }

        if (!isDetail) {
            /** 设置点赞数和监听 **/
            mPraiseTextView.setText(chatter.getPraiseNumber() + "");
            /** 设置点赞的check **/
            if (ChatterResManager.isSelect(mContext, chatter.getQid())) {
                mPraiseImageView.setImageResource(R.drawable.icon_praise_checked);
                mPraiseImageView.setEnabled(false);
                mPraiseTextView.setEnabled(false);
            } else {
                mPraiseImageView.setImageResource(R.drawable.icon_praise_unchecked);
                // 设置点赞点击事件
                mPraiseImageView.setEnabled(true);
                mPraiseTextView.setEnabled(true);
            }
            mReplyTextView.setText("" + chatter.getReplyNumber());
            mPraiseTextView.setVisibility(View.VISIBLE);
            mPraiseImageView.setVisibility(View.VISIBLE);
            mReplyTextView.setVisibility(View.VISIBLE);
            mReplyImageView.setVisibility(View.VISIBLE);
            mMessageTextView.setVisibility(View.GONE);
            mDeleteTextView.setVisibility(View.GONE);
        } else {
            mPraiseTextView.setVisibility(View.GONE);
            mPraiseImageView.setVisibility(View.GONE);
            mReplyTextView.setVisibility(View.GONE);
            mReplyImageView.setVisibility(View.GONE);
            mMessageTextView.setVisibility(View.VISIBLE);
            mDeleteTextView.setVisibility(View.VISIBLE);
            // 点击进入是自己      (TextUtils.isEmpty(currentUid) 我的圈中没有返回uid字段，因为那是自己，当uid为空时，判断为是自己，也就是我的圈跳转进入的，只显示删除)
            if (UserInfo.getUserInfo().getUid().equals(chatter.getUid()) || TextUtils.isEmpty(chatter.getUid())) {
                mMessageTextView.setVisibility(View.GONE);
            } else {
                mMessageTextView.setVisibility(View.VISIBLE);
            }
            if (chatter.isDeletable()) {
                mDeleteTextView.setVisibility(View.VISIBLE);
            } else {
                mDeleteTextView.setVisibility(View.GONE);
            }
        }
        mDeleteTextView.setTag(position);
        mPraiseTextView.setTag(position);
        mPraiseImageView.setTag(position);
        mHeadImageView.setTag(position);
        mMessageTextView.setTag(position);
    }
}
