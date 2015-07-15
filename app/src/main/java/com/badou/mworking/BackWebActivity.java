package com.badou.mworking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.fragment.WebViewFragment;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.widget.BottomRatingAndCommentView;

import java.io.File;

import butterknife.ButterKnife;

/**
 * 功能描述:  actionbar为返回的网页展示页面
 */
@SuppressLint("SetJavaScriptEnabled")
public class BackWebActivity extends BaseBackActionBarActivity {

    public static final String KEY_URL = "url";
    public static final String KEY_LOGO_URL = "logo";
    WebViewFragment mWebFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initData();
    }

    public static Intent getIntent(Context context, String url, String logoUrl) {
        Intent intent = new Intent(context, BackWebActivity.class);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_LOGO_URL, logoUrl);
        return intent;
    }

    private void initData() {
        String url = mReceivedIntent.getStringExtra(KEY_URL);
        mWebFragment = new WebViewFragment();
        mWebFragment.setArguments(WebViewFragment.getArgument(url));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_container, mWebFragment);
        transaction.commit();
        if (mReceivedIntent.hasExtra(KEY_LOGO_URL)) {
            String logoUrl = mReceivedIntent.getStringExtra(KEY_LOGO_URL);
            bannerDate(logoUrl);
        }
    }

    /**
     * 功能描述: banner 跳转进入
     */
    private void bannerDate(String logoUrl) {
        ImageView logoImage = new ImageView(mContext);
        logoImage.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, getResources().getDimensionPixelOffset(R.dimen.height_title_bar)));
        logoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        int padding = getResources().getDimensionPixelOffset(R.dimen.offset_lless);
        logoImage.setPadding(padding, padding, padding, padding);
        ImageViewLoader.setImageViewResource(logoImage, R.drawable.logo, logoUrl);
        setTitleCustomView(logoImage);
    }
}
