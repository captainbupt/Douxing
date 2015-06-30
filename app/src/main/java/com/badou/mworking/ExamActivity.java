package com.badou.mworking;

import android.os.Bundle;

import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * ExamActivity 考试页面
 */
public class ExamActivity extends BaseCategoryProgressListActivity {

    private int mClickPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Exam.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Exam.CATEGORY_KEY_UNREAD_NUM;
        super.onCreate(savedInstanceState);
        mNoneResultView.setContent(R.drawable.background_none_result_exam, R.string.none_result_category);
        try {
            // 如果8点提醒点击进入的话，这里会报空，应为极光推送没有收到内容，在这里做个异常捕获
            String JPushBundle = getIntent().getExtras().getString(JPushInterface.EXTRA_EXTRA);
            if (JPushBundle != null) {
                JSONObject extraJson = new JSONObject(JPushBundle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new ExamAdapter(mContext);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Exam(jsonObject);
    }

    @Override
    protected void onItemClick(int position) {
        mClickPosition = position - 1;
        super.onItemClick(position);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mClickPosition >= 0 && mClickPosition < mCategoryAdapter.getCount()) {
            updateExam(mClickPosition);
        }
    }

    private void updateExam(final int position) {
        showProgressBar();
        ServiceProvider.doUpdateLocalResource2(mContext, Exam.CATEGORY_KEY_NAME, tag, position , 1, "", null,
                new VolleyListener(mContext) {

                    @Override
                    public void onCompleted() {
                        hideProgressBar();
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        JSONObject data = null;
                        try {
                            data = response.optJSONObject(Net.DATA).optJSONArray("list").optJSONObject(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (data != null) {
                            Exam exam = new Exam(data);
                            mCategoryAdapter.setItem(position, exam);
                        }
                    }
                });
    }
}

