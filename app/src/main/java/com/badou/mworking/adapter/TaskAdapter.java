package com.badou.mworking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.category.Task;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;


public class TaskAdapter extends MyBaseAdapter {


    public TaskAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_task_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Task task = (Task) getItem(position);

        int size = mContext.getResources().getDimensionPixelSize(R.dimen.offset_lless);
        if (position == 0) {
            convertView.setPadding(0, size, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 0);
        }
        // 一定要保证else if 语句的顺序，应为在这一块，优先级别  已签到>已过期>未签到   然后 因为未过期  可能已经签过到了，
        //也可能没有，  如果已经签过到了，显示已签到，如果没有，才显示已过期，所以要注意else if语句的顺序
        // 先判断read字段， 已签到
        if (task.isRead()) {
            holder.unreadTextView.setVisibility(View.GONE);
            holder.iconImageView.setImageResource(R.drawable.icon_task_item_read);
        } else {
            holder.unreadTextView.setVisibility(View.VISIBLE);
            if (task.isOffline()) { //判断 offline字段， 已过期
                holder.iconImageView.setImageResource(R.drawable.icon_task_item_read);
                holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                holder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                holder.unreadTextView.setText(R.string.category_expired);
            } else { // 未签到
                holder.iconImageView.setImageResource(R.drawable.icon_task_item_unread);
                holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                holder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                holder.unreadTextView.setText(R.string.category_unsign);
            }
        }
        if (!TextUtils.isEmpty(task.place) && !" ".equals(task.place)) {
            holder.addressTextView.setText(task.place + "");
        } else {
            holder.addressTextView.setText(R.string.sign_in_task_address_empty);
        }
        holder.subjectTextView.setText(task.subject + "");
        holder.dateTextView.setText("" + TimeTransfer.long2StringDetailDate(mContext, task.time));
        if (task.isTop()) {
            holder.topImageView.setVisibility(View.VISIBLE);
        } else {
            holder.topImageView.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView subjectTextView;
        TextView dateTextView;
        ImageView iconImageView;
        TextView unreadTextView;
        ImageView topImageView;
        TextView addressTextView;

        public ViewHolder(View view) {
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_task_top);
            subjectTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_task_subject);
            dateTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_task_date);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_task_icon);
            unreadTextView = (android.widget.TextView) view.findViewById(R.id.tv_adapter_task_unread);
            addressTextView = (TextView) view.findViewById(R.id.tv_adapter_task_address);
        }
    }

    /**
     * 功能描述:设置已读
     *
     * @param position
     */
    public void setRead(int position) {
        Task task = (Task) getItem(position);
        if (!task.isRead()) {
            task.read = Constant.FINISH_YES;
            setItem(position, task);
            int unreadNum = SP.getIntSP(mContext, SP.DEFAULTCACHE, Task.CATEGORY_KEY_UNREAD_NUM, 0);
            if (unreadNum > 0) {
                SP.putIntSP(mContext, SP.DEFAULTCACHE, Task.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
            }
        }
    }

}
