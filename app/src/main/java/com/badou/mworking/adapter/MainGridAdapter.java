package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.util.DensityUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 功能描述:  主页面adapter
 */
public class MainGridAdapter extends MyBaseAdapter<MainIcon> {

    public MainGridAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_main_grid, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            int margin = mContext.getResources().getDimensionPixelOffset(R.dimen.offset_small);
            convertView.setLayoutParams(new AbsListView.LayoutParams(DensityUtil.getInstance().getScreenWidth() / 2 - margin, AbsListView.LayoutParams.WRAP_CONTENT));
        }
        MainIcon mainIcon = getItem(position);
        holder.iconImageView.setImageResource(mainIcon.getResId());
        holder.nameTextView.setText(mainIcon.getName());
        if (mainIcon.getUnreadNumber() == 0) {
            holder.unreadTextView.setVisibility(View.GONE);
        } else {
            holder.unreadTextView.setVisibility(View.VISIBLE);
            holder.unreadTextView.setText(mainIcon.getUnreadNumber() + "");
            // 如果是两位数的话，换一个背景
            if (mainIcon.getUnreadNumber() > 9) {
                holder.unreadTextView.setBackgroundResource(R.drawable.icon_chat_unread_long);
            } else {
                holder.unreadTextView.setBackgroundResource(R.drawable.icon_chat_unread);
            }
        }
        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.icon_image_view)
        ImageView iconImageView;
        @InjectView(R.id.name_text_view)
        TextView nameTextView;
        @InjectView(R.id.unread_text_view)
        TextView unreadTextView;

        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
