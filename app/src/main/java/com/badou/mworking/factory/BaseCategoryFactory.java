package com.badou.mworking.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2015/5/19.
 */
public abstract class BaseCategoryFactory {
    List<Object> mItemList;

    public void setList(List<Object> list){
        this.mItemList = list;
    }

    public void addList(List<Object> list){
        if(list == null || list.size() == 0){
            return;
        }
        if(mItemList == null){
            setList(list);
            return;
        }
        for(Object o: list){
            mItemList.add(o);
        }
    }

    public void addItem(Object object){
        if(object == null)
            return;
        if(mItemList == null){
            mItemList = new ArrayList<>();
        }
        mItemList.add(object);
    }

    public Object getItemByPosition(int position){
        if(mItemList == null)
            return null;
        if(position < 0 || position >= mItemList.size())
            return null;
        return mItemList.get(position);
    }

}
