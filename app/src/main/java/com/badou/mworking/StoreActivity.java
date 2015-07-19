package com.badou.mworking;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.adapter.StoreAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryIntentFactory;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.ResourceClickHandler;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoreActivity extends BaseBackActionBarActivity {

    @Bind(R.id.content_list_view)
    PullToRefreshListView mContentListView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;

    private int mCurrentIndex = 1;
    private StoreAdapter mStoreAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(R.string.user_center_my_store);
        setContentView(R.layout.activity_store);
        ButterKnife.bind(this);
        initListener();
        setItemFromCache();
        mContentListView.setRefreshing();
    }

    private void initListener() {
        mStoreAdapter = new StoreAdapter(mContext);
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mProgressDialog.show();
                Store store = (Store) mStoreAdapter.getItem(i - 1);
                ResourceClickHandler.OnCompleteListener onCompleteListener = new ResourceClickHandler.OnCompleteListener() {
                    @Override
                    public void onComplete(boolean isSuccess) {
                        mProgressDialog.dismiss();
                        if (!isSuccess) {
                            ToastUtil.showToast(mContext, R.string.tip_message_center_resource_gone);
                        }
                    }
                };
                if (store.type == Store.TYPE_NOTICE || store.type == Store.TYPE_TRAINING || store.type == Store.TYPE_EXAM || store.type == Store.TYPE_TASK || store.type == Store.TYPE_SHELF) {
                    mProgressDialog.dismiss();
                    startActivity(CategoryIntentFactory.getIntent(mContext, Store.getCategoryTypeFromStore(store.type), store.sid));
                } else if (store.type == Store.TYPE_CHATTER) {
                    ResourceClickHandler.toChatterPage(mContext, store.sid, onCompleteListener);
                } else if (store.type == Store.TYPE_ASK) {
                    ResourceClickHandler.toAskPage(mContext, store.sid, onCompleteListener);
                } else {
                    ToastUtil.showToast(mContext, R.string.category_unsupport_type);
                    mProgressDialog.dismiss();
                }
            }
        });
        mContentListView.setAdapter(mStoreAdapter);
        mContentListView.setMode(PullToRefreshBase.Mode.BOTH);
        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentIndex = 1;
                updateData(mCurrentIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                updateData(mCurrentIndex);
            }
        });
    }

    private void updateData(final int index) {
        showProgressBar();
        ServiceProvider.getStore(mContext, index, Constant.LIST_ITEM_NUM, new VolleyListener(mContext) {

            @Override
            public void onCompleted() {
                hideProgressBar();
                mContentListView.onRefreshComplete();
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                final String userNum = UserInfo.getUserInfo().getAccount();
                List<Object> list = new ArrayList<>();
                JSONArray resultArray = response.optJSONArray(Net.DATA);
                if (resultArray == null
                        || resultArray.length() == 0) {
                    if (index > 1) {
                        ToastUtil.showToast(mContext, R.string.no_more);
                    } else {
                        mNoneResultView.setVisibility(View.VISIBLE);
                        mStoreAdapter.setList(null);
                    }
                    return;
                }
                mCurrentIndex++;
                mNoneResultView.setVisibility(View.GONE);
                //添加缓存
                if (index == 1) {
                    //添加缓存
                    SP.putStringSP(mContext, SP.DEFAULTCACHE, userNum + "store", resultArray.toString());
                }
                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject jsonObject = resultArray
                            .optJSONObject(i);
                    list.add(new Store(mContext, jsonObject));
                }
                if (index == 1) {
                    mStoreAdapter.setList(list);
                } else {
                    mStoreAdapter.addList(list);
                }
            }
        });
    }

    /**
     * 功能描述:  获取缓存
     */
    public void setItemFromCache() {
        List<Object> list = new ArrayList<>();
        String userNum = UserInfo.getUserInfo().getAccount();
        String sp = SP.getStringSP(mContext, SP.DEFAULTCACHE, userNum + "store", "");
        if (TextUtils.isEmpty(sp)) {
            mNoneResultView.setVisibility(View.VISIBLE);
            return;
        } else {
            mNoneResultView.setVisibility(View.GONE);
        }
        JSONArray resultArray;
        try {
            resultArray = new JSONArray(sp);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.optJSONObject(i);
                list.add(new Store(mContext, jsonObject));
            }
            mStoreAdapter.setList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
