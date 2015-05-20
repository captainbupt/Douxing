package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.CommentAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.Question;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 类: <code> CommentActivity </code> 功能描述: 评论页面 创建人: 葛建锋 创建日期: 2014年7月16日
 * 上午9:54:33 开发环境: JDK7.0
 */
public class CommentActivity extends BaseBackActionBarActivity{

	public static final String VALUE_RID = "rid";
	public static final String VALUE_FEEDBACK_COUNT = "count";
	private PullToRefreshListView contentListView;//下拉刷新
	private CommentAdapter commentAdapter;
	private ProgressDialog mProgressDialog;
	
	private EditText contentEditText; // 评论输入框
	private TextView AllCommentNumTv;//顶部显示全部评论数
	private TextView submitButton; // 评论提交按钮

	private ImageView tishiImg;  // 没有内容时的提示
	public static int commentnum; 
	public static boolean success = false;

	private int currentPage;
	private String rid = "";
	private String whom = "";

	private InputMethodManager imm;
	private ArrayList<Question> commentList;
	
	private Boolean isClickRelay = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		layout.attachToActivity(this);
		Intent intent = getIntent();
		setActionbarTitle(mContext.getResources().getString(R.string.title_name_Comment));
		rid = intent.getStringExtra(VALUE_RID);
		initView();
		commentAdapter = new CommentAdapter(mContext);
		contentListView.setAdapter(commentAdapter);
		currentPage = 1;

		initListener();
		
		try {
			String commentContext = getIntent().getExtras().getString("commentContext");
			if(commentContext!=null&&!commentContext.equals("")){
				submitComment(commentContext);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		refreshComment(1);
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * pullToRefreshListView.setMode(Mode.BOTH);
	 * 功能描述:实例化view
	 */
	protected void initView() {
		// 隐藏输入法
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		tishiImg = (ImageView) findViewById(R.id.tishi_img);
		AllCommentNumTv = (TextView) findViewById(R.id.tv_comment_Num);
		contentListView = (PullToRefreshListView) findViewById(R.id.ptrlv_comments);
		contentListView.setMode(Mode.BOTH);
		contentEditText = (EditText) findViewById(R.id.et_comment_content);
		contentEditText.requestFocus();
		submitButton = (TextView) findViewById(R.id.tv_comment_submit);
		submitButton.setEnabled(false);
		submitButton.setBackgroundColor(getResources().getColor(R.color.color_grey));
		mProgressDialog = new WaitProgressDialog(mContext,R.string.message_wait);
		mProgressDialog.show();
	}

	/**
	 * 
	 * 功能描述:设置监听
	 */
	protected void initListener() {
		// 字符长度监听
		contentEditText.setFilters(new InputFilter[] { new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				if (dstart > 79)
					return "";
				return null;
			}
		} });

		// 字符改变监听
		contentEditText.addTextChangedListener(new TextWatcher() {

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
				// 文本改变监听
				int length = contentEditText.getText().toString().trim().length();
				
				if (length==0) {
					submitButton.setEnabled(false);
					submitButton.setBackgroundColor(getResources().getColor(R.color.color_grey));
				} else {
					submitButton.setEnabled(true);
					submitButton.setBackgroundResource(R.drawable.comment_send_blue);
				}
				
			}
		});

		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String comment = contentEditText.getText().toString();
				if (TextUtils.isEmpty(comment.trim()) || comment == null
						|| comment.isEmpty() || comment.length() <= 0) {
					ToastUtil.showToast(mContext, "评论内容不能为空！");
					return;
				}
				if (comment.length()<5) {
					ToastUtil.showToast(mContext, R.string.comment_tips_length);
					return;
				}
				contentEditText.setText("");
				comment = comment.replaceAll("\\n", "");
				submitComment(comment);
				
