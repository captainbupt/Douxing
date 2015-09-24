package com.badou.mworking.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseRecyclerAdapter;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 功能描述:评论adapter
 */
public class CommentAdapter extends MyBaseRecyclerAdapter<Comment, CommentAdapter.MyViewHolder> {

    boolean isChatter;

    private int mAllCount = 0;
    private String mQid;
    private boolean mDeletable;
    private OnClickListener mDeleteClickListener;
    private OnClickListener mItemClickListener;

    public CommentAdapter(Context context, OnClickListener itemClickListener) {
        super(context);
        isChatter = false;
        this.mItemClickListener = itemClickListener;
    }

    public CommentAdapter(Context context, String qid, boolean deletable, OnClickListener itemClickListener,OnClickListener deleteClickListener) {
        super(context);
        isChatter = true;
        mQid = qid;
        mDeletable = deletable;
        this.mItemClickListener = itemClickListener;
        mDeleteClickListener = deleteClickListener;
    }

    public void setAllCount(int allCount) {
        this.mAllCount = allCount;
    }

    public int getAllCount() {
        return mAllCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(mInflater.inflate(R.layout.adapter_comment, parent, false));
        viewHolder.deleteTextView.setOnClickListener(mDeleteClickListener);
        viewHolder.parentView.setOnClickListener(mItemClickListener);
        if (isChatter) {
            viewHolder.deleteTextView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.deleteTextView.setVisibility(View.GONE);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comment comment = mItemList.get(position);
        /**加载布局**/
        if (isChatter) {
            if (position == 0) {
                holder.parentView.setBackgroundResource(R.drawable.bg_around_detail);
            } else {
                holder.parentView.setBackgroundColor(0xffdde7ec);
            }
        }
        /*获取员工号*/
        String name = comment.getName();
        if (!TextUtils.isEmpty(name)) {
            holder.nameTextView.setText(name);
        }
        /*获取评论内容*/
        String content = comment.getContent();
        if (!TextUtils.isEmpty(content)) {
            holder.contentTextView.setText(content);
        }
        /*获取评论时间*/
        String pubTime = TimeTransfer.long2StringDetailDate(mContext, comment.getTime());
        holder.dateTextView.setText(pubTime);

        /**设置头像**/
        holder.mHeadImageView.setImageURI(UriUtil.getHttpUri(comment.getImgUrl()));

		/*设置楼数*/
        int floorNum = mAllCount - position;
        holder.floorNumTextView.setText(floorNum + mContext.getResources().getString(R.string.floor_num) + "   ·");
        if (!TextUtils.isEmpty(mQid) && (mDeletable || comment.getName().equals("我"))) {
            holder.deleteTextView.setVisibility(View.VISIBLE);
        } else {
            holder.deleteTextView.setVisibility(View.GONE);
        }
        holder.parentView.setTag(position);
        holder.deleteTextView.setTag(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView mHeadImageView;
        TextView nameTextView;
        TextView contentTextView;
        TextView dateTextView;
        TextView floorNumTextView;
        TextView deleteTextView;
        View parentView;

        public MyViewHolder(View view) {
            super(view);
            parentView = view;
            mHeadImageView = (SimpleDraweeView) view
                    .findViewById(R.id.iv_adapter_comment_head);
            nameTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_name);
            contentTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_content);
            dateTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_date);
            floorNumTextView = (TextView) view.findViewById(R.id.tv_adapter_comment_floor);
            deleteTextView = (TextView) view.findViewById(R.id.tv_adapter_comment_delete);
        }
    }
}
