package com.badou.mworking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.AroundReplaAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoScrollListView;
import com.badou.mworking.widget.NoScrollListView.OnNoScrollItemClickListener;
import com.badou.mworking.widget.WaitProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 类: <code> AroundDetailActivity </code> 功能描述: 同事圈详情 创建人:董奇 创建日期: 2014年7月15日
 * 下午6:35:15 开发环境: JDK7.0
 */
public class AroundDetailActivity extends BaseBackActionBarActivity {

    public static final String VALUE_QUESTION = "question";
    public static final String VALUE_TONG_SHI_CONTENT = "tongshiquan_content";
    public static final String VALUE_USER_PHONE = "my_group_user_PHONE";

    private Chatter question;
    private String whom = "";

    public static final String RESPONSE_KEY_RELAY_NUMBER = "KEY_RELA1Y_NO";
    private PullToRefreshScrollView pullToRefreshScrollView;
    private AroundReplaAdapter replyAdapter;// 同事圈list
    private EditText etQuestionDetailContetnAnswer;// bottom发送信息输入框

    private TextView ivQuestionAnswerContent;// 底部发送 按钮
    private TextView tvDelComment;
    private TextView sixinTv;    //私信
    private WaitProgressDialog mProgressDialog;
    private ImageView imgTitlePic;// title显示的图片
    private ImageView aroundDitailSHPImg; //表示视屏的图片
    private NoScrollListView lvQuestion;
    private Context mContext;

    private String videoURl = "";

    private Boolean isClickRelay = false;

    private int index = 1;

