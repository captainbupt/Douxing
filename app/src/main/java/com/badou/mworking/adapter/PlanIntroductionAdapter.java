package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.PlanIndex;
import com.badou.mworking.entity.category.PlanStage;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanIntroductionAdapter extends MyBaseAdapter<PlanStage> {

    PlanIndex mPlanIndex;

    public PlanIntroductionAdapter(Context context) {
        super(context);
    }

    public void setPlanIndex(PlanIndex planIndex) {
        mPlanIndex = planIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_plan_introduction, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        PlanStage planStage = getItem(position);
        holder.indexTextView.setText(String.format(mContext.getResources().getString(R.string.plan_stage_index), position + 1));
        holder.subjectTextView.setText(planStage.getSubject());
        if (position <= mPlanIndex.getStageIndex()) {
            holder.indexTextView.setBackgroundResource(R.drawable.background_button_enable_1bb2f1_normal);
            holder.subjectTextView.setTextColor(0xff1bb2f1);
        } else {
            holder.indexTextView.setBackgroundResource(R.drawable.background_button_disable);
            holder.subjectTextView.setTextColor(mContext.getResources().getColor(R.color.color_grey));
        }
        if (position < mPlanIndex.getStageIndex()) {
            holder.statusTextView.setVisibility(View.VISIBLE);
        } else {
            holder.statusTextView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.index_text_view)
        TextView indexTextView;
        @Bind(R.id.subject_text_view)
        TextView subjectTextView;
        @Bind(R.id.status_text_view)
        TextView statusTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
