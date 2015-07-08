package com.badou.mworking.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class MyBaseAdapter<T> extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected List<T> mItemList;
    protected Context mContext;

    public MyBaseAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public MyBaseAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mItemList = new ArrayList<>();
        if (list != null)
            mItemList.addAll(list);
        this.mInflater = LayoutInflater.from(mContext);
    }

    public void remove(int position) {
        if (position < 0 || position >= getCount()) {
            return;
        }
        mItemList.remove(position);
        notifyDataSetChanged();
    }

    public void setItem(int position, T item) {
        if (position < 0 || position >= getCount()) {
            return;
        }
        mItemList.set(position, item);
        notifyDataSetChanged();
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
        } else {
            mItemList.addAll(list);
        }
        notifyDataSetChanged();
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
        notifyDataSetChanged();
    }

    /**
     * 功能描述:替换一个item
     */
    public void changeItem(int position, T object) {
        if (object != null) {
            //替换指定元素
            mItemList.set(position, object);
        }
        notifyDataSetChanged();
    }

    public int getListCount() {
        if (mItemList == null)
            return 0;
        return mItemList.size();
    }

    @Override
    public int getCount() {
        return getListCount();
    }

    @Override
    public T getItem(int i) {
        if (mItemList == null || i < 0 || i >= mItemList.size())
            return null;
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
