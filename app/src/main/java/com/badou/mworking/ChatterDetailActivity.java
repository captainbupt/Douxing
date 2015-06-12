package com.badou.mworking;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.CommentAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.listener.DeleteClickListener;
import com.badou.mworking.listener.MessageClickListener;
import com.badou.mworking.listener.TopicClickableSpan;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.model.Comment;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.BottomSendMessageView;
import com.badou.mworking.widget.MultiImageShowGridView;
import com.badou.mworking.widget.NoScrollListView;
import com.badou.mworking.widget.NoScrollListView.OnNoScrollItemClickListener;
import com.badou.mworking.widget.VideoImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述: 同事圈详情
 */
public class ChatterDetailActivity extends BaseBackActionBarActivity {

    public static final String KEY_CHATTER = "chatter";
    public static final int RESULT_DELETE = 11;
    public static final int RESULT_REPLY = 12;
    public static final String RESULT_KEY_COUNT = "count";

    private Chatter mChatter;

    private CommentAdapter replyAdapter;// 同事圈list

    private PullToRefreshScrollView mPullToRefreshScrollView;
    private ImageView mHeadImageView;
    private TextView mNameTextView;
    private TextView mContentTextView;
    private MultiImageShowGridView mImageGridView;
    private VideoImageView mVideoImageView;
    private TextView mSaveInternetTextView;
    private TextView mTimeTextView;
    private TextView mDeleteTextView;
    private TextView mMessageTextView;    //私信
    private BottomSendMessageView mSendMessageView;
    private NoScrollListView mReplyListView;

