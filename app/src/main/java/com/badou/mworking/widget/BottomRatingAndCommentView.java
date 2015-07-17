package com.badou.mworking.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.CommentActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.presenter.CommentPresenter;

import org.json.JSONObject;

public class BottomRatingAndCommentView extends LinearLayout {

    private LinearLayout mRatingLayout;
    private LinearLayout mCommentLayout;
    private TextView mRatingNumberTextView;
    private TextView mCommentNumberTextView;
    private View mDividerView;

    public BottomRatingAndCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_bottom_comment_and_rating, this);
        initView();
        // initListener();
        initAttr(context, attrs);
    }

    public void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BottomRatingAndCommentView);
            boolean showRating = typedArray.getBoolean(R.styleable.BottomRatingAndCommentView_showRating, true);
            boolean showComment = typedArray.getBoolean(R.styleable.BottomRatingAndCommentView_showComment, true);
            typedArray.recycle();
            setContent(showRating, showComment);
        }
    }

    public void setContent(boolean isRating, boolean isComment) {
        if (isRating) {
            mRatingLayout.setVisibility(VISIBLE);
        } else {
            mRatingLayout.setVisibility(GONE);
            mDividerView.setVisibility(GONE);
        }
        if (isComment) {
            mCommentLayout.setVisibility(VISIBLE);
        } else {
            mCommentLayout.setVisibility(GONE);
            mDividerView.setVisibility(GONE);
        }
    }

    private void initView() {
        mRatingLayout = (LinearLayout) findViewById(R.id.ll_bottom_rating);
        mCommentLayout = (LinearLayout) findViewById(R.id.ll_bottom_comment);
        mRatingNumberTextView = (TextView) findViewById(R.id.tv_bottom_rating_number);
        mCommentNumberTextView = (TextView) findViewById(R.id.tv_bottom_comment_number);
        mDividerView = findViewById(R.id.view_bottom_center_divider);
    }

    public void setRatingClickListener(OnClickListener listener) {
        mRatingLayout.setOnClickListener(listener);
    }

    public void setCommentClickListener(OnClickListener listener) {
        mCommentLayout.setOnClickListener(listener);
    }

    public void setData(int ratingNumber, int commentNumber) {
        setRatingData(ratingNumber);
        setCommentData(commentNumber);
    }

    public void setRatingData(int ratingNumber) {
        mRatingNumberTextView.setText(String.format("(%d)", ratingNumber));
    }

    public void setCommentData(int commentNumber) {
        mCommentNumberTextView.setText(String.format("(%d)", commentNumber));
    }
}
