package com.badou.mworking;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.badou.mworking.adapter.AskAnswerAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.listener.DeleteClickListener;
import com.badou.mworking.listener.FullImageListener;
import com.badou.mworking.listener.MessageClickListener;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.Store;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoScrollListView;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 问答详情页面
 */
public class AskDetailActivity extends BaseBackActionBarActivity {

    public static final String KEY_ASK = "ask";

    public static final int REQUEST_REPLY = 11;

    public static final String RESULT_KEY_DELETE = "delete";
    public static final String RESULT_KEY_COUNT = "count";
    public static final String RESULT_KEY_STORE = "store";

    Intent resultIntent = new Intent();

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

    private PullToRefreshScrollView pullToRefreshScrollView;
    private NoneResultView mNoneResultView;

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
        // ScollView必须在Activity加载完界面后才能调用setRefreshing
        // 不然会导致无法滑动的bug
        // 性能较差的手机可能会出bug，只能通过其他方式进行刷新
        pullToRefreshScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pullToRefreshScrollView.setRefreshing();
            }
        }, 700);
    }

    @Override
    protected void onStoreChanged(boolean isStore) {
        mAsk.isStore = isStore;
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
        mHeadImageView = (ImageView) findViewById(R.id.iv_activity_ask_detail_user_head);
        mContentImageView = (ImageView) findViewById(R.id.iv_activity_ask_detail_content);
        // 自定义LinearLayout
        mAnswerListView = (NoScrollListView) findViewById(R.id.nslv_activity_ask_detail_answer);
        mBottomReplyLayout = (LinearLayout) findViewById(R.id.ll_activity_ask_detail_bottom_comment);
        mNoneResultView = (NoneResultView) findViewById(R.id.nrv_activity_ask_detail_none);
        mNoneResultView.setContent(-1, R.string.none_result_reply);
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
                Intent intent = new Intent(mContext, AskAnswerSubmitActivity.class);
                intent.putExtra(AskAnswerSubmitActivity.KEY_AID, mAsk.aid);
                startActivityForResult(intent, REQUEST_REPLY);
            }
        });

        mDeleteTextView.setOnClickListener(new DeleteClickListener(mContext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAsk();
            }
        }));

        mMessageTextView.setOnClickListener(new MessageClickListener(mContext, mAsk.userName, mAsk.whom, mAsk.userHeadUrl));
    }

    private void initData() {
        addStoreImageView(mAsk.isStore, Store.TYPE_STRING_ASK, mAsk.aid);
        ImageViewLoader.setSquareImageViewResource(mContentImageView, R.drawable.icon_image_default, mAsk.contentImageUrl, getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));
        mAnswerAdapter = new AskAnswerAdapter(AskDetailActivity.this, mAsk.aid, mAsk.count);
        mAnswerListView.setAdapter(mAnswerAdapter);

        mSubjectTextView.setText(mAsk.subject);
        mContentTextView.setText(mAsk.content);
        mDateTextView.append(TimeTransfer.long2StringDetailDate(mContext, mAsk.createTime));
        mNameTextView.setText(mAsk.userName);

        ImageViewLoader.setCircleImageViewResource(mHeadImageView, mAsk.userHeadUrl, getResources().getDimensionPixelSize(R.dimen.icon_head_size_small));

        if (!TextUtils.isEmpty(mAsk.contentImageUrl))
            ImageViewLoader.setSquareImageViewResource(mContentImageView, R.drawable.icon_image_default, mAsk.contentImageUrl, getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));
        else
            mContentImageView.setVisibility(View.GONE);
        if (mAsk.userName.equals("我")) {
            mMessageTextView.setVisibility(View.GONE);
        }
        if (mAsk.isDeletable) {
            mDeleteTextView.setVisibility(View.VISIBLE);
        } else {
            mDeleteTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 功能描述:删除我的圈中的item
     */
    private void deleteAsk() {
        mProgressDialog.show();
        ServiceProvider.deleteAsk(AskDetailActivity.this, mAsk.aid, new VolleyListener(AskDetailActivity.this) {

            @Override
            public void onResponseSuccess(JSONObject response) {
                resultIntent.putExtra(RESULT_KEY_DELETE, true);
                finish();
            }

            @Override
            public void onCompleted() {
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
            public void onResponseSuccess(JSONObject response) {
                List<Object> tempAsk = new ArrayList<>();
                JSONArray jsonArray = response.optJSONArray(Net.DATA);
                if (jsonArray == null) {
                    return;
                }
                int length = jsonArray.length();
                if (length == 0) {
                    if (beginIndex == 1) {
                        mAnswerAdapter.setList(null);
                        mNoneResultView.setVisibility(View.VISIBLE);
                    } else {
                        mNoneResultView.setVisibility(View.GONE);
                        ToastUtil.showToast(AskDetailActivity.this, "没有更多了");
                    }
                    return;
                }
                mNoneResultView.setVisibility(View.GONE);
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    tempAsk.add(new Ask(jsonObject));
                }
                if (beginIndex == 1) {
                    mAnswerAdapter.setList(tempAsk);
                } else {
                    mAnswerAdapter.addList(tempAsk);
                }
                beginIndex++;
            }

            @Override
            public void onCompleted() {
                if (!mActivity.isFinishing()) {
                    mProgressDialog.dismiss();
                }
                pullToRefreshScrollView.onRefreshComplete();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_REPLY) {
            mAsk.count++;
            mProgressDialog.show();
            beginIndex = 1;
            mAnswerAdapter.setReplyCount(mAsk.count);
            updateListView(1);
            mReceivedIntent.putExtra(RESULT_KEY_COUNT, mAsk.count);
        }
    }

    @Override
    public void finish() {
        mReceivedIntent.putExtra(RESULT_KEY_STORE, mAsk.isStore);
        setResult(RESULT_OK, mReceivedIntent);
        super.finish();
    }
}
