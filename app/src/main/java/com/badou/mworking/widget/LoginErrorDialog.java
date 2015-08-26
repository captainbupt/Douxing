package com.badou.mworking.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.badou.mworking.R;

public class LoginErrorDialog extends Dialog {


    public LoginErrorDialog(Context context, String tips) {
        super(context);
        initView(tips);
    }

    private void initView(String tips) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login_error);
        TextView tipTextView = (TextView) findViewById(R.id.tv_dialog_login_error);
        Button confirmButton = (Button) findViewById(R.id.btn_dialog_login_error);
        tipTextView.setText(tips);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
