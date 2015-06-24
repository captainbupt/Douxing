package com.badou.mworking;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.adapter.UserProgressAdapter;
import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.Main.MainIcon;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.HorizontalProgressDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的学习和我的考试模块
 * 逻辑和界面类似，合在一起
 */
public class UserProgressActivity extends BaseNoTitleActivity {

    public static final String KEY_TYPE = "type";
    public static final String KEY_USERINFO = "userinfo";

    public ImageView mBackImageView;  // action 左侧iv
    public TextView mTitleTextView;  // action 中间tv
    private TextView mTopContentTextView;
    private TextView mTopRankTextView;
    private TextView mMiddleTextView;
    private TextView mBottomTextView;
    private TextView mAverageTextView;
    private PullToRefreshListView mContentListView;
    private UserProgressAdapter mCategoryAdapter;

    private int beginIndex = 0;

    private int mType;
    private HorizontalProgressDialog pro;// 文件下载的进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_progress);
        mType = mReceivedIntent.getIntExtra(KEY_TYPE, -1);
        if (mType != Category.CATEGORY_EXAM && mType != Category.CATEGORY_TRAINING) {
            finish();
            return;
        }
        initView();
        initListener();
        initData();
    }

    protected void initView() {
        mBackImageView = (ImageView) findViewById(R.id.iv_user_progress_top_back);
        mTitleTextView = (TextView) findViewById(R.id.tv_user_progress_top_title);
        mTopContentTextView = (TextView) findViewById(R.id.tv_user_progress_top_content);
        mTopRankTextView = (TextView) findViewById(R.id.tv_user_progress_top_rank);
        mMiddleTextView = (TextView) findViewById(R.id.tv_user_progress_middle_tip);
        mContentListView = (PullToRefreshListView) findViewById(R.id.ptrlv_user_progress_content);
        mBottomTextView = (TextView) findViewById(R.id.tv_user_progress_bottom);
        mAverageTextView = (TextView) findViewById(R.id.tv_user_progress_top_average);
        mContentListView.setMode(Mode.BOTH);
    }

    protected void initListener() {
        mBackImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBottomTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mType == Category.CATEGORY_TRAINING) {
                    Intent intent = new Intent(mContext, TrainActivity.class);
                    intent.putExtra(BaseActionBarActivity.KEY_TITLE, MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_TRAINING).name);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ExamActivity.class);
                    intent.putExtra(BaseActionBarActivity.KEY_TITLE, MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_EXAM).name);
                    startActivity(intent);
                }
            }
        });

        mContentListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Category category = (Category) mCategoryAdapter.getItem(position - 1);
                CategoryClickHandler.categoryClicker(mContext, new CategoryDetail(mContext, category));
            }
        });

        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<android.widget.ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<android.widget.ListView> refreshView) {
                beginIndex = 0;
                updataListView(beginIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<android.widget.ListView> refreshView) {
                updataListView(beginIndex);
            }
        });
    }

    private void initData() {
        UserDetail userDetail = (UserDetail) mReceivedIntent.getSerializableExtra(
                KEY_USERINFO);
        if (userDetail == null) {
            // return 直接返回，不在进行其他操作，应为这回对空对象操作，下面都会报错
            finish();
            return;
        }
        if (mType == Category.CATEGORY_TRAINING) {
            String str1 = " <font color=\'#ffffff\'><b>"
                    + getResources().getString(R.string.study_fir)
                    + "</b></font>";// 你的学习进度
            String str2 = " <font color=\'#ffffff\'><b>"
                    + getResources().getString(R.string.study_sec)
                    + "</b></font>";// 的用户
            String html = str1 + " <font color=\'#DD523f\'><b>" + userDetail.study_rank
                    + "%</b></font>" + str2;
            mTopRankTextView.setText(Html.fromHtml(html));
            if (userDetail.study_rank >= 0 && userDetail.study_rank <= 50) {
                mTopContentTextView.setText(R.string.study_level_low);
            } else if (userDetail.study_rank > 50 && userDetail.study_rank <= 80) {
                mTopContentTextView.setText(R.string.study_level_middle);
            } else if (userDetail.study_rank > 80 && userDetail.study_rank <= 100) {
                mTopContentTextView.setText(R.string.study_level_high);
            }
            mMiddleTextView.setText(R.string.user_progress_middle_training);
            mBottomTextView.setText(R.string.user_progress_bottom_training);
            mTitleTextView.setText(R.string.user_center_my_study_progress);
            mAverageTextView.setVisibility(View.GONE);
            mTopContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.sp2px(mContext, 50));
        } else {
            String str1 = " <font color=\'#ffffff\'><b>" + "第" + "</b></font>";//第
            String str2 = " <font color=\'#ffffff\'><b>" + "名, " + "</b></font>";//名
            String html1 = str1 + " <font color=\'#DD523f\'><b>" + userDetail.score_over + "</b></font>" + str2;
            String str3 = " <font color=\'#ffffff\'><b>" + "超过" + "</b></font>";//你的学习成绩
            String str4 = " <font color=\'#ffffff\'><b>" + "学员" + "</b></font>";//的用户
            String html2 = str3 + " <font color=\'#DD523f\'><b>" + userDetail.score_rank + "%</b></font>" + str4;
            mTopRankTextView.setText(Html.fromHtml(html1 + html2));
            mTopContentTextView.setText(userDetail.score + "");
            mMiddleTextView.setText(R.string.user_progress_middle_exam);
            mBottomTextView.setText(R.string.user_progress_bottom_exam);
            mAverageTextView.setVisibility(View.VISIBLE);
            mTitleTextView.setText(R.string.user_center_my_exam);
            mTopContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, DensityUtil.sp2px(mContext, 70));
        }
        mCategoryAdapter = new UserProgressAdapter(mContext, mType);
        mContentListView.setAdapter(mCategoryAdapter);
        mContentListView.setRefreshing();
    }

    /**
     * 功能描述:
     *
     * @param beginNum
     */
    private void updataListView(final int beginNum) {
        ServiceProvider.doUpdateLocalResource2(mContext, Category.CATEGORY_KEY_NAMES[mType], 0, beginNum, Constant.LIST_ITEM_NUM, "", "1",
                new VolleyListener(UserProgressActivity.this) {

                    @Override
                    public void onCompleted() {
                        mContentListView.onRefreshComplete();
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        List<Object> list = new ArrayList<>();
                        JSONObject data = response.optJSONObject(Net.DATA);
                        if (data == null
                                || data.equals("")) {
                            return;
                        }
                        JSONArray resultArray = data
                                .optJSONArray(Net.LIST);
                        if (resultArray == null
                                || resultArray.length() == 0) {
                            if (beginIndex > 0) {
                                ToastUtil.showUpdateToast(UserProgressActivity.this);
                            }
                            return;
                        }
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject jsonObject = resultArray.optJSONObject(i);
                            if (mType == Category.CATEGORY_EXAM) {
                                list.add(new Exam(jsonObject));
                            } else {
                                list.add(new Train(jsonObject));
                            }
                            beginIndex++;
                        }
                        if (beginNum <= 0) {
                            mCategoryAdapter.setList(list);
                        } else {
                            mCategoryAdapter.addList(list);
                        }
                    }
                });
    }
}
