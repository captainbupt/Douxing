package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseProgressListActivity;
import com.badou.mworking.base.BaseStatisticalActionBarActivity;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.Task;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;

import org.json.JSONObject;

/**
 * @author gejianfeng
 *         任务签到界面
 */
public class TaskActivity extends BaseProgressListActivity {

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
        mClickPosition = position -1;
        // 获取点中的item所对应的task，并将其作为参数传递给下一个activity
        Task task = (Task) mCategoryAdapter.getItem(position - 1);
        int subtype = task.subtype;
        if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
            return;
        }
        Intent intent = new Intent(mContext, SignActivity.class);
        intent.putExtra(SignActivity.KEY_TASK, task);
        // 获取分类名
        intent.putExtra(BaseActionBarActivity.KEY_TITLE, task.getClassificationName(mContext));
        intent.putExtra(BaseStatisticalActionBarActivity.KEY_RID, task.rid);
        startActivity(intent);
        //设置切换动画，从右边进入，左边退出
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

