package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.chatter.ChatterHot;
import com.badou.mworking.util.UriUtil;
import com.badou.mworking.widget.LevelTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatterHotAdapter extends MyBaseAdapter<ChatterHot> {


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
        ChatterHot hot = getItem(i);
        holder.nameTextView.setText(hot.getName());
        holder.headImageView.setImageURI(UriUtil.getHttpUri(hot.getHeadUrl()));
        holder.dataTextView.setText("发帖 " + hot.getTopicNumber() + "\t获赞 " + hot.getPraiseNumber() + "\t获评 " + hot.getCommentNumber());
        holder.rankTextView.setText((i + 1) + "");
        holder.levelTextView.setLevel(hot.getLevel());
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
        @Bind(R.id.head_image_view)
        SimpleDraweeView headImageView;
        @Bind(R.id.name_text_view)
        TextView nameTextView;
        @Bind(R.id.data_text_view)
        TextView dataTextView;
        @Bind(R.id.level_text_view)
        LevelTextView levelTextView;
        @Bind(R.id.rank_image_view)
        ImageView rankImageView;
        @Bind(R.id.rank_text_view)
        TextView rankTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
