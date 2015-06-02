package com.badou.mworking.widget;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.badou.mworking.CommentActivity;
import com.badou.mworking.R;
import com.badou.mworking.TrainActivity;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/2.
 */
public class BottomRatingAndCommentView extends LinearLayout {

    private Context mContext;
    private String mRid;
    private int mCurrentScore;
    private LinearLayout mRatingLayout;
    private LinearLayout mCommentLayout;
    private TextView mRatingNumberTextView;
    private TextView mCommentNumberTextView;

    public BottomRatingAndCommentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_bottom_comment_and_rating, this);
        initView();
        initListener();
    }

    private void initView() {
        mRatingLayout = (LinearLayout) findViewById(R.id.ll_bottom_rating);
        mCommentLayout = (LinearLayout) findViewById(R.id.ll_bottom_comment);
        mRatingNumberTextView = (TextView) findViewById(R.id.tv_bottom_rating_number);
        mCommentNumberTextView = (TextView) findViewById(R.id.tv_bottom_comment_number);
    }

    private void initListener() {
        mRatingLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new RatingDilog(mContext, mRid, mCurrentScore, new RatingDilog.OnRatingCompletedListener() {

                    @Override
                    public void onRatingCompleted(int score) {
                        mCurrentScore = score;
                        updateData();
                        /*Intent intent = new Intent();
                        intent.putExtra(TrainActivity.KEY_RATING, score);
                        intent.putExtra(TrainActivity.KEY_RID, mCategoryEntity.rid);
                        ((Activity) mContext).setResult(Activity.RESULT_OK, intent);*/
                    }
                }).show();
            }
        });
        mCommentNumberTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // �Ҳ�button ��ת������ҳ��
                Intent intent = new Intent(mContext,
                        CommentActivity.class);
                intent.putExtra(CommentActivity.VALUE_RID, mRid);
                mContext.startActivity(intent);
            }
        });
    }

    public void setData(String rid, int ratingNumber, int commentNumber, int currentRating) {
        this.mRid = rid;
        setData(ratingNumber, commentNumber, currentRating);
    }

    public void setData(int ratingNumber, int commentNumber, int currentRating) {
        this.mCurrentScore = currentRating;
        if (ratingNumber > -1) {
            mRatingLayout.setVisibility(VISIBLE);
            mRatingNumberTextView.setText(String.format("(%d)", ratingNumber));
        } else {
            mRatingLayout.setVisibility(GONE);
        }
        if (commentNumber > -1) {
            mCommentLayout.setVisibility(VISIBLE);
            mCommentNumberTextView.setText(String.format("(%d)", commentNumber));
        } else {
            mCommentLayout.setVisibility(GONE);
        }
    }

    public void updateData() {
        ServiceProvider.getResourceDetail(mContext, mRid, new VolleyListener(mContext) {

            @Override
            public void onResponseData(JSONObject jsonObject) {
                CategoryDetail categoryDetail = new CategoryDetail(jsonObject);
                setData(categoryDetail.ratingNum, categoryDetail.commentNum, categoryDetail.rating);
            }
        });
    }

}