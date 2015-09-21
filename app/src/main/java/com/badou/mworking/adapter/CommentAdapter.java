package com.badou.mworking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.entity.comment.Comment;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 功能描述:评论adapter
 */
public class CommentAdapter extends MyBaseAdapter<Comment> {

    boolean isChatter;

    private int mAllCount = 0;
    private String mQid;
    private boolean mDeletable;
    private OnClickListener mDeleteClickListener;

    public CommentAdapter(Context context) {
        super(context);
        isChatter = false;
    }

    public CommentAdapter(Context context, String qid, boolean deletable, OnClickListener deleteClickListener) {
        super(context);
        isChatter = true;
        mQid = qid;
        mDeletable = deletable;
        mDeleteClickListener = deleteClickListener;
    }

    public void setAllCount(int allCount) {
        this.mAllCount = allCount;
    }

    public int getAllCount() {
        return mAllCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Comment comment = mItemList.get(position);
        /**加载布局**/
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_comment,
                    parent, false);
            holder = new ViewHolder(convertView, mDeleteClickListener);
            convertView.setTag(holder);
        }
        if (isChatter) {
            if (position == 0) {
                convertView.setBackgroundResource(R.drawable.bg_around_detail);
            } else {
                convertView.setBackgroundColor(0xffdde7ec);
            }
        }
        /*获取员工号*/
        String name = comment.getName();
        if (!TextUtils.isEmpty(name)) {
            holder.mNameTextView.setText(name);
        }
        /*获取评论内容*/
        String content = comment.getContent();
        if (!TextUtils.isEmpty(content)) {
            holder.mContentTextView.setText(content);
        }
        /*获取评论时间*/
        String pubTime = TimeTransfer.long2StringDetailDate(mContext, comment.getTime());
        holder.mDateTextView.setText(pubTime);

        /**设置头像**/
        holder.mHeadImageView.setImageURI(UriUtil.getHttpUri(comment.getImgUrl()));

		/*设置楼数*/
        int floorNum = mAllCount - position;
        holder.mFloorNumTextView.setText(floorNum + mContext.getResources().getString(R.string.floor_num) + "   ·");
        if (position == 0) {
            holder.mDividerView.setVisibility(View.GONE);
        } else {
            holder.mDividerView.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(mQid) && (mDeletable || comment.getName().equals("我"))) {
            holder.mDeleteTextView.setVisibility(View.VISIBLE);
        } else {
            holder.mDeleteTextView.setVisibility(View.GONE);
        }
        holder.mDeleteTextView.setTag(position);
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView mHeadImageView;
        TextView mNameTextView;
        TextView mContentTextView;
        TextView mDateTextView;
        TextView mFloorNumTextView;
        TextView mDeleteTextView;
        View mDividerView;

        public ViewHolder(View view, OnClickListener deleteClickListener) {
            mHeadImageView = (SimpleDraweeView) view
                    .findViewById(R.id.iv_adapter_comment_head);
            mNameTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_name);
            mContentTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_content);
            mDateTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_date);
            mFloorNumTextView = (TextView) view.findViewById(R.id.tv_adapter_comment_floor);
            mDividerView = view.findViewById(R.id.view_adapter_comment_divider);
            mDeleteTextView = (TextView) view.findViewById(R.id.tv_adapter_comment_delete);
            mDeleteTextView.setOnClickListener(deleteClickListener);
            if (isChatter) {
                mDeleteTextView.setVisibility(View.VISIBLE);
            } else {
                mDeleteTextView.setVisibility(View.GONE);
            }
        }
    }
}
