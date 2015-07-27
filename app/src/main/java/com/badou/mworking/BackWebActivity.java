package com.badou.mworking;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.DensityUtil;

/**
 * 功能描述:  actionbar为返回的网页展示页面
 */
@SuppressLint("SetJavaScriptEnabled")
public class BackWebActivity extends BaseBackActionBarActivity {

    private static final String KEY_URL = "url";
    private static final String KEY_LOGO_URL = "logo";
    private static final String KEY_TITLE = "title";
    WebViewFragment mWebFragment;

    public static Intent getIntent(Context context, String title, String url) {
        Intent intent = new Intent(context, BackWebActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        return intent;
    }

    public static Intent getIntentBanner(Context context, String url, String logoUrl) {
        Intent intent = new Intent(context, BackWebActivity.class);
        intent.putExtra(KEY_LOGO_URL, logoUrl);
        intent.putExtra(KEY_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initData();
    }

    private void initData() {
        String url = mReceivedIntent.getStringExtra(KEY_URL);
        mWebFragment = (WebViewFragment) WebViewFragment.getFragment(url);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, mWebFragment);
        transaction.commit();
        if (mReceivedIntent.hasExtra(KEY_LOGO_URL)) {
            String logoUrl = mReceivedIntent.getStringExtra(KEY_LOGO_URL);
            bannerDate(logoUrl);
        } else {
            setActionbarTitle(mReceivedIntent.getStringExtra(KEY_TITLE));
        }
    }

    /**
     * 功能描述: banner 跳转进入
     */
    private void bannerDate(String logoUrl) {
        ImageView logoImage = new ImageView(mContext);
        logoImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        logoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int padding = DensityUtil.getInstance().getOffsetLless();
        logoImage.setPadding(0, padding, 0, padding);
        ImageViewLoader.setImageViewResource(logoImage, R.drawable.logo, logoUrl);
        ViewGroup.LayoutParams lp = mTitleContainerLayout.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mTitleContainerLayout.setLayoutParams(lp);
        setTitleCustomView(logoImage);
    }
}
