package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.ChatterHot;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.widget.LevelTextView;

public class ChatterHotAdapter extends MyBaseAdapter {


    public ChatterHotAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.adapter_chatter_hot, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        ChatterHot hot = (ChatterHot) getItem(i);
        holder.nameTextView.setText(hot.name);
        ImageViewLoader.setCircleImageViewResource(holder.headImageView, hot.headUrl, mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));
        holder.dataTextView.setText("发帖 " + hot.topicNumber + "\t获赞 " + hot.praiseNumber + "\t获评 " + hot.commentNumber);
        holder.rankTextView.setText((i + 1) + "");
        holder.levelTextView.setLevel(hot.level);
        if (i <= 2) {
            switch (i) {
                case 0:
                    holder.rankImageView.setImageResource(R.drawable.icon_chatter_hot_1);
                    break;
                case 1:
                    holder.rankImageView.setImageResource(R.drawable.icon_chatter_hot_2);
                    break;
                case 2:
                    holder.rankImageView.setImageResource(R.drawable.icon_chatter_hot_3);
                    break;
            }
            holder.rankTextView.setVisibility(View.GONE);
            holder.rankImageView.setVisibility(View.VISIBLE);
        } else {
            holder.rankTextView.setVisibility(View.VISIBLE);
            holder.rankImageView.setVisibility(View.GONE);
        }
        if (i % 2 == 1) {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
        } else {
            view.setBackgroundColor(mContext.getResources().getColor(R.color.color_layout_bg));
        }
        return view;
    }

    class ViewHolder {
        ImageView headImageView;
        TextView nameTextView;
        TextView dataTextView;
        LevelTextView levelTextView;
        ImageView rankImageView;
        TextView rankTextView;

        public ViewHolder(View view) {
            headImageView = (ImageView) view.findViewById(R.id.iv_adapter_chatter_hot_head);
            nameTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_hot_name);
            dataTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_hot_data);
            rankImageView = (ImageView) view.findViewById(R.id.iv_adapter_chatter_hot_rank);
            rankTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_hot_rank);
            levelTextView = (LevelTextView) view.findViewById(R.id.tv_adapter_chatter_hot_level);
        }
    }
}
