package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.badou.mworking.adapter.WenDAdapter;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Ask;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author 葛建锋
 * 问答搜索页面
 */
public class WenDaSearchActivity  extends BaseNoTitleActivity implements OnClickListener,OnRefreshListener2<ListView>{
	
	private EditText etInput;
	private TextView searchBtn;
	private InputMethodManager imm;
	
	private WenDAdapter wenDAdapter;
	private ImageView backImg;
	
	private PullToRefreshListView pullToRefreshListView;
	private ArrayList<Ask> asks = new ArrayList<Ask>();

	private int beginIndex = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wendasearchactivity);
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

	protected void initView() {
		super.initView();
		// 隐藏输入法
		imm = (InputMethodManager) WenDaSearchActivity.this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		etInput = (EditText) this.findViewById(R.id.wenda_et_search);
		searchBtn = (TextView) this.findViewById(R.id.wenda_search_btn);
		backImg = (ImageView) this.findViewById(R.id.wenda_back_Img);
		searchBtn.setOnClickListener(this);
		backImg.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.pullListView);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent =  new Intent();
				intent.setClass(WenDaSearchActivity.this, WenDaDetailActivity.class);
				intent.putExtra("ask", asks.get(position-1));
				startActivity(intent);
			}
		});
		wenDAdapter = new WenDAdapter(this, asks);
		pullToRefreshListView.setAdapter(wenDAdapter);
	}

	protected void initListener() {
		super.initListener();
		// 输入法下标的点击响应事件
		etInput.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					// 避免用户不断点击搜索按钮
					if(asks!=null){
						asks.clear();
					}
					searchMethod();
					beginIndex = 1;
					upDateListView(beginIndex);
					return true;
				}
				return false;
			}
		});
	}

	
	/**
	 * 功能描述: 搜索方法
	 */
	public void searchMethod(){
		String searchStr = etInput.getText().toString();
		if (TextUtils.isEmpty(searchStr)) {
			ToastUtil.showToast(WenDaSearchActivity.this, "请输入关键字");
		} else {
			//在点击完搜索之后，隐藏键盘，该方法，如果键盘显示则隐藏，键盘隐藏则显示
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
	
	/**
	 * 功能描述:
	 * @param beginNum
	 */
	private void upDateListView(int beginNum) {
		String searchStr = etInput.getText().toString().trim();
		ServiceProvider.updateWendaList(WenDaSearchActivity.this, beginNum, Constant.LIST_ITEM_NUM, searchStr, new VolleyListener(WenDaSearchActivity.this) {
			
			@Override
			public void onResponse(Object responseObject) {
				JSONObject responseJson = (JSONObject) responseObject;
				int code = responseJson.optInt(Net.CODE);
				if (code == Net.LOGOUT) {
						AppApplication.logoutShow(mContext);
						return;
				}
				if (code != Net.SUCCESS) {
					return;
				}
				try {
					JSONArray data = responseJson.getJSONArray(Net.DATA);
					ArrayList<Ask> askTemp = new ArrayList<Ask>();
					int length = data.length();
					if(beginIndex==1&&length==0){
						ToastUtil.showToast(WenDaSearchActivity.this, WenDaSearchActivity.this.getResources().getString(R.string.search_no_content));
						return;
					}
					for (int i = 0; i < length; i++) {
						JSONObject jb = data.getJSONObject(i);
						Ask ask = new Ask(jb);
						askTemp.add(ask);
					}
					beginIndex++;
					asks.addAll(askTemp);
					wenDAdapter.notifyDataSetChanged();
					pullToRefreshListView.onRefreshComplete();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.wenda_search_btn:
			// 避免用户不断点击搜索按钮
			if(asks!=null){
				asks.clear();
			}
			beginIndex = 1;
			searchMethod();
			upDateListView(beginIndex);
			break;
		case R.id.wenda_back_Img:
			// 显示或者隐藏输入法
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			WenDaSearchActivity.this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		upDateListView(beginIndex);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		beginIndex = 1;
		asks.clear();
		upDateListView(beginIndex);
	}
}
