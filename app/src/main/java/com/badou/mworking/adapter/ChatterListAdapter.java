package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.widget.ChatterItemView;
import com.swipe.delete.SwipeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 功能描述:同事圈adapter
 */
public class ChatterListAdapter extends MyBaseAdapter<Chatter> {

    private boolean isHeadClickable;

    public ChatterListAdapter(Context context, boolean isHeadClickable) {
        super(context);
        this.isHeadClickable = isHeadClickable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_chatter_item,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Chatter chatter = mItemList.get(position);
        holder.chatterItemView.setData(chatter, isHeadClickable, false);
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.chatter_item_view)
        ChatterItemView chatterItemView;
        @Bind(R.id.swipe_layout)
        SwipeLayout swipeLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
