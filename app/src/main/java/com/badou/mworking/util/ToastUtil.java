package com.badou.mworking.util;

import android.content.Context;
import android.widget.Toast;

import com.badou.mworking.R;

/**
 * 类:  <code> ToastUtil </code>
 * 功能描述: Toast 工具类
 * 创建人:  葛建锋
 * 创建日期: 2014年7月15日 下午4:08:25
 * 开发环境: JDK7.0
 */
public class ToastUtil {

    /**
     * 功能描述: 用于直接输入文字的方法
     * @param context   上下文对象  
     * @param message   需要提醒的文字
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 功能描述:  用于输入String字符串id的方法
     * @param context   上下文对象
     * @param message   需要提醒的String字符串id
     */
    public static void showToast(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 功能描述:  网络异常提醒方法
     * @param context
     */
    public static void showNetExc(Context context){
        ToastUtil.showToast(context, R.string.error_service);
    }
    
    public static void showUpdateToast(Context context){
    	showToast(context,R.string.no_more);
    }

}
