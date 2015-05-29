package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseProgressListActivity;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.HorizontalProgressDialog;

import org.holoeverywhere.app.Activity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * @author gejianfeng
 *         微培训页面
 */
public class TrainActivity extends BaseProgressListActivity {

    public static final int PROGRESS_CHANGE = 10;
    public static final int PROGRESS_MAX = 11;
    public static final int PROGRESS_FINISH = 12;
    public static final String KEY_RATING = "rating";
    public static final String KEY_RID = "rid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Train.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Train.CATEGORY_KEY_UNREAD_NUM;
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

        if (!CategoryClickHandler.categoryClicker(mContext, train)) {
            ToastUtil.showToast(mContext, R.string.train_unsupport_type);
            return;
        }

        if (NetUtils.isNetConnected(mContext)) {
            // 向服务提交课件信息
            ((TrainAdapter) mCategoryAdapter).read(position - 1);
            ServiceProvider.doMarkRead(mContext, train.rid);
            mCategoryAdapter.notifyDataSetChanged();
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
            public void onResponse(Object responseObject) {
                JSONObject response = (JSONObject) responseObject;
                try {
                    int code = response.optInt(Net.CODE);
                    if (code != Net.SUCCESS) {
                        return;
                    }
                    JSONArray resultArray = response
                            .optJSONArray(Net.DATA);
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject jsonObject = resultArray.optJSONObject(i);
                        String rid = jsonObject.optString(ResponseParams.CATEGORY_RID);
/*                        int feedbackCount = jsonObject
                                .optInt(ResponseParams.RATING_NUM);
                        int comment = jsonObject
                                .optInt(ResponseParams.COMMENT_NUM);*/
                        int ecnt = jsonObject
                                .optInt(ResponseParams.ECNT); //评分人数
                        int eval = jsonObject
                                .optInt(ResponseParams.EVAL); //评分总分
                        for (int j = 0; j < mCategoryAdapter.getCount(); j++) {
                            Train t = (Train) mCategoryAdapter.getItem(j);
                            if (rid.equals(t.rid)) {
/*                                t.commentNum = comment;
                                t.feedbackCount = feedbackCount;*/
                                t.ecnt = ecnt;
                                t.eval = eval;
                            }
                        }
                    }
                    mCategoryAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    pullToRefreshListView.onRefreshComplete();
                    hideProgressBar();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                pullToRefreshListView.onRefreshComplete();
                hideProgressBar();
            }

        });
    }
}
