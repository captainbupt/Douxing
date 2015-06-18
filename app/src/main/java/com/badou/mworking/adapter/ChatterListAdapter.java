package com.badou.mworking.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.ChatterUserActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.ChatterResManager;
import com.badou.mworking.listener.CopyClickListener;
import com.badou.mworking.listener.TopicClickableSpan;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.model.user.UserChatterInfo;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.widget.MultiImageShowGridView;
import com.badou.mworking.widget.VideoImageView;

import org.json.JSONObject;

/**
 * 功能描述:同事圈adapter
 */
public class ChatterListAdapter extends MyBaseAdapter {

    private AdapterView.OnItemClickListener mOnItemClickListener;
    private boolean isHeadClickable;

    public ChatterListAdapter(Context context, AdapterView.OnItemClickListener onItemClickListener, boolean isHeadClickable) {
        super(context);
        this.mOnItemClickListener = onItemClickListener;
        this.isHeadClickable = isHeadClickable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_chatter_item,
                    parent, false);
            holder = new ViewHolder(mContext, convertView);
            convertView.setTag(holder);
        }
        final Chatter chatter = (Chatter) mItemList.get(position);

        holder.nameTextView.setText(chatter.name);
        String content = chatter.content;
        if (content.length() > 100) {
            content = content.substring(0, 100) + "...";
            holder.fullContentTextView.setVisibility(View.VISIBLE);
        } else {
            holder.fullContentTextView.setVisibility(View.GONE);
        }
        TopicClickableSpan.setClickTopic(mContext, holder.contentTextView, content, holder.chatterClickListener);
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext,
                chatter.publishTime));
        holder.replyNumberTextView.setText("" + chatter.replyNumber);
        ImageViewLoader.setCircleImageViewResource(holder.headImageView, chatter.headUrl, mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

        // 评论中添加的图片
        boolean isWifi = NetUtils.isWifiConnected(mContext);
        // 判断是否在2G/3G下显示图片
        boolean isShowImg = SP.getBooleanSP(mContext, SP.DEFAULTCACHE,
                "pic_show", false);

        // 没有的话，判断是否是wifi网络
        if (isWifi || isShowImg) {
            holder.saveInternetTextView.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(chatter.videoUrl)) {
                holder.videoImageView.setVisibility(View.VISIBLE);
                holder.videoImageView.setData(chatter.imgUrl, chatter.videoUrl, chatter.qid);
                holder.imageGridView.setVisibility(View.GONE);
            } else {
                holder.imageGridView.setVisibility(View.VISIBLE);
                holder.imageGridView.setList(chatter.photoUrls);
                holder.videoImageView.setVisibility(View.GONE);
            }
        } else {
            holder.videoImageView.setVisibility(View.GONE);
            holder.imageGridView.setVisibility(View.GONE);
            holder.saveInternetTextView.setVisibility(View.VISIBLE);
        }

        /** 设置点赞数和监听 **/
        holder.praiseNumberTextView.setText(chatter.praiseNumber + "");
        // 设置显示级别
        LVUtil.setTextViewBg(holder.levelTextView, chatter.level);
        /** 设置点赞的check **/
        if (ChatterResManager.isSelect(mContext, chatter.qid)) {
            holder.praiseCheckBox.setChecked(true);
            holder.praiseCheckBox.setEnabled(false);
            holder.praiseNumberTextView.setEnabled(false);
        } else {
            holder.praiseCheckBox.setChecked(false);
            holder.praiseCheckBox.setEnabled(true);
            holder.praiseNumberTextView.setEnabled(true);
        }
        holder.praiseClickListener.chatter = chatter;
        holder.praiseClickListener.checkBox = holder.praiseCheckBox;
        holder.praiseClickListener.numberTextView = holder.praiseNumberTextView;
        holder.headClickListener.position = position;
        holder.copyClickListener.content = chatter.content;
        holder.chatterClickListener.position = position;

        return convertView;
    }

    class ViewHolder {

        ImageView headImageView;// 头像
        TextView nameTextView;// 用户名称
        TextView dateTextView;// 下方日期时间
        TextView levelTextView; // 等级
        TextView contentTextView;// 评论的内容
        TextView fullContentTextView;
        VideoImageView videoImageView;
        MultiImageShowGridView imageGridView;
        TextView saveInternetTextView; // 省流量模式
        TextView replyNumberTextView;// 评论的数量
        CheckBox praiseCheckBox;// 点赞chk
        TextView praiseNumberTextView;// 点赞数
        HeadClickListener headClickListener;
        PraiseClickListener praiseClickListener;
        CopyClickListener copyClickListener;
        ChatterClickListener chatterClickListener;

        public ViewHolder(Context context, View view) {
            headImageView = (ImageView) view.findViewById(R.id.iv_adapter_chatter_head);
            nameTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_name);
            contentTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_content);
            fullContentTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_full_content);
            dateTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_time);
            videoImageView = (VideoImageView) view.findViewById(R.id.viv_adapter_chatter_video);
            imageGridView = (MultiImageShowGridView) view.findViewById(R.id.misgv_adapter_chatter_image);
            replyNumberTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_reply_number);
            praiseCheckBox = (CheckBox) view.findViewById(R.id.cb_adapter_chatter_praise);
            praiseNumberTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_praise_number);
            levelTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_level);
            saveInternetTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_save_internet);
            headClickListener = new HeadClickListener();
            praiseClickListener = new PraiseClickListener();
            copyClickListener = new CopyClickListener(context);
            chatterClickListener = new ChatterClickListener();
            if (isHeadClickable) {
                headImageView.setOnClickListener(headClickListener);
            }
            praiseNumberTextView.setOnClickListener(praiseClickListener);
            praiseCheckBox.setOnClickListener(praiseClickListener);
            view.setOnLongClickListener(copyClickListener);
            view.setOnClickListener(chatterClickListener);
            contentTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public class ChatterClickListener implements OnClickListener {

        public int position;

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(null, view, position, position);
        }
    }

    class HeadClickListener implements OnClickListener {
        public int position;

        @Override
        public void onClick(View v) {
            UserChatterInfo userChatterInfo = new UserChatterInfo((Chatter) getItem(position));
            Intent intent = new Intent(mContext, ChatterUserActivity.class);
            intent.putExtra(ChatterUserActivity.KEY_UID, ((Chatter) getItem(position)).uid);
            intent.putExtra(ChatterUserActivity.KEY_USER_CHATTER, userChatterInfo);
            mContext.startActivity(intent);
        }
    }

    class PraiseClickListener implements OnClickListener {
        public Chatter chatter;
        public CheckBox checkBox;
        public TextView numberTextView;

        @Override
        public void onClick(View arg0) {
            chatter.praiseNumber++;
            checkBox.setChecked(true);
            checkBox.setEnabled(false);
            numberTextView.setEnabled(false);
            numberTextView.setText(chatter.praiseNumber + "");

            /** 调用同事圈点赞接口 提交点赞 **/
            ServiceProvider.doSetCredit(mContext, chatter.qid,
                    new VolleyListener(mContext) {

                        @Override
                        public void onErrorCode(int code) {
                            chatter.praiseNumber--;
                            checkBox.setChecked(false);
                            numberTextView.setText(chatter.praiseNumber + "");
                            checkBox.setEnabled(true);
                            numberTextView.setEnabled(true);
                        }

                        @Override
                        public void onResponseSuccess(JSONObject response) {
                            ChatterResManager
                                    .insertItem(mContext, chatter);
                        }
                    });
        }
    }
}
