package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.model.category.Train;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 微培训页面
 */
public class TrainActivity extends BaseCategoryProgressListActivity {

    public static final int PROGRESS_CHANGE = 10;
    public static final int PROGRESS_MAX = 11;
    public static final int PROGRESS_FINISH = 12;
    public static final String KEY_RATING = "rating";
    public static final String KEY_RID = "rid";
    public static final String KEY_TRAINING = "training";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mReceivedIntent = getIntent();
        if (mReceivedIntent.getBooleanExtra(KEY_TRAINING, true)) {
            CATEGORY_NAME = Train.CATEGORY_KEY_NAME;
            CATEGORY_UNREAD_NUM = Train.CATEGORY_KEY_UNREAD_NUM;
        } else {
            CATEGORY_NAME = Category.CATEGORY_KEY_NAMES[Category.CATEGORY_SHELF];
            CATEGORY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[Category.CATEGORY_SHELF];
        }
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new TrainAdapter(mContext);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Train(jsonObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int rating = data.getIntExtra(KEY_RATING, 0);
            String rid = data.getStringExtra(KEY_RID);
            ((TrainAdapter) mCategoryAdapter).updateRating(rid, rating);
        }
    }

    @Override
    public void onItemClick(int position) {
        Train train = (Train) mCategoryAdapter.getItem(position - 1);

        if (NetUtils.isNetConnected(mContext)) {
            CategoryClickHandler.categoryClicker(mContext, new CategoryDetail(mContext, train));
            // 向服务提交课件信息
            ((TrainAdapter) mCategoryAdapter).read(position - 1);
            mCategoryAdapter.notifyDataSetChanged();
        } else {
            ToastUtil.showNetExc(mContext);
        }

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
