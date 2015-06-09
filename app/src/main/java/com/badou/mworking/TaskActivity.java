package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.model.category.Task;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

/**
 *  任务签到界面
 */
public class TaskActivity extends BaseCategoryProgressListActivity {

    private int mClickPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CATEGORY_NAME = Task.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Task.CATEGORY_KEY_UNREAD_NUM;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new TaskAdapter(mContext);
    }

    @Override
    protected void onItemClick(int position) {
        mClickPosition = position - 1;
        // 获取点中的item所对应的task，并将其作为参数传递给下一个activity
        Task task = (Task) mCategoryAdapter.getItem(position - 1);
        if (!NetUtils.isNetConnected(mContext)) {
            ToastUtil.showNetExc(mContext);
            return;
        } else {
            CategoryClickHandler.categoryClicker(mContext, task);
        }
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Task(jsonObject);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (mCategoryAdapter != null) {
                if (mClickPosition > -1 & mClickPosition < mCategoryAdapter.getCount()) {
                    ((TaskAdapter) mCategoryAdapter).setRead(mClickPosition);
                }
            }
        }
    }
}

