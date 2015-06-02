package com.badou.mworking.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/20.
 */
public abstract class MyBaseAdapter extends BaseAdapter {

    protected LayoutInflater mInflater;
    protected List<Object> mItemList;
    protected Context mContext;

    public MyBaseAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public MyBaseAdapter(Context context, List<Object> list) {
        this.mContext = context;
        this.mItemList = list;
        this.mInflater = LayoutInflater.from(mContext);
    }

    /**
     * 功能描述: 重新设置list
     */
    public void setList(List<Object> list) {
        this.mItemList = list;
        notifyDataSetChanged();
    }

    /**
     * 功能描述:添加上拉新加载的 list
     */
    public void addList(List<Object> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        if (mItemList == null) {
            setList(list);
            return;
        }else{
            mItemList.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 功能描述: 添加一个item
     */
    public void addItem(Object object) {
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
    public void changeItem(int position, Object object){
        if (object!=null) {
            //替换指定元素
            mItemList.set(position, object);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mItemList == null)
            return 0;
        return mItemList.size();
    }

    @Override
    public Object getItem(int i) {
        if (mItemList == null || i < 0 || i >= mItemList.size())
            return null;
        return mItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

}
