package com.badou.mworking;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.AskAnswerAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.listener.FullImageListener;
import com.badou.mworking.model.Ask;
import com.badou.mworking.model.WenDaAnswer;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.DialogUtil;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoScrollListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

/**
 * 问答详情页面
 */
public class AskDetailActivity extends BaseBackActionBarActivity {

    public static final String KEY_ASK = "ask";

    public static final int RESULT_DELETED = 1001;
    public static final int RESULT_REPLIED = 1002;

    private Ask mAsk;

    private AskAnswerAdapter mAnswerAdapter;

    private TextView mSubjectTextView;
    private TextView mDateTextView;
    private TextView mContentTextView;
    private ImageView mContentImageView;// title显示的图片
    private ImageView mHeadImageView;
    private TextView mNameTextView;
    private TextView mMessageTextView;
    private TextView mDeleteTextView;
    private NoScrollListView mAnswerListView;
    private LinearLayout mBottomReplyLayout;  //回复
    private ImageView mNoneAnswerImageView;  // 没有回答时显示的布局

    private PullToRefreshScrollView pullToRefreshScrollView;

    private int beginIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle("问答详情");
        mAsk = (Ask) mReceivedIntent.getSerializableExtra(KEY_ASK);
        setContentView(R.layout.activity_ask_detail);
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化
     */
    private void initView() {
        mSubjectTextView = (TextView) findViewById(R.id.tv_activity_ask_detail_subject);
        mDateTextView = (TextView) findViewById(R.id.tv_activity_ask_detail_time);
        mContentTextView = (TextView) findViewById(R.id.tv_activity_ask_detail_content);
        mNameTextView = (TextView) findViewById(R.id.tv_activity_ask_detail_name);
        mMessageTextView = (TextView) findViewById(R.id.tv_activity_ask_detail_message);
        mDeleteTextView = (TextView) findViewById(R.id.tv_activity_ask_detail_delete);
        mNoneAnswerImageView = (ImageView) findViewById(R.id.iv_activity_ask_detail_none_answer);
        mHeadImageView = (ImageView) findViewById(R.id.iv_activity_ask_detail_user_head);
        mContentImageView = (ImageView) findViewById(R.id.iv_activity_ask_detail_content);
        // 自定义LinearLayout
        mAnswerListView = (NoScrollListView) findViewById(R.id.nslv_activity_ask_detail_answer);
        mBottomReplyLayout = (LinearLayout) findViewById(R.id.ll_activity_ask_detail_bottom_comment);
    }