    private int mCurrentIndex = 1;
    private boolean isReply;
    private String mReplyWhom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(mContext.getResources().getString(R.string.title_name_NeiRongXiangQing));
        mChatter = (Chatter) mReceivedIntent.getSerializableExtra(KEY_CHATTER);
        setContentView(R.layout.activity_chatter_detail);
        initView();
        initListener();
        initData();
        mPullToRefreshScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullToRefreshScrollView.setRefreshing();
            }
        }, 700);
    }

    /**
     * 功能描述:实例化自定义listview,设置显示的内容
     */
    protected void initView() {
        mHeadImageView = (ImageView) findViewById(R.id.iv_activity_chatter_detail_head);
        mNameTextView = (TextView) findViewById(R.id.tv_activity_chatter_detail_name);
        mContentTextView = (TextView) findViewById(R.id.tv_activity_chatter_detail_content);
        mImageGridView = (MultiImageShowGridView) findViewById(R.id.misgv_activity_chatter_detail_image);
        mVideoImageView = (VideoImageView) findViewById(R.id.viv_activity_chatter_detail_video);
        mSaveInternetTextView = (TextView) findViewById(R.id.tv_activity_chatter_detail_save_internet);
        mTimeTextView = (TextView) findViewById(R.id.tv_activity_chatter_detail_time);
        mMessageTextView = (TextView) findViewById(R.id.tv_activity_chatter_detail_message);
        mDeleteTextView = (TextView) findViewById(R.id.tv_activity_chatter_detail_delete);
        mSendMessageView = (BottomSendMessageView) findViewById(R.id.bsmv_activity_chatter_detail);
        mReplyListView = (NoScrollListView) findViewById(R.id.nslv_activity_chatter_detail_reply);
        mPullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.ptrsv_activity_ask_detail);
    }

    /**
     * 功能描述:发送回复TextView设置监听,pullToRefreshScrollView设置下拉刷新监听
     */
    protected void initListener() {
        mMessageTextView.setOnClickListener(new MessageClickListener(mContext, mChatter.name, mChatter.whom, mChatter.headUrl));

        /***删除按钮**/
        mDeleteTextView.setOnClickListener(new DeleteClickListener(mContext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteComment(mChatter.qid);
            }
        }));
        mReplyListView.setOnItemClickListener(new OnNoScrollItemClickListener() {

            @Override
            public void onItemClick(View v, int position, long id) {
                Comment comment = (Comment) replyAdapter.getItem(position);
                if (comment.name.equals("我")) {
                    return;
                }
                isReply = true;
                mReplyWhom = comment.whom;
                mSendMessageView.setContent(getResources().getString(R.string.button_reply) + ": " + comment.name, getResources().getString(R.string.button_reply));
            }
        });
        mPullToRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                mCurrentIndex = 1;
                // 获取最新回复/提问
                updateReply(mCurrentIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                updateReply(mCurrentIndex);
            }
        });
        mSendMessageView.setOnSubmitListener(new BottomSendMessageView.OnSubmitListener() {
            @Override
            public void onSubmit(String content) {
                if (isReply) {
                    contentReply(content, mReplyWhom);
                } else {
                    chatterReply(content);
                }
            }
        });
    }

    /**
     * 功能描述:设置蓝色title显示内容
     */
    private void initData() {
        replyAdapter = new CommentAdapter(mContext, mChatter.qid, mChatter.deletable, mProgressDialog);
        mReplyListView.setAdapter(replyAdapter);
        TopicClickableSpan.setClickTopic(mContext, mContentTextView, mChatter.content);
        /**删除和私信逻辑 */
        String userUid = ((AppApplication) this.getApplicationContext())
                .getUserInfo().userId;
        String currentUid = mChatter.uid;
        // 点击进入是自己      (TextUtils.isEmpty(currentUid) 我的圈中没有返回uid字段，因为那是自己，当uid为空时，判断为是自己，也就是我的圈跳转进入的，只显示删除)
        if (userUid.equals(currentUid) || TextUtils.isEmpty(currentUid)) {
            mMessageTextView.setVisibility(View.GONE);
        } else {
            mMessageTextView.setVisibility(View.VISIBLE);
        }
        if (mChatter.deletable) {
            mDeleteTextView.setVisibility(View.VISIBLE);
        } else {
            mDeleteTextView.setVisibility(View.GONE);
        }
        mNameTextView.setText(mChatter.name);
        mTimeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, mChatter.publishTime) + "");
        /**设置头像**/
        ImageViewLoader.setCircleImageViewResource(mContext, mHeadImageView, mChatter.headUrl,
                mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));
        // 评论中添加的图片
        boolean isWifi = NetUtils.isWifiConnected(mContext);
        // 判断是否在2G/3G下显示图片
        boolean isShowImg = SP.getBooleanSP(mContext, SP.DEFAULTCACHE,
                "pic_show", false);

        // 没有的话，判断是否是wifi网络
        if (isWifi || isShowImg) {
            mSaveInternetTextView.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(mChatter.videoUrl)) {
                mImageGridView.setVisibility(View.GONE);
                mVideoImageView.setVisibility(View.VISIBLE);
                mVideoImageView.setData(mChatter.imgUrl, mChatter.videoUrl, mChatter.qid);
            } else {
                mImageGridView.setVisibility(View.VISIBLE);
                mVideoImageView.setVisibility(View.GONE);
                mImageGridView.setList(mChatter.photoUrls);
            }
        } else {
            mVideoImageView.setVisibility(View.GONE);
            mImageGridView.setVisibility(View.GONE);
            if (TextUtils.isEmpty(mChatter.imgUrl) && TextUtils.isEmpty(mChatter.videoUrl) && mChatter.photoUrls.size() == 0) {
                mSendMessageView.setVisibility(View.GONE);
            } else {
                mSaveInternetTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 功能描述:删除我的圈中的item
     *
     * @param qid
     */
    private void deleteComment(final String qid) {
        ServiceProvider.doMyGroup_del(mContext, qid, new VolleyListener(
                mContext) {
            @Override
            public void onResponse(Object responseObject) {
                if (!mActivity.isFinishing()) {
                    mProgressDialog.dismiss();
                }
                JSONObject response = (JSONObject) responseObject;
                int code = response.optInt(Net.CODE);
                if (responseObject == null) {
                    ToastUtil.showNetExc(mContext);
                    return;
                }
                if (code == Net.LOGOUT) {
                    AppApplication.logoutShow(mContext);
                    return;
                }
                if (Net.SUCCESS != code) {
                    ToastUtil.showNetExc(mContext);
                    return;
                }
                setResult(RESULT_DELETE);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError arg0) {
                super.onErrorResponse(arg0);
                if (!mActivity.isFinishing()) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    /**
     * 功能描述:通过网络获取最新 回复/提问 的内容
     */
    private void updateReply(int beginNum) {
        // 获取最新内容
        ServiceProvider.doQuestionShareAnswer(mContext, mChatter.qid, beginNum,
                Constant.LIST_AROUND_NUM, new VolleyListener(mContext) {
                    @Override
                    public void onResponse(Object responseObject) {
                        mPullToRefreshScrollView.onRefreshComplete();
                        JSONObject response = (JSONObject) responseObject;
                        JSONArray resultArray = null;
                        JSONObject data = response.optJSONObject(Net.DATA);
                        int ttlcnt = 0;
                        if (response != null && data != null) {
                            resultArray = data.optJSONArray(Net.RESULT);
                            ttlcnt = data.optInt("ttlcnt");
                        }
                        if (resultArray == null || resultArray.length() == 0) {
                            return;
                        }
                        List<Object> replys = new ArrayList<>();
                        for (int i = 0; i < resultArray.length(); i++) {
                            replys.add(new Comment((JSONObject) resultArray.opt(i), Comment.TYPE_CHATTER));
                        }
                        if (mCurrentIndex <= 1) {
                            replyAdapter.setList(replys, ttlcnt);
                        } else {
                            mCurrentIndex++;
                            replyAdapter.addList(replys, ttlcnt);
                        }
                        if (replyAdapter.getCount() == 0 && mReplyListView != null) {
                            mReplyListView.setVisibility(View.GONE);
                        } else {
                            mReplyListView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mPullToRefreshScrollView.isRefreshing())
                            mPullToRefreshScrollView.onRefreshComplete();
                    }

                });
    }

    /**
     * 功能描述:发起网络请求,回复问题/分享 (将回复内容发送到服务)
     *
     * @param text
     */
    private void chatterReply(String text) {
        mProgressDialog.setContent(R.string.action_comment_update_ing);
        mProgressDialog.show();
        ServiceProvider.doAnswerQuestionShare(mContext, mChatter.qid, text,
                new VolleyListener(mContext) {
                    @Override
                    public void onResponse(Object arg0) {
                        if (!mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                        JSONObject jsonObject = (JSONObject) arg0;
                        int errcode = jsonObject.optInt(Net.CODE);
                        if (errcode == 0) {
                            // 响应成功
                            mCurrentIndex = 1;
                            updateReply(1);
                        }
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
     * 评论回复
     */
    private void contentReply(String content, String whom) {
        mProgressDialog.setContent(R.string.action_comment_update_ing);
        mProgressDialog.show();
        ServiceProvider.ReplyComment(mContext, mChatter.qid, content, whom, new VolleyListener(mContext) {

            @Override
            public void onResponse(Object responseObject) {
                JSONObject response = (JSONObject) responseObject;
                if (!mActivity.isFinishing())
                    mProgressDialog.dismiss();
                int code = response.optInt(Net.CODE);
                if (code != Net.SUCCESS) {
                    return;
                }
                mCurrentIndex = 1;
                updateReply(1);
                // 关闭键盘
                mSendMessageView.hideKeyboard();
                mSendMessageView.setContent(getResources().getString(R.string.comment_hint), getResources().getString(R.string.button_send));
                mSendMessageView.clearContent();
                isReply = false;
            }
        });
    }
}
