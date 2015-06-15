package com.badou.mworking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.model.category.CategoryBasic;

import java.util.List;

public class MainSearchAdapter extends BaseAdapter {

    private List<CategoryBasic>[] mCategoryLists;
    private String[] mCategoryNames;
    private Context mContext;

    public MainSearchAdapter(Context context, String[] categoryNames) {
        this.mCategoryNames = categoryNames;
        this.mContext = context;
        this.mCategoryLists = new List[categoryNames.length];
    }

    public void clear() {
        for (int ii = 0; ii < mCategoryLists.length; ii++) {
            mCategoryLists[ii] = null;
        }
        notifyDataSetChanged();
    }

    public void setList(List<CategoryBasic>[] subList) {
        if (subList == null) {
            clear();
        } else {
            for (int ii = 0; ii < mCategoryLists.length && ii < subList.length; ii++) {
                mCategoryLists[ii] = subList[ii];
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mCategoryLists == null)
            return 0;
        int count = 0;
        for (int ii = 0; ii < mCategoryLists.length; ii++) {
            if (mCategoryLists[ii] != null && mCategoryLists[ii].size() > 0) {
                count++; // 标题本身占一项
                count += mCategoryLists[ii].size();
            }
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        for (int ii = 0; ii < mCategoryLists.length; ii++) {
            if (mCategoryLists[ii] != null && mCategoryLists[ii].size() > 0) {
                if (position <= 0) { // 为标题项
                    return mCategoryNames[ii];
                } else {
                    position--;
                    if (position >= mCategoryLists[ii].size()) {
                        position -= mCategoryLists[ii].size();
                    } else {
                        return mCategoryLists[ii].get(position);
                    }

                }
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView subjectTextView;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_main_search, parent, false);
            subjectTextView = (TextView) convertView.findViewById(R.id.tv_adapter_main_search);
            convertView.setTag(subjectTextView);
        } else {
            subjectTextView = (TextView) convertView.getTag();
        }
        Object item = getItem(position);
        if (item.getClass().equals(String.class)) {
            int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_less);
            subjectTextView.setPadding(padding, padding, padding, padding);
            subjectTextView.setText((String) item);
            subjectTextView.setBackgroundColor(mContext.getResources().getColor(R.color.color_grey));
        } else {
            int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_less);
            subjectTextView.setPadding(2 * padding, padding, 2 * padding, padding);
            subjectTextView.setText(((CategoryBasic) item).subject);
            subjectTextView.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
        }
        return convertView;
    }
}
