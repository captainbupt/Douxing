package com.badou.mworking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.listener.AdapterItemClickListener;
import com.badou.mworking.model.Ask;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.model.MessageCenter;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.CategoryClickHandler;
import com.badou.mworking.util.TimeTransfer;
import com.nineoldandroids.view.ViewHelper;
import com.swipe.delete.SwipeLayout;

import org.json.JSONObject;

/**
 * Created by Administrator on 2015/6/15.
 */
public class MessageCenterAdapter extends MyBaseAdapter {
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
        holder.timeTextView.setText(TimeTransfer.long2StringDate(mContext, messageCenter.ts));
        holder.deleteListener.position = i;
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
                    deleteItem(position);
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
    }
}
