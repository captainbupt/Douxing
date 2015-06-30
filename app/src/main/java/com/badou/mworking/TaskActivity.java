package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Task;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

/**
 * 任务签到界面
 */
public class TaskActivity extends BaseCategoryProgressListActivity {

    private int mClickPosition = -1;
    private final int REQUEST_TASK_SIGN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Category.CATEGORY_KEY_NAMES[Category.CATEGORY_TASK];
        CATEGORY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[Category.CATEGORY_TASK];
        super.onCreate(savedInstanceState);
        mNoneResultView.setContent(R.drawable.background_none_result_task, R.string.none_result_category);
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new TaskAdapter(mContext);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Task(mContext, jsonObject);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_TASK_SIGN) {
            if (mCategoryAdapter != null && mClickPosition > -1 && mClickPosition < mCategoryAdapter.getCount()) {
                Task task = (Task) data.getSerializableExtra(TaskSignActivity.RESPONSE_TASK);
                if (!task.isAvailable()) { // 之前可签到，之后不可签到，则签到成功，数量减1
                    setRead(mClickPosition);
                }
                mCategoryAdapter.setItem(mClickPosition, task);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

