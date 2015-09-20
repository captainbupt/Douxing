package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.PlanInfo;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.category.TaskSignPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.view.category.TaskSignView;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述:  任务签到页面
 */
public class TaskSignActivity extends CategoryBaseActivity implements TaskSignView {

    @Bind(R.id.begin_date_text_view)
    TextView mBeginDateTextView;
    @Bind(R.id.begin_time_text_view)
    TextView mBeginTimeTextView;
    @Bind(R.id.end_date_text_view)
    TextView mEndDateTextView;
    @Bind(R.id.end_time_text_view)
    TextView mEndTimeTextView;
    @Bind(R.id.description_text_view)
    TextView mDescriptionTextView;
    @Bind(R.id.location_text_view)
    TextView mLocationTextView;
    @Bind(R.id.sign_text_view)
    TextView mSignTextView;
    @Bind(R.id.self_position_layout)
    LinearLayout mSelfPositionLayout;
    @Bind(R.id.signed_image_view)
    ImageView mSignedImageView;

    TaskSignPresenter mPresenter;
    ImageChooser mImageChooser;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);

    public static Intent getIntent(Context context, String rid, String planTitle) {
        return CategoryBaseActivity.getIntent(context, TaskSignActivity.class, rid, planTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_sign);
        if (mPlanInfo == null) {
            setActionbarTitle(Category.getCategoryName(mContext, Category.CATEGORY_TASK));
        }
        ButterKnife.bind(this);
        mPresenter = (TaskSignPresenter) super.mPresenter;
        mPresenter.attachView(this);
        initListener();
    }


    @Override
    public Presenter getPresenter() {
        return new TaskSignPresenter(mContext, mReceivedIntent.getStringExtra(KEY_RID), mPlanInfo);
    }

    @OnClick(R.id.sign_text_view)
    void onSignClicked() {
        mPresenter.startSign();
    }

    @OnClick(R.id.self_position_layout)
    void onPositionClicked() {
        mPresenter.toMyPosition();
    }

    @OnClick(R.id.signed_image_view)
    void onImageClicked() {
        mPresenter.showFullImage();
    }

    private void initListener() {
        mImageChooser = new ImageChooser(mContext, false, true, false);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChosen(Bitmap bitmap, int type) {
                mPresenter.onImageChosen(bitmap);
            }
        });
    }

    @Override
    public void setData(String rid, CategoryDetail categoryDetail, PlanInfo planInfo) {
        super.setData(rid, categoryDetail, planInfo);
        initData(categoryDetail);
        initMap(categoryDetail.getTask().getLatitude(), categoryDetail.getTask().getLongitude());
    }

    private void initData(CategoryDetail categoryDetail) {
        String comment = categoryDetail.getTask().getComment();
        if (comment == null || comment.equals("")) {
            mDescriptionTextView.setText(mContext.getResources().getString(R.string.text_null));
        } else {
            mDescriptionTextView.setText(comment);
        }
        long beginTime = categoryDetail.getTask().getStartline();      //任务开始时间
        long endTime = categoryDetail.getTask().getDeadline();      //任务结束时间

        mBeginDateTextView.setText(TimeTransfer.long2StringDateUnit(beginTime));
        mBeginTimeTextView.setText(TimeTransfer.long2StringTimeHour(beginTime) + "至");
        mEndDateTextView.setText(TimeTransfer.long2StringDateUnit(endTime));
        mEndTimeTextView.setText(TimeTransfer.long2StringTimeHour(endTime));

        String place = categoryDetail.getTask().getPlace();
        if (!TextUtils.isEmpty(categoryDetail.getTask().getPlace().replace(" ", ""))) {
            mLocationTextView.setText(place);
        } else {
            mLocationTextView.setText(R.string.sign_in_task_address_empty);
        }
        int status;
        if (categoryDetail.getContent().isSigned()) {
            status = STATUS_SIGN;
        } else if (categoryDetail.getTask().isOffline()) {
            status = STATUS_OFFLINE;
        } else {
            status = STATUS_UNSIGN;
        }
        setStatus(status);
        if (!TextUtils.isEmpty(categoryDetail.getContent().getImgUrl())) {
            mSignedImageView.setVisibility(View.VISIBLE);
            ImageViewLoader.setSquareImageViewResource(mSignedImageView, -1, categoryDetail.getContent().getImgUrl(), DensityUtil.getInstance().getIconSizeXlarge());
        }
    }

    private void initMap(double latitude, double longitude) {
        LatLng location = new LatLng(latitude, longitude);
        MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(location);
        MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(16);
        SupportMapFragment map = (SupportMapFragment) (getSupportFragmentManager()
                .findFragmentById(R.id.map));
        OverlayOptions ooA = new MarkerOptions().position(location).icon(bdA).draggable(false);
        map.getBaiduMap().addOverlay(ooA);
        map.getBaiduMap().animateMapStatus(u1);
        map.getBaiduMap().animateMapStatus(u2);
    }

    private void disableSignButton(boolean isSigned) {
        if (isSigned) {
            mSignTextView.setBackgroundResource(R.drawable.background_button_activity_task_sign_orange);
            mSignTextView.setText(R.string.category_signed);
        } else {
            mSignTextView.setBackgroundResource(R.drawable.background_button_activity_task_sign_disable);
            mSignTextView.setText(R.string.category_expired);
        }
        mSignTextView.setEnabled(false);
    }

    private void enableSignButton() {
        mSignTextView.setBackgroundResource(R.drawable.background_button_activity_task_sign_enable);
        mSignTextView.setText(R.string.sign_task_button_sign);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageChooser.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setStatus(int status) {
        if (status == STATUS_SIGN) {
            disableSignButton(true);
        } else if (status == STATUS_OFFLINE) {
            disableSignButton(false);
        } else {
            enableSignButton();
        }
    }

    @Override
    public void setLocation(BDLocation location) {
        BaiduMap mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map))).getBaiduMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 构造定位数据
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius()).latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        // 设置定位数据
        mBaiduMap.setMyLocationData(locData);
        MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(16);
        mBaiduMap.animateMapStatus(u2);
        mBaiduMap.animateMapStatus(u1);
    }

    @Override
    public void takeImage() {
        mImageChooser.takeImage(null);
    }

    @Override
    public void setSignedImage(Bitmap bitmap) {
        mSignedImageView.setVisibility(View.VISIBLE);
        mSignedImageView.setImageBitmap(bitmap);
    }

    @Override
    public void setCommentNumber(int number) {

    }

    @Override
    public void setRatingNumber(int number) {

    }

    @Override
    public void setRated(boolean rated) {

    }

    @Override
    public void showTimingView() {

    }

    @Override
    public void setMaxPeriod(int minute) {

    }

    @Override
    public void setCurrentPeriod(int currentSecond) {

    }
}
