package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.category.Category;

public abstract class CategoryBaseAdapter extends MyBaseRecyclerAdapter<Category,CategoryBaseAdapter.BaseViewHolder> {

    View.OnClickListener mItemClickListener;

    public CategoryBaseAdapter(Context context, View.OnClickListener onClickListener) {
        super(context);
        mItemClickListener = onClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseViewHolder baseViewHolder = onCreateChildViewHolder(parent,viewType);
        baseViewHolder.parentView.setOnClickListener(mItemClickListener);
        return baseViewHolder;
    }

    public abstract BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.parentView.setTag(position);
    }


    public static class BaseViewHolder extends RecyclerView.ViewHolder{

        View parentView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
        }
    }
}
