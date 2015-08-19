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
import com.badou.mworking.net.bitmap.ImageViewLoader;

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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Plan plan = (Plan) getItem(position);
        holder.topImageView.setVisibility(plan.isTop() ? View.VISIBLE : View.INVISIBLE);
        // 图标资源，默认为已读
        ImageViewLoader.setImageViewResource(holder.iconImageView, R.drawable.icon_plan_item, plan.getImg());

        holder.subjectTextView.setText(plan.getSubject());
        if (plan.isOffline()) {
            holder.stageTextView.setText(R.string.category_expired);
        } else {
            holder.stageTextView.setText(plan.getStage());
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.icon_image_view)
        ImageView iconImageView;
        @Bind(R.id.subject_text_view)
        TextView subjectTextView;
        @Bind(R.id.stage_text_view)
        TextView stageTextView;
        @Bind(R.id.top_image_view)
        ImageView topImageView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}