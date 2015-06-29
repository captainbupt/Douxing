package com.badou.mworking.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.MainGridActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.MainSearchAdapter;
import com.badou.mworking.base.BaseFragment;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.model.category.Category;
import com.badou.mworking.model.category.CategoryBasic;
import com.badou.mworking.model.category.CategoryDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.FastBlur;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class MainSearchFragment extends BaseFragment {

    private static final int COUNT_CATEGORY = 5;
    private static final String[] KEY_LIST = new String[]{"notice", "training", "exam", "task", "shelf"};
    private static final String[] KEY_CATEGORY_NAME = new String[]{RequestParameters.CHK_UPDATA_PIC_NOTICE, RequestParameters.CHK_UPDATA_PIC_TRAINING,
            RequestParameters.CHK_UPDATA_PIC_EXAM, RequestParameters.CHK_UPDATA_PIC_TASK, RequestParameters.CHK_UPDATA_PIC_SHELF};

    private LinearLayout mContainerView;
    private TextView mCancelTextView;
    private EditText mTitleEditView;
    private LinearLayout mNoneResultLayout;
    private TextView mNoneResultTextView;
    private ImageView mNoneResultImageView;
    private PullToRefreshListView mResultListView;
    private MainSearchAdapter mResultAdpater;
    private String[] mCategoryNames;
    private WaitProgressDialog progressDialog;
    private static Handler mBackgroundHandler;

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContainerView = (LinearLayout) inflater.inflate(R.layout.fragment_main_search, null);
        initView(mContainerView);
        initListener();
        initData();
        if (mBackgroundHandler == null)
            mBackgroundHandler = new Handler();
/*        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                while (mContainerView.getHeight() == 0) ;
                blur(((MainGridActivity) mContext).myShot(), mContainerView);
            }
        });*/
        return mContainerView;
    }

    private void initView(View view) {
        mCancelTextView = (TextView) view.findViewById(R.id.tv_main_search_cancel);
        mNoneResultImageView = (ImageView) view.findViewById(R.id.iv_main_search_none);
        mNoneResultLayout = (LinearLayout) view.findViewById(R.id.ll_main_search_none);
        mNoneResultTextView = (TextView) view.findViewById(R.id.tv_main_search_none);
        mResultListView = (PullToRefreshListView) view.findViewById(R.id.ptrlv_main_search_results);
        mTitleEditView = (EditText) view.findViewById(R.id.et_main_search_title);
        progressDialog = new WaitProgressDialog(mContext);
        progressDialog.setCancelable(false);
        setFocus();
        clearResult();
    }

    private void initListener() {
        mCancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backPressed();
            }
        });
        mTitleEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    clearResultWithoutEditText();
                    mBackgroundHandler.removeCallbacks(mUpdateRunnable);
                } else {
                    showResult();
                    // 避免过快刷新
                    mBackgroundHandler.removeCallbacks(mUpdateRunnable);
                    mBackgroundHandler.postDelayed(mUpdateRunnable, 1000);
                }
            }
        });
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Object item = mResultAdpater.getItem(i - 1);
                if (item.getClass().equals(String.class))
                    return;
                final CategoryBasic basic = (CategoryBasic) item;
                progressDialog.show();
                ServiceProvider.getResourceDetail(mContext, basic.rid, new VolleyListener(mContext) {
                    @Override
                    public void onResponseSuccess(JSONObject jsonObject) {
                        if (!mActivity.isFinishing()) {
                            progressDialog.dismiss();
                        }
                        Category c = Category.getCategoryFromDetail(new CategoryDetail(mContext, jsonObject.optJSONObject(Net.DATA), basic.type, basic.rid, basic.subject, null));
                        startActivity(CategoryClickHandler.getIntent(mContext, c));
                    }

                    @Override
                    public void onErrorCode(int code) {
                        ToastUtil.showToast(mContext, R.string.tip_message_center_resource_gone);
                    }
                });
            }
        });
        mContainerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // 屏蔽点击事件，防止主页面相应点击
                return true;
            }
        });
    }

    public void backPressed() {
        if (TextUtils.isEmpty(mTitleEditView.getText().toString())) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.hide(MainSearchFragment.this);
            transaction.commit();
            ((MainGridActivity) mActivity).isSearching = false;
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTitleEditView.getWindowToken(), 0);
        } else {
            clearResult();
        }
    }

    public void setFocus() {
        if (mTitleEditView != null) {
            mTitleEditView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTitleEditView.setFocusable(true);
                    mTitleEditView.setFocusableInTouchMode(true);
                    mTitleEditView.requestFocus();
                    InputMethodManager inputManager = (InputMethodManager) mTitleEditView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(mTitleEditView, 0);
                }
            }, 300);
        }
    }

    private Runnable mUpdateRunnable = new Runnable() {

        @Override
        public void run() {
            updateResource(mTitleEditView.getText().toString());
        }
    };

    private void initData() {
        this.mCategoryNames = new String[COUNT_CATEGORY];
        for (int ii = 0; ii < COUNT_CATEGORY; ii++) {
            mCategoryNames[ii] = MainIcon.getMainIcon(mContext, KEY_CATEGORY_NAME[ii]).name;
        }
        mResultAdpater = new MainSearchAdapter(mContext, mCategoryNames);
        mResultListView.setAdapter(mResultAdpater);
        mResultListView.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
    }

    private void updateResource(final String key) {
        mResultListView.setRefreshing();
        mResultAdpater.clear();
        ServiceProvider.doSearch(mContext, key, new VolleyListener(mContext) {

            @Override
            public void onErrorCode(int code) {
                updateListFromJson(null, key);
            }

            @Override
            public void onResponseSuccess(JSONObject response) {
                updateListFromJson(response
                        .optJSONObject(Net.DATA), key);
            }
        });
    }

    private void updateListFromJson(JSONObject resultObject, String key) {
        if (!mTitleEditView.getText().toString().equals(key)) {
            // 结果不同步，舍弃返回值
            return;
        }
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTitleEditView.getWindowToken(), 0);
        if (resultObject == null) {
            mResultAdpater.clear();
        } else {
            List[] lists = new List[COUNT_CATEGORY];
            for (int ii = 0; ii < COUNT_CATEGORY; ii++) {
                JSONArray objectList = resultObject.optJSONArray(KEY_LIST[ii]);
                if (objectList == null || objectList.length() == 0)
                    continue;
                List<CategoryBasic> categoryBasicList = new ArrayList<>();
                for (int jj = 0; jj < objectList.length(); jj++) {
                    categoryBasicList.add(new CategoryBasic(Category.CATEGORY_NOTICE + ii, objectList.optJSONObject(jj)));
                }
                lists[ii] = categoryBasicList;
            }
            mResultAdpater.setList(lists);
        }
        mResultListView.onRefreshComplete();
        if (mResultAdpater.getCount() == 0) {
            showNoneResult(key);
        }
    }

    private void showResult() {
        mNoneResultLayout.setVisibility(View.GONE);
        mResultListView.setVisibility(View.VISIBLE);
    }

    private void showNoneResult(String key) {
        String tip = getResources().getString(R.string.main_search_result_none);
        mNoneResultTextView.setText(
                tip.replace("***", key));
        mNoneResultImageView.setImageResource(R.drawable.icon_main_search_tip);
        mNoneResultLayout.setVisibility(View.VISIBLE);
        mResultListView.setVisibility(View.GONE);
    }

    private void clearResult() {
        mTitleEditView.setText("");
        mNoneResultTextView.setText(R.string.main_search_result_tip);
        mNoneResultImageView.setImageResource(R.drawable.icon_main_search_tip);
        mNoneResultLayout.setVisibility(View.VISIBLE);
        mResultListView.setVisibility(View.GONE);
    }

    // 避免重复调用
    private void clearResultWithoutEditText() {
        mNoneResultTextView.setText(R.string.main_search_result_tip);
        mNoneResultImageView.setImageResource(R.drawable.icon_main_search_tip);
        mNoneResultLayout.setVisibility(View.VISIBLE);
        mResultListView.setVisibility(View.GONE);
    }

    private void blur(Bitmap bkg, View view) {
        float radius = 2;
        float scaleFactor = 32;

        Bitmap overlay = Bitmap.createBitmap((int) (view.getMeasuredWidth() / scaleFactor), (int) (view.getMeasuredHeight() / scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop() / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bkg, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
    }
}

