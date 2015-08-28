package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.presenter.MultiPhotoPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.MultiPhotoView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;

/**
 * 功能描述: 同事圈图片点击之后法放大查看页面
 */
public class MultiPhotoActivity extends BaseNoTitleActivity implements MultiPhotoView {

    private static final String KEY_URL = "url";
    private static final String KEY_PATH = "path";
    private static final String KEY_POSITION = "position";
    @Bind(R.id.image_view_pager)
    ViewPager mImageViewPager;
    @Bind(R.id.download_image_view)
    ImageView mDownloadImageView;

    Bitmap[] mBitmaps;
    PhotoView[] mPhotoViews;
    PhotoViewPagerAdapter mPhotoViewPagerAdapter;
    MultiPhotoPresenter mPresenter;

    public static Intent getIntentFromWeb(Context context, List<String> imgUrlList, int targetPosition) {
        Intent intent = new Intent(context, MultiPhotoActivity.class);
        intent.putExtra(KEY_URL, imgUrlList.toArray(new String[imgUrlList.size()]));
        intent.putExtra(KEY_POSITION, targetPosition);
        return intent;
    }

    public static Intent getIntentFromLocal(Context context, List<String> localPathList, int targetPosition) {
        Intent intent = new Intent(context, MultiPhotoActivity.class);
        intent.putExtra(KEY_PATH, localPathList.toArray(new String[localPathList.size()]));
        intent.putExtra(KEY_POSITION, targetPosition);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        ButterKnife.bind(this);
        mPresenter = (MultiPhotoPresenter) super.mPresenter;
        mPresenter.attachView(this);
        initialize();
    }

    private void initialize() {
        mImageViewPager.setOffscreenPageLimit(6);
        if (mReceivedIntent.hasExtra(KEY_URL)) {
            String[] urls = mReceivedIntent.getStringArrayExtra(KEY_URL);
            if (urls == null || urls.length == 0) {
                mPresenter.openFail();
                return;
            }
            mBitmaps = new Bitmap[urls.length];
            mPhotoViews = new PhotoView[urls.length];
            for (int ii = 0; ii < urls.length; ii++) {
                mPhotoViews[ii] = new PhotoView(mContext);
                final int finalIi = ii;
                ImageViewLoader.setPhotoView(mPhotoViews[ii], urls[ii], new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                        mBitmaps[finalIi] = imageContainer.getBitmap();
                        mPhotoViews[finalIi].setImageBitmap(imageContainer.getBitmap());
                        mPhotoViews[finalIi].setZoomable(true);
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
            }
            mPhotoViewPagerAdapter = new PhotoViewPagerAdapter(mPhotoViews);
            mImageViewPager.setAdapter(mPhotoViewPagerAdapter);
            mImageViewPager.setCurrentItem(mReceivedIntent.getIntExtra(KEY_POSITION, 0));
        } else if (mReceivedIntent.hasExtra(KEY_PATH)) {
            String[] paths = mReceivedIntent.getStringArrayExtra(KEY_PATH);
            if (paths == null || paths.length == 0) {
                mPresenter.openFail();
                return;
            }
            mBitmaps = new Bitmap[paths.length];
            mPhotoViews = new PhotoView[paths.length];
            for (int ii = 0; ii < paths.length; ii++) {
                mBitmaps[ii] = BitmapUtil.decodeSampledBitmapFromFile(paths[ii], DensityUtil.getInstance().getScreenWidth(), DensityUtil.getInstance().getScreenHeight());
                mPhotoViews[ii] = new PhotoView(mContext);
                mPhotoViews[ii].setImageBitmap(mBitmaps[ii]);
            }
            mPhotoViewPagerAdapter = new PhotoViewPagerAdapter(mPhotoViews);
            mImageViewPager.setAdapter(mPhotoViewPagerAdapter);
            mImageViewPager.setCurrentItem(mReceivedIntent.getIntExtra(KEY_POSITION, 0));
        } else {
            mPresenter.openFail();
        }
    }

    @Override
    public Presenter getPresenter() {
        return new MultiPhotoPresenter(mContext);
    }

    @OnClick(R.id.download_image_view)
    void onDownloadClicked() {
        mPresenter.downloadImage(mBitmaps[mImageViewPager.getCurrentItem()]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    static class PhotoViewPagerAdapter extends PagerAdapter {

        PhotoView[] photoViews;

        public PhotoViewPagerAdapter(PhotoView[] photoViews) {
            this.photoViews = photoViews;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoViews[position], ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoViews[position];
        }

        @Override
        public int getCount() {
            if (photoViews == null)
                return 0;
            return photoViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(photoViews[position]);
        }
    }

}
