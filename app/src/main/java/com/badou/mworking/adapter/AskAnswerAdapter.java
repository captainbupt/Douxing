package com.badou.mworking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.WenDaManage;
import com.badou.mworking.listener.FullImageListener;
import com.badou.mworking.model.Ask;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;

/**
 * 问答详情页面
 */
public class AskAnswerAdapter extends MyBaseAdapter {

    private WaitProgressDialog mProgressDialog;
    private String mAid;
    private int mReplyCount;

    public AskAnswerAdapter(Context context, String aid, int count) {
        super(context);
        this.mAid = aid;
        this.mReplyCount = count;
        mProgressDialog = new WaitProgressDialog(context, R.string.progress_tips_praise_ing);
    }

    public void setReplyCount(int count) {
        this.mReplyCount = count;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_ask_answer,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Ask ask = (Ask) getItem(position);
        holder.nameTextView.setText(ask.userName);
        holder.contentTextView.setText(ask.content);
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, ask.createTime));

        ImageViewLoader.setCircleImageViewResource(mContext, holder.headImageView,
                ask.userHeadUrl, mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));
        ImageViewLoader.setSquareImageViewResourceOnWifi(mContext, holder.contentImageView, ask.contentImageUrl, mContext.getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));
        holder.contentImageView.setOnClickListener(new FullImageListener(mContext, ask.contentImageUrl));

        /** 设置点赞的check **/
        if (WenDaManage.isSelect(mContext, mAid, ask.createTime / 1000 + "")) {
            holder.praiseCheckBox.setChecked(true);
            holder.praiseCheckBox.setEnabled(false);
        } else {
            holder.praiseCheckBox.setChecked(false);
            holder.praiseCheckBox.setEnabled(true);
        }
        holder.praiseTextView.setText(ask.count + "");

        holder.praiseCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                praise(position);
            }
        });

        final int floorNum = mReplyCount - position;
        holder.floorTextView.setText(floorNum + mContext.getResources().getString(R.string.floor_num));

        return convertView;
    }

    static class ViewHolder {

        ImageView headImageView;
        ImageView contentImageView;
        TextView nameTextView;
        TextView contentTextView;
        TextView dateTextView;
        TextView floorTextView;
        TextView praiseTextView;    //点赞数量
        CheckBox praiseCheckBox;   //点赞checkbox

        public ViewHolder(View view) {
            contentImageView = (ImageView) view.findViewById(R.id.iv_adapter_ask_answer);
            headImageView = (ImageView) view
                    .findViewById(R.id.iv_adapter_ask_answer_head);
            nameTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_ask_answer_name);
            contentTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_ask_answer_content);
            dateTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_ask_answer_date);
            floorTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_answer_floor);
            praiseTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_answer_praise_count);
            praiseCheckBox = (CheckBox) view.findViewById(R.id.cb_adapter_ask_answer_praise);
        }
    }

    /**
     * 回复点赞
     */
    private void praise(int position) {
        mProgressDialog.show();
        final Ask ask = (Ask) getItem(position);
        ServiceProvider.pollAnswer(mContext, mAid, ask.createTime / 1000 + "", new VolleyListener(mContext) {

            @Override
            public void onResponse(Object responseObject) {
                ask.count++;
                WenDaManage.insertItem(mContext, mAid, ask.createTime);
                AskAnswerAdapter.this.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ToastUtil.showNetExc(mContext);
                mProgressDialog.dismiss();
            }
        });
    }
}
