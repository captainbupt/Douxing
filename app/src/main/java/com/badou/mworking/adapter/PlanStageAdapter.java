package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.PlanDetail;
import com.badou.mworking.entity.category.PlanIndex;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanStageAdapter extends MyBaseAdapter<CategoryBase> {

    int mStageIndex;
    PlanIndex mCurrentIndex;
    int mCurrentCoursePeriod;

    public PlanStageAdapter(Context context) {
        super(context);
    }

    public void setStageIndex(int stageIndex) {
        mStageIndex = stageIndex;
    }

    public void setCurrentIndex(PlanIndex currentIndex, int currentCoursePeriod) {
        mCurrentIndex = currentIndex;
        mCurrentCoursePeriod = currentCoursePeriod;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_plan_stage_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CategoryBase categoryBase = getItem(position);
        boolean isReadable = PlanDetail.isReadable(mCurrentIndex, mStageIndex, position);
        holder.indexTextView.setText((position + 1) + "");
        holder.subjectTextView.setText(categoryBase.getSubject());
        holder.checkImageView.setImageResource(getResource(categoryBase.getFormat(), isReadable));
        holder.subjectTextView.setTextColor(mContext.getResources().getColor(isReadable ? R.color.color_text_black : R.color.color_text_grey));
        holder.indexTextView.setTextColor(mContext.getResources().getColor(isReadable ? R.color.color_text_black : R.color.color_text_grey));
        holder.indexTextView.setBackgroundResource(isReadable ? R.drawable.background_circle_black : R.drawable.background_circle_grey);
        // 为当前学习完成的课程
        if (PlanDetail.isFinish(mCurrentIndex, mStageIndex, position)) {
            holder.statusTextView.setText(R.string.category_finished);
            holder.statusTextView.setVisibility(View.VISIBLE);
        } else {
            // 为当前课程
            if (isReadable) {
                if (categoryBase.getType() == Category.CATEGORY_TRAINING || categoryBase.getType() == Category.CATEGORY_SHELF) {
                    holder.statusTextView.setText(String.format("%d/%d分钟", mCurrentCoursePeriod, categoryBase.getPeriod()));
                    holder.statusTextView.setVisibility(View.VISIBLE);
                } else {
                    holder.statusTextView.setVisibility(View.INVISIBLE);
                }
            } else {
                holder.statusTextView.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
    }

    private int getResource(int format, boolean isRead) {
        switch (format) {
            case 1:
                return isRead ? R.drawable.plan_icon_format_html_read : R.drawable.plan_icon_format_html_unread;
            case 2:
                return isRead ? R.drawable.plan_icon_format_pdf_read : R.drawable.plan_icon_format_pdf_unread;
            case 3:
                return isRead ? R.drawable.plan_icon_format_xml_read : R.drawable.plan_icon_format_xml_unread;
            case 4:
                return isRead ? R.drawable.plan_icon_format_video_read : R.drawable.plan_icon_format_video_unread;
            case 5:
                return isRead ? R.drawable.plan_icon_format_text_read : R.drawable.plan_icon_format_text_unread;
            case 6:
                return isRead ? R.drawable.plan_icon_format_mp3_read : R.drawable.plan_icon_format_mp3_unread;
        }
        return R.drawable.plan_icon_format_text_unread;
    }

    static class ViewHolder {
        @Bind(R.id.index_text_view)
        TextView indexTextView;
        @Bind(R.id.subject_text_view)
        TextView subjectTextView;
        @Bind(R.id.check_image_view)
        ImageView checkImageView;
        @Bind(R.id.status_text_view)
        TextView statusTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
