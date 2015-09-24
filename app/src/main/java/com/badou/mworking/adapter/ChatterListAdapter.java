package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.widget.ChatterItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 功能描述:同事圈adapter
 */
public class ChatterListAdapter extends MyBaseRecyclerAdapter<Chatter, ChatterListAdapter.MyViewHolder> {

    OnClickListener mItemClickListener;
    OnClickListener mHeadClickListener;
    OnClickListener mPraiseClickListener;

    public ChatterListAdapter(Context context, OnClickListener itemClickListener) {
        super(context);
        mItemClickListener = itemClickListener;
    }

    public void setHeadClickListener(OnClickListener headClickListener) {
        mHeadClickListener = headClickListener;
    }

    public void setPraiseClickListener(OnClickListener praiseClickListener) {
        mPraiseClickListener = praiseClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(mInflater.inflate(R.layout.adapter_chatter_item, parent, false));
        if (mHeadClickListener != null)
            viewHolder.chatterItemView.setHeadListener(mHeadClickListener);
        viewHolder.chatterItemView.setPraiseListener(mPraiseClickListener);
        viewHolder.parentView.setOnClickListener(mItemClickListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Chatter chatter = mItemList.get(position);
        holder.chatterItemView.setData(chatter, false, position);
        holder.parentView.setTag(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.chatter_item_view)
        ChatterItemView chatterItemView;
        View parentView;

        MyViewHolder(View view) {
            super(view);
            parentView = view;
            ButterKnife.bind(this, view);
        }
    }
}
