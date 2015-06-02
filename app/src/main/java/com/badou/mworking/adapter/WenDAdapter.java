package com.badou.mworking.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.PhotoActivity;
import com.badou.mworking.R;
import com.badou.mworking.WenDaDetailActivity;
import com.badou.mworking.model.Ask;
import com.badou.mworking.util.LVUtil;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;

/**
 * @author 葛建锋
 * 问答页面适配器
 */
public class WenDAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater layoutInflater;
	private ArrayList<Ask> asks;
	
	public WenDAdapter(Context context,ArrayList<Ask> asks) {
		super();
		this.layoutInflater = LayoutInflater.from(context);
		this.asks = asks;
		this.context = context;
	}

	@Override
	public int getCount() {
		return asks.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AllViewHolder holder;
		if (convertView != null) {
			holder = (AllViewHolder) convertView.getTag();
		} else {
			convertView = layoutInflater.inflate(R.layout.wendadapter,
					parent, false);
			holder = new AllViewHolder(convertView);
			convertView.setTag(holder);
		}
		final Ask ask = asks.get(position);
		String headImgUrl = ask.getImgurl();
		int size = context.getResources().getDimensionPixelSize(
				R.dimen.around_icon_head_size);
		Bitmap headBmp = BitmapLruCache.getBitmapLruCache().getCircleBitmap(
				headImgUrl);
		if (headBmp != null && !headBmp.isRecycled()) {
			holder.wendaTouxiangImg.setImageBitmap(headBmp);
			headBmp = null;
		} else {
			MyVolley.getImageLoader().get(
					headImgUrl,
					new CircleImageListener(context, headImgUrl,
							holder.wendaTouxiangImg, size, size), size, size);
		}
		// 在同事圈返回的是毫秒，在问答模块，也就是这里返回的是秒，所以要乘以1000
		String pubTime = TimeTransfer.long2StringDetailDate(context,ask.getCreate_ts()*1000);
		holder.wendaNameTv.setText(ask.getEid());
		
		LVUtil.setTextViewBg(holder.lvTv, ask.getCircle_lv());
		holder.wendaData.setText(pubTime);
		holder.replayNum.setText(ask.getCount()+"答复");
		
		String content = ask.getContent();
		if (!TextUtils.isEmpty(content)) {
			if(content.length()>100){
				holder.showAllContentTv.setVisibility(View.VISIBLE);
				holder.wendaContent.setText(content.substring(0, 100)+"...");
			}else{
				holder.showAllContentTv.setVisibility(View.GONE);
				holder.wendaContent.setText(content);
			}
		}
		// 评论中添加的图片
		boolean isWifi = NetUtils.isWifiConnected(context);
		// 逻辑1：只有在wifi状态下显示图片
		String imgUrl = ask.getPicurl();
		Bitmap contentBmp = BitmapLruCache.getBitmapLruCache().get(imgUrl);
		if(TextUtils.isEmpty(imgUrl)){
			holder.wendaPictureImg.setVisibility(View.GONE);
		}else{
			//没有的话，判断是否是wifi网络
			if (isWifi) {
				//判断缓存中是否有该图片
				if (contentBmp != null && !contentBmp.isRecycled()) {
					holder.wendaPictureImg.setVisibility(View.VISIBLE);
					holder.wendaPictureImg.setImageBitmap(contentBmp);
				} else {
					MyVolley.getImageLoader().get(
							imgUrl,
							new PicImageListener(context,
									holder.wendaPictureImg, imgUrl));
				}
			}else{
				// 判断是否在2G/3G下显示图片
				boolean isShowImg = SP.getBooleanSP(context,SP.DEFAULTCACHE, "pic_show", false);
				if(!isShowImg){
					if (contentBmp != null && !contentBmp.isRecycled()) {
						holder.wendaPictureImg.setVisibility(View.VISIBLE);
						holder.wendaPictureImg.setImageBitmap(contentBmp);
					} else {
						MyVolley.getImageLoader().get(
								imgUrl,
								new PicImageListener(context,
										holder.wendaPictureImg,imgUrl));
					}
				}else{
					if(!TextUtils.isEmpty(imgUrl)){
						holder.shenliuliangTv.setVisibility(View.VISIBLE);
					}else{
						holder.shenliuliangTv.setVisibility(View.GONE);
					}
					holder.wendaPictureImg.setVisibility(View.GONE);
				}
			}
		}
		holder.wendaPictureImg.setOnClickListener(new ImageClickListener(
				ask));
		holder.showAllContentTv.setOnClickListener(new ShowAllContent(ask));
		

		
		
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				builder.setTitle("请选择操作")
						.setItems(new String[] { "复制" },
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										ClipboardManager clip = (ClipboardManager) context
												.getSystemService(Context.CLIPBOARD_SERVICE);
										clip.setText(ask.getContent()); // 复制
										ToastUtil.showToast(context,
												"内容已复制到剪切板");
									}
								}).show();
				return true;
			}
		});

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent =  new Intent();
				intent.setClass(context, WenDaDetailActivity.class);
				intent.putExtra("ask", ask);
				context.startActivity(intent);
			}
		});
		return convertView;
	}
	
	static class AllViewHolder {
		ImageView wendaTouxiangImg; // 头像
		TextView wendaNameTv; //名字
		TextView lvTv; //等级
		TextView wendaData;  //时间
		TextView replayNum;  //回复人数
		TextView wendaContent;
		ImageView wendaPictureImg; //问答图片
		TextView shenliuliangTv;
		TextView showAllContentTv;
		
		public AllViewHolder(View view) {
			wendaTouxiangImg = (ImageView) view.findViewById(R.id.wenda_touxiang_img);
			wendaNameTv = (TextView) view.findViewById(R.id.wenda_name_tv);
			lvTv = (TextView) view.findViewById(R.id.tv_user_center_top_level);
			wendaData = (TextView) view.findViewById(R.id.wenda_data);
			replayNum = (TextView) view.findViewById(R.id.replay_num);
			wendaContent = (TextView) view.findViewById(R.id.wenda_content);
			wendaPictureImg = (ImageView)view.findViewById(R.id.wenda_picture_img);
			shenliuliangTv =(TextView) view.findViewById(R.id.shenliuliang_tv);
			showAllContentTv = (TextView) view.findViewById(R.id.show_all_content_tv);
		}
	}
	
	class ImageClickListener implements OnClickListener {
		private Ask ask;

		public ImageClickListener(Ask ask) {
			this.ask = ask;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//点击内容图片或者视屏
			case R.id.wenda_picture_img:
				Intent intent = new Intent(context, PhotoActivity.class);
				intent.putExtra(PhotoActivity.MODE_PICZOMM,ask.getPicurl());
				((Activity) context).startActivity(intent);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 显示全文的监听
	 * */
	class ShowAllContent implements OnClickListener{
		
		Ask ask;
		
		public ShowAllContent(Ask ask) {
			this.ask = ask;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context,WenDaDetailActivity.class);
			intent.putExtra("ask",
					ask);
			context.startActivity(intent);
		}
	}
}
