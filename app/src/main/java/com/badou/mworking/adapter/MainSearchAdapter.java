package com.badou.mworking.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.badou.mworking.R;
import com.badou.mworking.model.Category;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.holoeverywhere.widget.TextView;

import java.util.List;

public class MainSearchAdapter extends BaseAdapter {

    private List<Category>[] mCategoryLists;
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

    public void addList(int type, String name, List<Category> subList) {
        if (type < 0 || type >= mCategoryLists.length) {
            return;
        }
        mCategoryNames[type] = name;
        mCategoryLists[type] = subList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
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
                    if (position > mCategoryLists[ii].size()) {
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
        if (convertView == null) {
            convertView = new TextView(mContext);
            convertView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_medium));
            ((TextView) convertView).setTextColor(mContext.getResources().getColor(R.color.color_text_black));
        }
        Object item = getItem(position);
        if (item.getClass().equals(String.class)) {
            int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_less);
            ((TextView) convertView).setPadding(padding, padding, padding, padding);
            ((TextView) convertView).setText((String) item);
            ((TextView) convertView).setBackgroundColor(mContext.getResources().getColor(R.color.color_grey));
        } else {
            int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_less);
            ((TextView) convertView).setPadding(2 * padding, padding, 2 * padding, padding);
            ((TextView) convertView).setText(((Category) item).subject);
            ((TextView) convertView).setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
        }
        return convertView;
    }
}
