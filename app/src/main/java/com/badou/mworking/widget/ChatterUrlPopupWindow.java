package com.badou.mworking.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.badou.mworking.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatterUrlPopupWindow extends PopupWindow {

    @Bind(R.id.share_text_view)
    TextView mShareTextView;
    @Bind(R.id.url_text_view)
    TextView mUrlTextView;
    @Bind(R.id.cancel_image_view)
    ImageView mCancelImageView;
    private View contentView;
    Context mContext;

    public ChatterUrlPopupWindow(final Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_window_share_url, null);
        ButterKnife.bind(this, contentView);
        // 设置SelectPicPopupWindow的View
        setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 刷新状态
        update();
        // 设置SelectPicPopupWindow弹出窗体动画效果
        setAnimationStyle(android.R.style.Animation_Dialog);
    }

    public void setUrl(String url) {
        mUrlTextView.setText(url);
    }

    @OnClick(R.id.share_text_view)
    void share() {
        String url = mUrlTextView.getText().toString();
        dismiss();
    }

    @OnClick(R.id.cancel_image_view)
    public void dismiss() {
        super.dismiss();
    }

    public void showPopupWindow(final View parent) {
        if (!this.isShowing()) {
            contentView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAtLocation(parent, Gravity.BOTTOM, 0, 0);
                }
            }, 200);
        }
    }

}
