package com.badou.mworking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.SearchMainAdapter;
import com.badou.mworking.adapter.SearchMoreAdapter;
import com.badou.mworking.adapter.TrainAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseFragmentActivity;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Classification;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.DownloadListener;
import com.badou.mworking.net.HttpDownloader;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.CoursewareScoreDilog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * @author gejianfeng
 * 微培训页面
 */
public class TrainActivity extends BaseFragmentActivity implements OnClickListener,OnRefreshListener2<ListView>{

	private SearchMainAdapter oneadapter1 = null;
	private SearchMoreAdapter twoadapter1 = null;
	
	private ArrayList<Classification> classifications = new ArrayList<Classification>();
	private ArrayList<Classification> classificationsTemp = new ArrayList<Classification>();
	
	private ImageView ivLeft;  //action 左侧iv
	private ImageView triangleDownImg;   //下拉的图标
	private ImageView ivRight;  	//action 右侧 iv
	private ImageView tvSearchNull;
	private TextView tvTitle;  //action 中间tv
	private ListView mShoplist_onelist1;
	private ListView mShoplist_twolist1;
	private LinearLayout titleLay;  // title 的布局
	private LinearLayout classificationLinear;  // 下拉布局
	private PullToRefreshListView pullToRefreshListView;
	
	private static int tag = 0;
	private int beginIndex = 0;
	private String userNum = "";

	public static final int PROGRESS_CHANGE = 0x1;
	public static final int PROGRESS_FINISH = 0x2;
	public static final int PROGRESS_MAX = 0x3;
	public static final int REFRESH_EXAM_LV = 0x004;
	public static String BUNDLE_VALUE_SUBTYPE = "subtype";
	public static String KEY_webView_pdf = "webPDF";
	
	private String displayActName = "train";

	private TrainAdapter trainAdapter;
	private ProgressDialog mProgressDialog;
	private AlertDialog.Builder loadDialog;// 显示的提示框
	private Dialog dialog;//
	private ProgressBar pro;// 文件下载的进度条

	private View lastView;// listview 的footer 主要在我的学习进度页面用到占位
	private Train train;
	private ArrayList<Train> list;
	private int mainListClickPosition = 0;
	
