package com.badou.mworking.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.MyBaseAdapter;
import com.badou.mworking.model.Classification;

/**
 * 查找中的更多的界面中右边listview的适配器
 *
 */
public class ClassificationMoreAdapter extends MyBaseAdapter {

	private int mPosition = -1;
	private int mLayoutResId = R.layout.search_more_morelist_item;

	public ClassificationMoreAdapter(Context context) {
		super(context);
	}

	public void setLayoutResId(int layoutResId){
		this.mLayoutResId = layoutResId;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		Holder hold;
		if (arg1 == null) {
			hold = new Holder();
			arg1 = View.inflate(mContext, mLayoutResId, null);
			hold.txt = (TextView) arg1
					.findViewById(R.id.Search_more_moreitem_txt);
			hold.layout = (LinearLayout) arg1
					.findViewById(R.id.More_list_lishi);
			arg1.setTag(hold);
		} else {
			hold = (Holder) arg1.getTag();
		}
		Classification classification = (Classification) getItem(arg0);
		hold.txt.setText(classification.getName());
		hold.layout.setBackgroundResource(R.drawable.my_list_txt_background);
		hold.txt.setTextColor(Color.parseColor("#000000"));
		if (arg0 == mPosition) {
			hold.layout
					.setBackgroundResource(R.drawable.search_more_morelisttop_bkg);
			hold.txt.setTextColor(Color.parseColor("#FFFF8C00"));
		}
		return arg1;
	}

	public void setSelectItem(int i) {
		mPosition = i;
		notifyDataSetChanged();
	}

	private static class Holder {
		LinearLayout layout;
		TextView txt;
	}
}
