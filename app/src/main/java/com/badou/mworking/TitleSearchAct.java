package com.badou.mworking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.ExamAdapter;
import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.adapter.SearchAdapter;
import com.badou.mworking.adapter.TaskAdapter;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseFragmentActivity;
import com.badou.mworking.fragment.ChattingFragment;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.ContanctsList;
import com.badou.mworking.model.Exam;
import com.badou.mworking.model.Notice;
import com.badou.mworking.model.Task;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * 类: <code> TitleSearchAct </code> 功能描述: 搜索页面activity 创建人: 葛建锋 创建日期: 2014年9月4日
 * 下午7:19:21 开发环境: JDK7.0
 */
public class TitleSearchAct extends BaseFragmentActivity implements OnRefreshListener2<ListView>{
	
	public static final String SEARCH_KEY_VALUE = "search_key";
	public static final String SEARCH_CHAT = "SEARCH_CHAT";
	public static final String SEARCH_TAG = "SEARCH_tag";
	public static final String SEARCH_MY_EXAM = "SEARCH_MY_EXAM";

	private EditText etInput;
	private TextView searchBtn;
	private Context mContext;
	private Intent getIntent;
	private InputMethodManager imm;
	
	private SearchAdapter arrAdapter;
	private ArrayList<String> subjectList;
	private ListView tipsLv;
	private RelativeLayout layoutLv;
	private ImageView backImg;
	
	private String keyValue = "";      // 获取搜索栏目
	private int beginIndex = 0;

	private int tag = 0;  //点击的分类tag
	private String searchStr = "";
	
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<Notice> notices;     //通知公告
	private ArrayList<Task> tasks ;     //通知公告
	private ArrayList<Exam> exams ;     //通知公告
	private ArrayList<Train> trains ;     //通知公告
	
	private NoticeAdapter noticeAdapter = null;
	private TaskAdapter taskAdapter = null;
	private ExamAdapter examAdapter = null;
	private TrainAdapter trainAdapter = null;
	
