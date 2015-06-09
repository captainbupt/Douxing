package com.badou.mworking.widget;

/**
 * Created by Administrator on 2015/6/9.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.net.bitmap.ImageViewLoader;

import org.holoeverywhere.widget.GridView;

import java.util.List;

/**
 * Created by Administrator on 2015/6/8.
 */
public class MultiImageEditGridView extends GridView {

    private MultiImageEditAdapter mAdapter;

    public MultiImageEditGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAdapter = new MultiImageEditAdapter(context);
        setAdapter(mAdapter);
    }

// 如果需要显示全部item，则使用此段代码
/*    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }*/

    public void addImage(Bitmap bmp) {
        mAdapter.addItem(bmp);
    }

    public List<Object> getImages() {
        return mAdapter.getItemList();
    }
}

class MultiImageEditAdapter extends MyBaseAdapter {

    public MultiImageEditAdapter(Context context) {
        super(context);
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
        holder.contentImageView.setImageBitmap(bmp);
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        ImageView contentImageView;
        ImageView deleteImageView;

        public ViewHolder(View view) {
            contentImageView = (ImageView) view.findViewById(R.id.iv_adapter_multi_image_edit_content);
            deleteImageView = (ImageView) view.findViewById(R.id.iv_adapter_multi_image_edit_delete);
        }
    }
}