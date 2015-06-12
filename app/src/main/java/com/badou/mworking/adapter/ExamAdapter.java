package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.category.Exam;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.TimeTransfer;

import android.widget.TextView;

/**
 * 功能描述:  在线考试列表页适配器
 */
public class ExamAdapter extends MyBaseAdapter {


    public ExamAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Exam exam = (Exam) getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_exam_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 图标资源，默认为已读
        int iconResId = R.drawable.icon_exam_item_read;
        // 判断read字段， 已考完
        if (exam.isRead()) {
            holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_red));
            holder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            if (exam.isGraded) { //显示:已考完(判断是不是是不是个人中心进入的)
                holder.unreadTextView.setText(exam.score + mContext.getResources().getString(R.string.text_score));
            } else { //显示:待批阅
                holder.unreadTextView.setText(R.string.category_ungraded);
            }
            // 未考试
        } else {
            if (exam.offline == Constant.OVERDUE_YES) { //显示:已过期
                holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                holder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                holder.unreadTextView.setText(R.string.category_expired);
            } else { //显示:未考试, 只有未考试的情况下才需要改变图标
                holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                holder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                holder.unreadTextView.setText(R.string.category_unexam);
                iconResId = R.drawable.icon_exam_item_unread;
            }
        }
        holder.iconImageView.setImageResource(iconResId);
        if (exam.top == Constant.TOP_YES) {
            holder.topImageView.setVisibility(View.VISIBLE);
        } else {
            holder.topImageView.setVisibility(View.INVISIBLE);
        }
        holder.subjectTextView.setText(exam.subject);
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, exam.time));
        return convertView;
    }

    static class ViewHolder {
        TextView subjectTextView;
        TextView dateTextView;
        ImageView iconImageView;
        TextView unreadTextView;
        ImageView topImageView;

        public ViewHolder(View view) {
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_exam_top);
            subjectTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_exam_subject);
            dateTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_exam_date);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_exam_icon);
            unreadTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_exam_unread);
        }
    }
}
