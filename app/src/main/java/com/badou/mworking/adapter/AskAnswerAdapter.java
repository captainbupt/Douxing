package com.badou.mworking.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.AskResManager;
import com.badou.mworking.domain.ask.AskReplyPraiseUseCase;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.listener.AdapterItemClickListener;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.TimeTransfer;

/**
 * 问答详情页面
 */
public class AskAnswerAdapter extends MyBaseAdapter<Ask> {

    private String mAid;
    private int mReplyCount;
    AdapterItemClickListener mCopyListener;
    AdapterItemClickListener mPraiseListener;
    AdapterItemClickListener mFullImageListener;

    public AskAnswerAdapter(Context context, String aid, int count, AdapterItemClickListener copyListener, AdapterItemClickListener praiseListener, AdapterItemClickListener fullImageListener) {
        super(context);
        this.mAid = aid;
        this.mReplyCount = count;
        mCopyListener = copyListener;
        mPraiseListener = praiseListener;
        mFullImageListener = fullImageListener;
    }

    public void setReplyCount(int count) {
        this.mReplyCount = count;
    }

    public int getRelyCount() {
        return mReplyCount;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_ask_answer, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.praiseTextView.setOnClickListener(mPraiseListener);
            holder.praiseImageView.setOnClickListener(mPraiseListener);
            holder.contentImageView.setOnClickListener(mFullImageListener);
            convertView.setOnClickListener(mCopyListener);
        }
        final Ask ask = (Ask) getItem(position);
        holder.nameTextView.setText(ask.getUserName());
        holder.contentTextView.setText(ask.getContent());
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext, ask.getCreateTime()));

        ImageViewLoader.setCircleImageViewResource(holder.headImageView, ask.getUserHeadUrl(), mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));
        ImageViewLoader.setSquareImageViewResourceOnWifi(mContext, holder.contentImageView, R.drawable.icon_image_default, ask.getContentImageUrl(), mContext.getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));

        /** 设置点赞的check **/
        if (AskResManager.isSelect(mAid, ask.getCreateTime())) {
            holder.praiseImageView.setImageResource(R.drawable.icon_praise_checked);
            holder.praiseImageView.setEnabled(false);
        } else {
            holder.praiseImageView.setImageResource(R.drawable.icon_praise_unchecked);
            holder.praiseImageView.setEnabled(true);
        }
        holder.praiseTextView.setText(ask.getCount() + "");

        final int floorNum = mReplyCount - position;
        holder.floorTextView.setText(floorNum + mContext.getResources().getString(R.string.floor_num));
        holder.praiseTextView.setTag(position);
        holder.praiseImageView.setTag(position);
        holder.contentImageView.setTag(position);
        return convertView;
    }

    class ViewHolder {

        ImageView headImageView;
        ImageView contentImageView;
        TextView nameTextView;
        TextView contentTextView;
        TextView dateTextView;
        TextView floorTextView;
        TextView praiseTextView;    //点赞数量
        ImageView praiseImageView;   //点赞checkbox

        public ViewHolder(View view) {
            contentImageView = (ImageView) view.findViewById(R.id.content_image_view);
            headImageView = (ImageView) view
                    .findViewById(R.id.head_image_view);
            nameTextView = (TextView) view
                    .findViewById(R.id.name_text_view);
            contentTextView = (TextView) view
                    .findViewById(R.id.content_text_view);
            dateTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_ask_answer_date);
            floorTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_answer_floor);
            praiseTextView = (TextView) view.findViewById(R.id.tv_adapter_ask_answer_praise_count);
            praiseImageView = (ImageView) view.findViewById(R.id.iv_adapter_ask_answer_praise);
        }
    }
}
