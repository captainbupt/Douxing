package com.badou.mworking.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.adapter.ClassificationAdapter;
import com.badou.mworking.entity.Classification;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCategoryProgressListActivity extends BaseBackActionBarActivity {

    private ImageView mTitleTriangleImageView;
    private View mTitleLayout;
    private boolean status_menu_show = false;

    protected String CATEGORY_NAME = "";
    protected String CATEGORY_UNREAD_NUM = "";

    public int tag = 0;           //tag == 0 表示全部
    private boolean hasErjiClassification = false;
    private int mMainIndex = 0;

    private static final String SP_KEY_CATEGORY_MAIN = "main";
    private static final String SP_KEY_CATEGORY_MORE = "more";

    private ClassificationAdapter mMainClassificationAdapter = null;
    private ClassificationAdapter mMoreClassificationAdapter = null;
    protected MyBaseAdapter mCategoryAdapter = null;

    private ListView mMainListView;
    private ListView mMoreListView;

    private LinearLayout mClassificationLinear;  // 下拉布局
    private FrameLayout mClassificationContainer;

    protected NoneResultView mNoneResultView;

    protected PullToRefreshListView mContentListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_progress_list);
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
    protected void onItemClick(int position) {
        Category category = (Category) mCategoryAdapter.getItem(position - 1);
        // 考试没有联网
        if (!NetUtils.isNetConnected(mContext)) {
            ToastUtil.showNetExc(mContext);
            return;
        } else {
            CategoryClickHandler.categoryClicker(mContext, new CategoryDetail(mContext, category));
        }
    }

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
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mTitleLayout = inflater.inflate(R.layout.actionbar_progress, null);
        setTitleCustomView(mTitleLayout);
        mTitleTriangleImageView = (ImageView) mTitleLayout.findViewById(R.id.iv_actionbar_triangle);
        mTitleTriangleImageView.setVisibility(View.VISIBLE);
        mTitleTextView = (TextView) mTitleLayout.findViewById(R.id.tv_actionbar_title);
        mTitleTextView.setText(mReceivedIntent.getStringExtra(KEY_TITLE));
        mNoneResultView = (NoneResultView) findViewById(R.id.nrv_activity_base_progress_list_none_result);
        mContentListView = (PullToRefreshListView) findViewById(R.id.ptrlv_user_progress_content);
        mContentListView.setMode(PullToRefreshBase.Mode.BOTH);
        mContentListView.setVisibility(View.VISIBLE);
        mNoneResultView.setVisibility(View.GONE);
        mClassificationContainer = (FrameLayout) findViewById(R.id.fl_activity_base_progress_classification_container);
        mClassificationLinear = (LinearLayout) mClassificationContainer.findViewById(R.id.ll_activity_base_progress_classification);
        mMainListView = (ListView) mClassificationContainer.findViewById(R.id.lv_classification_list_main);
        mMoreListView = (ListView) mClassificationContainer.findViewById(R.id.lv_classification_list_more);
    }

    private void initProgressListener() {
        mTitleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status_menu_show) {
                    hideMenu();
                } else {
                    showMenu();
                }
            }
        });

        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                BaseCategoryProgressListActivity.this.onItemClick(position);
            }
        });
        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<android.widget.ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                updateListView(0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase refreshView) {
                updateListView(mCategoryAdapter.getCount());
            }
        });
        mMainListView.setOnItemClickListener(new OnMainClassificationClickListener());
        mMoreListView.setOnItemClickListener(new OnMoreClassificationClickListener());

        // 拦截底部scrollView的触摸事件
        mClassificationContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideMenu();
                return true;
            }
        });
    }

    private void initProgressData() {
        mMainClassificationAdapter = new ClassificationAdapter(mContext, true);
        mMoreClassificationAdapter = new ClassificationAdapter(mContext, false);
        mMoreListView.setAdapter(mMoreClassificationAdapter);
        mMainListView.setAdapter(mMainClassificationAdapter);
        getClassifications();
        initAdapter();
        if (mCategoryAdapter == null) {
            return;
        }
        mContentListView.setAdapter(mCategoryAdapter);
        setCategoryItemFromCache(tag);
        //updateListView(0);
        mContentListView.setRefreshing();
    }

    private class OnMainClassificationClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            mMainIndex = arg2;
            Classification classification = (Classification) mMainClassificationAdapter.getItem(arg2);
            mMoreClassificationAdapter.setList(classification.getClassifications());
            mMainClassificationAdapter.setSelectedPosition(arg2);
            if (mMoreClassificationAdapter.getCount() == 0) {
                tag = classification.getTag();
                String title = classification.getName();
                setActionbarTitle(title);
                hideMenu();
                updateListView(0);
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
            mMoreClassificationAdapter.setSelectedPosition(arg2);
            hideMenu();
            updateListView(0);
        }
    }

    /**
     * 功能描述:  获取缓存
     */
    public void setCategoryItemFromCache(int tag) {
        List<Object> list = new ArrayList<>();
        String userNum = ((AppApplication) getApplicationContext()).getUserInfo().account;
        String sp = SP.getStringSP(mContext, CATEGORY_NAME, userNum + tag, "");
        if (TextUtils.isEmpty(sp)) {
            mNoneResultView.setVisibility(View.VISIBLE);
            return;
        } else {
            mNoneResultView.setVisibility(View.GONE);
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
            public void onErrorCode(int code) {
                setClassificationListFromCache();
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                JSONArray resultArray = response.optJSONArray(Net.DATA);
                // 缓存分类信息
                SP.putStringSP(mContext, CATEGORY_NAME, CATEGORY_NAME, resultArray.toString());
                setClassificationListFromJson(resultArray);
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
        mMainClassificationAdapter.setSelectedPosition(0);
        List<Object> moreClassifications = ((Classification) mMainClassificationList.get(0)).getClassifications();
        mMoreClassificationAdapter.setList(moreClassifications);


        // 如果没有二级分类的话，只显示左边的一栏
        if (!hasErjiClassification) {
            mMoreListView.setVisibility(View.GONE);
        }
    }

    public void updateListView(final int beginNum) {
        showProgressBar();
        // 刷新的时候不显示缺省页面
        mNoneResultView.setVisibility(View.GONE);
        mContentListView.setVisibility(View.VISIBLE);
        ServiceProvider.doUpdateLocalResource2(mContext, CATEGORY_NAME, tag, beginNum, Constant.LIST_ITEM_NUM, "", null,
                new VolleyListener(mContext) {

                    @Override
                    public void onCompleted() {
                        mContentListView.onRefreshComplete();
                        hideProgressBar();
                    }

                    @Override
                    public void onResponse(Object responseObject) {
                        super.onResponse(responseObject);
                        System.out.println("response: " + responseObject);
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        updateListFromJson(response
                                .optJSONObject(Net.DATA), beginNum);
                        mContentListView.onRefreshComplete();
                        hideProgressBar();
                        updateCompleted();
                    }
                });
    }

    protected void updateCompleted() {
    }

    private void updateListFromJson(JSONObject data, int beginNum) {
        if (data == null
                || data.equals("")) {
            return;
        }
        final String userNum = ((AppApplication) getApplicationContext())
                .getUserInfo().account;
        /**
         * 保存未读数
         */
        if (tag == 0) {
            SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + CATEGORY_UNREAD_NUM, data.optInt(ResponseParameters.NEWCNT));
        }
        List<Object> list = new ArrayList<>();
        JSONArray resultArray = data.optJSONArray(Net.LIST);
        if (resultArray == null
                || resultArray.length() == 0) {
            if (beginNum > 0) {
                ToastUtil.showUpdateToast(mContext);
            } else {
                mNoneResultView.setVisibility(View.VISIBLE);
                mCategoryAdapter.setList(null);
            }
            return;
        }
        mNoneResultView.setVisibility(View.GONE);
        //添加缓存
        if (mCategoryAdapter.getCount() == 0) {
            //添加缓存
            SP.putStringSP(mContext, CATEGORY_NAME, userNum + tag, resultArray.toString());
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

    public void showMenu() {
        mTitleTriangleImageView.setImageResource(R.drawable.icon_triangle_up);
        if (mMainClassificationAdapter.getCount() > 0) {
            int main = SP.getIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MAIN, 0);
            int more = SP.getIntSP(mContext, CATEGORY_NAME, SP_KEY_CATEGORY_MORE, 0);
            mMainClassificationAdapter.setSelectedPosition(main);
            mMoreClassificationAdapter.setList(((Classification) mMainClassificationAdapter.getItem(main)).getClassifications());
            if (mMoreClassificationAdapter.getCount() > 0) {
                mMoreClassificationAdapter.setSelectedPosition(more);
            }
            mMoreClassificationAdapter.notifyDataSetChanged();
        }
        mClassificationContainer.setVisibility(View.VISIBLE);
        mClassificationLinear.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_enter);
        mClassificationLinear.startAnimation(anim);

        status_menu_show = true;
    }

    public void hideMenu() {
        mTitleTriangleImageView.setImageResource(R.drawable.icon_triangle_down);
        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.popup_exit);
        mClassificationLinear.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mClassificationContainer.setVisibility(View.GONE);
                mClassificationLinear.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        status_menu_show = false;
    }

}
