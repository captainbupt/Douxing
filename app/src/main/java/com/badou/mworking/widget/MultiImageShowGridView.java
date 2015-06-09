package com.badou.mworking.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.animoto.android.views.DraggableGridView;
import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.ImageChooser;

import org.holoeverywhere.widget.GridView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/8.
 */
public class MultiImageShowGridView extends GridView {

    private MultiImageShowAdapter mAdapter;

    public MultiImageShowGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAdapter = new MultiImageShowAdapter(context);
        setAdapter(mAdapter);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void setList(List<Object> imgUrlList) {
        mAdapter.setList(imgUrlList);
    }
}

class MultiImageShowAdapter extends MyBaseAdapter {

    public MultiImageShowAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String imgUrl = (String) getItem(i);
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge);
        if (view == null) {
            view = new ImageView(mContext);
            view.setLayoutParams(new AbsListView.LayoutParams(size, size));
        }
        ImageViewLoader.setSquareImageViewResource(mContext, (ImageView) view, imgUrl, size);
        return view;
    }
}