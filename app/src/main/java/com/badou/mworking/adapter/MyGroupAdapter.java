package com.badou.mworking.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.PhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.VideoPlayActivity;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;

import java.util.ArrayList;
import java.util.List;

/**
 * 类: <code> MyGroupAdapter </code> 功能描述: 我的圈adapter 创建人: 葛建锋 创建日期: 2014年7月22日
 * 下午5:44:25 开发环境: JDK7.0
 */
public class MyGroupAdapter extends BaseAdapter {

    private Context mContext;
    private List<Chatter> mData;
    private LayoutInflater mInflater;
    private OnAdapterItemListener mAdapterItemListener;
    private String qid = "";

    public void setDatas(List<Chatter> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public void setOnAdapterItemListener(OnAdapterItemListener mAdapterItemListener) {
        this.mAdapterItemListener = mAdapterItemListener;
    }

    public void addDatas(List<Chatter> Questions) {
        if (this.mData == null) {
            this.mData = Questions;
        } else {
            for (Chatter temp : Questions) {
                this.mData.add(temp);
            }
        }
        notifyDataSetChanged();
    }

    public List<Chatter> getDataList() {
        if (mData != null) {
            return mData;
        }
        return null;
    }

    public MyGroupAdapter(Context mContext, String qid) {
        super();
        mData = new ArrayList<Chatter>();
        this.qid = qid;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(this.mContext);
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Chatter getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getUserView(position, convertView, parent);
    }

    /**
     * 功能描述:我的圈
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private View getUserView(final int position, View convertView, ViewGroup parent) {
        UserViewHolder holder;
        if (convertView != null) {
            holder = (UserViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.mygroupadapter, parent,
                    false);
            holder = new UserViewHolder(convertView);
            convertView.setTag(holder);
        }
        final Chatter question = mData.get(position);
        //着重显示该条
        if (!"".equals(qid) && qid.equals(question.qid)) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.color_grey));
        } else {
            //设置透明的色值
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        String content = question.content;
        if (!TextUtils.isEmpty(content)) {
            if (content.length() > 50) {
                holder.showAllContentTv.setVisibility(View.VISIBLE);
                holder.tvContent.setText(content.substring(0, 50) + "...");
            } else {
                holder.showAllContentTv.setVisibility(View.GONE);
                holder.tvContent.setText(content);
            }
        }
        long publisTime = question.publishTime;
        String pubTime = TimeTransfer.long2StringDetailDate(mContext, publisTime);
        String[] s = getStrings(pubTime, " ");
        String today = mContext.getResources().getString(R.string.time_text_jinTian);
        if (s[0].equals(today)) {
            holder.tvYear.setText(today);
            holder.tvMonth.setText("");
            holder.tvMyCircleTime.setText(s[1]);
        } else {
            String[] ss = getStrings(s[0], "-");
            if (ss.length > 1) {
                holder.tvMonth.setText(ss[0] + mContext.getResources().getString(R.string.time_text_yue));
                holder.tvYear.setText(ss[1]);
                holder.tvMyCircleTime.setText(s[1]);
            }
        }

        // 评论中添加的图片
        boolean isWifi = NetUtils.isWifiConnected(mContext);
        Bitmap contentBmp = BitmapLruCache.getBitmapLruCache().get(
                question.imgUrl);
        holder.tvContent.setBackgroundColor(Color.TRANSPARENT);
        if (isWifi) {
            if (contentBmp != null && contentBmp.isRecycled()) {
                holder.imgUserPic.setVisibility(View.VISIBLE);
                holder.imgUserPic.setImageBitmap(contentBmp);
                contentBmp = null;
                isShowShipingSign(question, holder);
            } else {
                MyVolley.getImageLoader().get(
                        question.imgUrl,
                        new PicImageListener(mContext, holder.imgUserPic,
                                question.imgUrl));
                isShowShipingSign(question, holder);
            }
            holder.imgUserPic.setOnClickListener(new ImageClickListener(
                    question));
        } else {
            // 判断是否在2G/3G下显示图片
            boolean isShowImg = SP.getBooleanSP(mContext, SP.DEFAULTCACHE, "pic_show", false);
            if (isShowImg) {
                MyVolley.getImageLoader().get(
                        question.imgUrl,
                        new PicImageListener(mContext,
                                holder.imgUserPic, question
                                .imgUrl));
                isShowShipingSign(question, holder);
            } else {
                holder.imgUserPic.setVisibility(View.GONE);
            }
        }
        holder.tvCount.setText(question.replyNumber + "");
        holder.tvDelItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Constant.is_refresh = true;
                if (mAdapterItemListener != null) {
                    mAdapterItemListener.deleteItem(position, question.qid);
                    notifyDataSetChanged();
                }
            }
        });
        holder.showAllContentTv.setOnClickListener(new ShowAllContent(question));
        return convertView;
    }

    /**
     * 功能描述:是否显示视屏标志
     */
    private void isShowShipingSign(Chatter question, UserViewHolder holder) {
        String videoUrl = question.videoUrl;
        if (TextUtils.isEmpty(videoUrl)) {
            holder.mygroupShipingImg.setVisibility(View.GONE);
            return;
        } else {
            holder.mygroupShipingImg.setVisibility(View.VISIBLE);
        }
    }

    private static String[] getStrings(String s, String reg) {
        if (s == null)
            return null;
        else {
            String[] ss = s.split(reg);
            return ss;
        }
    }

    /**
     * 类: <code> UserViewHolder </code> 功能描述: 我的圈 创建人:董奇 创建日期: 2014年7月22日
     * 上午11:01:24 开发环境: JDK7.0
     */
    static class UserViewHolder {

        TextView tvDelItem;
        TextView tvYear;
        TextView tvMonth;
        TextView tvContent;
        TextView tvMyCircleTime;
        TextView tvCount;
        TextView typeTv;
        TextView showAllContentTv;    //显示全文

        ImageView imgUserPic;
        ImageView mygroupShipingImg;

        public UserViewHolder(View view) {
            tvYear = (TextView) view.findViewById(R.id.tvMyCircleYear);
            tvMonth = (TextView) view.findViewById(R.id.tvMyCircleMonth);
            tvContent = (TextView) view.findViewById(R.id.tvMyCircleContent);
            tvCount = (TextView) view.findViewById(R.id.tvMyCircleNum);
            typeTv = (TextView) view.findViewById(R.id.type_tv);
            tvDelItem = (TextView) view.findViewById(R.id.tv_delete);
            tvMyCircleTime = (TextView) view.findViewById(R.id.tvMyCircleTime);
            imgUserPic = (ImageView) view
                    .findViewById(R.id.imgUserQuestionShare);
            mygroupShipingImg = (ImageView) view.findViewById(R.id.mygroup_shiping_img);
            showAllContentTv = (TextView) view.findViewById(R.id.show_all_content_tv);
        }
    }

    class ImageClickListener implements OnClickListener {
        private Chatter question;

        public ImageClickListener(Chatter question) {
            this.question = question;
        }

        @Override
        public void onClick(View arg0) {
            Constant.is_refresh = false;
            String videoUrl = question.videoUrl;
            //videoUrl为空，点击的是图片
            if (TextUtils.isEmpty(videoUrl)) {
                Intent intent = new Intent(mContext, PhotoActivity.class);
                intent.putExtra(PhotoActivity.MODE_PICZOMM,
                        question.imgUrl);
                ((Activity) mContext).startActivity(intent);
                //videoUrl不为空，点击的是视屏
            } else {
                Intent intent = new Intent(mContext, VideoPlayActivity.class);
                intent.putExtra(VideoPlayActivity.KEY_VIDEOURL, question.videoUrl);
                intent.putExtra(VideoPlayActivity.KEY_VIDEOPATH, question.qid);
                ((Activity) mContext).startActivity(intent);
            }
        }
    }

    public interface OnAdapterItemListener {
        void deleteItem(int pos, String qid);
    }

    /**
     * 显示全文的监听
     */
    class ShowAllContent implements OnClickListener {

        Chatter question;

        public ShowAllContent(Chatter question) {
            this.question = question;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, ChatterDetailActivity.class);
            intent.putExtra(ChatterDetailActivity.KEY_CHATTER, question);
            mContext.startActivity(intent);
        }
    }

}
