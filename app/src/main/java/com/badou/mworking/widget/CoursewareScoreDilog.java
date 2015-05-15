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

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;

import org.json.JSONObject;

/**
 * 类:  <code> CoursewareScoreDilog </code>
 * 功能描述: 课件评分Dialog
 * 创建人:  葛建锋
 * 创建日期: 2015年1月12日 下午3:06:04
 * 开发环境: JDK7.0
 */
public class CoursewareScoreDilog extends Dialog implements View.OnClickListener{
	
	private Context context;
	private RatingBar scoreRatingbar;  // 评分选择器
	private TextView okBtn; //确定
	private TextView noBtn; //取消
	private TextView tipsTv; //得分提醒
	private TextView titleTv; // 文字提醒   轻点星星来评分  您已评过分了
	
	private RelativeLayout pingfenRelay;  //评分布局
	private RelativeLayout zhidaoleRelay;//已经评过分了布局
	
	private String rid;     //资源id
	private String coursewareScore="";
	
	public static Boolean ISPINGFEN = false;
	public static int SCORE;
	
	public CoursewareScoreDilogListener coursewareScoreDilogListener;
	
	public interface CoursewareScoreDilogListener{
		public void positiveListener(int coursewareScore);
	};
	
	public CoursewareScoreDilog(Context context,String rid,String coursewareScore,
			CoursewareScoreDilogListener coursewareScoreDilogListener) {
		super(context);
		//dilog 默认不要标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coursewarescoredilog);
		this.coursewareScoreDilogListener = coursewareScoreDilogListener;
		this.context = context;
		this.rid = rid;
		this.coursewareScore = coursewareScore;
		initView();
		scoreRatingbarListener();
	}
	
	
	/**
	 * 功能描述: 布局初始化
	 */
	private void initView(){
		scoreRatingbar = (RatingBar) findViewById(R.id.score_ratingbar);
		okBtn = (TextView) findViewById(R.id.ok_btn);
		noBtn = (TextView) findViewById(R.id.no_btn);
		tipsTv = (TextView) findViewById(R.id.tips_tv);
		titleTv = (TextView) findViewById(R.id.title_tv);
		pingfenRelay = (RelativeLayout) findViewById(R.id.pingfen_relay);
		zhidaoleRelay = (RelativeLayout) findViewById(R.id.zhidaole_relay);
		okBtn.setOnClickListener(this);
		noBtn.setOnClickListener(this);
		zhidaoleRelay.setOnClickListener(this);
		if(!TextUtils.isEmpty(coursewareScore)){
			pingfenRelay.setVisibility(View.GONE);
			zhidaoleRelay.setVisibility(View.VISIBLE);
			titleTv.setText("您已评过分了");
			int score = 0;
			try {
				score = Integer.valueOf(coursewareScore);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			scoreTips(score);
			scoreRatingbar.setRating(score);
			scoreRatingbar.setEnabled(false);
		}
	}
	
	@Override
	public void onClick(View v) {
		CoursewareScoreDilog.this.dismiss();     //消失对话框
		switch (v.getId()) {
		case R.id.ok_btn:   //确定
			coursewareScoring();
			break;
		case R.id.no_btn:  //取消
			break;
		default:
			break;
		}
	}
	
	/**
	 * 功能描述: Ratingbar监听器
	 */
	private void scoreRatingbarListener(){
		scoreRatingbar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				int score = (int) rating;
				scoreTips(score);
				if(rating>0){
					okBtn.setEnabled(true);
					okBtn.setBackgroundResource(R.drawable.pingfen_tijiao);
				}else{
					okBtn.setEnabled(false);
					okBtn.setBackgroundResource(R.drawable.pingfen_wei_tijiao);
				}
			}
		});
	}
	
	/**
	 *  得分文案提醒
	 */
	private void scoreTips(int score) {
		switch (score) {
		case 0:
			tipsTv.setText("");
			break;
		case 1:
			tipsTv.setText(context.getResources().getString(R.string.score_one));
			break;
		case 2:
			tipsTv.setText(context.getResources().getString(R.string.score_two));
			break;
		case 3:
			tipsTv.setText(context.getResources().getString(R.string.score_thi));
			break;
		case 4:
			tipsTv.setText(context.getResources().getString(R.string.score_fou));
			break;
		case 5:
			tipsTv.setText(context.getResources().getString(R.string.score_fif));
			break;
		default:
			break;
		}
	}
	
	/**
	 * 功能描述: 提交课件评分
	 */
	private void coursewareScoring(){
		CoursewareScoreDilog.SCORE = (int) scoreRatingbar.getRating();
		ServiceProvider.coursewareScoring(context, rid, scoreRatingbar.getRating()+"", new VolleyListener(context) {
			
			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (code == Net.SUCCESS) {
					CoursewareScoreDilog.ISPINGFEN = true;
					coursewareScoreDilogListener.positiveListener(CoursewareScoreDilog.SCORE);
				}else{
					return;
				}
			}
		});
	}
	
}
