package com.badou.mworking.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.PhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Chatter;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;

/**
 * 类: <code> AroundReplaAdapter </code> 功能描述: 同事圈item点击进入的评论页adapter 创建人:董奇
 * 创建日期: 2014年7月23日 下午7:00:22 开发环境: JDK7.0
 */
public class AroundReplaAdapter extends BaseAdapter {

    private List<Chatter> mData;
    private Context mContext;
    private LayoutInflater mInflater;
    private int count = 0;
    private String qid = "";
    private WaitProgressDialog mProgressDialog;

    public void setDatas(List<Chatter> mData, int count) {
        this.mData = mData;
        this.count = count;
        notifyDataSetChanged();
    }

    public void addDatas(List<Chatter> Questions, int count) {
        if (this.mData == null) {
            this.mData = Questions;
        } else {
            this.mData.addAll(Questions);
        }
        this.count = count;
        notifyDataSetChanged();
    }

    public AroundReplaAdapter(Context mContext, int count, String qid,
                              WaitProgressDialog mProgressDialog) {
        super();
        mData = new ArrayList<Chatter>();
        this.mContext = mContext;
        this.count = count;
        this.mInflater = LayoutInflater.from(this.mContext);
        this.qid = qid;
        this.mProgressDialog = mProgressDialog;
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
        return getAllView(position, convertView, parent);
    }

    /**
     * 功能描述:
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    private View getAllView(int position, View convertView, ViewGroup parent) {
        AllViewHolder holder;
        if (convertView != null) {
            holder = (AllViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.adapter_around_detail,
                    parent, false);
            holder = new AllViewHolder(convertView);
            convertView.setTag(holder);
        }
        Chatter question = mData.get(position);

        String name = question.name;
        if (!TextUtils.isEmpty(name)) {
            holder.tvQuestionShareName.setText(name + "");
        }
        String content = question.content;
        if (!TextUtils.isEmpty(content)) {
            holder.tvQuestionShareContent.setText(content);
        }
        String pubTime = TimeTransfer.long2StringDetailDate(mContext,
                question.publishTime);
        holder.tvQuestionShareDate.setText(pubTime);

        int size = mContext.getResources().getDimensionPixelSize(
                R.dimen.icon_head_size_middle);
        Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(
                question.imgUrl);
        if (headBmp != null && !headBmp.isRecycled()) {
            holder.headImg.setImageBitmap(headBmp);
        } else {
            MyVolley.getImageLoader().get(
                    question.imgUrl,
                    new CircleImageListener(mContext, question.imgUrl,
                            holder.headImg, size, size), size, size);

        }

        // 评论中添加的图片
        boolean isWifi = NetUtils.isWifiConnected(mContext);

        Bitmap contentBmp = null;
        if (question.imgUrl != null) {
            contentBmp = BitmapLruCache.getBitmapLruCache().get(
                    question.imgUrl);
        }
        if (contentBmp != null && contentBmp.isRecycled()) {
            holder.imgContentPic.setImageBitmap(contentBmp);
        } else {
            if (isWifi) {
                holder.imgContentPic.setVisibility(View.VISIBLE);
                if (question.imgUrl != null) {
                    MyVolley.getImageLoader().get(
                            question.imgUrl,
                            new PicImageListener(mContext,
                                    holder.imgContentPic, question
                                    .imgUrl));
                    holder.imgContentPic
                            .setOnClickListener(new ViewClickListener(question));
                } else {
                    holder.imgContentPic.setVisibility(View.GONE);
                }
            }
        }
        final int floorNum = count - position;
        holder.tvFloorNum.setText(floorNum
                + mContext.getResources().getString(R.string.floor_num)
                + "   ·");

        if (((AppApplication) mContext.getApplicationContext()).getUserInfo()
                .isAdmin || name.equals("我")) {
            holder.tvQuestionShareDelete
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            new AlertDialog.Builder(mContext)
                                    .setTitle(R.string.myQuan_dialog_title_tips)
                                    .setMessage(
                                            mContext.getResources().getString(
                                                    R.string.tip_delete_confirmation))
                                    .setPositiveButton(
                                            R.string.text_ok,
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface arg0,
                                                        int arg1) {
                                                    deleteReply(floorNum);
                                                }

                                            })
                                    .setNegativeButton(R.string.text_cancel,
                                            null).show();

                        }
                    });
        } else {
            holder.tvQuestionShareDelete.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void deleteReply(final int floor) {
        ServiceProvider.deleteReplyComment(mContext, qid, floor,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponse(Object responseObject) {
                        if (null != mProgressDialog && mContext != null) {
                            mProgressDialog.dismiss();
                        }
                        JSONObject response = (JSONObject) responseObject;
                        if (responseObject == null) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        }
                        int code = response.optInt(Net.CODE);
                        if (code == Net.LOGOUT) {
                            AppApplication.logoutShow(mContext);
                            return;
                        }
                        if (Net.SUCCESS != code) {
                            ToastUtil.showNetExc(mContext);
                            return;
                        }
                        ToastUtil.showToast(mContext, "删除评论成功！");
                        int position = count - floor;
                        mData.remove(position);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        super.onErrorResponse(arg0);
                        if (null != mProgressDialog && mContext != null) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

    /**
     * 类: <code> AllViewHolder </code> 功能描述: 同事圈 创建人:董奇 创建日期: 2014年7月18日
     * 下午8:52:45 开发环境: JDK7.0
     */
    static class AllViewHolder {
        ImageView headImg;
        ImageView imgContentPic;
        TextView tvQuestionShareName;
        TextView tvQuestionShareContent;
        TextView tvQuestionShareDate;
        TextView tvFloorNum; // 第几楼
        TextView tvQuestionShareDelete;

        public AllViewHolder(View view) {
            imgContentPic = (ImageView) view
                    .findViewById(R.id.imgQuestionShare);
            headImg = (ImageView) view
                    .findViewById(R.id.iv_adapter_comment_head);
            tvFloorNum = (TextView) view.findViewById(R.id.tv_adapter_comment_floor);
            tvQuestionShareName = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_name);
            tvQuestionShareContent = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_content);
            tvQuestionShareDate = (TextView) view
                    .findViewById(R.id.tv_adapter_comment_date);
            tvQuestionShareDelete = (TextView) view
                    .findViewById(R.id.tvQuestionShareDelete);
        }
    }

    class ViewClickListener implements OnClickListener {
        private Chatter question;

        public ViewClickListener(Chatter question) {
            this.question = question;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgQuestionShare:
                    Constant.is_refresh = false;
                    Intent intent = new Intent(mContext, PhotoActivity.class);
                    intent.putExtra(PhotoActivity.MODE_PICZOMM,
                            question.imgUrl);
                    ((Activity) mContext).startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
}
