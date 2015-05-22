package com.badou.mworking.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Task;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;

import java.util.ArrayList;


public class TaskAdapter extends MyBaseAdapter {


	public TaskAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_list_tasks, parent,
					false);
		} 
		Task task = (Task) getItem(position);
		// 一定要保证else if 语句的顺序，应为在这一块，优先级别  已签到>已过期>未签到   然后 因为未过期  可能已经签过到了，
		//也可能没有，  如果已经签过到了，显示已签到，如果没有，才显示已过期，所以要注意else if语句的顺序
		
		TextView subject = ViewHolder.getVH(convertView, R.id.tv_adapter_base_item_subject);
		TextView publishTime = ViewHolder.getVH(convertView, R.id.tv_adapter_item_dpt_date);
		RelativeLayout rl_bg = ViewHolder.getVH(convertView, R.id.rl_item_bg_isread);
		TextView tvFinish = ViewHolder.getVH(convertView, R.id.tv_unFinish);
		TextView address = ViewHolder.getVH(convertView, R.id.tv_adapter_item_address);
		ImageView top = ViewHolder.getVH(convertView, R.id.tv_adapter_base_item_top);
		
		// 先判断read字段， 已签到
		if (task.isFinish()){
			rl_bg.setBackgroundResource(R.drawable.icon_read_);
			tvFinish.setTextColor(mContext.getResources().getColor(R.color.color_grey));
			tvFinish.setText(mContext.getResources().getString(R.string.task_isFinish));
		} else {
			//判断 offline字段， 已过期
			if(task.isOverdue()){
				rl_bg.setBackgroundResource(R.drawable.icon_read_);
				tvFinish.setTextColor(mContext.getResources().getColor(R.color.color_grey));
				tvFinish.setText(mContext.getResources().getString(R.string.isDeadtime));
			// 未签到	
			}else{
				rl_bg.setBackgroundResource(R.drawable.icon_unread_orange);
				tvFinish.setTextColor(mContext.getResources().getColor(R.color.color_black));
				tvFinish.setText(mContext.getResources().getString(R.string.task_nottask));
			}
		}	
		if (TextUtils.isEmpty(task.place)) {
			address.setText(task.place+"");
		} else {
			address.setText(""); 
		}
		subject.setText(task.subject+"");
		publishTime.setText( ""+ TimeTransfer.long2StringDetailDate(mContext,task.time));
		if (task.top== Constant.TOP_YES) {
			top.setVisibility(View.VISIBLE);
		} else {
			top.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	/**
	 * 功能描述:设置已读
	 * @param position
	 */
	public void setRead(int position) {
		Task task = (Task) getItem(position);
		if (task.read == Constant.FINISH_NO) {
			task.read = Constant.FINISH_YES;
			this.notifyDataSetChanged();
			int unreadNum = SP.getIntSP(mContext,SP.DEFAULTCACHE,Task.CATEGORY_KEY_UNREAD_NUM, 0);
			if (unreadNum > 0 ) {
				SP.putIntSP(mContext,SP.DEFAULTCACHE, Task.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
			}
		}
	}

}
