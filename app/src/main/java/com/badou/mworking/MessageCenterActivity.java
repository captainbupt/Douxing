package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.adapter.MessageCenterAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.Chatter;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoneResultView;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/15.
 */
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
        mNoneResultView.setContent(R.drawable.background_none_result_notice, R.string.none_result_message_center);
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
        if (messageCenter.type.equals(MessageCenter.TYPE_NOTICE) || messageCenter.type.equals(MessageCenter.TYPE_EXAM)
                || messageCenter.type.equals(MessageCenter.TYPE_TRAINING) || messageCenter.type.equals(MessageCenter.TYPE_TASK)
                || messageCenter.type.equals(MessageCenter.TYPE_SHELF)) {
            ServiceProvider.getResourceDetail(context, messageCenter.add, new VolleyListener(context) {
                @Override
                public void onResponseSuccess(JSONObject jsonObject) {
                    mProgressDialog.dismiss();
                    CategoryDetail detail = new CategoryDetail(context, jsonObject.optJSONObject(Net.DATA), messageCenter.getCategoryType(), messageCenter.add, messageCenter.description, null);
                    CategoryClickHandler.categoryClicker(mContext, detail);
                    mContentAdapter.deleteItem(position);
                }

                @Override
                public void onErrorCode(int code) {
                    showErrorResponse(position);
                }
            });
        } else if (messageCenter.type.equals(MessageCenter.TYPE_CHATTER)) {
            ServiceProvider.doGetChatterById(context, messageCenter.add, new VolleyListener(context) {
                @Override
                public void onResponseSuccess(JSONObject response) {
                    mProgressDialog.dismiss();
                    Chatter chatter = new Chatter(response.optJSONObject(Net.DATA));
                    Intent intent = new Intent(mContext, ChatterDetailActivity.class);
                    intent.putExtra(ChatterDetailActivity.KEY_CHATTER, chatter);
                    context.startActivity(intent);
                    mContentAdapter.deleteItem(position);
                }

                @Override
                public void onErrorCode(int code) {
                    showErrorResponse(position);
                }

            });
        } else if (messageCenter.type.equals(MessageCenter.TYPE_ASK)) {
            ServiceProvider.doGetAskById(context, messageCenter.add, new VolleyListener(context) {
                @Override
                public void onResponseSuccess(JSONObject response) {
                    mProgressDialog.dismiss();
                    Ask ask = new Ask(response.optJSONObject(Net.DATA));
                    Intent intent = new Intent(mContext, AskDetailActivity.class);
                    intent.putExtra(AskDetailActivity.KEY_ASK, ask);
                    context.startActivity(intent);
                    mContentAdapter.deleteItem(position);
                }

                @Override
                public void onErrorCode(int code) {
                    showErrorResponse(position);
                }
            });
        } else {
            ToastUtil.showToast(mContext, R.string.category_unsupport_type);
            mProgressDialog.dismiss();
        }
    }

    private void showErrorResponse(int position) {
        mContentAdapter.deleteItem(position);
        ToastUtil.showToast(mContext, R.string.tip_message_center_resource_gone);
        mProgressDialog.dismiss();
    }
}
