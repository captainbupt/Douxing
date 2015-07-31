package com.badou.mworking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.Store;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.widget.ChatterItemView;
import com.swipe.delete.SwipeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class StoreAdapter extends MyBaseAdapter<Store> {

    private final int TYPE_NORMAL = 1;
    private final int TYPE_CHATTER = 2;

    View.OnClickListener mDeleteClickListener;
    View.OnClickListener mPraiseClickListener;

    public StoreAdapter(Context context, View.OnClickListener deleteClickListener, View.OnClickListener praiseClickListener) {
        super(context);
        this.mDeleteClickListener = deleteClickListener;
        this.mPraiseClickListener = praiseClickListener;
    }

    // 通过type来优化
    @Override
    public int getViewTypeCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getChatter() == null ? TYPE_NORMAL : TYPE_CHATTER;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (getItemViewType(i) == TYPE_NORMAL) {
            return getNormalView(i, view, viewGroup);
        } else {
            return getChatterView(i, view, viewGroup);
        }
    }

    private View getNormalView(int i, View view, ViewGroup viewGroup) {
        NormalViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.adapter_store_normal, viewGroup, false);
            holder = new NormalViewHolder(view);
            view.setTag(holder);
            holder.deleteTextView.setOnClickListener(mDeleteClickListener);
            ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        } else {
            holder = (NormalViewHolder) view.getTag();
        }
        Store store = getItem(i);
        holder.typeImageView.setImageResource(Store.getIconRes(store.getType()));
        if (TextUtils.isEmpty(store.getSubject())) {
            holder.subjectTextView.setText(R.string.tip_message_center_resource_gone);
        } else {
            holder.subjectTextView.setText(store.getSubject());
        }
        holder.timeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, store.getTs()));
        holder.deleteTextView.setTag(i);
        holder.swipeLayout.close(true);
        return view;
    }

    class NormalViewHolder extends BaseSwipeViewHolder {
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

    private View getChatterView(int i, View view, ViewGroup viewGroup) {
        ChatterViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.adapter_store_chatter, viewGroup, false);
            holder = new ChatterViewHolder(view);
            view.setTag(holder);
            holder.deleteTextView.setOnClickListener(mDeleteClickListener);
            ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        } else {
            holder = (ChatterViewHolder) view.getTag();
        }
        Store store = getItem(i);
        holder.chatterItemView.setData(store.getChatter(), false, i);
        holder.chatterItemView.setPraiseListener(mPraiseClickListener);
        holder.deleteTextView.setTag(i);
        holder.swipeLayout.close(true);
        return view;
    }

    class ChatterViewHolder extends BaseSwipeViewHolder {
        @Bind(R.id.chatter_item_view)
        ChatterItemView chatterItemView;

        ChatterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class BaseSwipeViewHolder {
        @Bind(R.id.delete_text_view)
        TextView deleteTextView;
        @Bind(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        BaseSwipeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
