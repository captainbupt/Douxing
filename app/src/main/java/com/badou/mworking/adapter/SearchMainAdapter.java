package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.model.Classification;

import java.util.ArrayList;

/**
 * @author gejianfeng
 * 查找中的更多的界面中左边listview的适配器
 */
public class SearchMainAdapter extends BaseAdapter {

	private Context ctx;
	private ArrayList<Classification> list;
	private int position = 0;
	private int layout = R.layout.search_more_mainlist_item;

	public SearchMainAdapter(Context ctx, ArrayList<Classification> list) {
		this.ctx = ctx;
		this.list = list;
	}

	public SearchMainAdapter(Context ctx, ArrayList<Classification> list,
			int layout) {
		this.ctx = ctx;
		this.list = list;
		this.layout = layout;
	}

	public int getCount() {
		return list.size();
	}

	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	public long getItemId(int arg0) {
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Holder hold;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(ctx, layout, null);
			hold.txt = (TextView) arg1
					.findViewById(R.id.Search_more_mainitem_txt);
			hold.layout = (LinearLayout) arg1
					.findViewById(R.id.Search_more_mainitem_layout);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		hold.txt.setText(list.get(arg0).getName());
		hold.layout
				.setBackgroundResource(R.drawable.search_more_mainlistselect);
		hold.txt.setTextColor(Color.parseColor("#000000"));
		if (arg0 == position) {
			hold.layout.setBackgroundResource(R.drawable.list_bkg_line_u);
			if(list.get(arg0).getClassifications() == null || list.get(arg0).getClassifications().size()==0){
				hold.txt.setTextColor(Color.parseColor("#FFFF8C00"));
			}
		}
			
		return arg1;
	}

	public void setSelectItem(int i) {
		position = i;
	}

	public int getSelectItem() {
		return position;
	}
	
	private static class Holder {
		LinearLayout layout;
		TextView txt;
	}

}
