package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * ExamActivity 考试页面
 */
public class ExamActivity extends BaseBackActionBarActivity {

/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Category.CATEGORY_KEY_NAMES[Category.CATEGORY_EXAM];
        CATEGORY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[Category.CATEGORY_EXAM];
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
        return new Exam(mContext, jsonObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DETAIL && resultCode == RESULT_OK) {
            if (mClickPosition >= 0 && mClickPosition < mCategoryAdapter.getCount()) {
                updateExam(mClickPosition);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateExam(final int position) {
        showProgressBar();
        ServiceProvider.doUpdateLocalResource2(mContext, CATEGORY_NAME, tag, position, 1, "", null,
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
                            Exam exam = new Exam(mContext, data);
                            if (!exam.isAvailable())
                                setRead(position);
                            mCategoryAdapter.setItem(position, exam);
                        }
                    }
                });
    }*/
}

