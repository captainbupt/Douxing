package com.badou.mworking;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.badou.mworking.adapter.CommentAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.model.Comment;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.BottomSendMessageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 评论页面
 */
public class CommentActivity extends BaseBackActionBarActivity {

    public static final String KEY_RID = "rid";
    private PullToRefreshListView mContentListView;//下拉刷新
    private CommentAdapter mCommentAdapter;
    private BottomSendMessageView mBottomView;
    private FrameLayout mNoneContentFrameLayout;  // 没有内容时的提示

    private int mCurrentPage;
    private String mRid = "";
    private String whom = "";

    private boolean isReply = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        initData();
        initListener();
        refreshComment(1);
    }

    /**
     * 功能描述:实例化view
     */
    protected void initView() {
        mNoneContentFrameLayout = (FrameLayout) findViewById(R.id.fl_activity_comments_none_content);
        mContentListView = (PullToRefreshListView) findViewById(R.id.ptrlv_activity_comments);
        mContentListView.setMode(Mode.BOTH);
        mBottomView = (BottomSendMessageView) findViewById(R.id.bsmv_activity_comment);
    }

    protected void initData() {
        mRid = mReceivedIntent.getStringExtra(KEY_RID);
        mCommentAdapter = new CommentAdapter(mContext);
        mContentListView.setAdapter(mCommentAdapter);
        mCurrentPage = 1;
    }

    /**
     * 功能描述:设置监听
     */
    protected void initListener() {
        mBottomView.setOnSubmitListener(new BottomSendMessageView.OnSubmitListener() {
            @Override
            public void onSubmit(String content) {
                submitComment(content);
            }
        });
        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshComment(1);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                refreshComment(mCurrentPage + 1);
            }
        });

        mContentListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                Chatter question = (Chatter) mCommentAdapter.getItem(position - 1);
                String userName = question.name.trim();
                // 不可以回复我自己
                if (userName.equals("我")) {
                    return;
                }
                whom = question.whom;
                isReply = true;
                mBottomView.clearContent();
                mBottomView.showKeyboard();
                mBottomView.setContent(getResources().getString(R.string.button_reply) + ": " + question.name, getResources().getString(R.string.button_reply));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isReply) {
            isReply = false;
            mBottomView.clearContent();
            mBottomView.setContent(getResources().getString(R.string.comment_hint), getResources().getString(R.string.button_submit));
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 功能描述:根据页码刷新评论
     *
     * @param pageNumber
     */
    private void refreshComment(final int pageNumber) {
        ServiceProvider.doUpdateComment(mContext, mRid, pageNumber,
                new VolleyListener(mContext) {
                    @Override
                    public void onCompleted() {
                        if (!mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                        mContentListView.onRefreshComplete();
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        mCurrentPage = pageNumber;
                        int allCount = response
                                .optJSONObject(Net.DATA).optInt("ttlcnt");
                        updateSuccess(response
                                .optJSONObject(Net.DATA)
                                .optJSONArray(ResponseParameters.COMMENT_RESULT), allCount);
                    }
                });
    }

    private void updateSuccess(JSONArray jsonArray, int allCount) {
        int length = jsonArray.length();
        // 如果没有内容的话，显示默认图片
        List<Object> commentList;
        if (mCurrentPage <= 1 && length == 0) {
            mNoneContentFrameLayout.setVisibility(View.VISIBLE);
            mContentListView.setVisibility(View.GONE);
        } else {
            mNoneContentFrameLayout.setVisibility(View.GONE);
            mContentListView.setVisibility(View.VISIBLE);
        }
        commentList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            try {
                commentList.add(new Comment(jsonArray.getJSONObject(i), Comment.TYPE_COMMENT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (mCurrentPage == 1) {
            mCommentAdapter.setList(commentList, allCount);
        } else {
            mCommentAdapter.addList(commentList, allCount);
        }
    }

    private void submitComment(String comment) {
        mProgressDialog.setContent(R.string.action_comment_update_ing);
        mProgressDialog.show();
        VolleyListener volleyListener = new VolleyListener(mContext) {
            @Override
            public void onCompleted() {
                if (!mActivity.isFinishing()) {
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onErrorCode(int code) {
                ToastUtil.showToast(mContext, R.string.result_comment_submit_fail);
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                submitSuccess();
            }
        };
        if (isReply) {
            ServiceProvider.doReplyComment(mContext, mRid, whom, comment, volleyListener);
        } else {
            ServiceProvider.doSubmitComment(mContext, mRid, comment, volleyListener);
        }
    }

    /**
     * 发送成功
     */
    private void submitSuccess() {
        refreshComment(1);
    }
}
