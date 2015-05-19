package com.badou.mworking.base;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.badou.mworking.R;
import com.badou.mworking.TitleSearchAct;
import com.badou.mworking.adapter.NoticeAdapter;
import com.badou.mworking.adapter.SearchMainAdapter;
import com.badou.mworking.adapter.SearchMoreAdapter;
import com.badou.mworking.factory.BaseCategoryFactory;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Classification;
import com.badou.mworking.model.Notice;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BaseProgressListActivity extends BaseBackActionBarActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {

	private ProgressBar progressBar;
	private ImageView triangleImageView;
	private LinearLayout titleLinearLayout;
	private boolean status_menu_show = false;

    protected String CATEGORY_SP_KEY = "";
    protected String CATEGORY_NAME = "";
    public int tag = 0;           //tag == 0 表示全部
    private int beginIndex = 0;
    private String userNum = "";

    private static final String SP_KEY_CATEGORY_MAIN = "main";
    private static final String SP_KEY_CATEGORY_MORE = "more";

    private SearchMainAdapter searchMainAdapter = null;
    private SearchMoreAdapter searchMoreAdapter = null;

    private ArrayList<Classification> classifications = new ArrayList<>();
    private ArrayList<Classification> classificationsTemp = new ArrayList<>();

    private ListView mShoplist_mainlist;
    private ListView mShoplist_morelist;

    private int mainListClickPosition = 0;

    private android.widget.LinearLayout classificationLinear;  // 下拉布局

    private ImageView tvSearchNull;

    private PullToRefreshListView pullToRefreshListView;
    private BaseAdapter mAdapter = null;
    private BaseCategoryFactory mFactory = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.act_name_notice);
        layout.attachToActivity(this);
		initProgressView();
		initProgressListener();
        getClassifications();
        initProgressListener();
        if(ToastUtil.showNetExc(mContext)){
            String classificationStr =  SP.getStringSP(mContext, CATEGORY_SP_KEY, CATEGORY_NAME, "");
            try {
                JSONArray jsonArray = new JSONArray(classificationStr);
                setClassifications(jsonArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getCache(tag);
        updataListView(0);
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 以免该值被下次重用，所以在这里还原一下
        Classification.isHasErjiClassification = false;
        tag = 0;
        SP.putIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MAIN, 0);
        SP.putIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MORE, 0);
    }

    /**
     * 初始化action 布局
     */
	private void initProgressView(){
        setRightImage(R.drawable.search);
		progressBar = (ProgressBar)findViewById(R.id.pb_action_bar);
		triangleImageView = (ImageView)findViewById(R.id.iv_action_bar_triangle);
		titleLinearLayout= (LinearLayout)findViewById(R.id.ll_action_bar_title);
        tvSearchNull = (ImageView) findViewById(R.id.tv_tishi);
        mShoplist_mainlist = (ListView) findViewById(R.id.Shoplist_onelist1);
        mShoplist_morelist = (ListView) findViewById(R.id.Shoplist_twolist1);
        classificationLinear = (android.widget.LinearLayout) findViewById(R.id.classification_linear);
        pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.PullToRefreshListView);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setVisibility(View.VISIBLE);
        tvSearchNull.setVisibility(View.GONE);
	}

	private void initProgressListener(){
		titleLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status_menu_show) {
                    hideMenu();
                } else {
                    showMenu();
                }
            }
        });

        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                BaseProgressListActivity.this.onItemClick();
            }
        });
        pullToRefreshListView.setOnRefreshListener(this);
        mShoplist_mainlist.setOnItemClickListener(new Onelistclick1());
        mShoplist_morelist.setOnItemClickListener(new Twolistclick());
	}

    protected void onItemClick(){}

    private class Onelistclick1 implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            mainListClickPosition = arg2;
            classificationsTemp = classifications.get(arg2).getClassifications();
            if(classificationsTemp == null){
                classificationsTemp = new ArrayList<Classification>();
            }
            initAdapter1(classificationsTemp);
            searchMainAdapter.setSelectItem(arg2);
            searchMainAdapter.notifyDataSetChanged();
            if(classificationsTemp == null || classificationsTemp.size()==0){
                tag = classifications.get(arg2).getTag();
                String title = classifications.get(arg2).getName();
                setTitle(title);
                tvSearchNull.setVisibility(View.GONE);
                classificationLinear.setVisibility(View.GONE);
                beginIndex = 0;
                updataListView(0);
                SP.putIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MAIN, mainListClickPosition);
                SP.putIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MORE, 0);
            }
        }

    }

    private class Twolistclick implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            SP.putIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MAIN, mainListClickPosition);
            SP.putIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MORE, arg2);
            String title = classificationsTemp.get(arg2).getName();
            tag = classificationsTemp.get(arg2).getTag();
            setTitle(title);
            searchMoreAdapter.setSelectItem(arg2);
            searchMoreAdapter.notifyDataSetChanged();
            classificationLinear.setVisibility(View.GONE);
            tvSearchNull.setVisibility(View.GONE);
            beginIndex = 0;
            updataListView(0);
        }
    }

    private void initAdapter1(ArrayList<Classification> classificationsTemp) {
        searchMoreAdapter = new SearchMoreAdapter(mContext, classificationsTemp,R.layout.shop_list2_item);
        mShoplist_morelist.setAdapter(searchMoreAdapter);
        searchMoreAdapter.notifyDataSetChanged();
    }

    public void clickRight() {
        Intent intent = new Intent(mContext, TitleSearchAct.class);
        intent.putExtra(TitleSearchAct.SEARCH_KEY_VALUE, Category.CATEGORY_NOTICE);
        intent.putExtra(TitleSearchAct.SEARCH_TAG, tag);
        startActivity(intent);
    }

    /**
     * 功能描述:  获取缓存
     */
    public void getCache(int tag){
        List<Object> list = new ArrayList<>();
        userNum = ((AppApplication)getApplicationContext()).getUserInfo().getUserNumber();
        String sp = SP.getStringSP(mContext,CATEGORY_SP_KEY, userNum+ tag, "");
        if(ToastUtil.showNetExc(mContext)){
            if(TextUtils.isEmpty(sp)){
                mFactory.setList(list);
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
            mFactory.setList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述:通过网络获取 类别 列表
     */
    private void getClassifications() {
        ServiceProvider.doGetCategorys(mContext, Category.CATEGORY_NOTICE, new VolleyListener(mContext) {
            @Override
            public void onResponse(Object responseObject) {
                JSONObject response = (JSONObject) responseObject;
                int code = response.optInt(Net.CODE);
                if (code == Net.LOGOUT) {
                    AppApplication.logoutShow(mContext);
                    return;
                }
                if (code != Net.SUCCESS) {
                    return;
                }
                JSONArray resultArray = response.optJSONArray(Net.DATA);
                // 缓存分类信息
                SP.putStringSP(mContext, CATEGORY_SP_KEY, CATEGORY_NAME, resultArray.toString());
                setClassifications(resultArray);
            }
        });
    }

    /**
     * @param resultArray
     * 解析jsonArray
     */
    private void setClassifications(JSONArray resultArray){
        classifications = new ArrayList<>();
        if (resultArray != null && resultArray.length() != 0) {
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.optJSONObject(i);
                Classification category = new Classification(mContext,jsonObject,CATEGORY_SP_KEY);
                classifications.add(category);
            }
        }
        searchMainAdapter = new SearchMainAdapter(mContext, classifications,R.layout.shop_list1_item);
        searchMainAdapter.setSelectItem(0);
        mShoplist_mainlist.setAdapter(searchMainAdapter);
        ArrayList<Classification> classificationsFirst = classifications.get(0).getClassifications();
        if(classificationsFirst!=null&&classificationsFirst.size()>0){
            initAdapter1(classificationsFirst);
        }
        // 如果没有二级分类的话，只显示左边的一栏
        if(!Classification.isHasErjiClassification){
            mShoplist_morelist.setVisibility(View.GONE);
        }
    }

    private void updataListView(final int beginNum){
        showProgressBar();
        userNum = ((AppApplication)getApplicationContext())
                .getUserInfo().getUserNumber();
        pullToRefreshListView.setVisibility(View.VISIBLE);
        if(beginIndex==0&&list!=null&&list.size()>0){
            list.clear();
        }
        ServiceProvider.doUpdateLocalResource2(mContext, CATEGORY_NAME, tag, beginNum, Constant.LIST_ITEM_NUM, "", null,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponse(Object responseObject) {
                        list = new ArrayList<Notice>();
                        pullToRefreshListView.onRefreshComplete();
                        hideProgressBar();
                        JSONObject response = (JSONObject) responseObject;
                        try {
                            int code = response.optInt(Net.CODE);
                            if (code != Net.SUCCESS) {
                                return;
                            }
                            JSONObject data = response
                                    .optJSONObject(Net.DATA);
                            if (data == null
                                    || data.equals("")) {
                                return;
                            }
                            JSONArray resultArray = data.optJSONArray(Net.LIST);
                            if (resultArray == null
                                    || resultArray.length() == 0) {
                                if (beginIndex > 0) {
                                    ToastUtil.showUpdateToast(mContext);
                                } else {
                                    tvSearchNull.setVisibility(View.VISIBLE);
                                    pullToRefreshListView.setVisibility(View.GONE);
                                }
                                return;
                            }
                            /**
                             * 保存未读数
                             */
                            if (tag == 0) {
                                SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + Notice.UNREAD_NUM_NOTICE, data.optInt(ResponseParams.NEWCNT));
                            }
                            //添加缓存
                            if (beginIndex == 0) {
                                //添加缓存
                                SP.putStringSP(mContext, CATEGORY_SP_KEY, userNum + tag, resultArray.toString());
                            } else {
                                String SPJSONArray = SP.getStringSP(mContext, CATEGORY_SP_KEY, userNum + tag, "");
                                Notice.putSPJsonArray(mContext, tag + "", userNum + tag, SPJSONArray, resultArray);
                            }
                            for (int i = 0; i < resultArray.length(); i++) {
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
                        hideProgressBar();
                    }
                });
    }
    
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        getCache(tag);
        beginIndex = 0;
        updataListView(beginIndex);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        updataListView(beginIndex);
    }

    public void setAdapter(BaseAdapter adapter){
        mAdapter = adapter;
        pullToRefreshListView.setAdapter(mAdapter);
    }

	public void showProgressBar(){
		progressBar.setVisibility(View.VISIBLE);
	}

	public void hideProgressBar(){
		progressBar.setVisibility(View.INVISIBLE);
	}

	public void showMenu(){
		triangleImageView.setImageResource(R.drawable.icon_triangle_up);
        classificationLinear.setVisibility(View.VISIBLE);
        if(classifications!=null&&classifications.size()>0){
            int main = SP.getIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MAIN, 0);
            int more = SP.getIntSP(mContext, CATEGORY_SP_KEY, SP_KEY_CATEGORY_MORE, 0);
            searchMainAdapter.setSelectItem(main);
            searchMainAdapter.notifyDataSetChanged();
            classificationsTemp = classifications.get(main).getClassifications();
            if(searchMoreAdapter !=null){
                if(classificationsTemp == null ||classificationsTemp.size()==0){
                    initAdapter1(classificationsTemp);
                }else{
                    initAdapter1(classificationsTemp);
                    searchMoreAdapter.setSelectItem(more);
                }
                searchMoreAdapter.notifyDataSetChanged();
            }
        }
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_enter);
        classificationLinear.startAnimation(anim);
	}

	public void hideMenu(){
		triangleImageView.setImageResource(R.drawable.icon_triangle_down);
        classificationLinear.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_exit);
        classificationLinear.startAnimation(anim);
	}

}
