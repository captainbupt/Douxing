package com.badou.mworking.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.Store;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.widget.ChatterItemView;
import com.daimajia.swipe.SwipeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class StoreAdapter extends MyBaseAdapter<Store> {

    private final int TYPE_NORMAL = 0;
    private final int TYPE_CHATTER = 1;

    View.OnClickListener mDeleteClickListener;
    View.OnClickListener mPraiseClickListener;


    public StoreAdapter(Context context, View.OnClickListener deleteClickListener, View.OnClickListener praiseClickListener) {
        super(context);
        this.mDeleteClickListener = deleteClickListener;
        this.mPraiseClickListener = praiseClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseSwipeViewHolder holder;
        if(convertView == null) {
            if (getItemViewType(position) == TYPE_NORMAL) {
                convertView = mInflater.inflate(R.layout.adapter_store_normal, parent, false);
                NormalViewHolder viewHolder = new NormalViewHolder(convertView);
                viewHolder.deleteTextView.setOnClickListener(mDeleteClickListener);
                holder = viewHolder;
            } else {
                convertView = mInflater.inflate(R.layout.adapter_store_chatter, parent, false);
                ChatterViewHolder viewHolder = new ChatterViewHolder(convertView);
                viewHolder.deleteTextView.setOnClickListener(mDeleteClickListener);
                viewHolder.chatterItemView.setPraiseListener(mPraiseClickListener);
                holder = viewHolder;
            }
            convertView.setTag(holder);
        }else{
            holder = (BaseSwipeViewHolder) convertView.getTag();
        }
        Store store = getItem(position);
        if (holder instanceof NormalViewHolder) {
            NormalViewHolder viewHolder = (NormalViewHolder) holder;
            viewHolder.typeImageView.setImageResource(Store.getIconRes(store.getType()));
            if (TextUtils.isEmpty(store.getSubject())) {
                viewHolder.subjectTextView.setText(R.string.tip_message_center_resource_gone);
            } else {
                viewHolder.subjectTextView.setText(store.getSubject());
            }
            viewHolder.timeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, store.getTs()));
            viewHolder.deleteTextView.setTag(position);
            viewHolder.contentLayout.setTag(position);
            viewHolder.swipeLayout.close(true);
        } else {
            ChatterViewHolder viewHolder = (ChatterViewHolder) holder;
            viewHolder.chatterItemView.setData(store.getChatter(), false, position);
            viewHolder.deleteTextView.setTag(position);
            viewHolder.chatterItemView.setTag(position);
            viewHolder.swipeLayout.close(true);
        }
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getChatter() == null ? TYPE_NORMAL : TYPE_CHATTER;
    }

    public static class NormalViewHolder extends BaseSwipeViewHolder {
        @Bind(R.id.type_image_view)
        ImageView typeImageView;
        @Bind(R.id.subject_text_view)
        TextView subjectTextView;
        @Bind(R.id.time_text_view)
        TextView timeTextView;
        @Bind(R.id.content_layout)
        LinearLayout contentLayout;

        NormalViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }

    public static class ChatterViewHolder extends BaseSwipeViewHolder {
        @Bind(R.id.chatter_item_view)
        ChatterItemView chatterItemView;

        ChatterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class BaseSwipeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.delete_text_view)
        TextView deleteTextView;
        @Bind(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        BaseSwipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
