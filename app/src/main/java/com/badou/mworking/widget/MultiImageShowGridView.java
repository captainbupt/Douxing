package com.badou.mworking.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.badou.mworking.MultiPhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class MultiImageShowGridView extends GridView {

    private MultiImageShowAdapter mAdapter;

    public MultiImageShowGridView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mAdapter = new MultiImageShowAdapter(context);
        setAdapter(mAdapter);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = MultiPhotoActivity.getIntentFromWeb(context, mAdapter.getItemList(), i);
                context.startActivity(intent);
            }
        });
        setVerticalSpacing(getResources().getDimensionPixelSize(R.dimen.offset_less));
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void setList(List<String> imgUrlList) {
        LinearLayout.LayoutParams layoutParams;
        if (imgUrlList != null) {
            int paddingSide = getPaddingLeft();
            int size = getResources().getDimensionPixelSize(R.dimen.image_size_content);
            int paddingVertical = getResources().getDimensionPixelOffset(R.dimen.offset_less);
            int column = Math.min(2, imgUrlList.size());
            setNumColumns(column);
            layoutParams = new LinearLayout.LayoutParams(2 * paddingSide + size * column + paddingVertical * (column), LinearLayout.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        setLayoutParams(layoutParams);
        mAdapter.setList(imgUrlList);
    }


    static class MultiImageShowAdapter extends MyBaseAdapter<String> {

        GenericDraweeHierarchy hierarchy;

        public MultiImageShowAdapter(Context context) {
            super(context);
            GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
            hierarchy = builder
                    .setPlaceholderImage(ContextCompat.getDrawable(context, R.drawable.icon_image_default), ScalingUtils.ScaleType.CENTER_CROP)
                    .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                    .build();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            String imgUrl = getItem(i);
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.image_size_content);
            if (view == null) {
                view = new SimpleDraweeView(mContext);
                view.setLayoutParams(new AbsListView.LayoutParams(size, size));
                GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(mContext.getResources());
                GenericDraweeHierarchy hierarchy = builder
                        .setPlaceholderImage(ContextCompat.getDrawable(mContext, R.drawable.icon_image_default), ScalingUtils.ScaleType.CENTER_CROP)
                        .setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP)
                        .build();
                ((SimpleDraweeView) view).setHierarchy(hierarchy);
            }
            ((SimpleDraweeView) view).setImageURI(UriUtil.getHttpUri(imgUrl));
            return view;
        }
    }
}