package com.badou.mworking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.badou.mworking.R;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {

	private ArrayList<String> subList;
	private Context context;
	
	public SearchAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		subList = new ArrayList<String>();
	}
	
	public void setData(ArrayList<String> subList) {
		this.subList = subList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return subList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return subList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View view, ViewGroup arg2) {
		ViewHolder vh = null;
		if (null == view) {
			view = LayoutInflater.from(context).inflate(R.layout.adapter_item_autotext, null);
			vh = new ViewHolder(view);
			view.setTag(vh);
		}else {
			vh = (ViewHolder) view.getTag();
		}
		
		vh.tv.setText(subList.get(arg0));
		
		return view;
	}

	static class ViewHolder{
		TextView tv;
		public ViewHolder(View view) {
			tv = (TextView) view.findViewById(R.id.tv_AutoText);
		}
	}
	

}
