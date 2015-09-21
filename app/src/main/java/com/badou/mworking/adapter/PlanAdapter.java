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

public class PlanAdapter extends MyBaseAdapter<Category> {

    public PlanAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_plan_item, parent, false);
            holder = new ViewHolder(convertView);
            holder.circleProgressView.setTextMode(TextMode.PERCENT);
            holder.circleProgressView.setShowUnit(true);
            holder.circleProgressView.setTextSize((int) (DensityUtil.getInstance().getTextSizeMicro() * 0.9f));
            holder.circleProgressView.setUnitSize((int) (DensityUtil.getInstance().getTextSizeMicro() * 0.9f));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Plan plan = (Plan) getItem(position);
        holder.topImageView.setVisibility(plan.isTop() ? View.VISIBLE : View.INVISIBLE);

        if (TextUtils.isEmpty(plan.getImg())) {
            holder.iconImageView.setImageURI(UriUtil.getHttpUri(plan.getImg()));
        }
        holder.subjectTextView.setText(plan.getSubject());
        if (plan.isOffline()) {
            holder.stageTextView.setText(R.string.category_expired);
        } else {
            holder.stageTextView.setText(plan.getStage());
        }
        holder.infoTextView.setText(String.format("总学时:%d分钟  阶段数:%d", plan.getTotalTime(), plan.getStageNumber()));
        holder.circleProgressView.setValue(plan.getPercent());
        return convertView;
    }

    static class ViewHolder {
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}