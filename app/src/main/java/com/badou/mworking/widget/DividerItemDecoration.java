package com.badou.mworking.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.badou.mworking.R;
import com.badou.mworking.util.DensityUtil;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private boolean isPaddingFirst = true;

    /**
     * Default divider will be used
     */
    public DividerItemDecoration(Context context) {
        //mDivider = ContextCompat.getDrawable(context, R.drawable.divider);
        mDivider = new ColorDrawable(ContextCompat.getColor(context,R.color.color_border));
    }

    /**
     * Custom divider will be used
     */
    public DividerItemDecoration(Context context, int resId) {
        mDivider = ContextCompat.getDrawable(context, resId);
    }

    public DividerItemDecoration(Context context, boolean isPaddingFirst) {
        this(context);
        this.isPaddingFirst = isPaddingFirst;
    }

    public DividerItemDecoration(Context context, int resId, boolean isPaddingFirst) {
        this(context, resId);
        this.isPaddingFirst = isPaddingFirst;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0 && isPaddingFirst) {
            outRect.top = DensityUtil.getInstance().getOffsetLess();
        }
        outRect.bottom = DensityUtil.dip2px(view.getContext(),1);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}