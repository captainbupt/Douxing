package com.badou.mworking.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.badou.mworking.MainGridActivity;
import com.badou.mworking.R;
import com.badou.mworking.adapter.MainSearchAdapter;
import com.badou.mworking.model.Category;
import com.badou.mworking.model.Exam;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FastBlur;
import com.badou.mworking.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.FrameLayout;
import org.holoeverywhere.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class SearchLinearView extends LinearLayout {

    private Context mContext;
    private TextView mCancelTextView;
    private EditText mTitleEditView;
    private LinearLayout mNoneResultLayout;
    private TextView mNoneResultTextView;
    private ImageView mNoneResultImageView;
    private PullToRefreshListView mResultListView;
    private MainSearchAdapter mResultAdpater;
    private String[] mCategoryNames;
    private int[] mCategoryTypes;
    private Handler mBackgroundHandler;
    private OnRemoveListener mOnRemoveListener;
    private boolean isStop;
    private final int interval = 10;

    public SearchLinearView(Context context, String[] categoryNames, int[] categoryTypes, OnRemoveListener onRemoveListener) {
        super(context);
        this.mCategoryNames = categoryNames;
        this.mCategoryTypes = categoryTypes;
        this.mContext = context;
        this.mOnRemoveListener = onRemoveListener;
        setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_activity_main_search, this);
        mResultAdpater = new MainSearchAdapter(mContext, categoryNames);
        mBackgroundHandler = new Handler();
        isStop = false;
        mBackgroundHandler.postDelayed(new BackgroundRunnable(), interval);
        initView();
        initListener();
    }

    private void initView() {
        mCancelTextView = (TextView) findViewById(R.id.tv_main_search_cancel);
        mNoneResultImageView = (ImageView) findViewById(R.id.iv_main_search_none);
        mNoneResultLayout = (LinearLayout) findViewById(R.id.ll_main_search_none);
        mNoneResultTextView = (TextView) findViewById(R.id.tv_main_search_none);
        mResultListView = (PullToRefreshListView) findViewById(R.id.ptrlv_main_search_results);
        mTitleEditView = (EditText) findViewById(R.id.et_main_search_title);
        mResultListView.setAdapter(mResultAdpater);
        clearResult();
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (!isStop)
            mBackgroundHandler.postDelayed(new BackgroundRunnable(), interval);
    }

    private void initListener() {
        mCancelTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleEditView.getText().toString())) {
                    isStop = true;
                    if (mOnRemoveListener != null)
                        mOnRemoveListener.onRemove();
                } else {
                    clearResult();
                }
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
                } else {
                    showResult();
                    if (mResultListView.isRefreshing()) {
                        updateResource(editable.toString());
                    } else {
                        mResultListView.setRefreshing(true);
                    }
                }
            }
        });
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        mResultListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                updateResource(mTitleEditView.getText().toString());
            }
        });
    }

    private void updateResource(String key) {
        mResultAdpater.clear();
        for (int ii = 0; ii < mCategoryTypes.length; ii++) {
            updateResource(key, ii);
        }
    }

    private void updateResource(final String key, final int type) {
        ServiceProvider.doUpdateLocalResource2(mContext, Category.CATEGORY_KEY_NAMES[mCategoryTypes[type]], 0, 0, Constant.LIST_ITEM_NUM, key, null,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponse(Object responseObject) {
                        JSONObject response = (JSONObject) responseObject;

                        int code = response.optInt(Net.CODE);
                        if (code != Net.SUCCESS) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        }
                        updateListFromJson(response
                                .optJSONObject(Net.DATA), key, type);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        updateListFromJson(null, key, type);
                    }
                });
    }

    private void updateListFromJson(JSONObject resultObject, String key, int type) {
        if (!mTitleEditView.getText().toString().equals(key)) {
            // 结果不同步，舍弃返回值
            return;
        }
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTitleEditView.getWindowToken(), 0);
        if (resultObject == null)
            mResultAdpater.addList(type, mCategoryNames[type], null);
        else {
            JSONArray resultArray = resultObject.optJSONArray(Net.LIST);
            if (resultArray == null) {
                mResultAdpater.addList(type, mCategoryNames[type], null);
            } else {
                List<Category> resultList = new ArrayList<>();
                for (int ii = 0; ii < resultArray.length(); ii++) {
                    if (mCategoryTypes[type] == Category.CATEGORY_TRAIN) {
                        resultList.add(new Train((JSONObject) resultArray.opt(ii)));
                    } else if (mCategoryTypes[type] == Category.CATEGORY_EXAM) {
                        resultList.add(new Exam((JSONObject) resultArray.opt(ii)));
                    }
                }
                mResultAdpater.addList(type, mCategoryNames[type], new ArrayList<>(resultList));
            }
        }
        mResultListView.onRefreshComplete();
        if (mResultAdpater.getCount() == 0) {
            noneResult(key);
        }
    }

    private void showResult() {
        mNoneResultLayout.setVisibility(View.GONE);
        mResultListView.setVisibility(View.VISIBLE);
    }

    private void noneResult(String key) {
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

    class BackgroundRunnable implements Runnable {

        @Override
        public void run() {
            blur(((MainGridActivity) mContext).myShot(), SearchLinearView.this);
/*            if (!isStop)
                mBackgroundHandler.postDelayed(this, interval);*/
        }
    }

    public interface OnRemoveListener {
        void onRemove();
    }

    private void blur(Bitmap bkg, View view) {
        float radius = 2;
        float scaleFactor = 16;

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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}

