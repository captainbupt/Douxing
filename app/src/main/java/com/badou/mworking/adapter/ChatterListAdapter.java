package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

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

    OnClickListener mHeadClickListener;
    OnClickListener mPraiseClickListener;

    public ChatterListAdapter(Context context) {
        super(context);
    }

    public void setHeadClickListener(OnClickListener headClickListener) {
        mHeadClickListener = headClickListener;
    }

    public void setPraiseClickListener(OnClickListener praiseClickListener) {
        mPraiseClickListener = praiseClickListener;
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
            if (mHeadClickListener != null)
                holder.chatterItemView.setHeadListener(mHeadClickListener);
            holder.chatterItemView.setPraiseListener(mPraiseClickListener);
        }
        final Chatter chatter = mItemList.get(position);
        holder.chatterItemView.setData(chatter, false, position);
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.chatter_item_view)
        ChatterItemView chatterItemView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
