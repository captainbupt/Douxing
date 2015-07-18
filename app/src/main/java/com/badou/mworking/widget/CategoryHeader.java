package com.badou.mworking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.badou.mworking.R;

public class CategoryHeader extends RelativeLayout {
    public CategoryHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_category_header, this, true);
    }
}
