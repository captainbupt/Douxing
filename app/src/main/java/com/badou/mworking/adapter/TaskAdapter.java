package com.badou.mworking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.model.Task;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;

import java.util.ArrayList;


public class TaskAdapter extends BaseAdapter {

	private ArrayList<Task> mData;
	private Context mContext;
	private LayoutInflater mInflater;

	public TaskAdapter(Context mContext, ArrayList<Task> mData) {
		this.mContext = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.mData = mData == null ? new ArrayList<Task>() : mData;
	}

	/**
	 * 功能描述: 重新设置list
	 * @param mData
	 */
	public void setDatas(ArrayList<Task> mData) {
		this.mData = mData == null ? new ArrayList<Task>() : mData;
		notifyDataSetChanged();
	}
	/**
	 * 
	 * 功能描述:添加上拉新加载的 list
	 * @param mData
	 */
	public void addData(ArrayList<Task> mData) {
		if (null!=mData) {
			this.mData.addAll(mData);
		} 
		notifyDataSetChanged();
	}
	
	/**
	 * 功能描述:替换一个item
	 * @param position
	 * @param task
	 */
	public void changeItem(int position,Task task){
		if (task!=null) {
			//替换指定元素
			this.mData.set(position, task);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.mData.size();
	}

	@Override
	public Task getItem(int position) {
		return this.mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_list_tasks, parent,
					false);
		} 
		Task task = this.mData.get(position);
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
		if (task.getPlace() != null && !task.getPlace().equals("")) {
			address.setText(task.getPlace()+"");
		} else {
			address.setText(""); 
		}
		subject.setText(task.getSubject()+"");
		publishTime.setText( ""+ TimeTransfer.long2StringDetailDate(mContext,task.getTime()));
		if (task.getTop()== Constant.TOP_YES) {
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
	public void read(int position) {
		if (mData.get(position).getRead() == Constant.FINISH_NO) {
			mData.get(position).setRead(Constant.FINISH_YES);
			this.notifyDataSetChanged();
			int unreadNum = SP.getIntSP(mContext,SP.DEFAULTCACHE,Task.UNREAD_NUM_TASK, 0);
			if (unreadNum > 0 ) {
				SP.putIntSP(mContext,SP.DEFAULTCACHE, Task.UNREAD_NUM_TASK, unreadNum - 1);
			}
		}
	}

}
