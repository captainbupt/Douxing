package com.badou.mworking.adapter;

import org.holoeverywhere.widget.AdapterView;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.AroundDetailActivity;
import com.badou.mworking.MyGroupActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.database.TongShQuResManage;
import com.badou.mworking.fragment.ChatterListFragment;
import com.badou.mworking.listener.CopyClickListener;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.MultiImageShowGridView;
import com.badou.mworking.widget.VideoImageView;

/**
 * 功能描述:同事圈adapter
 */
public class ChatterAdapter extends MyBaseAdapter {

    private AdapterView.OnItemClickListener mOnItemClickListener;

    public ChatterAdapter(Context context, AdapterView.OnItemClickListener onItemClickListener) {
        super(context);
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_arount_item,
                    parent, false);
            holder = new ViewHolder(mContext, convertView, mOnItemClickListener);
            convertView.setTag(holder);
        }
        final Chatter chatter = (Chatter) mItemList.get(position);

        holder.nameTextView.setText(chatter.name);
        if (chatter.content.length() > 100) {
            holder.contentTextView.setText(chatter.content.substring(0, 100)
                    + "...");
        } else {
            holder.contentTextView.setText(chatter.content);
        }
        holder.dateTextView.setText(TimeTransfer.long2StringDetailDate(mContext,
                chatter.publishTime));
        holder.replyNumberTextView.setText("" + chatter.replyNumber);
        ImageViewLoader.setCircleImageViewResource(mContext, holder.headImageView, chatter.headUrl,
                mContext.getResources().getDimensionPixelSize(R.dimen.icon_head_size_middle));

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
        if (TongShQuResManage.isSelect(mContext, chatter.qid)) {
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
        holder.headClickListener.uid = chatter.uid;
        holder.copyClickListener.content = chatter.content;
        holder.chatterClickListener.position = position;

        return convertView;
    }

    static class ViewHolder {

        ImageView headImageView;// 头像
        TextView nameTextView;// 用户名称
        TextView dateTextView;// 下方日期时间
        TextView levelTextView; // 等级
        TextView contentTextView;// 评论的内容
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

        public ViewHolder(Context context, View view, AdapterView.OnItemClickListener onItemClickListener) {
            headImageView = (ImageView) view
                    .findViewById(R.id.iv_adapter_chatter_head);
            nameTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_chatter_name);
            contentTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_chatter_content);
            dateTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_chatter_time);
            videoImageView = (VideoImageView) view.findViewById(R.id.viv_adapter_chatter_video);
            imageGridView = (MultiImageShowGridView) view.findViewById(R.id.misgv_adapter_chatter_image);
            replyNumberTextView = (TextView) view
                    .findViewById(R.id.tv_adapter_chatter_reply_number);
            praiseCheckBox = (CheckBox) view.findViewById(R.id.cb_adapter_chatter_praise);
            praiseNumberTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_praise_number);
            levelTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_level);
            saveInternetTextView = (TextView) view.findViewById(R.id.tv_adapter_chatter_save_internet);
            headClickListener = new HeadClickListener(context);
            praiseClickListener = new PraiseClickListener(context);
            copyClickListener = new CopyClickListener(context);
            chatterClickListener = new ChatterClickListener(onItemClickListener);
            headImageView.setOnClickListener(headClickListener);
            praiseNumberTextView.setOnClickListener(praiseClickListener);
            praiseCheckBox.setOnClickListener(praiseClickListener);
            view.setOnLongClickListener(copyClickListener);
            view.setOnClickListener(chatterClickListener);
        }
    }

    static class ChatterClickListener implements OnClickListener {

        public int position;
        private AdapterView.OnItemClickListener mOnItemClickListener;

        public ChatterClickListener(AdapterView.OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(null, view, position, position);
        }
    }

    static class HeadClickListener implements OnClickListener {
        private Context mContext;
        public String uid;

        public HeadClickListener(Context context) {
            this.mContext = context;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, MyGroupActivity.class);
            intent.putExtra(MyGroupActivity.KEY_QID, uid);
            mContext.startActivity(intent);
        }
    }

    static class PraiseClickListener implements OnClickListener {
        private Context mContext;
        public Chatter chatter;
        public CheckBox checkBox;
        public TextView numberTextView;

        public PraiseClickListener(Context context) {
            this.mContext = context;
        }

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
                        public void onResponse(Object responseObject) {
                            JSONObject respon = (JSONObject) responseObject;
                            try {
                                int code = respon.optInt(Net.CODE);
                                if (code == Net.LOGOUT) {
                                    AppApplication.logoutShow(mContext);
                                    return;
                                }
                                if (Net.SUCCESS != code) {
                                    ToastUtil.showToast(mContext,
                                            R.string.credit_fail);
                                    chatter.praiseNumber--;
                                    checkBox.setChecked(false);
                                    numberTextView.setText(chatter.praiseNumber + "");
                                    checkBox.setEnabled(true);
                                    numberTextView.setEnabled(true);
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            TongShQuResManage
                                    .insertItem(mContext, chatter);
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            super.onErrorResponse(error);
                            chatter.praiseNumber--;
                            checkBox.setChecked(false);
                            numberTextView.setText(chatter.praiseNumber + "");
                            checkBox.setEnabled(true);
                            numberTextView.setEnabled(true);
                        }
                    });
        }
    }
}
