package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;

import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.base.BaseCategoryProgressListActivity;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.util.CategoryClickHandler;

import org.json.JSONObject;

/**
 * 功能描述: 通知公告页面
 */
public class NoticeActivity extends BaseBackActionBarActivity {
/*
    @Override
    protected void onCreate(Bundle arg0) {
        CATEGORY_NAME = Category.CATEGORY_KEY_NAMES[Category.CATEGORY_NOTICE];
        CATEGORY_UNREAD_NUM = Category.CATEGORY_KEY_UNREADS[Category.CATEGORY_NOTICE];
        super.onCreate(arg0);
        mNoneResultView.setContent(R.drawable.background_none_result_notice, R.string.none_result_category);
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new NoticeAdapter(mContext);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Notice(mContext,jsonObject);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_DETAIL && resultCode == RESULT_OK) {
            if (mClickPosition >= 0 && mClickPosition < mCategoryAdapter.getCount()) {
                Notice notice = (Notice) data.getSerializableExtra(NoticeBaseActivity.RESPONSE_NOTICE);
                if (!notice.isAvailable()) {
                    setRead(mClickPosition);
                }
                mCategoryAdapter.setItem(mClickPosition, notice);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/
}
