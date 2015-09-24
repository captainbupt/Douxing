package com.badou.mworking.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class MyBaseRecyclerAdapter<T,U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<U> {

    protected LayoutInflater mInflater;
    protected List<T> mItemList;
    protected Context mContext;

    public MyBaseRecyclerAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public MyBaseRecyclerAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mItemList = new ArrayList<>();
        if (list != null)
            mItemList.addAll(list);
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void remove(int position) {
        if (position < 0 || position >= getListCount()) {
            return;
        }
        mItemList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        if (mItemList != null)
            mItemList.clear();
        notifyDataSetChanged();
    }

    public void setItem(int position, T item) {
        if (position < 0 || position >= getListCount()) {
            return;
        }
        mItemList.set(position, item);
        notifyItemChanged(position);
    }

    public List<T> getItemList() {
        return mItemList;
    }

    /**
     * 功能描述: 重新设置list
     */
    public void setList(List<T> list) {
        if (mItemList == null)
            mItemList = new ArrayList<>();
        this.mItemList.clear();
        if (list != null)
            this.mItemList.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 功能描述:添加上拉新加载的 list
     */
    public void addList(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (mItemList == null) {
            setList(list);
            return;
        }
        int oldSize = mItemList.size();
        mItemList.addAll(list);
        notifyItemRangeInserted(oldSize, list.size());
    }

    /**
     * 功能描述: 添加一个item
     */
    public void addItem(T object) {
        if (object == null)
            return;
        if (mItemList == null) {
            mItemList = new ArrayList<>();
        }
        mItemList.add(object);
        notifyItemInserted(mItemList.size());
    }

    /**
     * 功能描述:替换一个item
     */
    public void changeItem(int position, T object) {
        if (object != null) {
            //替换指定元素
            mItemList.set(position, object);
        }
        notifyItemChanged(position);
    }

    public int getListCount() {
        if (mItemList == null)
            return 0;
        return mItemList.size();
    }

    @Override
    public int getItemCount() {
        return getListCount();
    }

    /**
     * 返回这个list对应位置
     *
     * @param i
     * @return
     */
    public T getItem(int i) {
        if (mItemList == null || i < 0 || i >= mItemList.size())
            return null;
        return mItemList.get(i);
    }
}
