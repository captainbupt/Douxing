package com.badou.mworking.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.NotFoundException;

import com.badou.mworking.R;

public class WaitProgressDialog extends ProgressDialog {


	public WaitProgressDialog(Context context, String msg) {
		super(context);
		init(context,msg);
		
	}
	
	public WaitProgressDialog(Context context, int resId) {
		super(context);
		try {
			init(context,context.getResources().getString(resId));
		} catch (NotFoundException e) {
			init(context,context.getResources().getString(R.string.message_wait));
			e.printStackTrace();
		}
	}
	
	private void init(Context context, String msg) {
		if (msg.equals("")) {
			setMessage(context.getString(R.string.message_wait));
		}else {
			setMessage(msg);
		}
		setTitle(context.getString(R.string.message_tips));
		setCanceledOnTouchOutside(false);
		setCancelable(false);
	}
}
