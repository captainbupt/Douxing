package com.badou.mworking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.ChatterResManager;
import com.badou.mworking.listener.TopicClickableSpan;
import com.badou.mworking.entity.Chatter;
import com.badou.mworking.entity.user.UserChatterInfo;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SPUtil;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.widget.ChatterItemView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能描述:同事圈adapter
 */
public class ChatterListAdapter extends MyBaseAdapter {

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
        final Chatter chatter = (Chatter) mItemList.get(position);

        holder.chatterItemView.setData(chatter,isHeadClickable);
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.chatter_item_view)
        ChatterItemView chatterItemView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
