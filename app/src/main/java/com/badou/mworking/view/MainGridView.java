package com.badou.mworking.view;

import com.badou.mworking.entity.main.MainBanner;
import com.badou.mworking.fragment.MainSearchFragment;

import java.util.List;

public interface MainGridView extends BaseView {
    void showExperienceDialog();

    void setMessageCenterStatus(boolean isUnread);

    void showSearchFragment();

    void hideSearchFragment();

    void setLogoImage(String url);

    void setBannerData(List<MainBanner> bannerList);

    void updateUnreadNumber();

    void setIndicator(int index);

    MainSearchFragment getSearchFragment();
}
