package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.TimeTransfer;

import java.util.List;

/**
 * 功能描述:评论adapter
 */
public class CommentAdapter extends MyBaseAdapter {

    private int mAllCount = 0;

    public CommentAdapter(Context context) {
        super(context);
    }

    public void setList(List<Object> list, int AllCount) {
        super.setList(list);
        this.mAllCount = AllCount;
        notifyDataSetChanged();
    }

    public void addList(List<Object> list, int AllCount) {
        super.addList(list);
        this.mAllCount = AllCount;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        /**加载布局**/
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_comment,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        Chatter Question = (Chatter) mItemList.get(position);
        /*获取员工号*/
        String name = Question.name;
        if (!TextUtils.isEmpty(name)) {
            holder.mNameTextView.setText(name);
        }
        /*获取评论内容*/
        String content = Question.content;
        if (!TextUtils.isEmpty(content)) {
            holder.mContentTextView.setText(content);
        }
        /*获取评论时间*/
        String pubTime = TimeTransfer.long2StringDetailDate(mContext, Question
                .publishTime);
        holder.mDateTextView.setText(pubTime);

        /**设置头像**/
        int size = mContext.getResources().getDimensionPixelSize(
                R.dimen.icon_head_size_middle);
        Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(
                Question.imgUrl);
        if (headBmp != null && !headBmp.isRecycled()) {
            holder.mHeadImageView.setImageBitmap(headBmp);
        } else {
            MyVolley.getImageLoader().get(
                    Question.imgUrl,
                    new CircleImageListener(mContext, Question.imgUrl, holder.mHeadImageView, size,
                            size), size, size);

        }

        Bitmap contentBmp = null;
        if (Question.imgUrl != null) {
            contentBmp = BitmapLruCache.getBitmapLruCache().get(
                    Question.imgUrl);
        }
        if (contentBmp != null && contentBmp.isRecycled()) {
            holder.mContentPicImageView.setImageBitmap(contentBmp);
        } else {

        }

		/*设置楼数*/
        int floorNum = mAllCount - position;
        holder.mFloorNumTextView.setText(floorNum + mContext.getResources().getString(R.string.floor_num) + "   ·");

        return convertView;
    }

    static class ViewHolder {
        ImageView mHeadImageView;
        ImageView mContentPicImageView;
        TextView mNameTextView;
        TextView mContentTextView;
        TextView mDateTextView;
        TextView mFloorNumTextView;

        public ViewHolder(View view) {
            mContentPicImageView = (ImageView) view.findViewById(R.id.imgQuestionShare);
            mHeadImageView = (ImageView) view
                    .findViewById(R.id.iv_adapter_comment_head);
            mNameTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_name);
            mContentTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_content);
            mDateTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_date);
            mFloorNumTextView = (TextView) view.findViewById(R.id.tv_adapter_comment_floor);
        }
    }
}