    private List<Chatter> replys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = AroundDetailActivity.this;
        setActionbarTitle(mContext.getResources().getString(R.string.title_name_NeiRongXiangQing));
        setContentView(R.layout.activity_around_detail);
        layout.attachToActivity(this);
        try {
            question = (Chatter) getIntent().getSerializableExtra(VALUE_QUESTION);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        initView();

        setViewValue(question);
        initListener();
        updateReply();


    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        String text = getIntent().getStringExtra(VALUE_TONG_SHI_CONTENT);
        if (!TextUtils.isEmpty(text)) {
            reply(text, question.qid);
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 功能描述:实例化自定义listview,设置显示的内容
     */
    protected void initView() {
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // 自定义LinearLayout
        lvQuestion = (NoScrollListView) findViewById(R.id.nslv_activity_ask_detail_answer);
        sixinTv = (TextView) findViewById(R.id.tv_activity_ask_detail_message);
        replyAdapter = new AroundReplaAdapter(mContext, 0, question.qid, mProgressDialog);
        lvQuestion.setAdapter(replyAdapter);
        tvDelComment = (TextView) findViewById(R.id.tv_delete_comment);
        aroundDitailSHPImg = (ImageView) findViewById(R.id.around_ditail_shiping_img);

        mProgressDialog = new WaitProgressDialog(mContext,
                R.string.message_wait);
        mProgressDialog.show();
        imgTitlePic = (ImageView) findViewById(R.id.imgTitleAroundDetail);
        Bitmap titleBmp = BitmapLruCache.getBitmapLruCache().get(question.imgUrl);
        videoURl = question.videoUrl;
        if (titleBmp != null && !titleBmp.isRecycled()) {
            imgTitlePic.setImageBitmap(titleBmp);
            isShowSHPImg(videoURl);
        } else {
            MyVolley.getImageLoader().get(
                    question.imgUrl,
                    new PicImageListener(mContext, imgTitlePic, question
                            .imgUrl));
            isShowSHPImg(videoURl);
        }
        ivQuestionAnswerContent = (TextView) findViewById(R.id.tv_view_bottom_send_message_submit);
        ivQuestionAnswerContent.setEnabled(false);
        ivQuestionAnswerContent.setBackgroundColor(getResources().getColor(R.color.color_grey));
        etQuestionDetailContetnAnswer = (EditText) findViewById(R.id.et_view_bottom_send_message_content);

        etQuestionDetailContetnAnswer.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isClickRelay) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        isClickRelay = false;
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        etQuestionDetailContetnAnswer.setHint(R.string.comment_hint);
                        ivQuestionAnswerContent.setText("发送");
                        return true;
                    }
                }
                return false;
            }
        });

        lvQuestion.setOnItemClickListener(new OnNoScrollItemClickListener() {

            @Override
            public void onItemClick(View v, int position, long id) {
                Chatter question = replyAdapter.getItem(position);
                showKeyboard(question);
            }
        });

        sixinTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(AroundDetailActivity.this, ChattingActivity.class);
                intent.putExtra(ChattingActivity.KEY_NAME, question.name);
                intent.putExtra(ChattingActivity.KEY_WHOM, question.whom);
                intent.putExtra(ChattingActivity.KEY_OTHER_IMG, question.imgUrl);
                startActivity(intent);
            }
        });

    }

    /**
     * 功能描述:判断是否是视屏，是否显示视屏标志图片
     */
    private void isShowSHPImg(String videoURl) {
        if (!TextUtils.isEmpty(videoURl)) {
            aroundDitailSHPImg.setVisibility(View.VISIBLE);
        } else {
            aroundDitailSHPImg.setVisibility(View.GONE);
            return;
        }
    }

    @Override
    public void clickRight() {
        Intent intent = new Intent(mContext, ChatterSubmitActivity.class);
        intent.putExtra(ChatterSubmitActivity.KEY_QUESTION_VALUE,
                ChatterSubmitActivity.ACT_NAME_TONG_SHI_DETAIL);
        intent.putExtra(VALUE_QUESTION, question.qid);
        startActivity(intent);
    }

    /**
     * 功能描述:设置蓝色title显示内容
     *
     * @param question
     */
    private void setViewValue(Chatter question) {
        if (question != null) {
            TextView tvQuestionContent = (TextView) findViewById(R.id.tvQuestionContent);
            TextView tvQuestionName = (TextView) findViewById(R.id.tvQuestionName);
            TextView tvTiem = (TextView) this.findViewById(R.id.tv_adapter_chat_list_time);// 时间
            ImageView ivHeadimg = (ImageView) this
                    .findViewById(R.id.iv_user_head_icon);

            String content = question.content;
            if (!TextUtils.isEmpty(content)) {
                tvQuestionContent.setText(content);
            }
            /**删除和私信逻辑 */
            String userUid = ((AppApplication) this.getApplicationContext())
                    .getUserInfo().userId;
            String currentUid = question.uid;
            // 点击进入是自己      (TextUtils.isEmpty(currentUid) 我的圈中没有返回uid字段，因为那是自己，当uid为空时，判断为是自己，也就是我的圈跳转进入的，只显示删除)
            if (userUid.equals(currentUid) || TextUtils.isEmpty(currentUid)) {
                sixinTv.setVisibility(View.GONE);
                tvDelComment.setVisibility(View.VISIBLE);
                // 点击进入不是自己
            } else {
                // 是管理员
                if (question.deletable) {
                    sixinTv.setVisibility(View.VISIBLE);
                    tvDelComment.setVisibility(View.VISIBLE);
                    // 不是管理员
                } else {
                    sixinTv.setVisibility(View.VISIBLE);
                    tvDelComment.setVisibility(View.GONE);
                }
            }
            if (question.name != null) {
                tvQuestionName.setText(question.name);
            }
            tvTiem.setText(TimeTransfer.long2StringDetailDate(mContext, question
                    .publishTime) + "");
            Bitmap userHeadBm = BitmapLruCache.getBitmapLruCache()
                    .getCircleBitmap(question.imgUrl);
            if (userHeadBm != null) {
                ivHeadimg.setImageBitmap(userHeadBm);
                userHeadBm = null;
            } else {
                /**设置头像**/
                int size = mContext.getResources().getDimensionPixelSize(
                        R.dimen.icon_head_size_middle);
                MyVolley.getImageLoader().get(
                        question.imgUrl,
                        new CircleImageListener(mContext, question
                                .imgUrl, ivHeadimg, size, size));
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
                if (null != mProgressDialog && mContext != null) {
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
                finish();
                Constant.is_del = true;
                Constant.is_refresh = true;
            }

            @Override
            public void onErrorResponse(VolleyError arg0) {
                super.onErrorResponse(arg0);
                if (null != mProgressDialog && mContext != null) {
                    mProgressDialog.dismiss();
                }
            }
        });
    }

    /**
     * 功能描述:发起网络请求,回复问题/分享 (将回复内容发送到服务)
     *
     * @param text
     */
    private void reply(String text, String qid) {
        mProgressDialog = new WaitProgressDialog(mContext,
                R.string.action_comment_update_ing);
        mProgressDialog.show();
        ServiceProvider.doAnswerQuestionShare(mContext, qid, text,
                new VolleyListener(mContext) {
                    @Override
                    public void onResponse(Object arg0) {
                        if (null != mProgressDialog && mContext != null
                                && !mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                        JSONObject jsonObject = (JSONObject) arg0;
                        int errcode = jsonObject.optInt(Net.CODE);
                        if (errcode == 0) {
                            // 响应成功
                            updateReply();

                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        if (null != mProgressDialog && mContext != null
                                && !mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });

    }

    /**
     * 功能描述:发送回复TextView设置监听,pullToRefreshScrollView设置下拉刷新监听
     */
    protected void initListener() {

        // 点击图片放大显示
        imgTitlePic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 如果视屏url为空则点击的时候显示图片，如果url不为空，点击的时候显示视屏
                if (TextUtils.isEmpty(videoURl)) {
                    Intent goToPhotoAct = new Intent(mContext, PhotoActivity.class);
                    goToPhotoAct.putExtra(PhotoActivity.MODE_PICZOMM,
                            question.imgUrl);
                    startActivity(goToPhotoAct);
                } else {
                    Intent intent = new Intent(mContext, TongSHQVideoPlayActivity.class);
                    intent.putExtra(TongSHQVideoPlayActivity.VIDEOURL, question.videoUrl);
                    intent.putExtra(TongSHQVideoPlayActivity.QID, question.qid);
                    startActivity(intent);
                }
            }
        });
        ivQuestionAnswerContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 验证EditText的格式是否正确
                String text = etQuestionDetailContetnAnswer.getText()
                        .toString().trim();
                if (TextUtils.isEmpty(text) || null == text
                        || text.length() <= 0) {
                    ToastUtil.showToast(mContext,
                            R.string.question_content_null);
                    return;
                }
                String btnText = ivQuestionAnswerContent.getText().toString();
                if (btnText.equals("发送")) {
                    sendPingLun(text);
                } else if (btnText.equals("回复")) {
                    contentReplay(text);
                }
            }
        });

        /***删除按钮**/
        tvDelComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.myQuan_dialog_title_tips)
                        .setMessage(
                                mContext.getResources().getString(
                                        R.string.tip_delete_confirmation))
                        .setPositiveButton(R.string.text_ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {
                                        deleteComment(question.qid);
                                    }

                                }).setNegativeButton(R.string.text_cancel, null).show();

            }
        });
        etQuestionDetailContetnAnswer.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                int length = etQuestionDetailContetnAnswer.getText().toString()
                        .trim().length();

                if (length == 0) {
                    ivQuestionAnswerContent.setEnabled(false);
                    ivQuestionAnswerContent.setBackgroundColor(getResources().getColor(R.color.color_grey));
                } else {
                    ivQuestionAnswerContent.setEnabled(true);
                    ivQuestionAnswerContent.setBackgroundResource(R.drawable.comment_send_blue);
                }
            }
        });

        pullToRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.ptrsv_activity_ask_detail);
        pullToRefreshScrollView
                .setOnRefreshListener(new OnRefreshListener<ScrollView>() {

                    @Override
                    public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                        index = 1;
                        // 获取最新回复/提问
                        updateReply();
                    }
                });
    }

    @Override
    public void clickLeft() {
        super.clickLeft();
        Constant.is_refresh = false;
    }

    /**
     * 发送评论
     */
    private void sendPingLun(String content) {
        if (question == null) {
            return;
        }
        final String qid = question.qid;
        if (TextUtils.isEmpty(qid)) {
            return;
        }
        if (etQuestionDetailContetnAnswer.getText().length() < 5) {
            ToastUtil.showToast(mContext, R.string.comment_tips_length);
            return;
        }
        // 输入法不自动弹出
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                etQuestionDetailContetnAnswer.getWindowToken(), 0);
        etQuestionDetailContetnAnswer.setText("");
        content = content.replaceAll("\\n", "");
        // 提交回复内容
        reply(content, qid);
    }

    /**
     * 功能描述:通过网络获取最新 回复/提问 的内容
     */
    private void updateReply() {
        // 获取最新内容
        ServiceProvider.doQuestionShareAnswer(mContext, question.qid, index,
                Constant.LIST_AROUND_NUM, new VolleyListener(mContext) {
                    @Override
                    public void onResponse(Object responseObject) {
                        replys = new ArrayList<Chatter>();
                        if (null != mProgressDialog && mContext != null
                                && !mActivity.isFinishing())
                            mProgressDialog.dismiss();
                        if (pullToRefreshScrollView.isRefreshing()) {
                            pullToRefreshScrollView.onRefreshComplete();
                        }
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
                        int itemNums = resultArray.length();
                        for (int i = 0; i < itemNums; i++) {
                            JSONObject jo2 = resultArray.optJSONObject(i);
                            if (jo2 == null)
                                return;
                            String c = jo2
                                    .optString(ResponseParams.QUESTION_DETAIL_ANSWER_CONTENT);
                            String e = jo2
                                    .optString(ResponseParams.QUESTION_DETAIL_EMPLOYEE);
                            long t = (Long.parseLong(jo2
                                    .optString(ResponseParams.QUESTION_DETAIL_TIME))) * 1000l;
                            String imgurl = jo2
                                    .optString(ResponseParams.QUESTION_IMG_URL);
                            String uid = jo2.optString("uid");
                            /*replys.add(new Chatter(question.getMode(), uid, c, t,
                                    e, imgurl));*/
                        }
                        if (index <= 1) {
                            replyAdapter.setDatas(replys, ttlcnt);
                        } else {
                            index++;
                            replyAdapter.addDatas(replys, ttlcnt);
                        }
                        if (replyAdapter.getCount() == 0 && lvQuestion != null) {
                            lvQuestion.setVisibility(View.GONE);
                        } else {
                            lvQuestion.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pullToRefreshScrollView.isRefreshing())
                            pullToRefreshScrollView.onRefreshComplete();
                        if (null != mProgressDialog && mContext != null
                                && !mActivity.isFinishing())
                            mProgressDialog.dismiss();
                    }

                });
    }

    @Override
    protected void onDestroy() {
        if (null != mProgressDialog && mContext != null) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void finish() {
        /**AroundActivity 同事圈改变评论数**/
        Intent data = new Intent();
        data.putExtra(RESPONSE_KEY_RELAY_NUMBER, replyAdapter.getCount());
        setResult(RESULT_OK, data);
        super.finish();
    }

    private InputMethodManager imm;

    /**
     * 显示键盘
     */
    public void showKeyboard(Chatter clickQuestion) {
        isClickRelay = true;
        imm.showSoftInput(etQuestionDetailContetnAnswer, 0);
        etQuestionDetailContetnAnswer.setHint("回复：" + clickQuestion.name);
        ivQuestionAnswerContent.setText("回复");
        whom = clickQuestion.uid;
    }


    /**
     * 评论回复
     */
    private void contentReplay(String content) {
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(mContext, "提问内容不能为空");
            return;
        } else if (content.length() < 5) {
            ToastUtil.showToast(mContext, "内容不少于5个字");
            return;
        }
        mProgressDialog = new WaitProgressDialog(mContext,
                R.string.action_comment_update_ing);
        mProgressDialog.show();
        ServiceProvider.ReplyComment(mContext, question.qid, content, whom, new VolleyListener(mContext) {

            @Override
            public void onResponse(Object responseObject) {
                JSONObject response = (JSONObject) responseObject;
                if (null != mProgressDialog && mContext != null
                        && !mActivity.isFinishing())
                    mProgressDialog.dismiss();
                if (pullToRefreshScrollView.isRefreshing()) {
                    pullToRefreshScrollView.onRefreshComplete();
                }
                int code = response.optInt(Net.CODE);
                if (code != Net.SUCCESS) {
                    return;
                }
                updateReply();
                // 关闭键盘
                isClickRelay = false;
                imm.hideSoftInputFromWindow(etQuestionDetailContetnAnswer.getWindowToken(), 0);
                etQuestionDetailContetnAnswer.setText("");
                etQuestionDetailContetnAnswer.setHint(R.string.comment_hint);
                ivQuestionAnswerContent.setText("发送");
            }
        });
    }
}