    /**
     * 功能描述:发送回复TextView设置监听,pullToRefreshScrollView设置下拉刷新监听
     */
    private void initListener() {

        // 点击图片放大显示
        mContentImageView.setOnClickListener(new FullImageListener(mContext, mAsk.contentImageUrl));

        pullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.ptrsv_activity_ask_detail);
        pullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                beginIndex = 1;
                updateListView(beginIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                updateListView(beginIndex);
            }
        });

        mBottomReplyLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, WenDaAnswerActivity.class);
                intent.putExtra("aid", mAsk.aid);
                startActivity(intent);
            }
        });

        mDeleteTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.showDeleteDialog(mContext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAsk();
                    }
                });
            }
        });

        mMessageTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(mContext, ChattingActivity.class);
                intent2.putExtra(BaseActionBarActivity.KEY_TITLE, mAsk.userName);
                intent2.putExtra(ChattingActivity.KEY_WHOM, mAsk.whom);
                intent2.putExtra(ChattingActivity.KEY_OTHER_IMG, mAsk.userHeadUrl);
                startActivity(intent2);
            }
        });
    }

    private void initData() {
        ImageViewLoader.setSquareImageViewResource(mContext, mContentImageView, mAsk.contentImageUrl, getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));
        mAnswerAdapter = new AskAnswerAdapter(AskDetailActivity.this, mAsk.aid, mAsk.count);
        mAnswerListView.setAdapter(mAnswerAdapter);

        mSubjectTextView.setText(mAsk.subject);
        mContentTextView.setText(mAsk.content);
        mDateTextView.append(TimeTransfer.long2StringDetailDate(mContext, mAsk.createTime));
        mNameTextView.setText(mAsk.userName);

        ImageViewLoader.setCircleImageViewResource(mContext, mHeadImageView, mAsk.userHeadUrl, getResources().getDimensionPixelSize(R.dimen.icon_head_size_small));

        if (!TextUtils.isEmpty(mAsk.contentImageUrl))
            ImageViewLoader.setSquareImageViewResource(mContext, mContentImageView, mAsk.contentImageUrl, getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));
        else
            mContentImageView.setVisibility(View.GONE);
        // 有了这句会导致无法刷新
        // pullToRefreshScrollView.setRefreshing();
        updateListView(1);

        if (mAsk.isDeletable) {
            mDeleteTextView.setVisibility(View.VISIBLE);
        } else {
            mDeleteTextView.setVisibility(View.GONE);
        }

        /**删除和私信逻辑 *//*
        String userUid = ((AppApplication) this.getApplicationContext())
                .getUserInfo().userId;
        String currentUid = ask.getUid();
        int isGuanliYuan = ask.getDelop();
        // 点击进入是自己
        if (userUid.equals(currentUid)) {
            sixinTv.setVisibility(View.GONE);
            delAsk.setVisibility(View.VISIBLE);
            // 点击进入不是自己
        } else {
            // 是管理员
            if (isGuanliYuan == 1) {
                sixinTv.setVisibility(View.VISIBLE);
                delAsk.setVisibility(View.VISIBLE);
                // 不是管理员
            } else {
                sixinTv.setVisibility(View.VISIBLE);
                delAsk.setVisibility(View.GONE);
            }
        }*/

    }

    /**
     * 功能描述:删除我的圈中的item
     */
    private void deleteAsk() {
        mProgressDialog.show();
        ServiceProvider.deleteAsk(AskDetailActivity.this, mAsk.aid, new VolleyListener(AskDetailActivity.this) {

            @Override
            public void onResponse(Object responseObject) {
                if (!mActivity.isFinishing()) {
                    mProgressDialog.dismiss();
                }
                JSONObject response = (JSONObject) responseObject;
                int code = response.optInt(Net.CODE);
                if (code == Net.LOGOUT) {
                    AppApplication.logoutShow(AskDetailActivity.this);
                    return;
                }
                if (Net.SUCCESS != code) {
                    ToastUtil.showNetExc(AskDetailActivity.this);
                    return;
                }
                setResult(RESULT_DELETED, null);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                if (!mActivity.isFinishing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    /**
     * 功能描述:获取回答列表
     */
    private void updateListView(final int beginNum) {
        // 获取最新内容
        ServiceProvider.updateAnswerList(mContext, beginNum, Constant.LIST_ITEM_NUM, mAsk.aid, new VolleyListener(AskDetailActivity.this) {

            @Override
            public void onResponse(Object responseObject) {
                pullToRefreshScrollView.onRefreshComplete();
                JSONObject response = (JSONObject) responseObject;
                int code = response.optInt(Net.CODE);
                if (code == Net.LOGOUT) {
                    AppApplication.logoutShow(mContext);
                    return;
                }
                if (Net.SUCCESS != code) {
                    ToastUtil.showNetExc(AskDetailActivity.this);
                    return;
                }
                List<Object> tempAsk = new ArrayList<>();
                JSONArray jsonArray = response.optJSONArray(Net.DATA);
                if (jsonArray == null) {
                    return;
                }
                int length = jsonArray.length();
                if (length == 0) {
                    if (beginIndex == 1) {
                        mAnswerAdapter.setList(null);
                        mNoneAnswerImageView.setVisibility(View.VISIBLE);
                    } else {
                        mNoneAnswerImageView.setVisibility(View.GONE);
                        ToastUtil.showToast(AskDetailActivity.this, "没有更多了");
                    }
                    return;
                }
                mNoneAnswerImageView.setVisibility(View.GONE);
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    tempAsk.add(new Ask(jsonObject));
                }
                if(beginNum == beginIndex) {
                    mAnswerAdapter.addList(tempAsk);
                }
                beginIndex++;
                mAnswerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                pullToRefreshScrollView.onRefreshComplete();
            }
        });
    }
}
