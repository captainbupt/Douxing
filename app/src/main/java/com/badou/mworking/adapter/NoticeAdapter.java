package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Notice;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.IconLoadListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;

/**
 * 类: <code> NoticeAdapter </code> 功能描述: 通知公告adapter 创建人: 葛建锋 创建日期: 2014年7月17日
 * 下午4:48:05 开发环境: JDK7.0
 */
public class NoticeAdapter extends MyBaseAdapter {

    public NoticeAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_notice_item, null);
        }
        ImageView iconImage = ViewHolder.getVH(convertView, R.id.iv_adapter_base_item_icon);
        TextView subject = ViewHolder.getVH(convertView, R.id.tv_adapter_base_item_subject);
        TextView department_time = ViewHolder.getVH(convertView, R.id.tv_adapter_item_dpt_date);
        ImageView isTop = ViewHolder.getVH(convertView, R.id.tv_adapter_base_item_top);
        RelativeLayout rl_isReadbg = ViewHolder.getVH(convertView, R.id.rl_item_bg_isread);
        final Notice notice = (Notice) getItem(position);

        // 加载图片
        if (null == notice.imgUrl || "".equals(notice.imgUrl)) {
            iconImage.setImageResource(R.drawable.icon_def_notice);
        } else {
            iconImage.setTag(notice.imgUrl);
            Bitmap bm = BitmapLruCache.getBitmapLruCache().getBitmap(
                    notice.imgUrl);
            if (bm != null && notice.imgUrl.equals(iconImage.getTag())) {
                iconImage.setImageBitmap(bm);
                bm = null;
            } else {
                MyVolley.getImageLoader().get(
                        notice.imgUrl,
                        new IconLoadListener(mContext, iconImage, notice
                                .imgUrl, R.drawable.icon_def_notice));
            }
        }


        if (notice.isRead == Constant.READ_YES) {
            rl_isReadbg.setBackgroundResource(R.drawable.icon_read_);
        } else {
            rl_isReadbg
                    .setBackgroundResource(R.drawable.icon_unread_orange);
        }

        subject.setText(notice.subject);
        department_time.setText(TimeTransfer.long2StringDetailDate(mContext, notice.time));
        if (notice.top == Constant.TOP_YES) {
            isTop.setVisibility(View.VISIBLE);
        } else {
            isTop.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 功能描述: 设置已读
     *
     * @param position
     */
    public void read(int position) {
        String userNum = ((AppApplication) mContext.getApplicationContext()).getUserInfo().account;
        Notice notice = (Notice) getItem(position);
        if (notice.isRead == Constant.READ_NO) {
            notice.isRead = Constant.READ_YES;
            this.notifyDataSetChanged();
            int unreadNum = SP.getIntSP(mContext, SP.DEFAULTCACHE, userNum + Notice.CATEGORY_KEY_UNREAD_NUM, 0);
            if (unreadNum > 0) {
                SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + Notice.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
            }
        }
    }
}
