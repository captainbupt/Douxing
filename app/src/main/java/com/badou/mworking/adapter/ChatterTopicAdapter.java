package com.badou.mworking.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.ChatterTopic;

/**
 * Created by Administrator on 2015/6/11.
 */
public class ChatterTopicAdapter extends MyBaseAdapter {

    public ChatterTopicAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            int smallSize = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_small);
            int mediumSize = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_medium);
            view = new TextView(mContext);
            view.setPadding(mediumSize, smallSize, mediumSize, smallSize);
            ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.text_size_medium));
            ((TextView) view).setTextColor(mContext.getResources().getColor(R.color.color_text_black));
        }
        ((TextView) view).setText("#" + ((ChatterTopic) getItem(i)).key + "#");
        return view;
    }
}