	public static int pingfen = 0;   //评了多少分
	private ProgressBar updatePro; // 刷新进度条
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout.attachToActivity(this);
		setContentView(R.layout.act_name_notice);
		TrainActivity.tag = 0;
		initView();
		String titleName= SP.getStringSP(TrainActivity.this,SP.DEFAULTCACHE, RequestParams.CHK_UPDATA_PIC_TRAIN, "");
		MainIcon mainIcon = new MainIcon();
		tvTitle.setText(mainIcon.getMainIcon(titleName).getName());
		if (getIntent().getIntExtra(MyStudyProgressAct.VALUE_STUDY,0) == 1) {
			ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		}else {
			ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		}
		Onelistclick1 onelistclick1 = new Onelistclick1();
		Twolistclick twolistclick = new Twolistclick();
		mShoplist_onelist1.setOnItemClickListener(onelistclick1);
		mShoplist_twolist1.setOnItemClickListener(twolistclick);
		titleLay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(View.GONE == classificationLinear.getVisibility()){
					classificationLinear.setVisibility(View.VISIBLE);
					triangleDownImg.setBackgroundResource(R.drawable.icon_triangle_up);
					if(classifications!=null&&classifications.size()>0){
						int main = SP.getIntSP(TrainActivity.this, SP.TRAINING, "main", 0);
						int more = SP.getIntSP(TrainActivity.this, SP.TRAINING, "more", 0);
						oneadapter1.setSelectItem(main);
						oneadapter1.notifyDataSetChanged();
						classificationsTemp = classifications.get(main).getClassifications();
						if(twoadapter1!=null){
							if(classificationsTemp == null ||classificationsTemp.size()==0){
								initAdapter1(classificationsTemp);
							}else{
								initAdapter1(classificationsTemp);
								twoadapter1.setSelectItem(more);
							}
							twoadapter1.notifyDataSetChanged();
						}
					}
					Animation anim = AnimationUtils.loadAnimation(TrainActivity.this, R.anim.popup_enter);
					classificationLinear.startAnimation(anim);
				}else{
					triangleDownImg.setBackgroundResource(R.drawable.icon_triangle_down);
					classificationLinear.setVisibility(View.GONE);
					Animation anim = AnimationUtils.loadAnimation(TrainActivity.this, R.anim.popup_exit);
					classificationLinear.startAnimation(anim);
				}
			}
		});
		initListener();
		getClassifications();
		if(ToastUtil.showNetExc(TrainActivity.this)){
			String classificationStr =  SP.getStringSP(TrainActivity.this, SP.TRAINING,Train.CATEGORY_TRAIN, "");
			try {
				JSONArray jsonArray = new JSONArray(classificationStr);
				setClassifications(jsonArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		getCash(TrainActivity.tag);
	}
	
	/**
	 *  初始化view
	 * @param view
	 */
	private void initView() {
		updatePro = (ProgressBar) findViewById(R.id.update_pro);
		tvSearchNull = (ImageView) this.findViewById(R.id.tv_tishi);
		ivLeft = (ImageView) this.findViewById(R.id.iv_actionbar_left);
		ivLeft.setOnClickListener(this);
		tvTitle = (TextView) this.findViewById(R.id.txt_actionbar_title);
		ivRight = (ImageView) this.findViewById(R.id.iv_actionbar_right);
		triangleDownImg = (ImageView) findViewById(R.id.triangle_down_img);
		titleLay = (LinearLayout) findViewById(R.id.title_lay);
		mShoplist_onelist1 = (ListView) findViewById(R.id.Shoplist_onelist1);
		mShoplist_twolist1 = (ListView) findViewById(R.id.Shoplist_twolist1);
		classificationLinear = (LinearLayout) findViewById(R.id.classification_linear);
		ivRight.setVisibility(View.VISIBLE);
		ivRight.setImageResource(R.drawable.search);
		triangleDownImg.setVisibility(View.VISIBLE);
		if (tvSearchNull == null) {
			tvSearchNull = (ImageView)findViewById(R.id.tv_tishi);
		}
		if (pullToRefreshListView == null) {
			pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.PullToRefreshListView);
		} 
		if (loadDialog == null) {
			// 初始化对话框
			loadDialog = new AlertDialog.Builder(this);
		}
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setVisibility(View.VISIBLE);
		ivRight.setOnClickListener(this);
		if (trainAdapter== null) {
			trainAdapter = new TrainAdapter(this, null, displayActName);
		}
		pullToRefreshListView.setAdapter(trainAdapter);
		tvSearchNull.setVisibility(View.GONE);
		// 设置点击不消失
		loadDialog.setCancelable(false);

		if (TrainAdapter.VALUE_ACT_STUDY.equals(displayActName)) {
			/** 添加空白的footer */
			ListView lv = pullToRefreshListView.getRefreshableView();
			lastView = new View(TrainActivity.this);
			AbsListView.LayoutParams vLp = new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, getResources()
							.getDimensionPixelOffset(
									R.dimen.my_examAndStudy_lvFooter_h));
			lastView.setLayoutParams(vLp);
			lv.addFooterView(lastView, null, false);
			lastView.setVisibility(View.GONE);
		}
		updataListView(0);
	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	public void clickRight() {
		Intent inten = new Intent(TrainActivity.this, TitleSearchAct.class);
		inten.putExtra(TitleSearchAct.SEARCH_KEY_VALUE, Category.CATEGORY_TRAIN);
		startActivity(inten);
		overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.iv_actionbar_left:
			finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.iv_actionbar_right:
			clickRight();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		//点赞刷新
		if(TrainActivity.pingfen!=0&&train!=null){
			train.setEcnt(train.getEcnt()+1);
			train.setCoursewareScore(TrainActivity.pingfen+"");
			trainAdapter.notifyDataSetChanged();
			TrainActivity.pingfen = 0;
		}
		//评分刷新
		if(CoursewareScoreDilog.ISPINGFEN && train!=null){
			CoursewareScoreDilog.ISPINGFEN = false;
			CoursewareScoreDilog.SCORE = 0;
			train.setCoursewareScore(CoursewareScoreDilog.SCORE+"");
			trainAdapter.notifyDataSetChanged();
		}
	}

	private class Onelistclick1 implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mainListClickPosition = arg2;
			classificationsTemp = classifications.get(arg2).getClassifications();
			if(classificationsTemp == null){
				classificationsTemp = new ArrayList<Classification>();
			}
			initAdapter1(classificationsTemp);
			oneadapter1.setSelectItem(arg2);
			oneadapter1.notifyDataSetChanged();
			// 当该分类下没有二级菜单的时候
			if(classificationsTemp == null || classificationsTemp.size()==0){
				TrainActivity.tag = classifications.get(arg2).getTag();
				String title = classifications.get(arg2).getName();
				tvTitle.setText(title);
				tvSearchNull.setVisibility(View.GONE);
				classificationLinear.setVisibility(View.GONE);
				beginIndex = 0;
				updataListView(0);
				SP.putIntSP(TrainActivity.this, SP.TRAINING, "main", mainListClickPosition);
				SP.putIntSP(TrainActivity.this, SP.TRAINING, "more", 0);
			}
		}
	}
	
	private class Twolistclick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String title = classificationsTemp.get(arg2).getName();
			TrainActivity.tag = classificationsTemp.get(arg2).getTag();
			tvTitle.setText(title);
			twoadapter1.setSelectItem(arg2);
			twoadapter1.notifyDataSetChanged();
			classificationLinear.setVisibility(View.GONE);
			tvSearchNull.setVisibility(View.GONE);
			beginIndex = 0;
			updataListView(0);
			SP.putIntSP(TrainActivity.this, SP.TRAINING, "main", mainListClickPosition);
			SP.putIntSP(TrainActivity.this, SP.TRAINING, "more", arg2);
		}
	}
	
	private void initAdapter1(ArrayList<Classification> classifications) {
		twoadapter1 = new SearchMoreAdapter(TrainActivity.this, classifications,R.layout.shop_list2_item);
		mShoplist_twolist1.setAdapter(twoadapter1);
		twoadapter1.notifyDataSetChanged();
	}
	
	/**
	 * 功能描述:通过网络获取 类别 列表
	 */
	private void getClassifications() {
		ServiceProvider.doGetCategorys(TrainActivity.this, Category.CATEGORY_TRAIN , new VolleyListener(TrainActivity.this) {
			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (code==Net.LOGOUT) {
					AppApplication.logoutShow(TrainActivity.this);
					return;
				}
				if (code != Net.SUCCESS) {
					return;
				}
				JSONArray resultArray = response.optJSONArray(Net.DATA);
				// 缓存分类信息
				SP.putStringSP(TrainActivity.this, SP.TRAINING, Train.CATEGORY_TRAIN, resultArray.toString());
				setClassifications(resultArray);
			}
		});
	}
	
	/**
	 * @param resultArray
	 * 解析jsonArray
	 */
	private void setClassifications(JSONArray resultArray){
		classifications = new ArrayList<Classification>();
		if (resultArray != null && resultArray.length() != 0) {
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray.optJSONObject(i);
				Classification category = new Classification(TrainActivity.this,jsonObject,SP.TRAINING);
				classifications.add(category);
			}
		}
		oneadapter1 = new SearchMainAdapter(TrainActivity.this, classifications,R.layout.shop_list1_item);
		oneadapter1.setSelectItem(0);
		mShoplist_onelist1.setAdapter(oneadapter1);
		ArrayList<Classification> classificationsFirst = classifications.get(0).getClassifications();
		if(classificationsFirst!=null&&classificationsFirst.size()>0){
			initAdapter1(classificationsFirst);
		}
		// 如果没有二级分类的话，只显示左边的一栏
		if(!Classification.isHasErjiClassification){
			mShoplist_twolist1.setVisibility(View.GONE);
		}
	}

	private void initListener() {
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				train = trainAdapter.getItem(position - 1);
				int subtype = train.getSubtype();
				if (NetUtils.isNetConnected(TrainActivity.this)) {
					// 向服务提交课件信息
					trainAdapter.read(position - 1);
					ServiceProvider.doMarkRead(TrainActivity.this, train.getRid());
				}
				//返回PDF格式
				if(Constant.MWKG_FORAMT_TYPE_PDF == subtype){
					toPDFAndWeb(train);
				// 返回MP4格式	
				}else if(Constant.MWKG_FORAMT_TYPE_MPEG == subtype){
					Intent intentToMusic = new Intent(TrainActivity.this, TrainVideoPlayerAct.class);
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
					intent.putExtra(BackWebActivity.ISSHOWTONGJI, true);
					intent.putExtra(BackWebActivity.VALUE_URL, url);
					// 获取分类名
					String title = SP.getStringSP(TrainActivity.this, SP.TRAINING, train.getTag()+"", "");
					intent.putExtra(BackWebActivity.VALUE_TITLE, title);
					intent.setClass(TrainActivity.this, BackWebActivity.class);
					BackWebActivity.PAGEFLAG = BackWebActivity.TRAINING;   // 设置是通过微培训跳转过去的
					TrainActivity.this.startActivity(intent);
				}else if(Constant.MWKG_FORAMT_TYPE_MP3 == subtype){
					Intent intentToMusic = new Intent(TrainActivity.this, TrainMusicActivity.class);
					Bundle bu = new Bundle();
					bu.putSerializable("train", train);
					intentToMusic.putExtra("train", bu);
					startActivity(intentToMusic);
				}else{
					return;
				}
				trainAdapter.notifyDataSetChanged();
			}
		});
	}

	/**
	 * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
	 * 
	 * @param train
	 */
	private void toPdfViewer(Train train) {
		if (!((Activity) TrainActivity.this).isFinishing()) {
			// 系统版本>=11 使用第三方的pdf阅读
			if (android.os.Build.VERSION.SDK_INT >= 11) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("train", train);
				intent.putExtra("train", bundle);
				intent.setClass(TrainActivity.this, BaseViewerActivity.class);
				startActivity(intent);
				// 设置切换动画，从右边进入，左边退出
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} 
		}
	}

	private void toPDFAndWeb(Train train) {       
		/*** 判断api,太小用web **/
		/*** 判断是pdf还是web **/
		if (android.os.Build.VERSION.SDK_INT >= 11) {// pdf
			// 声明pdf文件要保存的路径
			if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
				ToastUtil.showToast(TrainActivity.this, R.string.train_sd_size_);
				return;
			}
			String path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".pdf";
			File file = new File(path);
			// pdf文件不存在
			if (!file.exists() || !file.isFile() || file.isDirectory()
					|| file.length() == 0) {
				file.delete();
				// 显示对话框
				dialog = loadPro(loadDialog).show();
				if (NetUtils.isNetConnected(TrainActivity.this)) {
					// 开启线程
					new DownloadThread(train).start();
				} else {
					if (dialog != null && dialog.isShowing()
							&& !((Activity) TrainActivity.this).isFinishing()) {
						// 关闭进度条对话框
						dialog.dismiss();
					}
					ToastUtil.showToast(TrainActivity.this, R.string.error_service);
				}
			} else {
				// pdf文件已存在 调用
				toPdfViewer(train);
			}
		} else {// web
			Intent intent = new Intent(TrainActivity.this, BaseViewerActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("train", train);
			intent.putExtra("train", bundle);
			intent.putExtra(KEY_webView_pdf, KEY_webView_pdf);
			startActivity(intent);
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
					 path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".pdf";
				}else if(Constant.MWKG_FORAMT_TYPE_MPEG == train.getSubtype()){
					 path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".mp4";
				}
				if(path==null||path.equals("")){
					return;
				}
				File file = new File(path);
				if (statu == -1 || !file.exists() || file.length() == 0) {
					// 文件下载失败 提示
					ToastUtil.showToast(TrainActivity.this,
							R.string.train_result_download_fail);
				} else {
					if (statu != 0) {
						ToastUtil.showToast(TrainActivity.this,
								R.string.train_result_download_exist);
					} 
					if(Constant.MWKG_FORAMT_TYPE_PDF == train.getSubtype()){
						// 下载完成 调用
						toPdfViewer(train);
					}
				}
			}
			switch (msg.what) {
			case REFRESH_EXAM_LV:
				if (Constant.setAdapterRefresh) {
					// 刷新 listview
					updataListView(0);
					Constant.setAdapterRefresh = false;
				} 
				break;
			case PROGRESS_MAX:
				if (pro != null) {
					pro.setMax((int) msg.obj);
				}
				break;
			case PROGRESS_CHANGE:
				// 设置进度条改变
				if (pro != null) {
					pro.setProgress((int) msg.obj);
				}
				break;
			case PROGRESS_FINISH:
				if (dialog != null && dialog.isShowing()
						&& !((Activity) TrainActivity.this).isFinishing()) {
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
				path = FileUtils.getTrainCacheDir(TrainActivity.this) + train.getRid() + ".pdf";
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
							Message.obtain(handler, PROGRESS_CHANGE,
									downloadSize).sendToTarget();
						}

						@Override
						public void onDownloadFinish(String filePath) {
							// 下载完成
							Message.obtain(handler, PROGRESS_FINISH, "")
									.sendToTarget();
						}

						@Override
						public void onGetTotalSize(int totalSize) {
							// 文件大小
							Message.obtain(handler, PROGRESS_MAX, totalSize)
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
		ServiceProvider.doUpdateFeedbackCount(TrainActivity.this, rids, new VolleyListener(
				TrainActivity.this) {

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

	/**
	 * 
	 * 功能描述:初始化AlertDialog的布局 初始化progerssBar
	 * 
	 * @param dialog
	 * @return
	 */
	public AlertDialog.Builder loadPro(AlertDialog.Builder dialog) {        
		if (dialog == null) {
			dialog = new AlertDialog.Builder(TrainActivity.this);
		}

		View loadView = new View(TrainActivity.this);
		// 对话框加载布局文件
		loadView = LayoutInflater.from(TrainActivity.this).inflate(
				R.layout.load_progerss_layout, null);
		pro = (ProgressBar) loadView.findViewById(R.id.load_progressBar);
		pro.setProgress(0);
		dialog.setView(loadView);
		return dialog;
	}
	
	/**
	 * 功能描述:
	 * @param beginNum
	 */
	private void updataListView(final int beginNum){
		updatePro.setVisibility(View.VISIBLE);
		userNum = ((AppApplication) getApplicationContext())
				.getUserInfo().getUserNumber();
		pullToRefreshListView.setVisibility(View.VISIBLE);
		if(beginIndex==0&&list!=null&&list.size()>0){
			list.clear();
		}
		ServiceProvider.doUpdateLocalResource2(TrainActivity.this, Train.CATEGORY_TRAIN, TrainActivity.tag, beginNum, Constant.LIST_ITEM_NUM,"",null,
				new VolleyListener(TrainActivity.this) {

					@Override
					public void onResponse(Object responseObject) {
						pullToRefreshListView.onRefreshComplete();
						list = new ArrayList<Train>();
						JSONObject response = (JSONObject) responseObject;
						updatePro.setVisibility(View.GONE);
						try {
							int code = response.optInt(Net.CODE);
							if (code != Net.SUCCESS) {
								return;
							}
							JSONObject data = response.optJSONObject(Net.DATA);
							if (data == null
									|| data.equals("")) {
								return;
							}
							JSONArray resultArray = data
									.optJSONArray(Net.LIST);
							if (resultArray == null
									|| resultArray.length() == 0) {
								if(beginIndex>0){
									ToastUtil.showUpdateToast(TrainActivity.this);
								}else{
									tvSearchNull.setVisibility(View.VISIBLE);
									pullToRefreshListView.setVisibility(View.GONE);
								}
								return;
							}
							
							//保存未读数
							if (tag == 0) {
								SP.putIntSP(TrainActivity.this,SP.DEFAULTCACHE, userNum+Train.UNREAD_NUM_TRAIN, data.optInt(ResponseParams.NEWCNT));
							}
							//添加缓存
							if(beginIndex == 0){
								//添加缓存
								SP.putStringSP(mContext,SP.TRAINING, userNum+TrainActivity.tag, resultArray.toString());
							}else{
								String SPJSONArray =  SP.getStringSP(mContext,SP.TRAINING, userNum+TrainActivity.tag, "");
								Train.putSPJsonArray(mContext, TrainActivity.tag+"", userNum, SPJSONArray, resultArray);
							}
							for (int i = 0 ; i < resultArray.length(); i++) {
								JSONObject jsonObject = resultArray.optJSONObject(i);
								Train entity = new Train(jsonObject);
								list.add(entity);
								beginIndex++;
							}
							if (beginNum <= 0) {
								trainAdapter.setData(updateFeedback(list));
							} else {
								trainAdapter.addData(updateFeedback(list));
							}
							
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}

					@Override
					public void onErrorResponse(VolleyError error) {
						super.onErrorResponse(error);
						beginIndex = 0;
						pullToRefreshListView.onRefreshComplete();
						updatePro.setVisibility(View.GONE);
					}
				});
	}
	
	
	/**
	 * 功能描述:  获取缓存
	 */
	public void getCash(int tag){
		ArrayList<Train> list = new ArrayList<Train>();
		userNum = ((AppApplication) TrainActivity.this.getApplicationContext()).getUserInfo().getUserNumber();
		String sp = SP.getStringSP(TrainActivity.this,SP.TRAINING, userNum+TrainActivity.tag, "");
		if(ToastUtil.showNetExc(TrainActivity.this)){
			if(TextUtils.isEmpty(sp)){
				trainAdapter.setData(updateFeedback(list));
				tvSearchNull.setVisibility(View.VISIBLE);
				return;
			}else{
				tvSearchNull.setVisibility(View.GONE);
			}
		}
		JSONArray resultArray;
		try {
			resultArray = new JSONArray(sp);
			for (int i = 0 ; i < resultArray.length(); i++) {
				JSONObject jsonObject = resultArray.optJSONObject(i);
				Train entity = new Train(jsonObject);
				list.add(entity);
			}
			trainAdapter.setData(updateFeedback(list));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCash(TrainActivity.tag);
		beginIndex = 0;
		updataListView(beginIndex);
	}
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		updataListView(beginIndex);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 以免该值被下次重用，所以在这里还原一下
		Classification.isHasErjiClassification = false;
		TrainActivity.tag = 0;
		SP.putIntSP(TrainActivity.this, SP.TRAINING, "main", 0);
		SP.putIntSP(TrainActivity.this, SP.TRAINING, "more", 0);
	}
}
