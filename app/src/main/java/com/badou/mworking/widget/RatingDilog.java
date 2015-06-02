/* 
 * 文件名: CoursewareScoreDilog.java
 * 包路径: com.badou.mworking.widget
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2015年1月12日 下午3:06:04
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2015年1月12日 下午3:06:04 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking.widget;

import android.content.Context;
import android.view.View;
import android.widget.RatingBar;

import com.badou.mworking.R;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.holoeverywhere.app.Dialog;
import org.holoeverywhere.widget.RelativeLayout;
import org.holoeverywhere.widget.TextView;
import org.json.JSONObject;

/**
 * 功能描述: 课件评分Dialog
 */
public class RatingDilog extends Dialog {

    private Context mContext;
    private RatingBar scoreRatingbar;  // 评分选择器
    private TextView okBtn; //确定
    private TextView noBtn; //取消
    private TextView tipsTv; //得分提醒
    private TextView titleTv; // 文字提醒   轻点星星来评分  您已评过分了

    private RelativeLayout pingfenRelay;  //评分布局
    private RelativeLayout zhidaoleRelay;//已经评过分了布局

    private String mRid;     //资源id
    private int mCurrentScore = -1;

    public OnRatingCompletedListener mOnRatingCompletedListener;

    public interface OnRatingCompletedListener {
        public void onRatingCompleted(int coursewareScore);
    }

    ;

    public RatingDilog(Context context, String rid, int currentScore,
                       OnRatingCompletedListener listener) {
        super(context);
        setContentView(R.layout.dialog_rating);
        this.mOnRatingCompletedListener = listener;
        this.mContext = context;
        this.mRid = rid;
        this.mCurrentScore = currentScore;
        initView();
        initListener();
    }


    /**
     * 功能描述: 布局初始化
     */
    private void initView() {
        scoreRatingbar = (RatingBar) findViewById(R.id.score_ratingbar);
        okBtn = (TextView) findViewById(R.id.ok_btn);
        noBtn = (TextView) findViewById(R.id.no_btn);
        tipsTv = (TextView) findViewById(R.id.tips_tv);
        titleTv = (TextView) findViewById(R.id.title_tv);
        pingfenRelay = (RelativeLayout) findViewById(R.id.pingfen_relay);
        zhidaoleRelay = (RelativeLayout) findViewById(R.id.zhidaole_relay);
        if (mCurrentScore > -1) {
            pingfenRelay.setVisibility(View.GONE);
            zhidaoleRelay.setVisibility(View.VISIBLE);
            titleTv.setText("您已评过分了");
            scoreTips(mCurrentScore);
            scoreRatingbar.setRating(mCurrentScore);
            scoreRatingbar.setEnabled(false);
        }
    }

    private void initListener() {
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coursewareScoring();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        zhidaoleRelay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Ratingbar监听器
        scoreRatingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int score = (int) rating;
                scoreTips(score);
                if (rating > 0) {
                    okBtn.setEnabled(true);
                    okBtn.setBackgroundResource(R.drawable.pingfen_tijiao);
                } else {
                    okBtn.setEnabled(false);
                    okBtn.setBackgroundResource(R.drawable.pingfen_wei_tijiao);
                }
            }
        });
    }

    /**
     * 得分文案提醒
     */
    private void scoreTips(int score) {
        switch (score) {
            case 0:
                tipsTv.setText("");
                break;
            case 1:
                tipsTv.setText(mContext.getResources().getString(R.string.score_one));
                break;
            case 2:
                tipsTv.setText(mContext.getResources().getString(R.string.score_two));
                break;
            case 3:
                tipsTv.setText(mContext.getResources().getString(R.string.score_thi));
                break;
            case 4:
                tipsTv.setText(mContext.getResources().getString(R.string.score_fou));
                break;
            case 5:
                tipsTv.setText(mContext.getResources().getString(R.string.score_fif));
                break;
            default:
                break;
        }
    }

    /**
     * 功能描述: 提交课件评分
     */
    private void coursewareScoring() {
        final int rating = (int) scoreRatingbar.getRating();
        ServiceProvider.coursewareScoring(mContext, mRid, rating + "", new VolleyListener(mContext) {

            @Override
            public void onResponseData(JSONObject jsonObject) {
                if (mOnRatingCompletedListener != null)
                    mOnRatingCompletedListener.onRatingCompleted(rating);
                dismiss();
            }
        });
    }

}
