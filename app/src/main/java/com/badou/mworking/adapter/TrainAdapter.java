package com.badou.mworking.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;

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
    public TrainAdapter(Context mContext, boolean userCenter) {
        super(mContext);
        this.isUserCenter = userCenter;
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
        final Train train = (Train) getItem(position);
        if (isUserCenter) {
            /** 学习进度显示的布局 **/
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.adapter_item_study_progress, null);
            }
            TextView tvSubject = (TextView) convertView
                    .findViewById(R.id.lv_item_study_subject);
            TextView tvDpt = (TextView) convertView
                    .findViewById(R.id.lv_item_study_dpt_time);

            tvSubject.setText(train.subject + "");
            tvDpt.setText(TimeTransfer.long2StringDetailDate(mContext, train.time));
            return convertView;
        } else {
            /** 微培训列表页显示的布局 **/
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                convertView = mInflater.inflate(R.layout.adapter_train_item,
                        parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
        }
        if (TextUtils.isEmpty(train.imgUrl)) {
            holder.logoImage.setImageResource(R.drawable.pic_train_item);
        } else {
            holder.logoImage.setTag(train.imgUrl);
            /** 加载图片 **/
            Bitmap bm = BitmapLruCache.getBitmapLruCache().getBitmap(
                    train.imgUrl);
            if (bm != null
                    && holder.logoImage.getTag().equals(train.imgUrl)) {
                holder.logoImage.setImageBitmap(bm);
                bm = null;
            } else {
                /** 设置默认图在IconLoadListener 中 **/
                MyVolley.getImageLoader().get(
                        train.imgUrl,
                        new IconLoadListener(mContext, holder.logoImage, train
                                .imgUrl, R.drawable.pic_train_item));
            }
        }

        // 显示标题
        holder.subject.setText(train.subject);
        // 显示时间和部门
        holder.bumenAndDateTv.setText(TimeTransfer.long2StringDetailDate(mContext, train.time));
        // 显示评分人数
        holder.pingfenrenshuTv.setText(" (" + train.ecnt + ")");
        // 显示评分星星
        if (train.ecnt != 0) {
            holder.pingfenRatingbar.setRating((float) train.eval / train.ecnt);
        }
        // 该课件是否已读
        if (train.isRead == Constant.READ_YES) {
            holder.rl_bg.setBackgroundResource(R.drawable.icon_read_);
        } else {
            holder.rl_bg.setBackgroundResource(R.drawable.icon_unread_orange);
        }
        /** 显示是否置顶 **/
        if (train.top == Constant.TOP_YES) {
            holder.top.setVisibility(View.VISIBLE);
        } else {
            holder.top.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 功能描述:设置是否已读,并更新sp
     *
     * @param position
     */
    public void read(int position) {
        String userNum = ((AppApplication) mContext.getApplicationContext())
                .getUserInfo().getUserNumber();
        Train train = (Train) getItem(position);
        if (train.isRead == Constant.READ_NO) {
            train.isRead = Constant.READ_YES;
            this.notifyDataSetChanged();
            int unreadNum = SP.getIntSP(mContext, SP.DEFAULTCACHE, userNum + Train.CATEGORY_KEY_UNREAD_NUM, 0);
            if (unreadNum > 0) {
                SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + Train.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
            }
        }
    }

    static class ViewHolder {

        TextView subject;
        TextView bumenAndDateTv;   //显示部门和时间
        TextView pingfenrenshuTv;  //显示评分人数
        ImageView top;
        ImageView logoImage;
        RatingBar pingfenRatingbar; // 星星显示
        RelativeLayout rl_bg;

        public ViewHolder(View view) {
            rl_bg = (RelativeLayout) view.findViewById(R.id.rl_item_bg_isread);
            top = (ImageView) view.findViewById(R.id.iv_adapter_base_item_top);
            logoImage = (ImageView) view.findViewById(R.id.img_train_pic);
            subject = (TextView) view
                    .findViewById(R.id.tv_adapter_base_item_subject);
            bumenAndDateTv = (TextView) view.findViewById(R.id.bumen_and_date_tv);
            pingfenrenshuTv = (TextView) view.findViewById(R.id.pingfenrenshu_tv);
            pingfenRatingbar = (RatingBar) view.findViewById(R.id.pingfen_ratingbar);
        }
    }
}