	private Dialog dialog;//
	private AlertDialog.Builder loadDialog;// 显示的提示框
	private ProgressBar pro;// 文件下载的进度条
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_search);
		layout.attachToActivity(TitleSearchAct.this);
		mContext = TitleSearchAct.this;
		try {
			getIntent = getIntent();
			keyValue = getIntent.getStringExtra(SEARCH_KEY_VALUE);
			tag = getIntent.getIntExtra(SEARCH_TAG, 0);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		initView();
		initListener();
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	private void initView() {
		// 隐藏输入法
		imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		etInput = (EditText) this.findViewById(R.id.et_search);
		searchBtn = (TextView) this.findViewById(R.id.search_btn);
		backImg = (ImageView) this.findViewById(R.id.back_Img);
		tipsLv = (ListView) this.findViewById(R.id.lv_tips_subject);
		layoutLv = (RelativeLayout) this.findViewById(R.id.relayout_lv);
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.PullToRefreshListView);
		// 初始化对话框
		loadDialog = new AlertDialog.Builder(this);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.BOTH);
		
		// 考试
		if(keyValue.equals(Category.CATEGORY_EXAM)){
			examAdapter = new ExamAdapter(this, null,"0");
			pullToRefreshListView.setAdapter(examAdapter);
		}
		// 通知
		if(keyValue.equals(Category.CATEGORY_NOTICE)){
			noticeAdapter = new NoticeAdapter(TitleSearchAct.this,null);
			pullToRefreshListView.setAdapter(noticeAdapter);
		}
		// 签到
		if(keyValue.equals(Category.CATEGORY_TASK)){
			taskAdapter = new TaskAdapter(this, null);
			pullToRefreshListView.setAdapter(taskAdapter);
		}
		// 培训
		if(keyValue.equals(Category.CATEGORY_TRAIN)){
			trainAdapter = new TrainAdapter(this, null, "train");
			pullToRefreshListView.setAdapter(trainAdapter);
		}
		
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// 考试
				if(keyValue.equals(Category.CATEGORY_EXAM)){
					examItemClick(position);
				}
				// 通知
				if(keyValue.equals(Category.CATEGORY_NOTICE)){
					noticeItemClick(position);
				}
				// 签到
				if(keyValue.equals(Category.CATEGORY_TASK)){
					taskItemClick(position);
				}
				// 培训
				if(keyValue.equals(Category.CATEGORY_TRAIN)){
					trainItemClick(position);
				}
				
			}
		});
	}
	
	/**
	 * @param position
	 * 
	 */
	private void noticeItemClick(int position){
		Notice notice = noticeAdapter.getItem(position - 1);
		if (Constant.MWKG_FORAMT_TYPE_HTML!=notice.getSubType()) {
			return;
		}
		if (NetUtils.isNetConnected(TitleSearchAct.this)) {
			noticeAdapter.read(position - 1);
			ServiceProvider.doMarkRead(TitleSearchAct.this, notice.getRid());
			showDetail(notice);
			noticeAdapter.notifyDataSetChanged();
		} else {
			ToastUtil.showNetExc(TitleSearchAct.this);
			return;
		}
	}
	
	/**
	 * 功能描述: 显示通知的详细内容
	 * @param notice
	 */
	private void showDetail(Notice notice) {
		int subtype = notice.getSubType();
		if(Constant.MWKG_FORAMT_TYPE_HTML!=subtype){
			return;
		}
		Intent intent = new Intent();
		intent.setClass(TitleSearchAct.this, BackWebActivity.class);
		intent.putExtra(BackWebActivity.VALUE_URL, notice.getUrl()
				+ "&uid="
				+ ((AppApplication) getApplicationContext())
						.getUserInfo().getUserId());
		// 获取分类名
		String title = SP.getStringSP(TitleSearchAct.this, SP.NOTICE, notice.getTag()+"", "");
		intent.putExtra(BackWebActivity.VALUE_TITLE, title);
		intent.putExtra(BackWebActivity.VALUE_RID, notice.getRid());
		BackWebActivity.PAGEFLAG = BackWebActivity.NOTICE;    //设置跳转是通知公告
		TitleSearchAct.this.startActivity(intent);
		// 设置切换动画，从右边进入，左边退出
		overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
	}

	
	
	private void examItemClick(int position){
		BackWebActivity.PAGEFLAG = BackWebActivity.EXAM;
		Exam exam = examAdapter.getItem(position - 1);
		int subtype = exam.getType();
		if (Constant.MWKG_FORAMT_TYPE_XML != subtype) {
			return;
		}
		// 考试没有联网
		if(ToastUtil.showNetExc(TitleSearchAct.this)){
			return;
		}
		String uid = ((AppApplication) TitleSearchAct.this.getApplicationContext()).getUserInfo().getUserId();
		String url =  Net.getRunHost(TitleSearchAct.this)+Net.EXAM_ITEM(uid, exam.getExamId());
		Intent intents = new Intent(TitleSearchAct.this, BackWebActivity.class);
		intents.putExtra(BackWebActivity.VALUE_URL,url);
		int tag = exam.getTag();
		String title = "";
		if(tag>=0){
			// 获取分类名
			title = SP.getStringSP(TitleSearchAct.this, SP.NOTICE, tag+"", "");
		}else{
			title = ExamActivity.CLASSIFICATIONNAME;
		}
		intents.putExtra(BackWebActivity.VALUE_TITLE,title); 
		startActivity(intents);
		// 设置切换动画，从右边进入，左边退出
		overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}
	
	
	private void taskItemClick(int position){
		// 获取点中的item所对应的task，并将其作为参数传递给下一个activity
		Task task = taskAdapter.getItem(position - 1);
		int subtype = task.getSubtype();
		if(Constant.MWKG_FORAMT_TYPE_XML != subtype){
			return;
		}
		if (NetUtils.isNetConnected(mContext)) {
			// 向服务提交课件信息
			ServiceProvider.doMarkRead(mContext, task.getRid());
		}
		Bundle bundle = new Bundle();
		bundle.putSerializable(SignActivity.INTENT_TASK, task);
		Intent intent = new Intent(mContext, SignActivity.class);
		intent.putExtra(SignActivity.INTENT_TASK, bundle);
		// 获取分类名
		String title = SP.getStringSP(TitleSearchAct.this, SP.TASK, task.getTag()+"", "");
		intent.putExtra("title", title);
		startActivity(intent);
		//设置切换动画，从右边进入，左边退出
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	private void trainItemClick(int position){
		Train train = trainAdapter.getItem(position - 1);
		int subtype = train.getSubtype();
		if (NetUtils.isNetConnected(TitleSearchAct.this)) {
			// 向服务提交课件信息
			trainAdapter.read(position - 1);
			ServiceProvider.doMarkRead(TitleSearchAct.this, train.getRid());
		}
		//返回PDF格式
		if(Constant.MWKG_FORAMT_TYPE_PDF == subtype){
			toPDFAndWeb(train);
		// 返回MP4格式	
		}else if(Constant.MWKG_FORAMT_TYPE_MPEG == subtype){
			Intent intentToMusic = new Intent(TitleSearchAct.this, TrainVideoPlayerAct.class);
			Bundle bu = new Bundle();
			bu.putSerializable("train", train);
			intentToMusic.putExtra("train", bu);
			startActivity(intentToMusic);
		// 返回html格式	
		}else if(Constant.MWKG_FORAMT_TYPE_HTML == subtype){
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("train", train);
			intent.putExtra("train", bundle);
			String url = train.getUrl() + "&uid="
					+ ((AppApplication)getApplicationContext())
					.getUserInfo().getUserId();
			intent.putExtra(BackWebActivity.VALUE_URL, url);
			// 获取分类名
			String title = SP.getStringSP(TitleSearchAct.this, SP.TRAINING, train.getTag()+"", "");
			intent.putExtra(BackWebActivity.VALUE_TITLE, title);
			intent.setClass(TitleSearchAct.this, BackWebActivity.class);
			BackWebActivity.PAGEFLAG = BackWebActivity.TRAINING;   // 设置是通过微培训跳转过去的
			TitleSearchAct.this.startActivity(intent);
		}else if(Constant.MWKG_FORAMT_TYPE_MP3 == subtype){
			Intent intentToMusic = new Intent(TitleSearchAct.this, TrainMusicActivity.class);
			Bundle bu = new Bundle();
			bu.putSerializable("train", train);
			intentToMusic.putExtra("train", bu);
			startActivity(intentToMusic);
		}else{
			return;
		}
		trainAdapter.notifyDataSetChanged();
	}
	
	
	private void toPDFAndWeb(Train train) {       
		/*** 判断api,太小用web **/
		/*** 判断是pdf还是web **/
		if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
			// 声明pdf文件要保存的路径
			if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
				ToastUtil.showToast(TitleSearchAct.this, R.string.train_sd_size_);
				return;
			}
			String path = FileUtils.getTrainCacheDir(TitleSearchAct.this) + train.getRid() + ".pdf";
			File file = new File(path);
			// pdf文件不存在
			if (!file.exists() || !file.isFile() || file.isDirectory()
					|| file.length() == 0) {
				file.delete();
				// 显示对话框
				dialog = loadPro(loadDialog).show();
				if (NetUtils.isNetConnected(TitleSearchAct.this)) {
					// 开启线程
					new DownloadThread(train).start();
				} else {
					if (dialog != null && dialog.isShowing()
							&& !((Activity) TitleSearchAct.this).isFinishing()) {
						// 关闭进度条对话框
						dialog.dismiss();
					}
					ToastUtil.showToast(TitleSearchAct.this, R.string.error_service);
				}
			} else {
				// pdf文件已存在 调用
				toPdfViewer(train);
			}
		} else {// web
			Intent intent = new Intent(TitleSearchAct.this, BaseViewerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("train", train);
			intent.putExtra("train", bundle);
			intent.putExtra(TrainActivity.KEY_webView_pdf, TrainActivity.KEY_webView_pdf);
			startActivity(intent);
		}
	}
	
	/**
	 * 类: <code> DownloadThread </code> 功能描述: 下载pdf文件的线程 创建人:董奇 创建日期: 2014年7月16日
	 * 上午9:30:29 开发环境: JDK7.0
	 */
	class DownloadThread extends Thread {
		private Train train;
		private String path;

		public DownloadThread(Train train) {
			super();
			this.train = train;
		}

		@Override
		public void run() {
			super.run();
			if(Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()){
				path = FileUtils.getTrainCacheDir(TitleSearchAct.this) + train.getRid() + ".pdf";
			}
			if(path==null||path.equals("")){
				return;
			}
			// 通过url下载pdf文件
			int statu = HttpDownloader.downFile(train.getUrl()
					+ "&uid="
					+ ((AppApplication) getApplicationContext())
							.getUserInfo().getUserId(), path,
					new DownloadListener() {

						@Override
						public void onDownloadSizeChange(int downloadSize) {
							// 已下载的大小
							Message.obtain(handler, TrainActivity.PROGRESS_CHANGE,
									downloadSize).sendToTarget();
						}

						@Override
						public void onDownloadFinish(String filePath) {
							// 下载完成
							Message.obtain(handler, TrainActivity.PROGRESS_FINISH, "")
									.sendToTarget();
						}

						@Override
						public void onGetTotalSize(int totalSize) {
							// 文件大小
							Message.obtain(handler, TrainActivity.PROGRESS_MAX, totalSize)
									.sendToTarget();
						}
					});
			// 下载成功,向handler传递消息
			Message msg = new Message();
			msg.what = statu;
			Bundle bundle = new Bundle();
			bundle.putSerializable("train", train);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}
	
	private Handler handler = new Handler() {            

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 接受线程中传递的消息
			int statu = msg.what;
			Bundle bundle = msg.getData();
			Train train = (Train) bundle.getSerializable("train");
			String path = "";
			// 声明文件保存路径 用rid命名
			if (train != null) {
				if(Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()){
					 path = FileUtils.getTrainCacheDir(TitleSearchAct.this) + train.getRid() + ".pdf";
				}else if(Constant.MWKG_FORAMT_TYPE_MPEG == train.getSubtype()){
					 path = FileUtils.getTrainCacheDir(TitleSearchAct.this) + train.getRid() + ".mp4";
				}
				if(path==null||path.equals("")){
					return;
				}
				File file = new File(path);
				if (statu == -1 || !file.exists() || file.length() == 0) {
					// 文件下载失败 提示
					ToastUtil.showToast(TitleSearchAct.this,
							R.string.train_result_download_fail);
				} else {
					if (statu != 0) {
						ToastUtil.showToast(TitleSearchAct.this,
								R.string.train_result_download_exist);
					} 
					if(Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()){
						// 下载完成 调用
						toPdfViewer(train);
					}
				}
			}
			switch (msg.what) {
			case TrainActivity.REFRESH_EXAM_LV:
				if (Constant.setAdapterRefresh) {
					// 刷新 listview
					pullToRefreshListView.setRefreshing();
					Constant.setAdapterRefresh = false;
				} 
				break;
			case TrainActivity.PROGRESS_MAX:
				if (pro != null) {
					pro.setMax((int) msg.obj);
				}
				break;
			case TrainActivity.PROGRESS_CHANGE:
				// 设置进度条改变
				if (pro != null) {
					pro.setProgress((int) msg.obj);
				}
				break;
			case TrainActivity.PROGRESS_FINISH:
				if (dialog != null && dialog.isShowing()
						&& !((Activity) TitleSearchAct.this).isFinishing()) {
					// 关闭进度条对话框
					dialog.dismiss();
				}
				break;

			default:
				break;
			}

		}
	};
	
	/**
	 * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
	 * 
	 * @param train
	 */
	private void toPdfViewer(Train train) {
		if (!((Activity) TitleSearchAct.this).isFinishing()) {
			// 系统版本>=11 使用第三方的pdf阅读
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("train", train);
				intent.putExtra("train", bundle);
				intent.setClass(TitleSearchAct.this, BaseViewerActivity.class);
				startActivity(intent);
				// 设置切换动画，从右边进入，左边退出
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} 
		}
	}
	
	/**
	 * 
	 * 功能描述:初始化AlertDialog的布局 初始化progerssBar
	 * 
	 * @param dialog
	 * @return
	 */
	public AlertDialog.Builder loadPro(AlertDialog.Builder dialog) {        
		if (dialog == null) {
			dialog = new AlertDialog.Builder(TitleSearchAct.this);
		}

		View loadView = new View(TitleSearchAct.this);
		// 对话框加载布局文件
		loadView = LayoutInflater.from(TitleSearchAct.this).inflate(
				R.layout.load_progerss_layout, null);
		pro = (ProgressBar) loadView.findViewById(R.id.load_progressBar);
		pro.setProgress(0);
		dialog.setView(loadView);
		return dialog;
	}
	
	private void initListener() {
		searchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				searchMethod();
			}
		});
		// 返回按钮
		backImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// 显示或者隐藏输入法
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				finish();
			}
		});

		// 输入法下标的点击响应事件
		etInput.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchMethod();
					return true;
				}
				return false;
			}
		});
		
		arrAdapter = new SearchAdapter(mContext);
		tipsLv.setAdapter(arrAdapter);
		etInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				int inputCount = etInput.getText().length();
				String etStr = etInput.getText().toString();
				if (inputCount > 0) {
					 if (keyValue!=null && SEARCH_CHAT.equals(keyValue)) {
						layoutLv.setVisibility(View.VISIBLE);
						final ArrayList<ContanctsList> chatList = (ArrayList<ContanctsList>) getIntent.getSerializableExtra(ChattingActivity.CHAT_TAG);
						final ArrayList<ContanctsList> searchList = new ArrayList<ContanctsList>();
						subjectList = new ArrayList<String>();
						for (int i = 0; i < chatList.size(); i++) {
							String tempSubject = chatList.get(i).getName();
							if (tempSubject.indexOf(etStr)>-1) {
								subjectList.add(tempSubject);
								searchList.add(chatList.get(i));
							}
						}
						arrAdapter.setData(subjectList);
						tipsLv.setAdapter(arrAdapter);
						tipsLv.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
								Intent intent = new Intent(mContext, ChatInfoActivity.class);
								intent.putExtra(ChatInfoActivity.KEY_NAME, searchList.get(position).getName());
								intent.putExtra(ChatInfoActivity.KEY_whom, searchList.get(position).getWhom());
								intent.putExtra(ChatInfoActivity.KEY_img, searchList.get(position).getImg());
								intent.putExtra(ChattingFragment.KEY_HEAD_URL, SP.getStringSP(mContext,SP.DEFAULTCACHE,ChattingFragment.KEY_HEAD_URL, ""));
								startActivity(intent);
								finish();
							}
						});
					} 
				} else {
					layoutLv.setVisibility(View.GONE);
				}
				arrAdapter.notifyDataSetChanged();
			}
		});
	}

	
	/**
	 * 功能描述: 搜索方法
	 */
	public void searchMethod(){
		searchStr = etInput.getText().toString();
		if (TextUtils.isEmpty(searchStr)) {
			ToastUtil.showToast(mContext, "请输入关键字");
		} else {
			//在点击完搜索之后，隐藏键盘，该方法，如果键盘显示则隐藏，键盘隐藏则显示
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			
			// 显示搜索到的内容
			pullToRefreshListView.setRefreshing();
		}
	}
	
	// 判断按键 菜单的显示与隐藏
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}
	
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		beginIndex = 0;
		updataListView(beginIndex,searchStr);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		updataListView(beginIndex,searchStr);
	}
	
	private void updataListView(final int beginNum,String searchStr){
		pullToRefreshListView.setVisibility(View.VISIBLE);
		ServiceProvider.doUpdateLocalResource2(TitleSearchAct.this, keyValue ,tag, beginNum, Constant.LIST_ITEM_NUM,searchStr,null,
				new VolleyListener(TitleSearchAct.this) {

					@Override
					public void onResponse(Object responseObject) {
						pullToRefreshListView.onRefreshComplete();
						JSONObject response = (JSONObject) responseObject;
						System.out.println("response------------>"+response);
						try {
							int code = response.optInt(Net.CODE);
							if (code != Net.SUCCESS) {
								return;
							}
							JSONObject data = response
									.optJSONObject(Net.DATA);
							if (data == null
									|| data.equals("") ) {
								return;
							}
							JSONArray resultArray = data.optJSONArray(Net.LIST);
							if (resultArray == null
									|| resultArray.length() == 0) {
								if(beginIndex>0){
									ToastUtil.showUpdateToast(TitleSearchAct.this);
								}else{
									pullToRefreshListView.setVisibility(View.GONE);
								}
								return;
							}
							dealData(resultArray,beginNum);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
						}
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						super.onErrorResponse(error);
						beginIndex = 0;
						pullToRefreshListView.onRefreshComplete();
					}
				});
	}
	
	/**
	 *  处理数据
	 */
	private void dealData(JSONArray resultArray,int beginNum){
		if(TextUtils.isEmpty(keyValue)){
			return;
		}
		// 考试
		if(keyValue.equals(Category.CATEGORY_EXAM)){
			exams = new ArrayList<Exam>();
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Exam entity = new Exam(jsonObject);
				exams.add(entity);
				beginIndex++;
			}
			if (beginNum <= 0) {
				beginIndex = resultArray.length();
				examAdapter.setDatas(exams);
			} else {
				examAdapter.addData(exams);
			}
		}
		// 通知
		if(keyValue.equals(Category.CATEGORY_NOTICE)){
			notices = new ArrayList<Notice>();
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Notice entity = new Notice(jsonObject);
				notices.add(entity);
				beginIndex++;
			}
			if (beginNum <= 0) {
				beginIndex = resultArray.length();
				noticeAdapter.setDatas(notices);
			} else {
				noticeAdapter.addData(notices);
			}
		}
		// 签到
		if(keyValue.equals(Category.CATEGORY_TASK)){
			tasks = new ArrayList<Task>();
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Task entity = new Task(jsonObject);
				tasks.add(entity);
				beginIndex++;
			}
			if (beginNum <= 0) {
				beginIndex = resultArray.length();
				taskAdapter.setDatas(tasks);
			} else {
				taskAdapter.addData(tasks);
			}
		}
		// 培训
		if(keyValue.equals(Category.CATEGORY_TRAIN)){
			trains = new ArrayList<Train>();
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray
						.optJSONObject(i);
				Train entity = new Train(jsonObject);
				trains.add(entity);
				beginIndex++;
			}
			if (beginNum <= 0) {
				trainAdapter.setData(updateFeedback(trains));
			} else {
				trainAdapter.addData(updateFeedback(trains));
			}
		}
	}
	
	/**
	 * 功能描述:通过网络获取课件点赞数量的list
	 */
	private ArrayList<Train> updateFeedback(final ArrayList<Train> list) {
		int length = list.size();
		String[] rids = new String[length];
		for (int i = 0; i < length; i++) {
			rids[i] = list.get(i).getRid();
		}
		// 获取资源的点赞数／评论数／评分
		ServiceProvider.doUpdateFeedbackCount(TitleSearchAct.this, rids, new VolleyListener(
				TitleSearchAct.this) {

			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				try {
					int code = response.optInt(Net.CODE);
					if (code != Net.SUCCESS) {
						return;
					}
					JSONArray resultArray = response
							.optJSONArray(Net.DATA);
					for (int i = 0; i < resultArray.length(); i++) {
						JSONObject jsonObject = resultArray.optJSONObject(i);
						String rid = jsonObject.optString(ResponseParams.RESOURCE_ID);
						int feedbackCount = jsonObject
								.optInt(ResponseParams.ZAN_NUM);
						int comment = jsonObject
								.optInt(ResponseParams.COMMENT_NUM);
						int ecnt = jsonObject
								.optInt(ResponseParams.ECNT); //评分人数
						int eval = jsonObject
								.optInt(ResponseParams.EVAL); //评分总分
						for (int j = 0; j < list.size(); j++) {
							Train t = list.get(j);
							if (rid.equals(t.getRid())) {
								t.setCommentNum(comment);
								t.setFeedbackCount(feedbackCount);
								t.setEcnt(ecnt);
								t.setEval(eval);
							}
						}
					}
					trainAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					pullToRefreshListView.onRefreshComplete();
				}
			}

			@Override
			public void onErrorResponse(VolleyError error) {
				super.onErrorResponse(error);
				pullToRefreshListView.onRefreshComplete();
			}
		});
		
		return list;
	}
}