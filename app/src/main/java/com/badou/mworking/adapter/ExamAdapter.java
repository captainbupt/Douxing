package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Exam;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述:  在线考试列表页适配器
 */
public class ExamAdapter extends CategoryBaseAdapter {


    public ExamAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, onClickListener);
    }

    @Override
    public BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.adapter_notice_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Exam exam = (Exam) getItem(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        // 图标资源，默认为已读
        int iconResId = R.drawable.icon_exam_item_read;
        // 判断read字段， 已考完
        if (!exam.isUnread()) {
            if (exam.isGraded()) { //显示:已考完(判断是不是是不是个人中心进入的)
                viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_red));
                viewHolder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                viewHolder.unreadTextView.setText(exam.getScore() + mContext.getResources().getString(R.string.text_score));
            } else { //显示:待批阅
                viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                viewHolder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                viewHolder.unreadTextView.setText(R.string.category_ungraded);
            }
            // 未考试
        } else {
            if (exam.isOffline()) { //显示:已过期
                viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                viewHolder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                viewHolder.unreadTextView.setText(R.string.category_expired);
            } else { //显示:未考试, 只有未考试的情况下才需要改变图标
                viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                viewHolder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                viewHolder.unreadTextView.setText(R.string.category_unexam);
                iconResId = R.drawable.icon_exam_item_unread;
            }
        }
        viewHolder.iconImageView.setImageResource(iconResId);
        if (exam.isTop()) {
            viewHolder.topImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.topImageView.setVisibility(View.INVISIBLE);
        }
        viewHolder.subjectTextView.setText(exam.getSubject());
        viewHolder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, exam.getTime()));
    }

    public static class MyViewHolder extends BaseViewHolder {
        TextView subjectTextView;
        TextView dateTextView;
        ImageView iconImageView;
        TextView unreadTextView;
        ImageView topImageView;

        public MyViewHolder(View view) {
            super(view);
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_notice_top);
            subjectTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_notice_subject);
            dateTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_notice_date);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_notice_icon);
            unreadTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_notice_unread);
        }
    }
}