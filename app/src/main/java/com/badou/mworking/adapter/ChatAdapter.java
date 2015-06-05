package com.badou.mworking.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.ChattingListInfo;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.TimeTransfer;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.TextView;
import org.json.JSONObject;

public class ChatAdapter extends MyBaseAdapter {


    public ChatAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.adapter_chat_list, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ChattingListInfo info = (ChattingListInfo) mItemList.get(position);
        /**设置头像**/
        holder.headImageView.setImageResource(R.drawable.icon_user_detail_default_head);
        /**设置头像**/
        int size = mContext.getResources().getDimensionPixelSize(
                R.dimen.around_icon_head_size);
        if (null != info && !"".equals(info.img)) {
            Bitmap bm = BitmapLruCache.getBitmapLruCache().getCircleBitmap(info.img);
            if (bm != null && !bm.isRecycled()) {
                holder.headImageView.setImageBitmap(bm);
            } else {
                MyVolley.getImageLoader().get(info.img, new CircleImageListener(mContext,
                        info.img, holder.headImageView, size, size));
            }
        }

        /**设置名字**/
        holder.nameTextView.setText(info.name);
        /**设置发布时间**/
        if (info.ts == 0) {
            holder.timeTextView.setText("");
        } else {
            holder.timeTextView.setText(TimeTransfer.long2StringDetailDate(mContext, info.ts * 1000));
        }

        /**设置发布内容**/
        holder.contentTextView.setText(info.content);

        /**设置未读数**/
        if (info.msgcnt > 0) {
            holder.unreadNumTextView.setVisibility(View.VISIBLE);
            holder.unreadNumTextView.setText(info.msgcnt + "");
        } else {
            holder.unreadNumTextView.setVisibility(View.INVISIBLE);
        }
        if (info.msgcnt > 9) {
            holder.unreadNumTextView.setBackgroundResource(R.drawable.icon_chat_unread_long);
        } else {
            holder.unreadNumTextView.setBackgroundResource(R.drawable.icon_chat_unread);
        }
        // 添加删除布局的点击事件
        holder.deleteTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new AlertDialog.Builder(mContext).setTitle(R.string.tip_delete_confirmation)
                        .setPositiveButton(R.string.operation_delete, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteChat(info.whom);
                                mItemList.remove(position);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton(R.string.text_cancel, null).create().show();
            }
        });
        return view;
    }

    static class ViewHolder {
        ImageView headImageView;
        TextView unreadNumTextView;
        TextView timeTextView;
        TextView nameTextView;
        TextView contentTextView;
        TextView deleteTextView;

        public ViewHolder(View view) {
            headImageView = (ImageView) view.findViewById(R.id.iv_adapter_chat_list_head);
            nameTextView = (TextView) view.findViewById(R.id.tv_adapter_chat_list_name);
            timeTextView = (TextView) view.findViewById(R.id.tv_adapter_chat_list_time);
            unreadNumTextView = (TextView) view.findViewById(R.id.tv_adapter_chat_list_unread);
            contentTextView = (TextView) view.findViewById(R.id.tv_adapter_chat_list_content);
            deleteTextView = (TextView) view.findViewById(R.id.tv_adapter_chat_list_delete);
        }
    }

    /**
     * 删除会话
     */
    private void deleteChat(String whom) {
        ServiceProvider.delChat(mContext, whom, new VolleyListener(mContext) {

            @Override
            public void onResponse(Object responseObject) {
                JSONObject response = (JSONObject) responseObject;
                int code = response.optInt(Net.CODE);
                if (code != Net.SUCCESS) {
                    return;
                }
            }
        });
    }
}
