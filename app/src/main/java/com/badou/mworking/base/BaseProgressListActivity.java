package com.badou.mworking.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.badou.mworking.R;
import com.badou.mworking.adapter.ClassificationMainAdapter;
import com.badou.mworking.adapter.ClassificationMoreAdapter;
import com.badou.mworking.model.Classification;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
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

public abstract class BaseProgressListActivity extends BaseBackActionBarActivity implements PullToRefreshBase.OnRefreshListener2<ListView> {

    private ProgressBar progressBar;
    private ImageView triangleImageView;
    private LinearLayout titleLinearLayout;
    private boolean status_menu_show = false;

    protected String CATEGORY_NAME = "";
    protected String CATEGORY_UNREAD_NUM = "";

    public int tag = 0;           //tag == 0 表示全部
    private boolean hasErjiClassification = false;
    private int mMainIndex = 0;

    private static final String SP_KEY_CATEGORY_MAIN = "main";
    private static final String SP_KEY_CATEGORY_MORE = "more";

    private ClassificationMainAdapter mMainClassificationAdapter = null;
    private ClassificationMoreAdapter mMoreClassificationAdapter = null;
    protected MyBaseAdapter mCategoryAdapter = null;

    private ListView mMainListView;
    private ListView mMoreListView;

    private android.widget.LinearLayout classificationLinear;  // 下拉布局

    private ImageView tvSearchNull;

