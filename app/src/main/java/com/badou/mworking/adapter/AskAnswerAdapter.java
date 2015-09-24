package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.database.AskResManager;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.UriUtil;
import com.easemob.chatuidemo.adapter.MessageAdapter;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问答详情页面
 */
public class AskAnswerAdapter extends MyBaseRecyclerAdapter<Ask,AskAnswerAdapter.MyViewHolder> {

    private String mAid;
    private int mReplyCount;
    View.OnLongClickListener mCopyListener;
    OnClickListener mPraiseListener;
    OnClickListener mFullImageListener;
    OnClickListener mReplyListener;

    public AskAnswerAdapter(Context context, String aid, int replyCount, View.OnLongClickListener copyListener, OnClickListener praiseListener, OnClickListener fullImageListener, OnClickListener replyListener) {
        super(context);
        mAid = aid;
        mReplyCount = replyCount;
        mCopyListener = copyListener;
        mPraiseListener = praiseListener;
        mFullImageListener = fullImageListener;
        mReplyListener = replyListener;
    }

    public void setReplyCount(int count) {
        this.mReplyCount = count;
    }

    public int getRelyCount() {
        return mReplyCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adapter_ask_answer, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        holder.praiseCountTextView.setOnClickListener(mPraiseListener);
        holder.praiseImageView.setOnClickListener(mPraiseListener);
        holder.contentImageView.setOnClickListener(mFullImageListener);
        view.setOnClickListener(mReplyListener);
        view.setOnLongClickListener(mCopyListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Ask ask = getItem(position);
        holder.nameTextView.setText(ask.getUserName());
        holder.contentTextView.setText(ask.getContent());
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, ask.getCreateTime()));

        holder.headImageView.setImageURI(UriUtil.getHttpUri(ask.getUserHeadUrl()));
        holder.contentImageView.setImageURI(UriUtil.getHttpUri(ask.getContentImageUrl()));

        /** 设置点赞的check **/
        if (AskResManager.isSelect(mAid, ask.getCreateTime())) {
            holder.praiseImageView.setImageResource(R.drawable.icon_praise_checked);
            holder.praiseImageView.setEnabled(false);
        } else {
            holder.praiseImageView.setImageResource(R.drawable.icon_praise_unchecked);
            holder.praiseImageView.setEnabled(true);
        }
        holder.praiseCountTextView.setText(ask.getCount() + "");

        if (ask.getUid().equals(UserInfo.getUserInfo().getUid())) {
            holder.replyImageView.setVisibility(View.INVISIBLE);
        } else {
            holder.replyImageView.setVisibility(View.VISIBLE);
        }

        final int floorNum = mReplyCount - position;
        holder.floorTextView.setText(floorNum + mContext.getResources().getString(R.string.floor_num));
        holder.praiseCountTextView.setTag(position);
        holder.praiseImageView.setTag(position);
        holder.contentImageView.setTag(position);
        holder.parentView.setTag(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.head_image_view)
        SimpleDraweeView headImageView;
        @Bind(R.id.name_text_view)
        TextView nameTextView;
        @Bind(R.id.content_text_view)
        TextView contentTextView;
        @Bind(R.id.content_image_view)
        SimpleDraweeView contentImageView;
        @Bind(R.id.floor_text_view)
        TextView floorTextView;
        @Bind(R.id.date_text_view)
        TextView dateTextView;
        @Bind(R.id.praise_image_view)
        ImageView praiseImageView;
        @Bind(R.id.praise_count_text_view)
        TextView praiseCountTextView;
        @Bind(R.id.reply_image_view)
        ImageView replyImageView;
        View parentView;

        MyViewHolder(View view) {
            super(view);
            parentView = view;
            ButterKnife.bind(this, view);
        }
    }
}
