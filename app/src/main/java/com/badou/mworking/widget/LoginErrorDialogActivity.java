package com.badou.mworking.widget;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseFragmentActivity;

public class LoginErrorDialogActivity extends BaseFragmentActivity {
	
	private TextView tipTextView;
	private Button confirmButton;
	public static final String VALUE_TIPS = "tips";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_login_error);
		tipTextView = (TextView) findViewById(R.id.tv_dialog_login_error);
		confirmButton = (Button) findViewById(R.id.btn_dialog_login_error);
		String tips = getIntent().getStringExtra(VALUE_TIPS);
		tipTextView.setText(tips);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	}

}
