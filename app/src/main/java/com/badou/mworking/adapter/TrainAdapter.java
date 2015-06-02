package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.IconLoadListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;

import org.holoeverywhere.widget.FrameLayout;

/**
 * 功能描述: 微培训adapter
 */
public class TrainAdapter extends MyBaseAdapter {

    private boolean isUserCenter = false;

    /**
     * 微培训/我的学习
     *
     * @param mContext
     */
    public TrainAdapter(Context mContext) {
        super(mContext);
    }

    public void updateRating(String rid, int rating) {
        for (Object o : mItemList) {
            Train train = (Train) o;
            if (train.rid.equals(rid)) {
                train.ecnt++;
                train.eval += rating;
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        /** 微培训列表页显示的布局 **/
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_training_item,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Train train = (Train) getItem(position);
        if (TextUtils.isEmpty(train.imgUrl)) {
            holder.logoImageView.setImageResource(R.drawable.icon_training_item_default);
        } else {
            holder.logoImageView.setTag(train.imgUrl);
            /** 加载图片 **/
            Bitmap bm = BitmapLruCache.getBitmapLruCache().getBitmap(
                    train.imgUrl);
            if (bm != null
                    && holder.logoImageView.getTag().equals(train.imgUrl)) {
                holder.logoImageView.setImageBitmap(bm);
                bm = null;
            } else {
                /** 设置默认图在IconLoadListener 中 **/
                MyVolley.getImageLoader().get(
                        train.imgUrl,
                        new IconLoadListener(mContext, holder.logoImageView, train
                                .imgUrl, R.drawable.icon_training_item_default));
            }
        }

        // 显示标题
        holder.subjectTextView.setText(train.subject);
        // 显示时间和部门
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, train.time));
        // 显示评分人数
        holder.ratingNumberTextView.setText(" (" + train.ecnt + ")");
        // 显示评分星星
        if (train.ecnt != 0) {
            holder.ratingbar.setRating((float) train.eval / train.ecnt);
        }
        // 该课件是否已读
        if (train.isRead()) {
            holder.unreadTextView.setVisibility(View.GONE);
        } else {
            holder.unreadTextView.setVisibility(View.VISIBLE);
        }
        /** 显示是否置顶 **/
        if (train.top == Constant.TOP_YES) {
            holder.topImageView.setVisibility(View.VISIBLE);
        } else {
            holder.topImageView.setVisibility(View.GONE);
        }
        holder.commentNumberTextView.setText(train.commentNum + "");
        return convertView;
    }

    /**
     * 功能描述:设置是否已读,并更新sp
     *
     * @param position
     */
    public void read(int position) {
        String userNum = ((AppApplication) mContext.getApplicationContext())
                .getUserInfo().account;
        Train train = (Train) getItem(position);
        if (train.isRead()) {
            train.read = Constant.READ_YES;
            this.notifyDataSetChanged();
            int unreadNum = SP.getIntSP(mContext, SP.DEFAULTCACHE, userNum + Train.CATEGORY_KEY_UNREAD_NUM, 0);
            if (unreadNum > 0) {
                SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + Train.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
            }
        }
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

