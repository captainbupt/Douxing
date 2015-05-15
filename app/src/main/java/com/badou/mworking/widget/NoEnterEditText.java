package com.badou.mworking.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * 类:  <code> NoEnterEditText </code>
 * 功能描述: 使得多行下仍然可以显示imeOption选项，主要在于重写onCreateInputConnection方法
 * 创建人: 何为舟
 * 创建日期: 2014年7月17日 下午5:19:18
 * 开发环境: JDK7.0
 */
public class NoEnterEditText extends EditText {

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		InputConnection connection = super.onCreateInputConnection(outAttrs);
		if (connection == null)
			return null;
		// 移除EditorInfo.IME_FLAG_NO_ENTER_ACTION标志位
		// &= ~表示二进制位运算
		outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
		return connection;
	}

	public NoEnterEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

}
