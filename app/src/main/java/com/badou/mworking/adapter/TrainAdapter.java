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
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.bitmap.NormalImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.TimeTransfer;

/**
 * 功能描述: 微培训adapter
 */
public class TrainAdapter extends MyBaseAdapter<Category> {

    private int mIconWidth;
    private int mIconHeight;

    /**
     * 微培训/我的学习
     *
     * @param mContext
     */
    public TrainAdapter(Context mContext) {
        super(mContext);
        mIconWidth = mContext.getResources().getDimensionPixelSize(R.dimen.list_item_train_pic_width);
        mIconHeight = mContext.getResources().getDimensionPixelSize(R.dimen.list_item_train_pic_height);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        /** 微培训列表页显示的布局 **/
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_training_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Train train = (Train) getItem(position);
        if (TextUtils.isEmpty(train.getImg())) {
            holder.logoImageView.setImageResource(R.drawable.icon_training_item_default);
        } else {
            ImageViewLoader.setImageViewResource(holder.logoImageView, R.drawable.icon_training_item_default, train.getImg(), mIconWidth, mIconHeight);
        }

        // 显示标题
        holder.subjectTextView.setText(train.getSubject());
        // 显示时间和部门
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, train.getTime()));
        // 显示评分人数
        holder.ratingNumberTextView.setText(" (" + train.getRatingNumber() + ")");
        // 显示评分星星
        if (train.getRatingNumber() != 0) {
            holder.ratingbar.setRating((float) train.getRatingTotalValue() / train.getRatingNumber());
        }
        // 该课件是否已读
        if (!train.isUnread()) {
            holder.unreadTextView.setVisibility(View.GONE);
        } else {
            holder.unreadTextView.setVisibility(View.VISIBLE);
        }
        /** 显示是否置顶 **/
        if (train.isTop()) {
            holder.topImageView.setVisibility(View.VISIBLE);
        } else {
            holder.topImageView.setVisibility(View.GONE);
        }
        holder.commentNumberTextView.setText(train.getCommentNumber() + "");
        return convertView;
    }

    static class ViewHolder {

        TextView subjectTextView;
        TextView dateTextView;   //显示部门和时间
        TextView ratingNumberTextView;  //显示评分人数
        ImageView topImageView;
        ImageView logoImageView;
        RatingBar ratingbar; // 星星显示
        TextView commentNumberTextView;
        TextView unreadTextView;

        public ViewHolder(View view) {
            topImageView = (ImageView) view.findViewById(R.id.iv_adapter_training_item_top);
            logoImageView = (ImageView) view.findViewById(R.id.iv_adapter_training_item_logo);
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

