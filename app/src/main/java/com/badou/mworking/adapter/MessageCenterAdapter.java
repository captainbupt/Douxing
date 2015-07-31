package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.MessageCenter;
import com.swipe.delete.SwipeLayout;

import java.text.SimpleDateFormat;

public class MessageCenterAdapter extends MyBaseAdapter<MessageCenter> {
    SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
    OnClickListener mDeleteClickListener;

    public MessageCenterAdapter(Context context, OnClickListener deleteClickListener) {
        super(context);
        mDeleteClickListener = deleteClickListener;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.adapter_message_center, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
            holder.deleteImageView.setOnClickListener(mDeleteClickListener);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MessageCenter messageCenter = (MessageCenter) getItem(i);
        holder.descriptionTextView.setText(messageCenter.getDescription());
        holder.timeTextView.setText(df.format(messageCenter.getTs()));
        holder.deleteImageView.setTag(i);
        holder.swipeLayout.close(true);
        return view;
    }

    static class ViewHolder {
        SwipeLayout swipeLayout;
        ImageView deleteImageView;
        TextView descriptionTextView;
        TextView timeTextView;

        public ViewHolder(View view) {
            swipeLayout = (SwipeLayout) view.findViewById(R.id.sl_adapter_message_center);
            deleteImageView = (ImageView) view.findViewById(R.id.iv_adapter_message_center_delete);
            descriptionTextView = (TextView) view.findViewById(R.id.tv_adapter_message_center_description);
            timeTextView = (TextView) view.findViewById(R.id.tv_adapter_message_center_time);
        }
    }
}
