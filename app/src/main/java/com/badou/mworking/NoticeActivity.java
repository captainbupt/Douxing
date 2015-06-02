package com.badou.mworking;

import android.os.Bundle;

import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.base.BaseProgressListActivity;
import com.badou.mworking.model.Notice;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

/**
 * 功能描述: 通知公告页面
 */
public class NoticeActivity extends BaseProgressListActivity {

    public static final int PROGRESS_CHANGE = 0x1;
    public static final int PROGRESS_FINISH = 0x2;
    public static final int PROGRESS_MAX = 0x3;

    @Override
    protected void onCreate(Bundle arg0) {
        CATEGORY_NAME = Notice.CATEGORY_KEY_NAME;
        CATEGORY_UNREAD_NUM = Notice.CATEGORY_KEY_UNREAD_NUM;
        super.onCreate(arg0);
    }

    @Override
    protected void initAdapter() {
        mCategoryAdapter = new NoticeAdapter(mContext);
    }

    @Override
    protected Object parseObject(JSONObject jsonObject) {
        return new Notice(jsonObject);
    }

    @Override
    protected void onItemClick(int position) {
        Notice notice = (Notice) mCategoryAdapter.getItem(position - 1);
        int type = notice.subtype;
        ((NoticeAdapter) mCategoryAdapter).read(position - 1);
        ServiceProvider.doMarkRead(mContext, notice.rid);
        mCategoryAdapter.notifyDataSetChanged();
        if (!CategoryClickHandler.categoryClicker(mContext, notice)) {
            ToastUtil.showToast(mContext, R.string.category_unsupport_type);
            return;
        }
    }
}
