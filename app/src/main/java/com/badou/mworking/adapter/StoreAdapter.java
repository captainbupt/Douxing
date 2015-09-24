package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.Store;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.widget.ChatterItemView;
import com.swipe.delete.SwipeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class StoreAdapter extends MyBaseRecyclerAdapter<Store, StoreAdapter.BaseSwipeViewHolder> {

    private final int TYPE_NORMAL = 1;
    private final int TYPE_CHATTER = 2;

    View.OnClickListener mItemClickListener;
    View.OnClickListener mDeleteClickListener;
    View.OnClickListener mPraiseClickListener;

    public StoreAdapter(Context context, View.OnClickListener itemClickListener, View.OnClickListener deleteClickListener, View.OnClickListener praiseClickListener) {
        super(context);
        this.mItemClickListener = itemClickListener;
        this.mDeleteClickListener = deleteClickListener;
        this.mPraiseClickListener = praiseClickListener;
    }

    @Override
    public BaseSwipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseSwipeViewHolder holder;
        if(viewType == TYPE_NORMAL){
            NormalViewHolder viewHolder = new NormalViewHolder(mInflater.inflate(R.layout.adapter_store_normal, parent, false));
            viewHolder.deleteTextView.setOnClickListener(mDeleteClickListener);
            holder = viewHolder;
        }else{
            ChatterViewHolder viewHolder = new ChatterViewHolder(mInflater.inflate(R.layout.adapter_store_chatter, parent, false));
            viewHolder.deleteTextView.setOnClickListener(mDeleteClickListener);
            holder = viewHolder;
        }
        ((ViewGroup) holder.parentView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        holder.parentView.setOnClickListener(mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseSwipeViewHolder holder, int position) {
        Store store = getItem(position);
        if(holder instanceof NormalViewHolder){
            NormalViewHolder viewHolder = (NormalViewHolder) holder;
            viewHolder.typeImageView.setImageResource(Store.getIconRes(store.getType()));
            if (TextUtils.isEmpty(store.getSubject())) {
                viewHolder.subjectTextView.setText(R.string.tip_message_center_resource_gone);
            } else {
                viewHolder.subjectTextView.setText(store.getSubject());
            }
            viewHolder.timeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, store.getTs()));
            viewHolder.deleteTextView.setTag(position);
            viewHolder.swipeLayout.close(true);
        }else{
            ChatterViewHolder viewHolder = (ChatterViewHolder) holder;
            viewHolder.chatterItemView.setData(store.getChatter(), false, position);
            viewHolder.chatterItemView.setPraiseListener(mPraiseClickListener);
            viewHolder.deleteTextView.setTag(position);
            viewHolder.swipeLayout.close(true);
        }
        holder.parentView.setTag(position);
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

    public static class BaseSwipeViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.delete_text_view)
        TextView deleteTextView;
        @Bind(R.id.swipe_layout)
        SwipeLayout swipeLayout;
        View parentView;

        BaseSwipeViewHolder(View view) {
            super(view);
            parentView = view;
            ButterKnife.bind(this, view);
        }
    }
}
