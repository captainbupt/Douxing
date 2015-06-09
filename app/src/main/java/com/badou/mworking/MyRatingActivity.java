package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.badou.mworking.adapter.MakeupExaminationAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.category.Exam;
import com.badou.mworking.model.MyExamRating;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 等级考试进入的等级页面
 */
public class MyRatingActivity extends BaseNoTitleActivity implements OnClickListener,OnItemClickListener{
	
	protected SwipeBackLayout layout;
	private ImageView ivActionbarLeft;
	private ImageView RatingTtips; // 等级说明
	private TextView  actionbarTitle;
	private TextView ratingTv;   // 您在XX等级考试中
	private TextView  actionbarRight; 
	private TextView nowRatingTv;   //当前等级
	private TextView nextRatingTv;  //下一等级
	private TextView averageScoreTv;  //加权平均分
	private TextView aboveTv;   // 高于及格线
	private TextView commentRelat;  //参加补考，继续晋级
	private TextView tipsTv;    //  提示文本
	private TextView examTips;  // 你可以在加强复习下这些
	private ListView makeupExamLv;   //补考试题listview
	private Exam jinjiExam = null;     // 如果晋级的话，需要的exam
	private ArrayList<Exam> exams ;
	private MakeupExaminationAdapter makeupExaminationAdapter;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myratingactivity);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		exams = new ArrayList<Exam>();
		//exams.addAll(ExamActivity.list);
		initView();
		getViewrank();
	}
	
	protected void initView(){
		ivActionbarLeft = (ImageView) findViewById(R.id.iv_actionbar_left);
		RatingTtips = (ImageView) findViewById(R.id.Rating_tips);
		actionbarTitle = (TextView) findViewById(R.id.tv_actionbar_title);
		actionbarRight = (TextView) findViewById(R.id.tv_actionbar_right);
		nowRatingTv = (TextView) findViewById(R.id.now_rating_tv);
		nextRatingTv = (TextView) findViewById(R.id.next_rating_tv);
		averageScoreTv = (TextView) findViewById(R.id.average_score);
		tipsTv = (TextView) findViewById(R.id.tv_dialog_rating_tips);
		ratingTv = (TextView) findViewById(R.id.rating_tv);
		aboveTv = (TextView) findViewById(R.id.above_tv);
		examTips = (TextView) findViewById(R.id.exam_tips);
		commentRelat = (TextView) findViewById(R.id.tv_user_progress_bottom);
		makeupExamLv = (ListView) findViewById(R.id.makeup_exam_lv);
		makeupExamLv.setOnItemClickListener(this);
		ivActionbarLeft.setOnClickListener(this);
		actionbarRight.setOnClickListener(this);
		commentRelat.setOnClickListener(this);
		RatingTtips.setOnClickListener(this);
		actionbarRight.setVisibility(View.VISIBLE);
		actionbarTitle.setText("考试等级");
		commentRelat.setText("考试晋级");
		commentRelat.setVisibility(View.INVISIBLE);
		actionbarRight.setText(getResources().getString(R.string.yikaoshiti_str));
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_left:
			MyRatingActivity.this.finish();
			break;
		case R.id.tv_actionbar_right:
			Intent intent = new Intent();
			intent.setClass(this, HistoryExamActivity.class);
			startActivity(intent);
			break;
		case R.id.Rating_tips:
			Intent intentl = new Intent();
			intentl.setClass(MyRatingActivity.this, BackWebActivity.class);
			intentl.putExtra(BackWebActivity.KEY_URL, "http://mworking.cn:8421/webview/rankpfscope.html");
			intentl.putExtra(BackWebActivity.KEY_TITLE, "计算规则");
			MyRatingActivity.this.startActivity(intentl);
			break;
		case R.id.tv_user_progress_bottom:
			String buttonContent = commentRelat.getText().toString();
			if(buttonContent.equals("参加补考")){
				Exam exam =exams.get(0);
				chakanExam(exam);
			}else if(buttonContent.equals("考试晋级")){
				if(jinjiExam!=null){
					chakanExam(jinjiExam);
				}
			}
			//  为空的话，暂时没有分数， 用String来取值，应为int有默认值，没有的话，直接就是0了
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		Exam exam = exams.get(position);
		chakanExam(exam);
	}
	
	/**
	 *  查看考试
	 */
	private void chakanExam(Exam exam){
		if(exam == null){
			return;
		}
		int subtype = exam.subtype;
		if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
			return;
		}
		// 考试没有联网
		if (NetUtils.isNetConnected(mContext)) {
			ToastUtil.showNetExc(mContext);
			return;
		}
		String uid = ((AppApplication) mContext.getApplicationContext()).getUserInfo().userId;
		String url =  Net.getRunHost(mContext)+Net.EXAM_ITEM(uid, exam.rid);
		Intent intents = new Intent(mContext, BackWebActivity.class);
		intents.putExtra(BackWebActivity.KEY_URL,url);
		startActivity(intents);
	}
	
	/**
	 * 功能描述:
	 */
	private void getViewrank() {
		// 等级页面的tag返回时是负值，然后请求的时候需要变成正数
		ServiceProvider.getViewrank(MyRatingActivity.this,/*Math.abs(ExamActivity.tag)+""*/"",new VolleyListener(mContext) {
			
			@Override
			public void onResponse(Object responseObject) {
				JSONObject jsonObject = (JSONObject) responseObject;
				int errcode = jsonObject.optInt(Net.CODE);
				if (errcode != 0) {
					return;
				}
				JSONObject jObject = null;
				try {
					jObject = new JSONObject(jsonObject.optString(Net.DATA));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (jObject == null) {
					return;
				}
				MyExamRating myExamRating = new MyExamRating(jObject);
				nowRatingTv.setText(myExamRating.getTitle_now());
				String title_next = myExamRating.getTitle_next();
				if(TextUtils.isEmpty(title_next)){
					nextRatingTv.setText("暂时没有");
					tipsTv.setText("你已经考到了最高级了呀～");
					commentRelat.setBackgroundResource(R.drawable.pinglu_grey);
					commentRelat.setEnabled(false);
				}else{
					nextRatingTv.setText(myExamRating.getTitle_next());
				}
				String Self_score = myExamRating.getSelf_score();
				if(TextUtils.isEmpty(Self_score)){
					averageScoreTv.setText("暂时没有分数");
					aboveTv.setText("——");;
				}else{
					try {
						averageScoreTv.setText("加权平均分 "+myExamRating.getSelf_score());
						int aboveScore = Integer.valueOf(myExamRating.getSelf_score()) - myExamRating.getAvg_score();
						if(aboveScore>0){
							aboveTv.setText("高于及格线"+Math.abs(aboveScore));
						}else if(aboveScore < 0){
							aboveTv.setText("低于及格线"+Math.abs(aboveScore));
						}else if(aboveScore == 0){
							aboveTv.setText("刚好达到了及格线");
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				ratingTv.setText("在"+myExamRating.getTitle_now()+"中");
				getDataFromJsonObject();
			}
		});
	}
	
	private void getDataFromJsonObject(){
		ArrayList<Exam> examTemps = new ArrayList<Exam>();
		if(exams!=null||exams.size()>0){
			// 注意： 在集合便利的时候，不要移除考试，以防止报错，加强版的for循环，不知道会不会，但是int i 那种会报错，保险起见，用下面的方式
			for(Exam exam:exams){
				// 移除未考的考试
				if(!exam.isRead()){
					//　将其中第一个未考的考试保存下来，如果考试全部都通过了的话，点击考试晋级，进入到该页面
					if(jinjiExam == null){
						jinjiExam = exam;
					}
					examTemps.add(exam);
					continue;
				}
				// 移除已经通过的考试
				if(exam.score>=exam.pass){
					examTemps.add(exam);
					continue;
				}
			}
			exams.removeAll(examTemps);
		}
		if(exams.size()>0){
			commentRelat.setText("参加补考");
			commentRelat.setBackgroundResource(R.drawable.pinglu_bg);
			commentRelat.setEnabled(true);
		}else{
			examTips.setVisibility(View.GONE);
			makeupExamLv.setVisibility(View.GONE);
			tipsTv.setVisibility(View.VISIBLE);
		}
		commentRelat.setVisibility(View.VISIBLE);
		makeupExaminationAdapter = new MakeupExaminationAdapter(MyRatingActivity.this, exams);
		makeupExamLv.setAdapter(makeupExaminationAdapter);
	}
}