    protected PullToRefreshListView pullToRefreshListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_progress_list);
        layout.attachToActivity(this);
        initProgressView();
        initProgressListener();
        initProgressData();
    }

    /**
     * 创建mCategoryAdapter实例
     */
    protected abstract void initAdapter();

    /**
     * 添加点击事件
     */
    protected abstract void onItemClick(int position);

    /**
     * 将JSONObject转化为实例
     */
    protected abstract Object parseObject(JSONObject jsonObject);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 以免该值被下次重用，所以在这里还原一下
        SP.putIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MAIN, 0);
        SP.putIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MORE, 0);
    }

    /**
     * 初始化action 布局
     */
    private void initProgressView() {
        setRightImage(R.drawable.search);
        progressBar = (ProgressBar) findViewById(R.id.pb_action_bar);
        triangleImageView = (ImageView) findViewById(R.id.iv_action_bar_triangle);
        titleLinearLayout = (LinearLayout) findViewById(R.id.ll_action_bar_title);
        triangleImageView.setVisibility(View.VISIBLE);
        tvSearchNull = (ImageView) findViewById(R.id.tv_forget_password_tips);
        mMainListView = (ListView) findViewById(R.id.Shoplist_onelist1);
        mMoreListView = (ListView) findViewById(R.id.Shoplist_twolist1);
        classificationLinear = (android.widget.LinearLayout) findViewById(R.id.classification_linear);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.ptrlv_user_progress_content);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setVisibility(View.VISIBLE);
        tvSearchNull.setVisibility(View.GONE);
    }

    private void initProgressListener() {
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
                BaseProgressListActivity.this.onItemClick(position);
            }
        });
        pullToRefreshListView.setOnRefreshListener(this);
        mMainListView.setOnItemClickListener(new OnMainClassificationClickListener());
        mMoreListView.setOnItemClickListener(new OnMoreClassificationClickListener());
    }

    private void initProgressData() {
        mMainClassificationAdapter = new ClassificationMainAdapter(mContext);
        mMoreClassificationAdapter = new ClassificationMoreAdapter(mContext);
        mMoreClassificationAdapter.setLayoutResId(R.layout.shop_list2_item);
        mMoreListView.setAdapter(mMoreClassificationAdapter);
        mMainClassificationAdapter.setLayoutResId(R.layout.shop_list1_item);
        mMainListView.setAdapter(mMainClassificationAdapter);
        getClassifications();
        initAdapter();
        if (mCategoryAdapter == null) {
            return;
        }
        pullToRefreshListView.setAdapter(mCategoryAdapter);
        setCategoryItemFromCache(tag);
        updataListView(0);
    }

    private class OnMainClassificationClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            mMainIndex = arg2;
            Classification classification = (Classification) mMainClassificationAdapter.getItem(arg2);
            mMoreClassificationAdapter.setList(classification.getClassifications());
            mMainClassificationAdapter.setSelectItem(arg2);
            if (mMoreClassificationAdapter.getCount() == 0) {
                tag = classification.getTag();
                String title = classification.getName();
                setActionbarTitle(title);
                hideMenu();
                updataListView(0);
                SP.putIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MAIN, mMainIndex);
                SP.putIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MORE, 0);
            }
        }

    }

    private class OnMoreClassificationClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            SP.putIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MAIN, mMainIndex);
            SP.putIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MORE, arg2);
            Classification classification = (Classification) mMoreClassificationAdapter.getItem(arg2);
            String title = classification.getName();
            tag = classification.getTag();
            setActionbarTitle(title);
            mMoreClassificationAdapter.setSelectItem(arg2);
            hideMenu();
            updataListView(0);
        }
    }

    public void clickRight() {
    }

    /**
     * 功能描述:  获取缓存
     */
    public void setCategoryItemFromCache(int tag) {
        List<Object> list = new ArrayList<>();
        String userNum = ((AppApplication) getApplicationContext()).getUserInfo().account;
        String sp = SP.getStringSP(mContext, CATEGORY_NAME, userNum + tag, "");
        if (TextUtils.isEmpty(sp)) {
            tvSearchNull.setVisibility(View.VISIBLE);
            return;
        } else {
            tvSearchNull.setVisibility(View.GONE);
        }
        JSONArray resultArray;
        try {
            resultArray = new JSONArray(sp);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.optJSONObject(i);
                list.add(parseObject(jsonObject));
            }
            mCategoryAdapter.setList(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能描述:通过网络获取 类别 列表
     */
    private void getClassifications() {
        ServiceProvider.doGetCategorys(mContext, CATEGORY_NAME, new VolleyListener(mContext) {
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
                SP.putStringSP(mContext, CATEGORY_NAME, CATEGORY_NAME, resultArray.toString());
                setClassificationListFromJson(resultArray);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                error.printStackTrace();
                setClassificationListFromCache();
            }
        });
    }

    private void setClassificationListFromCache() {
        String classificationStr = SP.getStringSP(mContext, CATEGORY_NAME, CATEGORY_NAME, "");
        try {
            JSONArray jsonArray = new JSONArray(classificationStr);
            setClassificationListFromJson(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param resultArray 解析jsonArray
     */
    private void setClassificationListFromJson(JSONArray resultArray) {
        // 需要分析
        List<Object> mMainClassificationList = new ArrayList<>();
        if (resultArray != null && resultArray.length() != 0) {
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject jsonObject = resultArray.optJSONObject(i);
                Classification category = new Classification(mContext, jsonObject, CATEGORY_NAME);
                mMainClassificationList.add(category);
                if (category.hasErjiClassification) {
                    this.hasErjiClassification = true;
                }
            }
        }
        mMainClassificationAdapter.setList(mMainClassificationList);
        mMainClassificationAdapter.setSelectItem(0);
        List<Object> moreClassifications = ((Classification) mMainClassificationList.get(0)).getClassifications();
        mMoreClassificationAdapter.setList(moreClassifications);


        // 如果没有二级分类的话，只显示左边的一栏
        if (!hasErjiClassification) {
            mMoreListView.setVisibility(View.GONE);
        }
    }

    public void updataListView(final int beginNum) {
        showProgressBar();
        // 刷新的时候不显示缺省页面
        tvSearchNull.setVisibility(View.GONE);
        pullToRefreshListView.setVisibility(View.VISIBLE);
        ServiceProvider.doUpdateLocalResource2(mContext, CATEGORY_NAME, tag, beginNum, Constant.LIST_ITEM_NUM, "", null,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponse(Object responseObject) {
                        pullToRefreshListView.onRefreshComplete();
                        hideProgressBar();
                        JSONObject response = (JSONObject) responseObject;

                        int code = response.optInt(Net.CODE);
                        if (code != Net.SUCCESS) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        }
                        updateListFromJson(response
                                .optJSONObject(Net.DATA), beginNum);
                        updateCompleted();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        updateCompleted();
                    }
                });
    }

    private void updateListFromJson(JSONObject data, int beginNum) {
        if (data == null
                || data.equals("")) {
            return;
        }
        final String userNum = ((AppApplication) getApplicationContext())
                .getUserInfo().account;
        List<Object> list = new ArrayList<>();
        JSONArray resultArray = data.optJSONArray(Net.LIST);
        if (resultArray == null
                || resultArray.length() == 0) {
            if (beginNum > 0) {
                ToastUtil.showUpdateToast(mContext);
            } else {
                tvSearchNull.setVisibility(View.VISIBLE);
                mCategoryAdapter.setList(null);
            }
            return;
        }
        /**
         * 保存未读数
         */
        if (tag == 0) {
            SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + CATEGORY_UNREAD_NUM, data.optInt(ResponseParams.NEWCNT));
        }
        //添加缓存
        if (mCategoryAdapter.getCount() == 0) {
            //添加缓存
            SP.putStringSP(mContext, CATEGORY_NAME, userNum + tag, resultArray.toString());
        } else {
            String SPJSONArray = SP.getStringSP(mContext, CATEGORY_NAME, userNum + tag, "");
            addJsonArrayToSP(userNum + tag, SPJSONArray, resultArray);
        }
        for (int i = 0; i < resultArray.length(); i++) {
            JSONObject jsonObject = resultArray
                    .optJSONObject(i);
            list.add(parseObject(jsonObject));
        }
        if (beginNum == 0) {
            mCategoryAdapter.setList(list);
        } else {
            mCategoryAdapter.addList(list);
        }
    }

    protected void updateCompleted() {
        pullToRefreshListView.onRefreshComplete();
        hideProgressBar();
    }


    private void addJsonArrayToSP(String userNum, String SPJSONArray, JSONArray jsonArray) {
        try {
            if (TextUtils.isEmpty(SPJSONArray)) {
                SP.putStringSP(mContext, CATEGORY_NAME, userNum + tag, jsonArray.toString());
            } else {
                JSONArray SPJsonArray2 = new JSONArray(SPJSONArray);
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    SPJsonArray2.put(jsonArray.opt(i));
                }
                SP.putStringSP(mContext, CATEGORY_NAME, userNum + tag, SPJsonArray2.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        updataListView(0);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        updataListView(mCategoryAdapter.getCount());
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void showMenu() {
        triangleImageView.setImageResource(R.drawable.icon_triangle_up);
        classificationLinear.setVisibility(View.VISIBLE);
        if (mMainClassificationAdapter.getCount() > 0) {
            int main = SP.getIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MAIN, 0);
            int more = SP.getIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MORE, 0);
            mMainClassificationAdapter.setSelectItem(main);
            mMoreClassificationAdapter.setList(((Classification) mMainClassificationAdapter.getItem(main)).getClassifications());
            if (mMoreClassificationAdapter.getCount() > 0) {
                mMoreClassificationAdapter.setSelectItem(more);
            }
            mMoreClassificationAdapter.notifyDataSetChanged();
        }
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_enter);
        classificationLinear.startAnimation(anim);

        status_menu_show = true;
    }

    public void hideMenu() {
        triangleImageView.setImageResource(R.drawable.icon_triangle_down);
        classificationLinear.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_exit);
        classificationLinear.startAnimation(anim);

        status_menu_show = false;
    }

}
