package com.badou.mworking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.adapter.SearchMainAdapter;
import com.badou.mworking.adapter.SearchMoreAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseFragmentActivity;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Classification;
import com.badou.mworking.model.Notice;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * 类: <code> NoticesActivity </code> 功能描述: 通知公告页面 创建人: 葛建锋 创建日期: 2014年7月15日
 * 下午3:45:44 开发环境: JDK7.0
 */
@SuppressLint("NewApi")
public class NoticesActivity extends BaseFragmentActivity implements OnClickListener,OnRefreshListener2<ListView>{
	
	private SearchMainAdapter oneadapter1 = null;
	private SearchMoreAdapter twoadapter1 = null;
	private NoticeAdapter mAdapter = null;
	
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
	
	private ProgressBar updatePro; // 刷新进度条
	
	private ArrayList<Notice> list;
	
	private static int tag = 0;           //tag == 0 表示全部
	private int beginIndex = 0;
	private String userNum = "";
	private String title = "";
	
	private Dialog dialog;
	private AlertDialog.Builder loadDialog;// 显示的提示框
	private ProgressBar pro;// 文件下载的进度条
	
	public static final int PROGRESS_CHANGE = 0x1;
	public static final int PROGRESS_FINISH = 0x2;
	public static final int PROGRESS_MAX = 0x3;
	public static final int REFRESH_EXAM_LV = 0x004;


