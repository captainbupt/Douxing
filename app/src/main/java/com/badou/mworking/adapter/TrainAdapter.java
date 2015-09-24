package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 功能描述: 微培训adapter
 */
public class TrainAdapter extends CategoryBaseAdapter {

    /**
     * 微培训/我的学习
     *
     * @param mContext
     */
    public TrainAdapter(Context mContext, View.OnClickListener onClickListener) {
        super(mContext, onClickListener);
    }

    @Override
    public BaseViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(mInflater.inflate(R.layout.adapter_training_item, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Train train = (Train) getItem(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        if (!TextUtils.isEmpty(train.getImg())) {
            viewHolder.logoImageView.setImageURI(UriUtil.getHttpUri(train.getImg()));
        } else {
            viewHolder.logoImageView.setImageURI(UriUtil.getResourceUri(R.drawable.icon_training_item_default));
        }

        // 显示标题
        viewHolder.subjectTextView.setText(train.getSubject());
        // 显示时间和部门
        viewHolder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, train.getTime()));
        // 显示评分人数
        viewHolder.ratingNumberTextView.setText(" (" + train.getRatingNumber() + ")");
        // 显示评分星星
        if (train.getRatingNumber() != 0) {
            viewHolder.ratingbar.setRating((float) train.getRatingTotalValue() / train.getRatingNumber());
        }
        // 该课件是否已读
        if (!train.isUnread()) {
            viewHolder.unreadTextView.setVisibility(View.GONE);
        } else {
            viewHolder.unreadTextView.setVisibility(View.VISIBLE);
        }
        /** 显示是否置顶 **/
        if (train.isTop()) {
            viewHolder.topImageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.topImageView.setVisibility(View.GONE);
        }
        viewHolder.commentNumberTextView.setText(train.getCommentNumber() + "");
    }

    public static class MyViewHolder extends BaseViewHolder {

        TextView subjectTextView;
        TextView dateTextView;   //显示部门和时间
        TextView ratingNumberTextView;  //显示评分人数
        ImageView topImageView;
        SimpleDraweeView logoImageView;
        RatingBar ratingbar; // 星星显示
        TextView commentNumberTextView;
        TextView unreadTextView;

        public MyViewHolder(View view) {
            super(view);
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_training_item_top);
            logoImageView = (SimpleDraweeView) view.findViewById(R.id.iv_adapter_training_item_logo);
            subjectTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_training_item_subject);
            dateTextView = (TextView) view.findViewById(R.id.tv_adapter_trainng_item_date);
            ratingNumberTextView = (TextView) view.findViewById(R.id.tv_adapter_training_item_rating_number);
            ratingbar = (RatingBar) view.findViewById(R.id.rb_adapter_training_item_rating);
            commentNumberTextView = (TextView) view.findViewById(R.id.tv_adapter_training_item_comment_number);
            unreadTextView = (TextView) view.findViewById(R.id.tv_adapter_training_item_unread);
        }
    }
}

