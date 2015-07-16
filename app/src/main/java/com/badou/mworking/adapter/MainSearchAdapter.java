package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategorySearch;
import com.badou.mworking.util.DensityUtil;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class MainSearchAdapter extends MyBaseAdapter<CategorySearch> implements StickyListHeadersAdapter {

    public MainSearchAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            TextView subjectTextView = new TextView(mContext);
            int padding = DensityUtil.getInstance().getOffsetLess();
            subjectTextView.setPadding(2 * padding, padding, 2 * padding, padding);
            subjectTextView.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
            convertView = subjectTextView;
        }
        ((TextView) convertView).setText(getItem(position).subject);
        return convertView;
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            TextView subjectTextView = new TextView(mContext);
            int padding = DensityUtil.getInstance().getOffsetLess();
            subjectTextView.setPadding(padding, padding, padding, padding);
            subjectTextView.setBackgroundColor(mContext.getResources().getColor(R.color.color_grey));
            view = subjectTextView;
        }
        ((TextView) view).setText(Category.getCategoryName(mContext, getItem(i).type));
        return view;
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).type;
    }
}
