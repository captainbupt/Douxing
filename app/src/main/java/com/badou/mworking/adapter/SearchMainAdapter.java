package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Classification;

import java.util.ArrayList;

/**
 * @author gejianfeng
 *         查找中的更多的界面中左边listview的适配器
 */
public class SearchMainAdapter extends MyBaseAdapter {

    private int mPosition = 0;
    private int mLayoutResId = R.layout.search_more_mainlist_item;

    public SearchMainAdapter(Context context) {
        super(context);
    }

    public void setLayoutResId(int layoutResId) {
        this.mLayoutResId = layoutResId;
    }

    public View getView(int arg0, View arg1, ViewGroup arg2) {
        Holder hold;
        if (arg1 == null) {
            hold = new Holder();
            arg1 = View.inflate(mContext, mLayoutResId, null);
            hold.txt = (TextView) arg1
                    .findViewById(R.id.Search_more_mainitem_txt);
            hold.layout = (LinearLayout) arg1
                    .findViewById(R.id.Search_more_mainitem_layout);
            arg1.setTag(hold);
        } else {
            hold = (Holder) arg1.getTag();
        }
        Classification classification = (Classification) getItem(arg0);
        hold.txt.setText(classification.getName());
        hold.layout
                .setBackgroundResource(R.drawable.search_more_mainlistselect);
        hold.txt.setTextColor(Color.parseColor("#000000"));
        if (arg0 == mPosition) {
            hold.layout.setBackgroundResource(R.drawable.list_bkg_line_u);
            if (classification.getClassifications() == null || classification.getClassifications().size() == 0) {
                hold.txt.setTextColor(Color.parseColor("#FFFF8C00"));
            }
        }

        return arg1;
    }

    public void setSelectItem(int i) {
        mPosition = i;
        notifyDataSetChanged();
    }

    public int getSelectItem() {
        return mPosition;
    }

    private static class Holder {
        LinearLayout layout;
        TextView txt;
    }

}
