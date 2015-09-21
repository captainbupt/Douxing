package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问答页面适配器
 */
public class AskAdapter extends MyBaseAdapter<Ask> {

    OnClickListener mOnItemClickListener;
    OnLongClickListener mOnLongClickListener;

    public AskAdapter(Context context, OnClickListener onItemClickListener, OnLongClickListener onLongClickListener) {
        super(context);
        mOnItemClickListener = onItemClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        } else {
            convertView = mInflater.inflate(R.layout.adapter_ask, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.tag_holder, holder);
            convertView.setOnClickListener(mOnItemClickListener);
            convertView.setOnLongClickListener(mOnLongClickListener);
        }
        final Ask ask = getItem(position);
        holder.headImageView.setImageURI(UriUtil.getHttpUri(ask.getUserHeadUrl()));
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, ask.getCreateTime()));
        holder.replyTextView.setText(ask.getCount() + "");
        holder.contentTextView.setText(ask.getSubject());
        convertView.setTag(R.id.tag_position, position);
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.head_image_view)
        SimpleDraweeView headImageView;
        @Bind(R.id.content_text_view)
        TextView contentTextView;
        @Bind(R.id.date_text_view)
        TextView dateTextView;
        @Bind(R.id.reply_text_view)
        TextView replyTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
