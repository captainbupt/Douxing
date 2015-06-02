package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.base.BaseProgressListActivity;
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

    public int clickPosition;


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
        clickPosition = position - 1;
        // 获取点中的item所对应的task，并将其作为参数传递给下一个activity
        Task task = (Task) mCategoryAdapter.getItem(position - 1);
        int subtype = task.subtype;
        if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(SignActivity.INTENT_TASK, task);
        Intent intent = new Intent(mContext, SignActivity.class);
        intent.putExtra(SignActivity.INTENT_TASK, bundle);

        // 获取分类名
        String title = task.getClassificationName(mContext);
        intent.putExtra("title", title);
        startActivity(intent);
        //设置切换动画，从右边进入，左边退出
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Task(jsonObject);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (SignActivity.isSignSuccess) {
                SignActivity.isSignSuccess = false;
                if (mCategoryAdapter.getCount() > 0) {
                    ((TaskAdapter) mCategoryAdapter).setRead(clickPosition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10) {
            int pos = data.getIntExtra(Task.TASK_FRAGMENT_ITEM_POSITION, -1);
            if (mCategoryAdapter != null) {
                if (pos > -1 & pos < mCategoryAdapter.getCount()) {
                    mCategoryAdapter.changeItem(pos, data.getBundleExtra(Task.SIGN_BACK_TASK_FRAGMENT).getSerializable(Task.SIGN_BACK_TASK_FRAGMENT));
                }
            }
        }
    }
}