				// 显示或者隐藏输入法
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

			}
		});
		contentListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				
				if(refreshView.getCurrentMode()==Mode.PULL_FROM_START){
					// 这里刷新listview数据,只加载第一页的数据
					refreshComment(1);
				}else if(refreshView.getCurrentMode()==Mode.PULL_FROM_END){
					refreshComment(currentPage + 1);
				}
			}
		});
		contentEditText.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(isClickRelay){
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						isClickRelay = false;
				    	imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				    	contentEditText.setHint(R.string.comment_hint);
				    	submitButton.setText("发送");
						return true;
					}
				}
				return false;
			}
		});
		
		contentListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position,
					long arg3) {
				Question question =  commentList.get(position-1);
				whom = question.getWhom();
				String userName = question.getEmployee_id().trim();
				// 不可以回复我自己
				if(userName.equals("我")){
					return;
				}
				showKeyboard(question);
			}
		});
	}

	/**
	 * 功能描述:根据页码刷新评论
	 * @param pageNumber
	 */
	private void refreshComment(final int pageNumber) {
		ServiceProvider.doUpdateComment(mContext, rid, pageNumber,
				new VolleyListener(mContext) {

					@Override
					public void onResponse(Object responseObject) {
						JSONObject response = (JSONObject) responseObject;
						if (null != mProgressDialog && mContext != null
								&& !mActivity.isFinishing()) {
							mProgressDialog.dismiss();
						}
						contentListView.onRefreshComplete();
						try {
							int code = response.optInt(Net.CODE);
							if (code==Net.LOGOUT) {
								AppApplication.logoutShow(mContext);
								return;
							}
							if (code != Net.SUCCESS) {
								ToastUtil.showToast(mContext, R.string.result_comment_update_fail);
								return;
							}
							
							currentPage = pageNumber;
							int allCount = response
									.optJSONObject(Net.DATA).optInt("ttlcnt");
							AllCommentNumTv.setText(allCount+"");
							CommentActivity.commentnum = allCount;
							updateSuccess(response
									.optJSONObject(Net.DATA)
									.optJSONArray(ResponseParams.COMMENT_RESULT), allCount);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						if (null != mProgressDialog && mContext != null
								&& !mActivity.isFinishing()) {
							mProgressDialog.dismiss();
						}
						contentListView.onRefreshComplete();
						super.onErrorResponse(error);
					}
				});
	}

	private void updateSuccess(JSONArray jsonArray,int allCount) {
		CommentActivity.success = true;
		int length = jsonArray.length();
		// 如果没有内容的话，显示默认图片
		if(currentPage<=1&&length == 0){  
			tishiImg.setVisibility(View.VISIBLE);
			contentListView.setVisibility(View.GONE);
		}else{
			tishiImg.setVisibility(View.GONE);
			contentListView.setVisibility(View.VISIBLE);
		}
		commentList = new ArrayList<Question>();
		for (int i = 0; i < length; i++) {
			try {
				commentList.add(new Question(jsonArray.getJSONObject(i),
						Question.MODE_COMMENT));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (currentPage == 1){
			commentAdapter.setDatas(commentList,allCount);
		}else{
			commentAdapter.addDatas(commentList,allCount);
		}
	}

	private void submitComment(String comment) {
		mProgressDialog = new WaitProgressDialog(mContext,R.string.action_comment_update_ing);
		mProgressDialog.show();

		String buttonText = submitButton.getText().toString().trim();
		if(buttonText.equals("回复")){
			ServiceProvider.doReplayComment(mContext, rid, whom, comment, new VolleyListener(mContext) {
				
				@Override
				public void onResponse(Object responseObject) {
					JSONObject response = (JSONObject) responseObject;
					submitSuccess(response);
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
		}else if(buttonText.equals("发送")){
			ServiceProvider.doSubmitComment(mContext, rid, comment, new VolleyListener(mContext) {
				
				@Override
				public void onResponse(Object responseObject) {
					JSONObject response = (JSONObject) responseObject;
					submitSuccess(response);
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
	}
	
	/**
	 *  发送成功
	 */
	private void submitSuccess(JSONObject response){
		try {
			int code = response.optInt(Net.CODE);
			if (code==Net.LOGOUT) {
				AppApplication.logoutShow(mContext);
				return;
			}
			if (code != Net.SUCCESS) {
				ToastUtil.showToast(mContext, R.string.result_comment_submit_fail);
				return;
			}
			refreshComment(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 显示键盘
	 * */
	public void showKeyboard(Question clickQuestion) {
		isClickRelay = true;
		imm.showSoftInput(contentEditText, 0); 
		contentEditText.setHint("回复："+clickQuestion.getEmployee_id());
		submitButton.setText("回复");
	}
}
