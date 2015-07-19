package com.badou.mworking.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.R;
import com.badou.mworking.base.BaseActionBarActivity;
import com.captainhwz.layout.HeaderHandler;
import com.nineoldandroids.view.ViewHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CategoryHeader extends RelativeLayout implements HeaderHandler {
    @Bind(R.id.action_bar_background_view)
    View mActionBarBackgroundView;
    @Bind(R.id.back_image_view)
    ImageView mBackImageView;
    @Bind(R.id.right_button_container)
    LinearLayout mRightButtonContainer;
    @Bind(R.id.action_bar_container)
    RelativeLayout mActionBarContainer;
    @Bind(R.id.title_text_view)
    TextView mTitleTextView;

    Context mContext;
    final int leftOffset;

    public CategoryHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_category_header, this, true);
        ButterKnife.bind(this, this);
        ViewHelper.setAlpha(mActionBarBackgroundView, 0);
        leftOffset = context.getResources().getDimensionPixelOffset(R.dimen.width_title_bar);
    }

    @Override
    public void onChange(float ratio, float offsetY) {
        ViewHelper.setAlpha(mActionBarBackgroundView, (1 - ratio));
        ViewHelper.setTranslationX(mTitleTextView, (float) leftOffset * (1 - ratio));
        ViewHelper.setTranslationY(mActionBarContainer, ViewHelper.getTranslationY(mActionBarContainer) - offsetY);
    }

    public void addRightImage(ImageView imageView) {
        mRightButtonContainer.addView(imageView);
    }

    public void setLeftClick(OnClickListener onClickListener) {
        mBackImageView.setOnClickListener(onClickListener);
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }
}
