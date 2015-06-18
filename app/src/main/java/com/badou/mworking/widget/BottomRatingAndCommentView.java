package com.badou.mworking.widget;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.CommentActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

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
    private View mDividerView;

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
        mDividerView = findViewById(R.id.view_bottom_center_divider);
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
        mCommentLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,
                        CommentActivity.class);
                intent.putExtra(CommentActivity.KEY_RID, mRid);
                intent.putExtra(BaseActionBarActivity.KEY_TITLE, mContext.getResources().getString(R.string.title_name_comment));
                mContext.startActivity(intent);
            }
        });
    }

    public void setData(String rid, boolean isRating, boolean isComment) {
        this.mRid = rid;
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
        setData(0, 0, 0);
        updateData();
    }

    public void setData(int ratingNumber, int commentNumber, int currentRating) {
        this.mCurrentScore = currentRating;
        mRatingNumberTextView.setText(String.format("(%d)", ratingNumber));
        mCommentNumberTextView.setText(String.format("(%d)", commentNumber));
    }

    public void updateData() {
        if (TextUtils.isEmpty(mRid))
            return;
        final WaitProgressDialog progressDialog = new WaitProgressDialog(mContext);
        progressDialog.show();
        ServiceProvider.getResourceDetail(mContext, mRid, new VolleyListener(mContext) {

            @Override
            public void onResponseSuccess(JSONObject jsonObject) {
                CategoryDetail categoryDetail = new CategoryDetail(mContext, jsonObject.optJSONObject(Net.DATA));
                setData(categoryDetail.ratingNum, categoryDetail.commentNum, categoryDetail.rating);
                progressDialog.dismiss();
            }
        });
    }

}
