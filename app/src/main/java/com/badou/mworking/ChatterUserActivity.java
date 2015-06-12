package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.ChatterListAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.model.user.UserChatterInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.MyScrollListenerScrollView;
import com.badou.mworking.widget.NoScrollListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.RelativeLayout;
import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 我的圈页面
 */
public class ChatterUserActivity extends BaseNoTitleActivity {

    public static final String KEY_UID = "uid";
    public static final String KEY_USER_CHATTER = "user";
    public static final int REQUEST_CHATTER_DETAIL = 11;

    private String mUid;
    private UserChatterInfo mUserInfo;

    private ImageView mBackImageView;
    private TextView mTitleTextView;
    private ImageView mHeadImageView;
    private TextView mLevelTextView;
    private TextView mNameTextView;
    private NoScrollListView mContentListView;
    private RelativeLayout mTitleLayout;
    private ImageView mHideBackImageView;
    private TextView mHideTitleTextView;
    private MyScrollListenerScrollView mScrollView;
    private ChatterListAdapter mChatterAdapter;
    private int mCurrentPage;
    private int mClickPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_user);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        mBackImageView = (ImageView) findViewById(R.id.iv_chatter_user_top_back);
        mTitleTextView = (TextView) findViewById(R.id.tv_chatter_user_top_title);
        mHeadImageView = (ImageView) findViewById(R.id.iv_chatter_user_top_head);
        mLevelTextView = (TextView) findViewById(R.id.tv_chatter_user_top_level);
        mNameTextView = (TextView) findViewById(R.id.tv_chatter_user_top_name);
        mContentListView = (NoScrollListView) findViewById(R.id.nslv_chatter_user_content);
        mTitleLayout = (RelativeLayout) findViewById(R.id.rl_chatter_user_title);
        mHideBackImageView = (ImageView) findViewById(R.id.iv_actionbar_left);
        mHideTitleTextView = (TextView) findViewById(R.id.tv_actionbar_title);
        mScrollView = (MyScrollListenerScrollView) findViewById(R.id.ptrsv_chatter_user_content);
    }

    private void initListener() {
        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mHideBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mScrollView.setOnScrollChangedListener(new MyScrollListenerScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {

            }

            @Override
            public void onScrollStopped() {

            }
        });

        mScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                updateData(mCurrentPage);
            }
        });
        mChatterAdapter = new ChatterListAdapter(mContext, new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickPosition = position;
                // 跳转到单条的Item的页面，并传递数据
                Chatter chatter = (Chatter) mChatterAdapter.getItem(position);
                Intent intent = new Intent(mContext, ChatterDetailActivity.class);
                intent.putExtra(ChatterDetailActivity.KEY_CHATTER, chatter);
                startActivityForResult(intent, REQUEST_CHATTER_DETAIL);
            }
        }, false);
        mContentListView.setAdapter(mChatterAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mClickPosition >= 0 && mClickPosition < mChatterAdapter.getCount()) {
            if (resultCode == ChatterDetailActivity.RESULT_REPLY) {
                Chatter chatter = (Chatter) mChatterAdapter.getItem(mClickPosition);
                chatter.replyNumber = (data.getIntExtra(ChatterDetailActivity.RESULT_KEY_COUNT, 0));
                mChatterAdapter.setItem(mClickPosition, chatter);
            } else if (resultCode == ChatterDetailActivity.RESULT_DELETE) {
                mChatterAdapter.remove(mClickPosition);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initData() {
        String selfUid = ((AppApplication) getApplication()).getUserInfo().userId;
        mUid = mReceivedIntent.getStringExtra(KEY_UID);
        if (mUid.equals(selfUid)) {
            mTitleTextView.setText(R.string.chatter_user_title_myself);
            mHideTitleTextView.setText(R.string.chatter_user_title_myself);
        } else {
            mTitleTextView.setText(R.string.chatter_user_title_other);
            mHideTitleTextView.setText(R.string.chatter_user_title_other);
        }
        mUserInfo = (UserChatterInfo) mReceivedIntent.getSerializableExtra(KEY_USER_CHATTER);
        if (mUserInfo != null) {
            System.out.println(mUserInfo.headUrl);
            ImageViewLoader.setCircleImageViewResource(mContext, mHeadImageView, mUserInfo.headUrl, getResources().getDimensionPixelSize(R.dimen.user_center_image_head_size));
            mNameTextView.setText(mUserInfo.name + "\n" + mUserInfo.department);
            LVUtil.setTextViewBg(mLevelTextView, mUserInfo.level);
        }
        mCurrentPage = 1;
        mProgressDialog.show();
        updateData(mCurrentPage);
    }

    private void updateData(final int beginNum) {
        // 发起网络请求
        ServiceProvider.doGetUserChatterList(mContext, "share", mUid, beginNum,
                Constant.LIST_ITEM_NUM, new VolleyListener(mContext) {

                    @Override
                    public void onCompleted() {
                        if (!mActivity.isFinishing())
                            mProgressDialog.dismiss();
                        mScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        JSONObject contentObject = response
                                .optJSONObject(Net.DATA);
                        if (contentObject == null) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        }
                        // 加载到最后时 提示无更新
                        JSONArray resultArray = contentObject
                                .optJSONObject(Net.LIST).optJSONArray(Net.RESULT);
                        if (resultArray == null || resultArray.length() == 0) {
                            if (beginNum > 1) {
                                ToastUtil.showUpdateToast(mContext);
                            } else {
                                mChatterAdapter.setList(null);
                            }
                            return;
                        }
                        mCurrentPage++;
                        // 新加载的内容添加到list
                        List<Object> chatters = new ArrayList<>();
                        if (mUserInfo == null) {
                            mUserInfo = new UserChatterInfo(contentObject);
                            ImageViewLoader.setCircleImageViewResource(mContext, mHeadImageView, mUserInfo.headUrl, getResources().getDimensionPixelSize(R.dimen.user_center_image_head_size));
                            mNameTextView.setText(mUserInfo.name + "\n" + mUserInfo.department);
                            LVUtil.setTextViewBg(mLevelTextView, mUserInfo.level);
                        }
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jo2 = resultArray.optJSONObject(i);
                            Chatter chatter = new Chatter(jo2);
                            chatter.headUrl = mUserInfo.headUrl;
                            chatter.level = mUserInfo.level;
                            chatters.add(chatter);
                        }
                        if (beginNum == 1) {// 页码为1 重新加载第一页
                            final String userNum = ((AppApplication) mContext.getApplicationContext())
                                    .getUserInfo().account;
                            SP.putStringSP(mContext, SP.CHATTER, userNum, resultArray.toString());
                            mChatterAdapter.setList(chatters);
                        } else {// 继续加载
                            mChatterAdapter.addList(chatters);
                        }
                    }
                });
    }
}
