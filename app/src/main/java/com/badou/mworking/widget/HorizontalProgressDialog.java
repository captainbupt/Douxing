package com.badou.mworking.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;

import com.badou.mworking.R;

/**
 * Created by Administrator on 2015/5/19.
 * <p/>
 * 功能描述:初始化AlertDialog的布局 初始化progerssBar
 */
public class HorizontalProgressDialog extends Dialog {

    ProgressBar progressBar;
    private Context mContext;

    public HorizontalProgressDialog(Context context) {
        super(context);
        this.mContext = context;
        // 对话框加载布局文件
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.load_progerss_layout);
        setCanceledOnTouchOutside(false);
        progressBar = (ProgressBar) findViewById(R.id.load_progressBar);
        progressBar.setProgress(0);
    }

    public void setProgress(int progress) {
        progressBar.setProgress(progress);
    }

    public void setProgressMax(int max) {
        progressBar.setMax(max);
    }

    public int getProgressMax() {
        return progressBar.getMax();
    }

    @Override
    public void show() {
        if (!((Activity) mContext).isFinishing())
            super.show();
    }

    @Override
    public void dismiss() {
        if (!((Activity) mContext).isFinishing())
            super.dismiss();
    }
}
