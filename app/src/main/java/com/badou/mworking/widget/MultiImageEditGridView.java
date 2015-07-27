package com.badou.mworking.widget;

/**
 * Created by Administrator on 2015/6/9.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.util.BitmapUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/6/8.
 */
public class MultiImageEditGridView extends GridView {

    private MultiImageEditAdapter mAdapter;
    private int mMaxImage;

    public void setAddOnClickListener(OnClickListener addOnClickListener) {
        mAdapter.setAddOnClickListener(addOnClickListener);
    }

    public MultiImageEditGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        mAdapter = new MultiImageEditAdapter(context, mMaxImage);
        setAdapter(mAdapter);
    }

    public void initAttr(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.MultiImageEditGridView);
            mMaxImage = typedArray.getColor(
                    R.styleable.MultiImageEditGridView_maxImg, 4);
            typedArray.recycle();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    public void addImage(Bitmap bmp) {
        mAdapter.addItem(bmp);
        modifyLayout();
    }

    public void clear() {
        List<Bitmap> bitmaps = getImages();
        mAdapter.setList(null);
        if (bitmaps != null && bitmaps.size() > 0) {
            for (Bitmap o : bitmaps) {
                BitmapUtil.recycleBitmap(o);
            }
            bitmaps.clear();
        }
    }

    public boolean isMax() {
        return mAdapter.getListCount() >= mMaxImage;
    }

    public int getMaxImageCount() {
        return mMaxImage;
    }

    public List<Bitmap> getImages() {
        return mAdapter.getItemList();
    }

    private void modifyLayout() {
        ViewGroup.LayoutParams layoutParams;
        int paddingSide = getPaddingLeft();
        int size = getResources().getDimensionPixelSize(R.dimen.image_size_content);
        int paddingVertical = getResources().getDimensionPixelOffset(R.dimen.offset_lless);
        int column = Math.min(3, mAdapter.getCount());
        setNumColumns(column);
        layoutParams = getLayoutParams();
        layoutParams.width = 2 * paddingSide + size * column + paddingVertical * (column);
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        setLayoutParams(layoutParams);
    }

    static class MultiImageEditAdapter extends MyBaseAdapter<Bitmap> {

        private OnClickListener mAddOnClickListener;
        private int mMaxImage;

        public MultiImageEditAdapter(Context context, int max) {
            super(context);
            this.mMaxImage = max;
        }

        public void setAddOnClickListener(OnClickListener addOnClickListener) {
            this.mAddOnClickListener = addOnClickListener;
        }

        public void deleteItem(int position) {
            if (mItemList == null || position < 0 || position >= mItemList.size()) {
                return;
            }
            Bitmap bmp = (Bitmap) mItemList.get(position);
            mItemList.remove(position);
            notifyDataSetChanged();
            if (bmp != null && !bmp.isRecycled())
                bmp.recycle();
        }

        @Override
        public void addItem(Bitmap object) {
            Bitmap bitmap = (Bitmap) object;
            if (getListCount() < mMaxImage) {
                super.addItem(object);
            } else { // 已经满了，替换最后一张
                Bitmap old = (Bitmap) mItemList.get(mMaxImage - 1);
                mItemList.set(mMaxImage - 1, bitmap);
                notifyDataSetChanged();
                if (old != null && !old.isRecycled()) {
                    old.recycle();
                }
            }
        }

        @Override
        public int getCount() {
            return Math.min(super.getCount() + 1, mMaxImage);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.adapter_multi_image_edit, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Bitmap bmp = (Bitmap) getItem(position);
            if (bmp == null || bmp.isRecycled()) {
                holder.contentImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                holder.contentImageView.setImageResource(R.drawable.icon_multi_image_edit_add);
                holder.contentImageView.setOnClickListener(mAddOnClickListener);
                holder.deleteImageView.setVisibility(View.GONE);
            } else {
                holder.contentImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.contentImageView.setImageBitmap(bmp);
                holder.contentImageView.setOnClickListener(null);
                holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteItem(position);
                    }
                });
                holder.deleteImageView.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        ImageView contentImageView;
        ImageView deleteImageView;

        public ViewHolder(View view) {
            view.setEnabled(false);
            contentImageView = (ImageView) view.findViewById(R.id.iv_adapter_multi_image_edit_content);
            deleteImageView = (ImageView) view.findViewById(R.id.iv_adapter_multi_image_edit_delete);
        }

    }
}