package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问答页面适配器
 */
public class AskAdapter extends MyBaseRecyclerAdapter<Ask,AskAdapter.MyViewHolder> {

    OnClickListener mOnItemClickListener;
    OnLongClickListener mOnLongClickListener;

    public AskAdapter(Context context, OnClickListener onItemClickListener, OnLongClickListener onLongClickListener) {
        super(context);
        mOnItemClickListener = onItemClickListener;
        mOnLongClickListener = onLongClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_ask, parent, false);
        view.setOnClickListener(mOnItemClickListener);
        view.setOnLongClickListener(mOnLongClickListener);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Ask ask = getItem(position);
        holder.headImageView.setImageURI(UriUtil.getHttpUri(ask.getUserHeadUrl()));
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, ask.getCreateTime()));
        holder.replyTextView.setText(ask.getCount() + "");
        holder.contentTextView.setText(ask.getSubject());
        holder.parentView.setTag(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.head_image_view)
        SimpleDraweeView headImageView;
        @Bind(R.id.content_text_view)
        TextView contentTextView;
        @Bind(R.id.date_text_view)
        TextView dateTextView;
        @Bind(R.id.reply_text_view)
        TextView replyTextView;
        View parentView;

        MyViewHolder(View view) {
            super(view);
            parentView = view;
            ButterKnife.bind(this, view);
        }
    }
}
