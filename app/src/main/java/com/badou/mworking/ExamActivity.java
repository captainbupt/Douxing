package com.badou.mworking;

import android.os.Bundle;

import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.model.category.Exam;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * ExamActivity 考试页面
 */
public class ExamActivity extends BaseCategoryProgressListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Exam.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Exam.CATEGORY_KEY_UNREAD_NUM;
        super.onCreate(savedInstanceState);
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

/* 遗留功能，暂不需要
   @Override
    public void clickRight() {
        // tag 值大于 0 ，  代表在线考试，点击跳入搜索，    tag<0, 代表 等级考试， 点击跳入等级考试页面，  tag = 0 表示全部
        if (tag >= 0) {
        } else {
            Intent inten = new Intent(mContext, MyRatingActivity.class);
            startActivity(inten);
        }
    }*/
}

