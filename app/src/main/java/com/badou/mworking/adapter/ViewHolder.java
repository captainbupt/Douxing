package com.badou.mworking.adapter;

import android.util.SparseArray;
import android.view.View;

public class ViewHolder {
	
	public static <T extends View> T getVH(View view,int id) {
		SparseArray<View> arrView = (SparseArray<View>) view.getTag();
		
		if (null == arrView) {
			arrView = new SparseArray<View>();
			view.setTag(arrView);
		}
		
		View childView = arrView.get(id);
		if (null == childView) {
			childView = view.findViewById(id);
			arrView.put(id, childView);
		}
		return (T) childView;
	}
	
}
