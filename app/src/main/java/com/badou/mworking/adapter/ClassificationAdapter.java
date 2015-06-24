package com.badou.mworking.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.Classification;

/**
 * 查找中的更多的界面中左边listview的适配器
 */
public class ClassificationAdapter extends MyBaseAdapter {

    private int mCurrentPosition;
    private boolean isMain; // 是否为一级目录

    public ClassificationAdapter(Context context, boolean isMain) {
        super(context);
        this.isMain = isMain;
    }

    public void setSelectedPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
            convertView = new TextView(mContext);
            int padding = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_less);
            convertView.setPadding(padding, padding, padding, padding);
            ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_medium));
        }
        Classification classification = (Classification) getItem(position);
        TextView textView = (TextView) convertView;
        textView.setText(classification.getName());
        if (position == mCurrentPosition) {
            // 点击效果
            textView.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
            textView.setTextColor(mContext.getResources().getColor(R.color.color_text_blue));
        } else {
            // 选中效果
            if (isMain) {
                textView.setBackgroundResource(R.drawable.background_classification_list_main);
            } else {
                textView.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
            }
            textView.setTextColor(mContext.getResources().getColorStateList(R.color.color_classification_list_text));
        }
        return convertView;
    }
}
