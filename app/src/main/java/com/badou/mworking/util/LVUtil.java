package com.badou.mworking.util;

import android.widget.TextView;

import com.badou.mworking.R;

/**
 * @author gejianfeng
 * 等级页面
 */
public class LVUtil {
	
	/**
   	 * 2014-12-04  为等级添加颜色
   	 * 红色17-20级#de0704  
   	 * 黄色13-16级#eaa718 
   	 * 紫色9-12级b830f2 
   	 * 蓝色5-8级#293fe3 
   	 * 绿色1-4级#25cf65*/ 
	public static void setTextViewBg(TextView tv,int lv){
		if(tv == null){
			return;
		}
		int bg = 0;
		if(lv<=4){
			bg = R.drawable.background_lv_fir;
		}else if(lv<=8){
			bg = R.drawable.background_lv_sec;
		}else if(lv<=12){
			bg = R.drawable.background_lv_thi;
		}else if(lv<=16){
			bg = R.drawable.background_lv_fou;
		}else if(lv<=20){
			bg = R.drawable.background_lv_fif;
		}
		tv.setText("LV "+lv);
		tv.setBackgroundResource(bg);
	}
}
