package com.badou.mworking;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.badou.mworking.adapter.BannerAdapter;
import com.badou.mworking.adapter.MainGridAdapter;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.main.MainBanner;
import com.badou.mworking.entity.main.MainIcon;
import com.badou.mworking.fragment.MainGuideFragment;
import com.badou.mworking.fragment.MainSearchFragment;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.presenter.MainPresenter;
import com.badou.mworking.view.MainGridView;
import com.badou.mworking.widget.BannerGallery;
import com.badou.mworking.widget.LineGridView;
import com.badou.mworking.widget.TopFadeScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

/**
 * 功能描述: 主页面
 */
public class MainGridActivity extends BaseNoTitleActivity implements MainGridView {

    public static final String KEY_MESSAGE_CENTER = "messagecenter";

    @Bind(R.id.user_center_image_view)
    ImageView mUserCenterImageView;
    @Bind(R.id.logo_image_view)
    ImageView mLogoImageView;
    @Bind(R.id.message_center_image_view)
    ImageView mMessageCenterImageView;
    @Bind(R.id.search_image_view)
    ImageView mSearchImageView;
    @Bind(R.id.banner_gallery)
    BannerGallery mBannerGallery;
    @Bind(R.id.banner_indicator)
    RadioGroup mBannerIndicator;
    @Bind(R.id.content_grid_view)
    LineGridView mContentGridView;
    @Bind(R.id.top_fade_scroll_view)
    TopFadeScrollView mTopFadeScrollView;

    private MainGridAdapter mMainGridAdapter;

    // 保存banner的adapter
    private BannerAdapter mBannerAdapter;
    private List<RadioButton> mIndicatorRadioButtonList;

    private MainSearchFragment mMainSearchFragment;

    private MainPresenter mMainPresenter;

    public static Intent getIntent(Context context, boolean toMessageCenter) {
        Intent intent = new Intent(context, MainGridActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_MESSAGE_CENTER, toMessageCenter);
        return new Intent(context, MainGridActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mReceivedIntent.getBooleanExtra(MainGridActivity.KEY_MESSAGE_CENTER, false)) {
            mContext.startActivity(new Intent(mContext, MessageCenterActivity.class));
        }
        setContentView(R.layout.activity_main_grid);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        mTopFadeScrollView.setTopViewId(R.id.banner_container);
        disableSwipeBack();
        mBannerAdapter = new BannerAdapter(mContext);
        mBannerGallery.setAdapter(mBannerAdapter);
        mMainGridAdapter = new MainGridAdapter(mContext);
        mContentGridView.setAdapter(mMainGridAdapter);
        mMainPresenter = new MainPresenter(mContext);
        mMainPresenter.attachView(this);
    }


    @Override
    public void setLogoImage(String url) {
        ImageViewLoader.setImageViewResource(mLogoImageView, R.drawable.logo, url);
    }

    @Override
    public void setBannerData(List<MainBanner> bannerList) {
        updateIndicator(bannerList);
        updateBanner(bannerList);
    }

    @Override
    public void showExperienceDialog() {
        new AlertDialog.Builder(mContext).setTitle(R.string.tip_anonymous_title).setMessage(R.string.tip_anonymous_content).setPositiveButton(R.string.tip_anonymous_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(LoginActivity.getIntent(mContext));
                mActivity.finish();
            }
        }).setNegativeButton(R.string.tip_anonymous_cancel, null).show();
    }

    @Override
    public void showGuideFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.search_container, new MainGuideFragment()).commit();
    }

    @Override
    public void setMessageCenterStatus(boolean isUnread) {
        if (isUnread) {
            mMessageCenterImageView.setImageResource(R.drawable.button_title_main_message_checked);
        } else {
            mMessageCenterImageView.setImageResource(R.drawable.button_title_main_message_unchecked);
        }
    }

    @Override
    public void showSearchFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.show(getSearchFragment());
        transaction.commit();
    }

    @Override
    public void hideSearchFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(getSearchFragment());
        transaction.commit();
    }

    @Override
    public void setMainIconData(List<MainIcon> mainIconList) {
        mMainGridAdapter.setList(mainIconList);
    }

    @Override
    public void setIndicator(int index) {
        if (mBannerIndicator.getChildCount() > 0) {
            index = index % mBannerIndicator.getChildCount();
            if (mIndicatorRadioButtonList != null && index < mIndicatorRadioButtonList.size())
                mIndicatorRadioButtonList.get(index).setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMainPresenter.resume();
    }

    @Override
    public MainSearchFragment getSearchFragment() {
        if (mMainSearchFragment == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mMainSearchFragment = new MainSearchFragment();
            transaction.replace(R.id.search_container, mMainSearchFragment);
            transaction.hide(mMainSearchFragment);
            transaction.commit();
        }
        return mMainSearchFragment;
    }

    @OnItemClick(R.id.content_grid_view)
    void onCategoryClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        mMainPresenter.onItemClick(mMainGridAdapter.getItem(arg2));
    }

    @OnClick(R.id.user_center_image_view)
    void onUserCenterClick() {
        mMainPresenter.onUserCenterClick();
    }

    @OnClick(R.id.search_image_view)
    void onSearchClick() {
        mMainPresenter.onSearchClick();
    }

    @OnClick(R.id.message_center_image_view)
    void onMessageCenterClick() {
        mMainPresenter.onMessageCenterClick();
    }


    @OnItemClick(R.id.banner_gallery)
    void onBannerClick(AdapterView<?> parent, View view, int position, long id) {
        mMainPresenter.onBannerClick(parent, view, position, id);
    }

    @OnItemSelected(R.id.banner_gallery)
    void onBannerSelected(AdapterView<?> parent, View view, int selIndex, long id) {
        mMainPresenter.onBannerSelected(parent, view, selIndex, id);
    }


    /**
     * 功能描述: 更新显示的banner
     */
    private void updateBanner(final List<MainBanner> bList) {
        mBannerAdapter.setList(bList);
        mBannerGallery.setFocusable(true);
    }

    /**
     * 功能描述: 定义底部滑动的小点
     */
    private void updateIndicator(List<MainBanner> bannerList) {
        this.mBannerIndicator.removeAllViews();
        if (bannerList == null || bannerList.size() <= 0) {
            return;
        }
        int size = getResources().getDimensionPixelSize(R.dimen.icon_size_main_grid_rb);
        mIndicatorRadioButtonList = new ArrayList<>();
        for (int i = 0; i < bannerList.size(); i++) {
            RadioButton radioButton = new RadioButton(mContext);
            radioButton.setId(i);
            RadioGroup.LayoutParams localLayoutParams = new RadioGroup.LayoutParams(
                    size, size);
            localLayoutParams.setMargins(size / 2, 0, size / 2, 0);
            radioButton.setLayoutParams(localLayoutParams);
            radioButton.setButtonDrawable(android.R.color.transparent);
            radioButton.setBackgroundResource(R.drawable.background_rb_welcome);
            mIndicatorRadioButtonList.add(radioButton);
            this.mBannerIndicator.addView(radioButton);
        }
    }

    /**
     * 功能描述: 点击两次返回键退出应用程序，通过记录按键时间计算时间差实现
     */
    @Override
    public void onBackPressed() {
        if (mMainSearchFragment == null || !mMainSearchFragment.getPresenter().onBackPressed())
            mMainPresenter.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMainPresenter.destroy();
    }

}
