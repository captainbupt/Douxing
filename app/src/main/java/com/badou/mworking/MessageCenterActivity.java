package com.badou.mworking;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.adapter.MessageCenterAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.util.CategoryIntentFactory;
import com.badou.mworking.util.ResourceClickHandler;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoneResultView;

public class MessageCenterActivity extends BaseBackActionBarActivity {

    private ListView mContentListView;
    private MessageCenterAdapter mContentAdapter;
    private NoneResultView mNoneResultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        setActionbarTitle(R.string.title_name_message_center);
        initData();
    }

    private void initData() {
        mContentListView = (ListView) findViewById(R.id.lv_activity_message_center);
        mNoneResultView = (NoneResultView) findViewById(R.id.nrv_activity_message_center);
        // 点击事件已经集成到adapter中
        mContentAdapter = new MessageCenterAdapter(mContext);
        mContentAdapter.setList(MessageCenterResManager.getAllItem(mContext));
        mContentListView.setAdapter(mContentAdapter);
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toDetailPage(mContext, i, (MessageCenter) mContentAdapter.getItem(i));
            }
        });
        if (mContentAdapter.getCount() <= 0) {
            mNoneResultView.setVisibility(View.VISIBLE);
        } else {
            mNoneResultView.setVisibility(View.GONE);
        }
        mContentAdapter.setOnEmptyListener(new MessageCenterAdapter.OnEmptyListener() {
            @Override
            public void onEmpty() {
                mNoneResultView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void toDetailPage(final Context context, final int position, final MessageCenter messageCenter) {
        mProgressDialog.show();
        ResourceClickHandler.OnCompleteListener onCompleteListener = new ResourceClickHandler.OnCompleteListener() {
            @Override
            public void onComplete(boolean isSuccess) {
                mProgressDialog.dismiss();
                mContentAdapter.deleteItem(position);
                if (!isSuccess) {
                    ToastUtil.showToast(mContext, R.string.tip_message_center_resource_gone);
                }
            }
        };
        if (messageCenter.type.equals(MessageCenter.TYPE_NOTICE) || messageCenter.type.equals(MessageCenter.TYPE_EXAM)
                || messageCenter.type.equals(MessageCenter.TYPE_TRAINING) || messageCenter.type.equals(MessageCenter.TYPE_TASK)
                || messageCenter.type.equals(MessageCenter.TYPE_SHELF) || messageCenter.type.equals(MessageCenter.TYPE_ENTRY)) {
            mProgressDialog.dismiss();
            mContentAdapter.deleteItem(position);
            startActivity(CategoryIntentFactory.getIntent(context, messageCenter.getCategoryType(), messageCenter.add, true));
        } else if (messageCenter.type.equals(MessageCenter.TYPE_CHATTER)) {
            ResourceClickHandler.toChatterPage(context, messageCenter.add, onCompleteListener);
        } else if (messageCenter.type.equals(MessageCenter.TYPE_ASK)) {
            ResourceClickHandler.toAskPage(context, messageCenter.add, onCompleteListener);
        } /*else if (messageCenter.type.equals(MessageCenter.TYPE_CHAT)) {
            ResourceClickHandler.toChatPage(context, onCompleteListener);
        }*/ else {
            ToastUtil.showToast(mContext, R.string.category_unsupport_type);
            mProgressDialog.dismiss();
        }
    }
}
