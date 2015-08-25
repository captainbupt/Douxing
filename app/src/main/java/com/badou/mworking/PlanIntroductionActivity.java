package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badou.mworking.adapter.PlanIntroductionAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.presenter.category.PlanPresenter;

public class PlanIntroductionActivity extends BaseBackActionBarActivity {

    private final static String KEY_CATEGORY_DETAIL = "categoryDetail";

    LinearLayout mHeaderLayout;
    ListView mStageListView;
    TextView mSubjectTextView;
    TextView mDescriptionTextView;

    PlanIntroductionAdapter mPlanIntroductionAdapter;

    public static Intent getIntent(Context context, CategoryDetail categoryDetail) {
        Intent intent = new Intent(context, PlanIntroductionActivity.class);
        intent.putExtra(KEY_CATEGORY_DETAIL, categoryDetail);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_introduction);
        setActionbarTitle(R.string.plan_introduction_title);
        CategoryDetail categoryDetail = (CategoryDetail) mReceivedIntent.getSerializableExtra(KEY_CATEGORY_DETAIL);
        if (categoryDetail == null) {
            finish();
        }
        initView();
        initData(categoryDetail);
    }

    private void initView() {
        mStageListView = (ListView) findViewById(R.id.stage_list_view);
        mHeaderLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.header_plan_introduction, null);
        mSubjectTextView = (TextView) mHeaderLayout.findViewById(R.id.subject_text_view);
        mDescriptionTextView = (TextView) mHeaderLayout.findViewById(R.id.description_text_view);

        mStageListView.addHeaderView(mHeaderLayout, null, false);
        mPlanIntroductionAdapter = new PlanIntroductionAdapter(mContext);
        mStageListView.setAdapter(mPlanIntroductionAdapter);
    }

    private void initData(final CategoryDetail categoryDetail) {
        mSubjectTextView.setText(categoryDetail.getSubject());
        mDescriptionTextView.setText(categoryDetail.getPlan().getDescription());
        mPlanIntroductionAdapter.setPlanIndex(categoryDetail.getPlan().getNow());
        mPlanIntroductionAdapter.setList(categoryDetail.getPlan().getStages());
        mStageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position - 1 > categoryDetail.getPlan().getNow().getStageIndex()) { // 尚未开始
                    showToast(R.string.plan_stage_unreadable);
                } else {
                    setResult(RESULT_OK, PlanPresenter.getResultForStage(position - 1));
                    finish();
                }
            }
        });
    }
}
