package com.badou.mworking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.listener.AdapterItemClickListener;
import com.badou.mworking.listener.CopyClickListener;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.TimeTransfer;

/**
 * 问答页面适配器
 */
public class AskAdapter extends MyBaseAdapter {

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public AskAdapter(Context context, AdapterView.OnItemClickListener onItemClickListener) {
        super(context);
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AllViewHolder holder;
        if (convertView != null) {
            holder = (AllViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_ask,
                    parent, false);
            holder = new AllViewHolder(mContext, convertView);
            convertView.setTag(holder);
        }
        final Ask ask = (Ask) getItem(position);
        ImageViewLoader.setCircleImageViewResource(holder.headImageView, ask.userHeadUrl, mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, ask.createTime));
        holder.replyCountTextView.setText(ask.count + "");
        holder.contentTextView.setText(ask.subject);

        holder.copyClickListener.content = ask.subject;
        holder.viewClickListener.position = position;
        return convertView;
    }

    class AllViewHolder {
        ImageView headImageView; // 头像
        TextView dateTextView;  //时间
        TextView replyCountTextView;  //回复人数
        TextView contentTextView;
        CopyClickListener copyClickListener;
        AdapterItemClickListener viewClickListener;

        public AllViewHolder(Context context, View view) {
            headImageView = (ImageView) view.findViewById(R.id.iv_adapter_ask_head);
            dateTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_date);
            replyCountTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_reply_count);
            contentTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_content);
            copyClickListener = new CopyClickListener(context);
            viewClickListener = new AdapterItemClickListener(context) {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(null, null, position, getItemId(position));
                    Intent intent = new Intent();
                    intent.setClass(mContext, AskDetailActivity.class);
                    intent.putExtra(AskDetailActivity.KEY_ASK, (Ask) getItem(position));
                    // 任意
                    ((Activity) mContext).startActivityForResult(intent, 1);
                }
            };
            view.setOnLongClickListener(copyClickListener);
            view.setOnClickListener(viewClickListener);
        }
    }
}
