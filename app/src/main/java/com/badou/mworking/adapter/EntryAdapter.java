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
 * 功能描述:  报名列表页适配器
 */
public class EntryAdapter extends CategoryBaseAdapter {


    public EntryAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, onClickListener);
    }

    @Override
    public BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.adapter_notice_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        Entry entry = (Entry) getItem(position);
        // 图标资源，默认为已读
        int iconResId = R.drawable.icon_entry_item_read;
        if (!entry.isOffline()) {
            iconResId = R.drawable.icon_entry_item_unread;
            switch (entry.getRead()) {
                case 0:
                    viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                    viewHolder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                    viewHolder.unreadTextView.setText(R.string.category_not_sign_up);
                    break;
                case 1:
                    viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_white));
                    viewHolder.unreadTextView.setBackgroundResource(R.drawable.flag_category_unread);
                    viewHolder.unreadTextView.setText(R.string.category_check);
                    break;
                case 2:
                    viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                    viewHolder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    viewHolder.unreadTextView.setText(R.string.category_sign_up);
                    break;
                case 3:
                    viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
                    viewHolder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    viewHolder.unreadTextView.setText(R.string.category_check_fail);
                    break;
            }
        } else {
            iconResId = R.drawable.icon_entry_item_read;
            viewHolder.unreadTextView.setTextColor(mContext.getResources().getColor(R.color.color_text_grey));
            viewHolder.unreadTextView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            switch (entry.getRead()) {
                case 0:
                    viewHolder.unreadTextView.setText(R.string.category_expired);
                    break;
                case 1:
                    viewHolder.unreadTextView.setText(R.string.category_check_fail);
                    break;
                case 2:
                    viewHolder.unreadTextView.setText(R.string.category_sign_up);
                    break;
                case 3:
                    viewHolder.unreadTextView.setText(R.string.category_check_fail);
                    break;
            }
        }

        viewHolder.iconImageView.setImageResource(iconResId);
        if (entry.isTop()) {
            viewHolder.topImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.topImageView.setVisibility(View.INVISIBLE);
        }
        viewHolder.subjectTextView.setText(entry.getSubject());
        viewHolder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, entry.getTime()));
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
            subjectTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_subject);
            dateTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_date);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_notice_icon);
            unreadTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_unread);
        }
    }
}