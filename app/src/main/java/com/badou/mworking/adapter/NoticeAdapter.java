package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Notice;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述: 通知公告adapter
 */
public class NoticeAdapter extends MyBaseAdapter {

    public NoticeAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_notice_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Notice notice = (Notice) getItem(position);
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.offset_lless);
        // 使得第一条上端有一段空白
        if (position == 0) {
            convertView.setPadding(0, size, 0, 0);
        } else {
            convertView.setPadding(0, 0, 0, 0);
        }
        if (notice.isRead()) {
            holder.iconImageView.setImageResource(R.drawable.icon_notice_item_read);
            holder.unreadTextView.setVisibility(View.GONE);
        } else {
            holder.iconImageView.setImageResource(R.drawable.icon_notice_item_unread);
            holder.unreadTextView.setVisibility(View.VISIBLE);
        }
        holder.subjectTextView.setText(notice.subject);
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, notice.time));
        if (notice.top == Constant.TOP_YES) {
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

        public ViewHolder(View view) {
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_notice_top);
            subjectTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_subject);
            dateTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_date);
            iconImageView = (ImageView) view.findViewById(R.id.iv_adapter_notice_icon);
            unreadTextView = (TextView) view.findViewById(R.id.tv_adapter_notice_unread);
        }
    }


    /**
     * 9
     * 功能描述: 设置已读
     *
     * @param position
     */
    public void read(int position) {
        String userNum = ((AppApplication) mContext.getApplicationContext()).getUserInfo().account;
        Notice notice = (Notice) getItem(position);
        if (!notice.isRead()) {
            notice.read = Constant.READ_YES;
            this.notifyDataSetChanged();
            int unreadNum = SP.getIntSP(mContext, SP.DEFAULTCACHE, userNum + Notice.CATEGORY_KEY_UNREAD_NUM, 0);
            if (unreadNum > 0) {
                SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + Notice.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
            }
        }
    }
}
