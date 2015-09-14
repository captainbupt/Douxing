package com.badou.mworking.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BottomRatingAndCommentView extends LinearLayout {

    @Bind(R.id.comment_number_text_view)
    TextView mCommentNumberTextView;
    @Bind(R.id.rating_number_text_view)
    TextView mRatingNumberTextView;
    @Bind(R.id.comment_layout)
    RelativeLayout mCommentLayout;
    @Bind(R.id.view_bottom_center_divider_1)
    View mDividerView1;
    @Bind(R.id.view_bottom_center_divider_2)
    View mDividerView2;
    @Bind(R.id.rating_layout)
    RelativeLayout mRatingLayout;
    @Bind(R.id.share_layout)
    RelativeLayout mShareLayout;
    @Bind(R.id.rating_image_view)
    ImageView mRatingImageView;

    public BottomRatingAndCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_bottom_comment_and_rating, this);
        ButterKnife.bind(this);
        // initListener();
        initAttr(context, attrs);
    }

    public void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomRatingAndCommentView);
            boolean showRating = typedArray.getBoolean(R.styleable.BottomRatingAndCommentView_showRating, true);
            boolean showComment = typedArray.getBoolean(R.styleable.BottomRatingAndCommentView_showComment, true);
            boolean showShare = typedArray.getBoolean(R.styleable.BottomRatingAndCommentView_showShare, true);
            typedArray.recycle();
            setContent(showRating, showComment, showShare);
        }
    }

    public void setContent(boolean isRating, boolean isComment, boolean isShare) {
        int count = 0;
        if (isRating) {
            count++;
            mRatingLayout.setVisibility(VISIBLE);
        } else {
            mRatingLayout.setVisibility(GONE);
        }
        if (isComment) {
            count++;
            mCommentLayout.setVisibility(VISIBLE);
        } else {
            mCommentLayout.setVisibility(GONE);
        }
        if (isShare) {
            count++;
            mShareLayout.setVisibility(VISIBLE);
        } else {
            mShareLayout.setVisibility(GONE);
        }
        if (count == 2) {
            mDividerView1.setVisibility(GONE);
        } else if (count == 1) {
            mDividerView1.setVisibility(GONE);
            mDividerView2.setVisibility(GONE);
        }
    }

    public void setRatingClickListener(OnClickListener listener) {
        mRatingLayout.setOnClickListener(listener);
    }

    public void setCommentClickListener(OnClickListener listener) {
        mCommentLayout.setOnClickListener(listener);
    }

    public void setShareClickListener(OnClickListener listener) {
        mShareLayout.setOnClickListener(listener);
    }

    public void setData(int ratingNumber, int commentNumber) {
        setRatingData(ratingNumber);
        setCommentData(commentNumber);
    }

    public void setRatingData(int ratingNumber) {
        mRatingNumberTextView.setText(String.format("(%d)", ratingNumber));
    }

    public void setIsRated(boolean isRated) {
        if (!isRated) {
            mRatingImageView.setImageResource(R.drawable.icon_bottom_rating);
        } else {
            mRatingImageView.setImageResource(R.drawable.icon_bottom_rated);
        }
    }

    public void setCommentData(int commentNumber) {
        mCommentNumberTextView.setText(String.format("(%d)", commentNumber));
    }
}
