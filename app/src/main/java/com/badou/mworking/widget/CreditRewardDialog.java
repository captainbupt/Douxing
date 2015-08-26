package com.badou.mworking.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.badou.mworking.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreditRewardDialog extends Dialog {
    @Bind(R.id.credit_text_view)
    TextView mCreditTextView;

    public CreditRewardDialog(Context context, int credit) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_credit_reward);
        ButterKnife.bind(this);
        mCreditTextView.append(credit + "");
    }

    @OnClick(R.id.confirm_button)
    @Override
    public void dismiss() {
        super.dismiss();
    }
}
