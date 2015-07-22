package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.listener.AdapterItemClickListener;
import com.swipe.delete.SwipeLayout;

import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2015/6/15.
 */
public class MessageCenterAdapter extends MyBaseAdapter {
    SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");

    public MessageCenterAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.adapter_message_center, viewGroup, false);
            holder = new ViewHolder(mContext, view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        MessageCenter messageCenter = (MessageCenter) getItem(i);
        holder.descriptionTextView.setText(messageCenter.description);
        holder.timeTextView.setText(df.format(messageCenter.ts));
        holder.deleteListener.setPosition(i);
        return view;
    }

    class ViewHolder {
        SwipeLayout swipeLayout;
        ImageView deleteImageView;
        TextView descriptionTextView;
        TextView timeTextView;
        AdapterItemClickListener deleteListener;

        public ViewHolder(Context context, View view) {
            swipeLayout = (SwipeLayout) view.findViewById(R.id.sl_adapter_message_center);
            deleteImageView = (ImageView) view.findViewById(R.id.iv_adapter_message_center_delete);
            descriptionTextView = (TextView) view.findViewById(R.id.tv_adapter_message_center_description);
            timeTextView = (TextView) view.findViewById(R.id.tv_adapter_message_center_time);
            initListener(context, view);
        }

        private void initListener(Context context, View view) {
            // 删除事件
            deleteListener = new AdapterItemClickListener(context) {
                @Override
                public void onClick(View view) {
                    deleteItem(getPosition());
                    swipeLayout.close();
                }
            };
            deleteImageView.setOnClickListener(deleteListener);
        }
    }

    public void deleteItem(int position) {
        MessageCenter messageCenter = (MessageCenter) getItem(position);
        MessageCenterResManager.deleteItem(mContext, messageCenter);
        remove(position);
        if (getCount() == 0 && onEmptyListener != null) {
            onEmptyListener.onEmpty();
        }
    }

    OnEmptyListener onEmptyListener;

    public void setOnEmptyListener(OnEmptyListener onEmptyListener) {
        this.onEmptyListener = onEmptyListener;
    }

    public interface OnEmptyListener {
        void onEmpty();
    }

}
