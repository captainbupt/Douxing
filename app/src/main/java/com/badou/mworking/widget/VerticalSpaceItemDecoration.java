package com.badou.mworking.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;
    private boolean isPaddingFirst = true;

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight, boolean isPaddingFirst) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        this.isPaddingFirst = isPaddingFirst;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) != 0 || isPaddingFirst) {
            outRect.top = mVerticalSpaceHeight;
        }
    }
}