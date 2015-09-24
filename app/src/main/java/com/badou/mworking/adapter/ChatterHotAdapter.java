package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.chatter.ChatterHot;
import com.badou.mworking.util.UriUtil;
import com.badou.mworking.widget.LevelTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatterHotAdapter extends MyBaseRecyclerAdapter<ChatterHot,ChatterHotAdapter.MyViewHolder> {

    View.OnClickListener mItemClickListener;

    public ChatterHotAdapter(Context context, View.OnClickListener onClickListener) {
        super(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(mInflater.inflate(R.layout.adapter_chatter_hot, parent, false));
        holder.parentView.setOnClickListener(mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatterHot hot = getItem(position);
        holder.nameTextView.setText(hot.getName());
        holder.headImageView.setImageURI(UriUtil.getHttpUri(hot.getHeadUrl()));
        holder.dataTextView.setText("发帖 " + hot.getTopicNumber() + "\t获赞 " + hot.getPraiseNumber() + "\t获评 " + hot.getCommentNumber());
        holder.rankTextView.setText((position + 1) + "");
        holder.levelTextView.setLevel(hot.getLevel());
        if (position <= 2) {
            switch (position) {
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
        if (position % 2 == 1) {
            holder.parentView.setBackgroundColor(mContext.getResources().getColor(R.color.color_white));
        } else {
            holder.parentView.setBackgroundColor(mContext.getResources().getColor(R.color.color_layout_bg));
        }
        holder.parentView.setTag(position);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
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
        View parentView;

        MyViewHolder(View view) {
            super(view);
            parentView = view;
            ButterKnife.bind(this, view);
        }
    }
}
