package com.badou.mworking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.listener.DeleteClickListener;
import com.badou.mworking.model.Comment;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;

import org.json.JSONObject;

import java.util.List;

/**
 * 功能描述:评论adapter
 */
public class CommentAdapter extends MyBaseAdapter {

    private int mAllCount = 0;
    private String mQid;
    private int mType;
    private boolean mDeletable;
    private WaitProgressDialog mProgressDialog;

    public CommentAdapter(Context context) {
        super(context);
        mType = Comment.TYPE_COMMENT;
    }

    public CommentAdapter(Context context, String qid, boolean deletable, WaitProgressDialog progressDialog) {
        super(context);
        mQid = qid;
        mType = Comment.TYPE_CHATTER;
        mProgressDialog = progressDialog;
        mDeletable = deletable;
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

    public int getAllCount() {
        return mAllCount;
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
        Comment comment = (Comment) mItemList.get(position);
        /*获取员工号*/
        String name = comment.name;
        if (!TextUtils.isEmpty(name)) {
            holder.mNameTextView.setText(name);
        }
        /*获取评论内容*/
        String content = comment.content;
        if (!TextUtils.isEmpty(content)) {
            holder.mContentTextView.setText(content);
        }
        /*获取评论时间*/
        String pubTime = TimeTransfer.long2StringDetailDate(mContext, comment
                .time);
        holder.mDateTextView.setText(pubTime);

        /**设置头像**/
        ImageViewLoader.setCircleImageViewResource(holder.mHeadImageView, comment.imgUrl,mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

		/*设置楼数*/
        int floorNum = mAllCount - position;
        holder.mFloorNumTextView.setText(floorNum + mContext.getResources().getString(R.string.floor_num) + "   ·");
        if (position == 0) {
            holder.mDividerView.setVisibility(View.GONE);
        } else {
            holder.mDividerView.setVisibility(View.VISIBLE);
        }
        if (mType == Comment.TYPE_CHATTER && (mDeletable || comment.name.equals("我"))) {
            holder.mDeleteTextView.setVisibility(View.VISIBLE);
            holder.mDeleteConfirmListener.floor = floorNum;
        } else {
            holder.mDeleteTextView.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder {
        ImageView mHeadImageView;
        TextView mNameTextView;
        TextView mContentTextView;
        TextView mDateTextView;
        TextView mFloorNumTextView;
        TextView mDeleteTextView;
        View mDividerView;
        DeleteConfirmListener mDeleteConfirmListener;

        public ViewHolder(View view) {
            mHeadImageView = (ImageView) view
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
            mDeleteConfirmListener = new DeleteConfirmListener();
            if (mType == Comment.TYPE_CHATTER) {
                mDeleteTextView.setOnClickListener(new DeleteClickListener(mContext, mDeleteConfirmListener));
                view.setBackgroundColor(0x00000000);
                mDeleteTextView.setVisibility(View.VISIBLE);
            } else {
                mDeleteTextView.setVisibility(View.GONE);
            }
        }
    }

    class DeleteConfirmListener implements DialogInterface.OnClickListener {

        public int floor;

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            mProgressDialog.setContent(R.string.progress_tips_delete_ing);
            ServiceProvider.deleteReplyComment(mContext, mQid, floor,
                    new VolleyListener(mContext) {

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            ToastUtil.showToast(mContext, "删除评论成功！");
                            int position = mAllCount - floor;
                            mAllCount--;
                            remove(position);
                        }

                        @Override
                        public void onCompleted() {
                            if (!((Activity) mContext).isFinishing()) {
                                mProgressDialog.dismiss();
                            }
                        }
                    });
        }
    }
}
