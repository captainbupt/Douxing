package com.badou.mworking.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.util.DensityUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatterUrlTipPopupWindow extends PopupWindow {

    @Bind(R.id.confirm_text_view)
    TextView mConfirmTextView;

    public ChatterUrlTipPopupWindow(Context context) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.popup_window_share_url_tip, null);
        ButterKnife.bind(this, contentView);
        setContentView(contentView);
        setWidth(DensityUtil.getInstance().getScreenWidth() - 2 * DensityUtil.getInstance().getOffsetXlarge());
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 刷新状态
        update();
        // 设置SelectPicPopupWindow弹出窗体动画效果
        setAnimationStyle(android.R.style.Animation_Dialog);
    }

    @OnClick(R.id.confirm_text_view)
    void onConfirm() {
        dismiss();
    }
}
