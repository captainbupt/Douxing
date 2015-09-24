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
public class SurveyAdapter extends CategoryBaseAdapter {

    public SurveyAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, onClickListener);
    }

    @Override
    public BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.adapter_survey_item, null));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        final Survey survey = (Survey) getItem(position);
        if (!survey.isUnread()) {
            viewHolder.iconImageView.setImageResource(R.drawable.icon_survey_item_read);
            viewHolder.unreadTextView.setText(R.string.category_finished);
            viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
            viewHolder.unreadTextView.setBackgroundColor(0x00000000);
        } else {
            viewHolder.iconImageView.setImageResource(R.drawable.icon_survey_item_unread);
            viewHolder.unreadTextView.setText(R.string.category_unresponse);
            viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
            viewHolder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
        }
        viewHolder.subjectTextView.setText(survey.getSubject());
        if (survey.isTop()) {
            viewHolder.topImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.topImageView.setVisibility(View.GONE);
        }
    }

    public static class MyViewHolder extends BaseViewHolder{
        TextView subjectTextView;
        ImageView iconImageView;
        TextView unreadTextView;
        ImageView topImageView;

        public MyViewHolder(View view) {
            super(view);
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_survey_top);
            subjectTextView = (TextView) view.findViewById(R.id.tv_adapter_survey_subject);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_survey_icon);
            unreadTextView = (TextView) view.findViewById(R.id.tv_adapter_survey_unread);
        }
    }
}
