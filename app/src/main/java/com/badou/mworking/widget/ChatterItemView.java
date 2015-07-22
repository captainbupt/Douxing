package com.badou.mworking.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.R;
import com.badou.mworking.database.ChatterResManager;
import com.badou.mworking.entity.Chatter;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.listener.TopicClickableSpan;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.TimeTransfer;

import org.json.JSONObject;

public class ChatterItemView extends LinearLayout {

    private Context mContext;
    ImageView headImageView;// 头像
    TextView nameTextView;// 用户名称
    TextView dateTextView;// 下方日期时间
    LevelTextView levelTextView; // 等级
    TextViewFixTouchConsume contentTextView;// 评论的内容
    TextView fullContentTextView;
    VideoImageView videoImageView;
    MultiImageShowGridView imageGridView;
    TextView saveInternetTextView; // 省流量模式
    TextView replyNumberTextView;// 评论的数量
    CheckBox praiseCheckBox;// 点赞chk
    TextView praiseNumberTextView;// 点赞数
    HeadClickListener headClickListener;
    PraiseClickListener praiseClickListener;

    public ChatterItemView(Context context) {
        super(context);
        initialize(context);
    }

    public ChatterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void initialize(Context context) {
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.view_chatter_item, this, true);
        headImageView = (ImageView) findViewById(R.id.iv_adapter_chatter_head);
        nameTextView = (TextView) findViewById(R.id.tv_adapter_chatter_name);
        contentTextView = (TextViewFixTouchConsume) findViewById(R.id.tv_adapter_chatter_content);
        fullContentTextView = (TextView) findViewById(R.id.tv_adapter_chatter_full_content);
        dateTextView = (TextView) findViewById(R.id.tv_adapter_chatter_time);
        videoImageView = (VideoImageView) findViewById(R.id.viv_adapter_chatter_video);
        imageGridView = (MultiImageShowGridView) findViewById(R.id.misgv_adapter_chatter_image);
        replyNumberTextView = (TextView) findViewById(R.id.tv_adapter_chatter_reply_number);
        praiseCheckBox = (CheckBox) findViewById(R.id.cb_adapter_chatter_praise);
        praiseNumberTextView = (TextView) findViewById(R.id.tv_adapter_chatter_praise_number);
        levelTextView = (LevelTextView) findViewById(R.id.tv_adapter_chatter_level);
        saveInternetTextView = (TextView) findViewById(R.id.tv_adapter_chatter_save_internet);
        headClickListener = new HeadClickListener();
        praiseClickListener = new PraiseClickListener();
        praiseNumberTextView.setOnClickListener(praiseClickListener);
        praiseCheckBox.setOnClickListener(praiseClickListener);
    }

    public void setData(Chatter chatter, boolean isHeadClickable) {
        nameTextView.setText(chatter.name);
        String content = chatter.content;
        TopicClickableSpan.setClickTopic(mContext, contentTextView, content, 100);
        if (contentTextView.getText().length() > 100) {
            fullContentTextView.setVisibility(View.VISIBLE);
        } else {
            fullContentTextView.setVisibility(View.GONE);
        }
        dateTextView.setText(TimeTransfer.long2ChatterDetailData(mContext, chatter.publishTime));
        replyNumberTextView.setText("" + chatter.replyNumber);
        ImageViewLoader.setCircleImageViewResource(headImageView, chatter.headUrl, mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

        // 评论中添加的图片
        // 没有的话，判断是否是wifi网络
        if (NetUtils.isWifiConnected(mContext) || !SPHelper.getSaveInternetOption()) {
            saveInternetTextView.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(chatter.videoUrl)) {
                videoImageView.setVisibility(View.VISIBLE);
                videoImageView.setData(chatter.imgUrl, chatter.videoUrl, chatter.qid);
                imageGridView.setVisibility(View.GONE);
            } else {
                imageGridView.setVisibility(View.VISIBLE);
                imageGridView.setList(chatter.photoUrls);
                videoImageView.setVisibility(View.GONE);
            }
        } else {
            videoImageView.setVisibility(View.GONE);
            imageGridView.setVisibility(View.GONE);
            saveInternetTextView.setVisibility(View.VISIBLE);
        }

        /** 设置点赞数和监听 **/
        praiseNumberTextView.setText(chatter.praiseNumber + "");
        // 设置显示级别
        if (chatter.name.equals("神秘的TA")) {
            levelTextView.setVisibility(View.GONE);
        } else {
            levelTextView.setVisibility(View.VISIBLE);
            levelTextView.setLevel(chatter.level);
        }
        /** 设置点赞的check **/
        if (ChatterResManager.isSelect(mContext, chatter.qid)) {
            praiseCheckBox.setChecked(true);
            praiseCheckBox.setEnabled(false);
            praiseNumberTextView.setEnabled(false);
        } else {
            praiseCheckBox.setChecked(false);
            praiseCheckBox.setEnabled(true);
            praiseNumberTextView.setEnabled(true);
        }
        if (isHeadClickable) {
            headImageView.setOnClickListener(headClickListener);
        }
        praiseClickListener.chatter = chatter;
        praiseClickListener.checkBox = praiseCheckBox;
        praiseClickListener.numberTextView = praiseNumberTextView;
        headClickListener.chatter = chatter;
    }

    class HeadClickListener implements OnClickListener {
        public Chatter chatter;

        @Override
        public void onClick(View v) {
            UserChatterInfo userChatterInfo = new UserChatterInfo(chatter);
            if (userChatterInfo.name.equals("神秘的TA")) {
                return;
            }
            Intent intent = new Intent(mContext, ChatterUserActivity.class);
            intent.putExtra(ChatterUserActivity.KEY_UID, chatter.uid);
            intent.putExtra(ChatterUserActivity.KEY_USER_CHATTER, userChatterInfo);
            mContext.startActivity(intent);
        }
    }

    class PraiseClickListener implements OnClickListener {
        public Chatter chatter;
        public CheckBox checkBox;
        public TextView numberTextView;

        @Override
        public void onClick(View arg0) {
            chatter.praiseNumber++;
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
            numberTextView.setEnabled(false);
            numberTextView.setText(chatter.praiseNumber + "");

            /** 调用同事圈点赞接口 提交点赞 **/
            ServiceProvider.doSetCredit(mContext, chatter.qid,
                    new VolleyListener(mContext) {

                        @Override
                        public void onErrorCode(int code) {
                            chatter.praiseNumber--;
                            checkBox.setChecked(false);
                            numberTextView.setText(chatter.praiseNumber + "");
                            checkBox.setEnabled(true);
                            numberTextView.setEnabled(true);
                        }

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            ChatterResManager
                                    .insertItem(mContext, chatter);
                        }
                    });
        }
    }
}
