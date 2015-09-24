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
import com.badou.mworking.entity.category.Task;
import com.badou.mworking.util.TimeTransfer;


public class TaskAdapter extends CategoryBaseAdapter {


    public TaskAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, onClickListener);
    }

    @Override
    public BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.adapter_task_item, null));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Task task = (Task) getItem(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        // 一定要保证else if 语句的顺序，应为在这一块，优先级别  已签到>已过期>未签到   然后 因为未过期  可能已经签过到了，
        //也可能没有，  如果已经签过到了，显示已签到，如果没有，才显示已过期，所以要注意else if语句的顺序
        // 先判断read字段， 已签到
        if (!task.isUnread()) {
            viewHolder.unreadTextView.setVisibility(View.GONE);
            viewHolder.iconImageView.setImageResource(R.drawable.icon_task_item_read);
        } else {
            viewHolder.unreadTextView.setVisibility(View.VISIBLE);
            if (task.isOffline()) { //判断 offline字段， 已过期
                viewHolder.iconImageView.setImageResource(R.drawable.icon_task_item_read);
                viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                viewHolder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                viewHolder.unreadTextView.setText(R.string.category_expired);
            } else { // 未签到
                viewHolder.iconImageView.setImageResource(R.drawable.icon_task_item_unread);
                viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                viewHolder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                viewHolder.unreadTextView.setText(R.string.category_unsign);
            }
        }
        if (!TextUtils.isEmpty(task.getPlace()) && !" ".equals(task.getPlace())) {
            viewHolder.addressTextView.setText(task.getPlace() + "");
        } else {
            viewHolder.addressTextView.setText(R.string.sign_in_task_address_empty);
        }
        viewHolder.subjectTextView.setText(task.getSubject() + "");
        viewHolder.dateTextView.setText("" + TimeTransfer.long2StringDetailDate(mContext, task.getTime()));
        if (task.isTop()) {
            viewHolder.topImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.topImageView.setVisibility(View.GONE);
        }
    }

    static class MyViewHolder extends BaseViewHolder {
        TextView subjectTextView;
        TextView dateTextView;
        ImageView iconImageView;
        TextView unreadTextView;
        ImageView topImageView;
        TextView addressTextView;

        public MyViewHolder(View view) {
            super(view);
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_task_top);
            subjectTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_task_subject);
            dateTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_task_date);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_task_icon);
            unreadTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_task_unread);
            addressTextView = (TextView) view.findViewById(R.id.tv_adapter_task_address);
        }
    }

}
