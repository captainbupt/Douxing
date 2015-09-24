package com.badou.mworking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Plan;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanAdapter extends CategoryBaseAdapter {

    public PlanAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, onClickListener);
    }

    @Override
    public BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.adapter_plan_item, parent, false));
        holder.circleProgressView.setTextMode(TextMode.PERCENT);
        holder.circleProgressView.setShowUnit(true);
        holder.circleProgressView.setTextSize((int) (DensityUtil.getInstance().getTextSizeMicro() * 0.9f));
        holder.circleProgressView.setUnitSize((int) (DensityUtil.getInstance().getTextSizeMicro() * 0.9f));
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        Plan plan = (Plan) getItem(position);
        viewHolder.topImageView.setVisibility(plan.isTop() ? View.VISIBLE : View.INVISIBLE);

        if (TextUtils.isEmpty(plan.getImg())) {
            viewHolder.iconImageView.setImageURI(UriUtil.getHttpUri(plan.getImg()));
        }
        viewHolder.subjectTextView.setText(plan.getSubject());
        if (plan.isOffline()) {
            viewHolder.stageTextView.setText(R.string.category_expired);
        } else {
            viewHolder.stageTextView.setText(plan.getStage());
        }
        viewHolder.infoTextView.setText(String.format("总学时:%d分钟  阶段数:%d", plan.getTotalTime(), plan.getStageNumber()));
        viewHolder.circleProgressView.setValue(plan.getPercent());
    }

    public static class MyViewHolder extends BaseViewHolder {
        @Bind(R.id.icon_image_view)
        SimpleDraweeView iconImageView;
        @Bind(R.id.circle_progress_view)
        CircleProgressView circleProgressView;
        @Bind(R.id.subject_text_view)
        TextView subjectTextView;
        @Bind(R.id.stage_text_view)
        TextView stageTextView;
        @Bind(R.id.info_text_view)
        TextView infoTextView;
        @Bind(R.id.top_image_view)
        ImageView topImageView;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}