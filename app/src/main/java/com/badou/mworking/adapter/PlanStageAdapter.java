package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.PlanIndex;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PlanStageAdapter extends MyBaseAdapter<CategoryBase> {

    int mStageIndex;
    PlanIndex mCurrentIndex;

    public PlanStageAdapter(Context context) {
        super(context);
    }

    public void setStageIndex(int stageIndex) {
        mStageIndex = stageIndex;
    }

    public void setCurrentIndex(PlanIndex currentIndex) {
        mCurrentIndex = currentIndex;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_entry_operation_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.mContentTextView.setVisibility(View.GONE);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CategoryBase categoryBase = getItem(position);
        boolean isRead = isRead(position);
        holder.mIndexTextView.setText((position + 1) + "");
        holder.mSubjectTextView.setText(categoryBase.getSubject());
        holder.mCheckImageView.setImageResource(getResource(categoryBase.getFormat(), isRead));
        holder.mSubjectTextView.setTextColor(mContext.getResources().getColor(isRead ? R.color.color_text_black : R.color.color_text_grey));
        holder.mIndexTextView.setBackgroundResource(isRead ? R.drawable.background_circle_black : R.drawable.background_circle_grey);
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

    private boolean isRead(int index) {
        if (mStageIndex < mCurrentIndex.getStageIndex()) {
            return true;
        } else if (mStageIndex == mCurrentIndex.getStageIndex()) {
            return index < mCurrentIndex.getResourceIndex();
        } else {
            return false;
        }

    }

    static class ViewHolder {
        @Bind(R.id.index_text_view)
        TextView mIndexTextView;
        @Bind(R.id.subject_text_view)
        TextView mSubjectTextView;
        @Bind(R.id.content_text_view)
        TextView mContentTextView;
        @Bind(R.id.check_image_view)
        ImageView mCheckImageView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
