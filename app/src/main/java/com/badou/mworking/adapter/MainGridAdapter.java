package com.badou.mworking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.model.Exam;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.model.Notice;
import com.badou.mworking.model.Task;
import com.badou.mworking.model.Train;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.util.SP;

import java.util.List;

/**
 * 类:  <code> MainGridAdapter </code>
 * 功能描述:  主页面adapter
 * 创建人:  葛建锋
 * 创建日期: 2014年7月18日 上午9:39:08
 * 开发环境: JDK7.0
 */
public class MainGridAdapter extends BaseAdapter {

	private Context mContext;
    private List<MainIcon> mainIcons;
	
	public MainGridAdapter(Context mContext, int access,List<MainIcon> mainIcons) {
		super();
		this.mContext = mContext;
		this.mainIcons= mainIcons;
		access(access,this.mainIcons);
	}
	
	@Override
	public int getCount() {
		return mainIcons.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mainIcons.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.adapter_main_grid, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		holder.imageView.setTag(mainIcons.get(position).getMainIconId());
		holder.imageView.setImageResource(R.drawable.icon_default);
		String mainIconUrl = mainIcons.get(position).getUrl();
		try {
			int mainIconId = Integer.valueOf(mainIconUrl);
			holder.imageView.setImageDrawable(mContext.getResources().getDrawable(mainIconId));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		holder.textView.setText(mainIcons.get(position).getName()+"");
		setIconUnreadNum(holder.tvUnreadNum, (String)holder.imageView.getTag());
		return convertView;
	}

	static class ViewHolder {
		ImageView imageView;
		TextView textView;
		TextView tvUnreadNum;
		public ViewHolder(View view) {
			imageView = (ImageView) view
					.findViewById(R.id.iv_adapter_main_grid);
			textView = (TextView) view
					.findViewById(R.id.tv_adapter_main_name);
			tvUnreadNum = (TextView) view.findViewById(R.id.tv_main_item_unreadNum);
		}
	}
	
	/**
	 * 显示未读数量
	 * @param tv 要显示的控件
	 * @param tag tag名称
	 */
	private void setIconUnreadNum(TextView tv, String tag){
		int num = 0;
		// 为了解决换帐号登录的问题
		String userNum = ((AppApplication) mContext.getApplicationContext())
				.getUserInfo().getUserNumber();
		if (RequestParams.CHK_UPDATA_PIC_NOTICE.equals(tag)) {//通知公告
			num = SP.getIntSP(mContext, SP.DEFAULTCACHE,userNum+Notice.UNREAD_NUM_NOTICE, 0);
		}else if (RequestParams.CHK_UPDATA_PIC_TRAIN.equals(tag)) {//微培训
			num = SP.getIntSP(mContext, SP.DEFAULTCACHE,userNum+Train.UNREAD_NUM_TRAIN, 0);
		}else if (RequestParams.CHK_UPDATA_PIC_EXAM.equals(tag)) {//在线考试
			num = SP.getIntSP(mContext,SP.DEFAULTCACHE, userNum+Exam.UNREAD_NUM_EXAM, 0);
		}else if (RequestParams.CHK_UPDATA_PIC_TASK.equals(tag)) {//任务签到
			num = SP.getIntSP(mContext, SP.DEFAULTCACHE,userNum+Task.UNREAD_NUM_TASK, 0);
		}
		//主页没有聊天模块，默认这个数量就没有了
//		else if (RequestParams.CHK_UPDATA_PIC_CHAT.equals(tag)) { //聊天
//			num = SP.getIntSP(mContext, userNum+ChattingFragment.KEY_SP_UNREAD_NUM, 0);
//		}
		if (num==0) {
			tv.setVisibility(View.GONE);
		} else {
			tv.setVisibility(View.VISIBLE);
			tv.setText(num+"");
			// 如果是两位数的话，换一个背景
			if (num>9) {
				tv.setBackgroundResource(R.drawable.icon_chat_unread_long);
			} else {
				tv.setBackgroundResource(R.drawable.icon_chat_unread);
			}
			
		}
	}
	
	/** 
	 * 功能描述: 权限， 设置隐藏显示
	 * @param access 后台返回的十进制权限制
	 */
	public void access(int access,List<MainIcon> mainIcons){
		//access == 255全显示
		if(access == 255){
			return;
		}
		char[] accessArrayTemp = Integer.toBinaryString(access).toCharArray();
		char[] accessArray = {'0','0','0','0','0','0','0','0'};
		int length = accessArrayTemp.length-1;
		for(int i = 0;i<=length;i++){
			accessArray[i] = accessArrayTemp[length-i];
		}
		if('0' == accessArray[0]){
			getMainiconID(RequestParams.CHK_UPDATA_PIC_NOTICE);
		}
		if('0' == accessArray[1]){
			getMainiconID(RequestParams.CHK_UPDATA_PIC_TRAIN);
		}
		if('0' == accessArray[2]){
			getMainiconID(RequestParams.CHK_UPDATA_PIC_EXAM);
		}
		if('0' == accessArray[3]){
			getMainiconID(RequestParams.CHK_UPDATA_PIC_TASK);
		}
		if('0' == accessArray[4]){
			getMainiconID(RequestParams.CHK_UPDATA_PIC_SURVEY);
		}
		if('0' == accessArray[5]){
			getMainiconID(RequestParams.CHK_UPDATA_PIC_CHATTER);
		}
//		if('0' == accessArray[6]){
//			getMainiconID(RequestParams.CHK_UPDATA_PIC_CHAT);
//		}
		if('0' == accessArray[7]){
			getMainiconID(RequestParams.CHK_UPDATA_PIC_ASK);
		}
	}
	
	public void getMainiconID(String mainIconsID){
		for(int i = 0;i<mainIcons.size();i++){
			if(mainIconsID.equals(mainIcons.get(i).getMainIconId())){
				mainIcons.remove(i);
			}
		}
	}
}
