package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.listener.AdapterItemClickListener;
import com.badou.mworking.entity.Store;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.widget.ChatterItemView;
import com.badou.mworking.widget.WaitProgressDialog;
import com.swipe.delete.SwipeLayout;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class StoreAdapter extends MyBaseAdapter {

    private final int TYPE_NORMAL = 1;
    private final int TYPE_CHATTER = 2;
    @InjectView(R.id.delete_text_view)
    TextView mDeleteTextView;
    @InjectView(R.id.chatter_item_view)
    ChatterItemView mChatterItemView;
    @InjectView(R.id.swipe_layout)
    SwipeLayout mSwipeLayout;


    public StoreAdapter(Context context) {
        super(context);
    }

    // 通过type来优化
    @Override
    public int getViewTypeCount() {
        return 10;
    }

    @Override
    public int getItemViewType(int position) {
        return ((Store) mItemList.get(position)).chatter == null ? TYPE_NORMAL : TYPE_CHATTER;
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
        } else {
            holder = (NormalViewHolder) view.getTag();
        }
        holder.itemClickListener.position = i;
        Store store = (Store) getItem(i);
        holder.store = store;
        holder.typeImageView.setImageResource(store.getIconRes());
        holder.subjectTextView.setText(store.subject);
        holder.timeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, store.ts));
        ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        return view;
    }

    class NormalViewHolder extends BaseSwipeViewHolder {
        @InjectView(R.id.type_image_view)
        ImageView typeImageView;
        @InjectView(R.id.subject_text_view)
        TextView subjectTextView;
        @InjectView(R.id.time_text_view)
        TextView timeTextView;

        NormalViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }

    }

    private View getChatterView(int i, View view, ViewGroup viewGroup) {
        ChatterViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.adapter_store_chatter, viewGroup, false);
            holder = new ChatterViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ChatterViewHolder) view.getTag();
        }
        holder.itemClickListener.position = i;
        Store store = (Store) getItem(i);
        holder.store = store;
        holder.chatterItemView.setData(store.chatter, true);
        ((ViewGroup) view).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        return view;
    }

    class ChatterViewHolder extends BaseSwipeViewHolder {
        @InjectView(R.id.chatter_item_view)
        ChatterItemView chatterItemView;

        ChatterViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    class BaseSwipeViewHolder {
        @InjectView(R.id.delete_text_view)
        TextView deleteTextView;
        @InjectView(R.id.swipe_layout)
        SwipeLayout swipeLayout;
        Store store;
        AdapterItemClickListener itemClickListener;

        BaseSwipeViewHolder(View view) {
            ButterKnife.inject(this, view);
            itemClickListener = new AdapterItemClickListener(mContext) {
                @Override
                public void onClick(View view) {
                    final WaitProgressDialog progressDialog = new WaitProgressDialog(mContext, R.string.progress_tips_delete_ing);
                    progressDialog.show();
                    ServiceProvider.deleteStore(mContext, store.sid, store.getTypeString(), new VolleyListener(mContext) {
                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            swipeLayout.close();
                            remove(position);
                        }

                        @Override
                        public void onCompleted() {
                            progressDialog.dismiss();
                        }
                    });
                }
            };
            deleteTextView.setOnClickListener(itemClickListener);
        }
    }
}
