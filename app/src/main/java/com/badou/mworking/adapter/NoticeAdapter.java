package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述: 通知公告adapter
 */
public class NoticeAdapter extends CategoryBaseAdapter {

    public NoticeAdapter(Context context, View.OnClickListener onClickListener) {
        super(context, onClickListener);
    }

    @Override
    public BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View convertView = mInflater.inflate(R.layout.adapter_notice_item, parent, false);
        return new MyViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Notice notice = (Notice) getItem(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        if (!notice.isUnread()) {
            viewHolder.iconImageView.setImageResource(R.drawable.icon_notice_item_read);
            viewHolder.unreadTextView.setVisibility(View.GONE);
        } else {
            viewHolder.iconImageView.setImageResource(R.drawable.icon_notice_item_unread);
            viewHolder.unreadTextView.setVisibility(View.VISIBLE);
        }
        viewHolder.subjectTextView.setText(notice.getSubject());
        viewHolder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, notice.getTime()));
        if (notice.isTop()) {
            viewHolder.topImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.topImageView.setVisibility(View.GONE);
        }
    }

    public static class MyViewHolder extends BaseViewHolder{
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
