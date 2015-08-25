package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.entity.category.Survey;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述: 通知公告adapter
 */
public class SurveyAdapter extends MyBaseAdapter<Category> {

    public SurveyAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_survey_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Survey survey = (Survey) getItem(position);
        int size = DensityUtil.getInstance().getOffsetLess();
        // 使得第一条上端有一段空白
        if (position == 0) {
            convertView.setPadding(0, size, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 0);
        }
        if (!survey.isUnread()) {
            holder.iconImageView.setImageResource(R.drawable.icon_survey_item_read);
            holder.unreadTextView.setText(R.string.category_finished);
            holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
            holder.unreadTextView.setBackgroundColor(0x00000000);
        } else {
            holder.iconImageView.setImageResource(R.drawable.icon_survey_item_unread);
            holder.unreadTextView.setText(R.string.category_unresponse);
            holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
            holder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
        }
        holder.subjectTextView.setText(survey.getSubject());
        if (survey.isTop()) {
            holder.topImageView.setVisibility(View.VISIBLE);
        } else {
            holder.topImageView.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView subjectTextView;
        ImageView iconImageView;
        TextView unreadTextView;
        ImageView topImageView;

        public ViewHolder(View view) {
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_survey_top);
            subjectTextView = (TextView) view.findViewById(R.id.tv_adapter_survey_subject);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_survey_icon);
            unreadTextView = (TextView) view.findViewById(R.id.tv_adapter_survey_unread);
        }
    }
}
