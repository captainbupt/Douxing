package com.badou.mworking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.AskActivity;
import com.badou.mworking.R;
import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Ask;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.DialogUtil;
import com.badou.mworking.util.TimeTransfer;

import org.holoeverywhere.app.Activity;

/**
 * 问答页面适配器
 */
public class AskAdapter extends MyBaseAdapter {


    public AskAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final AllViewHolder holder;
        if (convertView != null) {
            holder = (AllViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_ask,
                    parent, false);
            holder = new AllViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Ask ask = (Ask) getItem(position);
        ImageViewLoader.setCircleImageViewResource(mContext, holder.headImageView,
                ask.userHeadUrl, mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, ask.createTime));
        holder.replyCountTextView.setText(ask.count + "");
        holder.contentTextView.setText(ask.content);
        convertView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                DialogUtil.showCopyDialog(mContext, ask.content);
                return true;
            }
        });

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mContext, AskDetailActivity.class);
                intent.putExtra(AskDetailActivity.KEY_ASK, ask);
                // 任意
                ((Activity)mContext).startActivityForResult(intent, 1);
            }
        });
        return convertView;
    }

    static class AllViewHolder {
        ImageView headImageView; // 头像
        TextView dateTextView;  //时间
        TextView replyCountTextView;  //回复人数
        TextView contentTextView;

        public AllViewHolder(View view) {
            headImageView = (ImageView) view.findViewById(R.id.iv_adapter_ask_head);
            dateTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_date);
            replyCountTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_reply_count);
            contentTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_content);
        }
    }
}
