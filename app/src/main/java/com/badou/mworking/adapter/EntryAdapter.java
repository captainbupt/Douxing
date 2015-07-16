package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Entry;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述:  在线考试列表页适配器
 */
public class EntryAdapter extends MyBaseAdapter<Category> {


    public EntryAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Entry entry = (Entry) getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_notice_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.offset_lless);
        if (position == 0) {
            convertView.setPadding(0, size, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 0);
        }
        // 图标资源，默认为已读
        int iconResId = R.drawable.icon_entry_item_read;
        if (!entry.isOffline()) {
            iconResId = R.drawable.icon_entry_item_unread;
            switch (entry.getRead()) {
                case 0:
                    holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                    holder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                    holder.unreadTextView.setText(R.string.category_not_sign_up);
                    break;
                case 1:
                    holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                    holder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                    holder.unreadTextView.setText(R.string.category_check);
                    break;
                case 2:
                    holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                    holder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    holder.unreadTextView.setText(R.string.category_sign_up);
                    break;
                case 3:
                    holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                    holder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    holder.unreadTextView.setText(R.string.category_check_fail);
                    break;
            }
        } else {
            iconResId = R.drawable.icon_entry_item_read;
            holder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
            holder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            switch (entry.getRead()) {
                case 0:
                    holder.unreadTextView.setText(R.string.category_expired);
                    break;
                case 1:
                    holder.unreadTextView.setText(R.string.category_check_fail);
                    break;
                case 2:
                    holder.unreadTextView.setText(R.string.category_sign_up);
                    break;
                case 3:
                    holder.unreadTextView.setText(R.string.category_check_fail);
                    break;
            }
        }

        holder.iconImageView.setImageResource(iconResId);
        if (entry.isTop()) {
            holder.topImageView.setVisibility(View.VISIBLE);
        } else {
            holder.topImageView.setVisibility(View.INVISIBLE);
        }
        holder.subjectTextView.setText(entry.getSubject());
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, entry.getTime()));
        return convertView;
    }

    static class ViewHolder {
        TextView subjectTextView;
        TextView dateTextView;
        ImageView iconImageView;
        TextView unreadTextView;
        ImageView topImageView;

        public ViewHolder(View view) {
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_notice_top);
            subjectTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_subject);
            dateTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_date);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_notice_icon);
            unreadTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_unread);
        }
    }
}