package com.badou.mworking.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseNoTitleActivity;

import org.holoeverywhere.app.Dialog;

public class LoginErrorDialog extends Dialog {

    private TextView tipTextView;
    private Button confirmButton;


    public LoginErrorDialog(Context context, String tips) {
        super(context);
        initView(tips);
    }

    private void initView(String tips) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_login_error);
        tipTextView = (TextView) findViewById(R.id.tv_dialog_login_error);
        confirmButton = (Button) findViewById(R.id.btn_dialog_login_error);
        tipTextView.setText(tips);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