	private int mainListClickPosition = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_name_notice);
		layout.attachToActivity(this);
		NoticesActivity.tag = 0;
		initAction();
		ivRight.setVisibility(View.VISIBLE);
		triangleDownImg.setVisibility(View.VISIBLE);
		ivLeft.setImageResource(R.drawable.title_bar_back_normal);
		title = getIntent().getStringExtra(MainGridActivity.KEY_TITLE_NAME);
		tvTitle.setText(title);
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
						int main = SP.getIntSP(NoticesActivity.this, SP.NOTICE, "main", 0);
						int more = SP.getIntSP(NoticesActivity.this, SP.NOTICE, "more", 0);
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
					Animation anim = AnimationUtils.loadAnimation(NoticesActivity.this, R.anim.popup_enter);
					classificationLinear.startAnimation(anim);
				}else{
					triangleDownImg.setBackgroundResource(R.drawable.icon_triangle_down);
					classificationLinear.setVisibility(View.GONE);
					Animation anim = AnimationUtils.loadAnimation(NoticesActivity.this, R.anim.popup_exit);
					classificationLinear.startAnimation(anim);
				}
			}
		});
		getClassifications();
		initListener();
		if(ToastUtil.showNetExc(NoticesActivity.this)){
			String classificationStr =  SP.getStringSP(NoticesActivity.this, SP.NOTICE,Notice.CATEGORY_NOTICE, "");
			try {
				JSONArray jsonArray = new JSONArray(classificationStr);
				setClassifications(jsonArray);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		getCash(NoticesActivity.tag);
		updataListView(0);
	}
	
	/**
	 * c初始化action 布局
	 * @param onclick
	 */
	private void initAction(){
		updatePro = (ProgressBar) findViewById(R.id.update_pro);
		tvSearchNull = (ImageView) findViewById(R.id.tv_tishi);
		ivLeft = (ImageView) findViewById(R.id.iv_actionbar_left);
		tvTitle = (TextView)findViewById(R.id.txt_actionbar_title);
		ivRight = (ImageView)findViewById(R.id.iv_actionbar_right);
		triangleDownImg = (ImageView) findViewById(R.id.triangle_down_img);
		titleLay = (LinearLayout) findViewById(R.id.title_lay);
		mShoplist_onelist1 = (ListView) findViewById(R.id.Shoplist_onelist1);
		mShoplist_twolist1 = (ListView) findViewById(R.id.Shoplist_twolist1);
		classificationLinear = (LinearLayout) findViewById(R.id.classification_linear);
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.PullToRefreshListView);
		tvTitle.setText("");
		ivRight.setVisibility(View.VISIBLE);
		ivRight.setImageResource(R.drawable.search);
		ivLeft.setOnClickListener(this);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setVisibility(View.VISIBLE);
		tvSearchNull.setVisibility(View.GONE);
		ivRight.setOnClickListener(this);
		if (loadDialog == null) {
			// 初始化对话框
			loadDialog = new AlertDialog.Builder(this);
		}
		if (mAdapter == null) {
			mAdapter = new NoticeAdapter(NoticesActivity.this,null);
		} 
		pullToRefreshListView.setAdapter(mAdapter);
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
			if(classificationsTemp == null || classificationsTemp.size()==0){
				NoticesActivity.tag = classifications.get(arg2).getTag();
				String title = classifications.get(arg2).getName();
				tvTitle.setText(title);
				tvSearchNull.setVisibility(View.GONE);
				classificationLinear.setVisibility(View.GONE);
				beginIndex = 0;
				updataListView(0);
				SP.putIntSP(NoticesActivity.this, SP.NOTICE, "main", mainListClickPosition);
				SP.putIntSP(NoticesActivity.this, SP.NOTICE, "more", 0);
			}
		}
	}
	
	private class Twolistclick implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			SP.putIntSP(NoticesActivity.this, SP.NOTICE, "main", mainListClickPosition);
			SP.putIntSP(NoticesActivity.this, SP.NOTICE, "more", arg2);
			String title = classificationsTemp.get(arg2).getName();
			NoticesActivity.tag = classificationsTemp.get(arg2).getTag();
			tvTitle.setText(title);
			twoadapter1.setSelectItem(arg2);
			twoadapter1.notifyDataSetChanged();
			classificationLinear.setVisibility(View.GONE);
			tvSearchNull.setVisibility(View.GONE);
			beginIndex = 0;
			updataListView(0);
		}
	}
	
	private void initAdapter1(ArrayList<Classification> classificationsTemp) {
		twoadapter1 = new SearchMoreAdapter(NoticesActivity.this, classificationsTemp,R.layout.shop_list2_item);
		mShoplist_twolist1.setAdapter(twoadapter1);
		twoadapter1.notifyDataSetChanged();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	public void clickRight() {
		Intent inten = new Intent(NoticesActivity.this, TitleSearchAct.class);
		inten.putExtra(TitleSearchAct.SEARCH_KEY_VALUE, Category.CATEGORY_NOTICE);
		inten.putExtra(TitleSearchAct.SEARCH_TAG, NoticesActivity.tag);
		startActivity(inten);
		overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}
	
	/**
	 * 功能描述:通过网络获取 类别 列表
	 */
	private void getClassifications() {
		ServiceProvider.doGetCategorys(NoticesActivity.this, Category.CATEGORY_NOTICE , new VolleyListener(NoticesActivity.this) {
			@Override
			public void onResponse(Object responseObject) {
				JSONObject response = (JSONObject) responseObject;
				int code = response.optInt(Net.CODE);
				if (code==Net.LOGOUT) {
					AppApplication.logoutShow(NoticesActivity.this);
					return;
				}
				if (code != Net.SUCCESS) {
					return;
				}
				JSONArray resultArray = response.optJSONArray(Net.DATA);
				// 缓存分类信息
				SP.putStringSP(NoticesActivity.this, SP.NOTICE, Notice.CATEGORY_NOTICE, resultArray.toString());
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
				Classification category = new Classification(NoticesActivity.this,jsonObject,SP.NOTICE);
				classifications.add(category);
			}
		}
		oneadapter1 = new SearchMainAdapter(NoticesActivity.this, classifications,R.layout.shop_list1_item);
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

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		getCash(NoticesActivity.tag);
		beginIndex = 0;
		updataListView(beginIndex);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		updataListView(beginIndex);
	}
	
	private void updataListView(final int beginNum){
		updatePro.setVisibility(View.VISIBLE);
		userNum = ((AppApplication)getApplicationContext())
				.getUserInfo().getUserNumber();
		pullToRefreshListView.setVisibility(View.VISIBLE);
		if(beginIndex==0&&list!=null&&list.size()>0){
			list.clear();
		}
		ServiceProvider.doUpdateLocalResource2(NoticesActivity.this, Notice.CATEGORY_NOTICE, NoticesActivity.tag, beginNum, Constant.LIST_ITEM_NUM,"",null,
				new VolleyListener(NoticesActivity.this) {

					@Override
					public void onResponse(Object responseObject) {
						list = new ArrayList<Notice>();
						pullToRefreshListView.onRefreshComplete();
						updatePro.setVisibility(View.GONE);
						JSONObject response = (JSONObject) responseObject;
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
									ToastUtil.showUpdateToast(NoticesActivity.this);
								}else{
									tvSearchNull.setVisibility(View.VISIBLE);
									pullToRefreshListView.setVisibility(View.GONE);
								}
								return;
							}
							/**
							 * 保存未读数
							 */
							if (tag == 0) {
								SP.putIntSP(NoticesActivity.this,SP.DEFAULTCACHE, userNum+Notice.UNREAD_NUM_NOTICE, data.optInt(ResponseParams.NEWCNT));
							}
							//添加缓存
							if(beginIndex == 0){
								//添加缓存
								SP.putStringSP(NoticesActivity.this,SP.NOTICE, userNum+NoticesActivity.tag, resultArray.toString());
							}else{
								String SPJSONArray =  SP.getStringSP(NoticesActivity.this,SP.NOTICE, userNum+NoticesActivity.tag, "");
								Notice.putSPJsonArray(NoticesActivity.this, NoticesActivity.tag+"",userNum+NoticesActivity.tag, SPJSONArray, resultArray);
							}
							for (int i = 0 ; i < resultArray.length(); i++) {
								JSONObject jsonObject = resultArray
										.optJSONObject(i);
								Notice entity = new Notice(jsonObject);
								list.add(entity);
								beginIndex++;
							}
							if (beginNum <= 0) {
								beginIndex = resultArray.length();
								mAdapter.setDatas(list);
							} else {
								mAdapter.addData(list);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							mAdapter.notifyDataSetChanged();
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
		ArrayList<Notice> list = new ArrayList<Notice>();
		userNum = ((AppApplication)getApplicationContext()).getUserInfo().getUserNumber();
		String sp = SP.getStringSP(NoticesActivity.this,SP.NOTICE, userNum+NoticesActivity.tag, "");  
		if(ToastUtil.showNetExc(NoticesActivity.this)){
			if(TextUtils.isEmpty(sp)){
				mAdapter.setDatas(list);
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
				Notice entity = new Notice(jsonObject);
				list.add(entity);
			}
			mAdapter.setDatas(list);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化item点击监听
	 */
	private void initListener() {
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Notice notice = mAdapter.getItem(position - 1);
				int type = notice.getSubType();
				mAdapter.read(position - 1);
				ServiceProvider.doMarkRead(NoticesActivity.this, notice.getRid());
				mAdapter.notifyDataSetChanged();
				// 返回类型是 html
				if (Constant.MWKG_FORAMT_TYPE_HTML == type) {
					// 因为是html，所以只有联网才可以点击查看
					if (NetUtils.isNetConnected(NoticesActivity.this)) {
						showDetail(notice);
					} 
				// 返回类型是pdf
				}else if(Constant.MWKG_FORAMT_TYPE_PDF == type){
					toPDFAndWeb(notice);
				}else{
					return;
				}
			}
		});
	}

	
	private void toPDFAndWeb(Notice notice) {       
		// 声明pdf文件要保存的路径
		if (FileUtils.getAvailaleSize() / 1024 / 1024 <= 9) {
			ToastUtil.showToast(NoticesActivity.this, R.string.train_sd_size_);
			return;
		}
		String path = FileUtils.getTrainCacheDir(NoticesActivity.this) + notice.getRid() + ".pdf";
		File file = new File(path);
		// pdf文件不存在
		if (!file.exists() || !file.isFile() || file.isDirectory()
				|| file.length() == 0) {
			file.delete();
			// 显示对话框
			dialog = loadPro(loadDialog).show();
			if (NetUtils.isNetConnected(NoticesActivity.this)) {
				// 开启线程
				new DownloadThread(notice).start();
			} else {
				if (dialog != null && dialog.isShowing()
						&& !((Activity) NoticesActivity.this).isFinishing()) {
					// 关闭进度条对话框
					dialog.dismiss();
				}
				ToastUtil.showToast(NoticesActivity.this, R.string.error_service);
			}
		} else {
			// pdf文件已存在 调用
			toPdfViewer(notice);
		}
	}

	/**
	 * 功能描述:跳转到pdf浏览页面,设置此资源课件已读
	 * 
	 * @param train
	 */
	private void toPdfViewer(Notice notice) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("notice", notice);
		intent.putExtra("notice", bundle);
		intent.setClass(NoticesActivity.this, NoticePdfViewerActivity.class);
		startActivity(intent);
		// 设置切换动画，从右边进入，左边退出
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
	
	
	/**
	 * 类: <code> DownloadThread </code> 功能描述: 下载pdf文件的线程 创建人:董奇 创建日期: 2014年7月16日
	 * 上午9:30:29 开发环境: JDK7.0
	 */
	class DownloadThread extends Thread {
		private Notice notice;
		private String path;

		public DownloadThread(Notice notice) {
			super();
			this.notice = notice;
		}

		@Override
		public void run() {
			super.run();
			if(Constant.MWKG_FORAMT_TYPE_PDF == notice.getSubType()){
				path = FileUtils.getTrainCacheDir(NoticesActivity.this) + notice.getRid() + ".pdf";
			}
			if(path==null||path.equals("")){
				return;
			}
			// 通过url下载pdf文件
			int statu = HttpDownloader.downFile(notice.getUrl()
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
			bundle.putSerializable("notice", notice);
			msg.setData(bundle);
			handler.sendMessage(msg);
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
			dialog = new AlertDialog.Builder(NoticesActivity.this);
		}

		View loadView = new View(NoticesActivity.this);
		// 对话框加载布局文件
		loadView = LayoutInflater.from(NoticesActivity.this).inflate(
				R.layout.load_progerss_layout, null);
		pro = (ProgressBar) loadView.findViewById(R.id.load_progressBar);
		pro.setProgress(0);
		dialog.setView(loadView);
		return dialog;
	}
	
	private Handler handler = new Handler() {            

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 接受线程中传递的消息
			int statu = msg.what;
			Bundle bundle = msg.getData();
			Notice notice = (Notice) bundle.getSerializable("notice");
			String path = "";
			// 声明文件保存路径 用rid命名
			if (notice != null) {
				path = FileUtils.getTrainCacheDir(NoticesActivity.this) + notice.getRid() + ".pdf";
				if(path==null||path.equals("")){
					return;
				}
				File file = new File(path);
				if (statu == -1 || !file.exists() || file.length() == 0) {
					// 文件下载失败 提示
					ToastUtil.showToast(NoticesActivity.this,
							R.string.train_result_download_fail);
				} else {
					if (statu != 0) {
						ToastUtil.showToast(NoticesActivity.this,
								R.string.train_result_download_exist);
					} 
					if(Constant.MWKG_FORAMT_TYPE_PDF == notice.getSubType()){
						// 下载完成 调用
						toPdfViewer(notice);
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
				if (dialog != null && dialog.isShowing() && !NoticesActivity.this.isFinishing()) {
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
	 * 功能描述: 显示通知的详细内容
	 * @param notice
	 */
	private void showDetail(Notice notice) {
		int subtype = notice.getSubType();
		if(Constant.MWKG_FORAMT_TYPE_HTML!=subtype){
			return;
		}
		Intent intent = new Intent();
		intent.setClass(NoticesActivity.this, BackWebActivity.class);
		intent.putExtra(BackWebActivity.VALUE_URL, notice.getUrl()
				+ "&uid="
				+ ((AppApplication) getApplicationContext())
						.getUserInfo().getUserId());
		// 获取分类名
		String title = SP.getStringSP(NoticesActivity.this, SP.NOTICE, notice.getTag()+"", "");
		intent.putExtra(BackWebActivity.VALUE_TITLE, title);
		intent.putExtra(BackWebActivity.VALUE_RID, notice.getRid());
		intent.putExtra(BackWebActivity.ISSHOWTONGJI, true);
		BackWebActivity.PAGEFLAG = BackWebActivity.NOTICE;    //设置跳转是通知公告
		NoticesActivity.this.startActivity(intent);
		// 设置切换动画，从右边进入，左边退出
		overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 以免该值被下次重用，所以在这里还原一下
		Classification.isHasErjiClassification = false;
		NoticesActivity.tag = 0;
		SP.putIntSP(NoticesActivity.this, SP.NOTICE, "main", 0);
		SP.putIntSP(NoticesActivity.this, SP.NOTICE, "more", 0);
	}
}
