package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.Notice;
import com.badou.mworking.model.category.Train;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 微培训页面
 */
public class TrainActivity extends BaseCategoryProgressListActivity {

    public static final String KEY_IS_TRAINING = "training";
    private boolean isTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mReceivedIntent = getIntent();
        isTraining = mReceivedIntent.getBooleanExtra(KEY_IS_TRAINING, true);
        if (mReceivedIntent.getBooleanExtra(KEY_IS_TRAINING, true)) {
            CATEGORY_NAME = Category.CATEGORY_KEY_NAMES[Category.CATEGORY_TRAINING];
            CATEGORY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[Category.CATEGORY_TRAINING];
        } else {
            CATEGORY_NAME = Category.CATEGORY_KEY_NAMES[Category.CATEGORY_SHELF];
            CATEGORY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[Category.CATEGORY_SHELF];
        }
        super.onCreate(savedInstanceState);
        mNoneResultView.setContent(R.drawable.background_none_result_training, R.string.none_result_category);
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new TrainAdapter(mContext);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Train(mContext, jsonObject, isTraining);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DETAIL && resultCode == RESULT_OK) {
            if (mClickPosition >= 0 && mClickPosition < mCategoryAdapter.getCount()) {
                Train train = (Train) data.getSerializableExtra(TrainBaseActivity.RESPONSE_TRAINING);
                if (!train.isAvailable()) {
                    setRead(mClickPosition);
                }
                mCategoryAdapter.setItem(mClickPosition, train);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void updateCompleted() {
        // 通过网络获取课件点赞数量的list
        String[] rids = new String[mCategoryAdapter.getCount()];
        for (int i = 0; i < mCategoryAdapter.getCount(); i++) {
            rids[i] = ((Train) mCategoryAdapter.getItem(i)).rid;
        }
        // 获取资源的点赞数／评论数／评分
        ServiceProvider.doUpdateFeedbackCount(mContext, rids, new VolleyListener(
                mContext) {

            @Override
            public void onCompleted() {
                mContentListView.onRefreshComplete();
                hideProgressBar();
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                JSONArray resultArray = response
                        .optJSONArray(Net.DATA);
                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject jsonObject = resultArray.optJSONObject(i);
                    String rid = jsonObject.optString(ResponseParameters.CATEGORY_RID);
                    int feedbackCount = jsonObject
                            .optInt(ResponseParameters.RATING_NUM);
                    int comment = jsonObject
                            .optInt(ResponseParameters.COMMENT_NUM);
                    int ecnt = jsonObject
                            .optInt(ResponseParameters.ECNT); //评分人数
                    int eval = jsonObject
                            .optInt(ResponseParameters.EVAL); //评分总分
                    for (int j = 0; j < mCategoryAdapter.getCount(); j++) {
                        Train t = (Train) mCategoryAdapter.getItem(j);
                        if (rid.equals(t.rid)) {
                            t.commentNum = comment;
                            t.ecnt = ecnt;
                            t.eval = eval;
                            mCategoryAdapter.setItem(j, t);
                        }
                    }
                }
            }

        });
    }
}
