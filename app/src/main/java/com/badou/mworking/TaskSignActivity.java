package com.badou.mworking;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseStatisticalActionBarActivity;
import com.badou.mworking.model.category.Task;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONObject;

/**
 * 功能描述:  任务签到页面
 */
public class TaskSignActivity extends BaseStatisticalActionBarActivity implements BDLocationListener {

    public static final String KEY_TASK = "task";

    private TextView mBeginDateTextView;
    private TextView mBeginTimeTextView;
    private TextView mEndDateTextView;
    private TextView mEndTimeTextView;
    private TextView mDescriptionTextView;
    private TextView mLocationTextView;
    private TextView mSignTextView;
    private LinearLayout mSelfPositionLayout;


    private Task task;
    private Bitmap photo = null;
    public LocationClient mLocationClient;
    private boolean isSign = false; //是否签到， 否表示显示我的位置
    private boolean isFirst = true;
    private String locationStr = "";

    private ImageChooser mImageChooser;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_sign);
        initView();
        initListener();
        initData();
        initlocation();
        initMap();
    }

    protected void initView() {
        mBeginDateTextView = (TextView) findViewById(R.id.tv_activity_task_sign_date_begin);
        mBeginTimeTextView = (TextView) findViewById(R.id.tv_activity_task_sign_time_begin);
        mEndDateTextView = (TextView) findViewById(R.id.tv_activity_task_sign_date_end);
        mEndTimeTextView = (TextView) findViewById(R.id.tv_activity_task_sign_time_end);
        mDescriptionTextView = (TextView) findViewById(R.id.tv_activity_task_sign_description);
        mLocationTextView = (TextView) findViewById(R.id.tv_activity_task_sign_location);
        mSignTextView = (TextView) findViewById(R.id.tv_activity_task_sign_bottom);
        mSelfPositionLayout = (LinearLayout) findViewById(R.id.ll_activity_task_sign_my_position);
    }

    private void initListener() {
        mSignTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //无网络状态下不允许点击
                if (!NetUtils.isNetConnected(mContext)) {
                    ToastUtil.showNetExc(mContext);
                    return;
                }
                long timeNow = System.currentTimeMillis();
                if (task.startline > timeNow) {
                    ToastUtil.showToast(mContext, R.string.task_notStart);
                    return;
                }
                if (task.photo == 1) {
                    mImageChooser.takeImage(null);
                } else {
                    mProgressDialog.show();
                    startLocation(true);
                }
            }
        });
        mImageChooser = new ImageChooser(mContext, false, true, false);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChose(Bitmap bitmap, int type) {
                if (bitmap != null) {
                    photo = bitmap;
                    mProgressDialog.show();
                    startLocation(true);
                }
            }
        });
        mSelfPositionLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirst = false;
                startLocation(false);
            }
        });
    }

    private void startLocation(boolean isSign) {
        this.isSign = isSign;
/*        if (mLocationClient.isStarted())
            mLocationClient.stop();*/
        mLocationClient.start();
    }

    private void initData() {
        task = (Task) mReceivedIntent.getSerializableExtra(KEY_TASK);
        boolean finish = task.isRead();
        String comment = task.comment;
        if (comment == null || comment.equals("")) {
            mDescriptionTextView.setText(mContext.getResources().getString(R.string.text_null));
        } else {
            mDescriptionTextView.setText(comment);
        }
        long beginTime = task.startline;      //任务开始时间
        long endTime = task.deadline;      //任务结束时间

        mBeginDateTextView.setText(TimeTransfer.long2StringDateUnit(beginTime));
        mBeginTimeTextView.setText(TimeTransfer.long2StringTimeHour(beginTime) + "至");
        mEndDateTextView.setText(TimeTransfer.long2StringDateUnit(endTime));
        mEndTimeTextView.setText(TimeTransfer.long2StringTimeHour(endTime));

        mProgressDialog.setContent(R.string.sign_action_sign_ing);

        if (finish) {// 已签到
            disableSignButton(true);
        } else {
            if (task.isOffline()) { // 已过期
                disableSignButton(false);
                task.read = Constant.FINISH_YES;
            } else { // 未签到
                enableSignButton();
            }
        }

        String place = task.place;
        if (!TextUtils.isEmpty(task.place) && !" ".equals(task.place)) {
            mLocationTextView.setText(place);
        } else {
            mLocationTextView.setText(R.string.sign_in_task_address_empty);
        }
    }

    /**
     * 功能描述:  初始化定位数据
     */
    private void initlocation() {
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(this);
        // 定位初始化
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
    }

    private void initMap() {
        startLocation(false);
        if (!task.isFreeSign()) {
            LatLng location = new LatLng(task.latitude, task.longitude);
            MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(location);
            MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(16);
            SupportMapFragment map = (SupportMapFragment) (getSupportFragmentManager()
                    .findFragmentById(R.id.map));
            OverlayOptions ooA = new MarkerOptions().position(location).icon(bdA).draggable(false);
            map.getBaiduMap().addOverlay(ooA);
            map.getBaiduMap().animateMapStatus(u1);
            map.getBaiduMap().animateMapStatus(u2);
        }
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
    public void onReceiveLocation(BDLocation location) {
        mLocationClient.stop();
        if (location == null || String.valueOf(location.getLatitude()).equals(4.9E-324) || String.valueOf(location.getLongitude()).equals(4.9E-324)) {
            ToastUtil.showToast(TaskSignActivity.this, R.string.task_get_gps_fail);
            if (!mActivity.isFinishing()) {
                mProgressDialog.dismiss();
            }
            return;
        }
        if (!isSign) {
            BaiduMap mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
                    .findFragmentById(R.id.map))).getBaiduMap();
            // 开启定位图层  
            mBaiduMap.setMyLocationEnabled(true);
            // 构造定位数据  
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            // 设置定位数据  
            mBaiduMap.setMyLocationData(locData);
            if (!(isFirst && !task.isFreeSign())) { // 首次进入且有目标地点的时候，不需要跳转
                MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(16);
                mBaiduMap.animateMapStatus(u2);
                mBaiduMap.animateMapStatus(u1);
            }
        } else {
            isSign = false;
            uploadImage(location);
        }
    }

    private void uploadImage(final BDLocation location) {
        if (location == null) {
            return;
        }
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        String uid = ((AppApplication) getApplicationContext())
                .getUserInfo().userId;
        ServiceProvider.doUpdateBitmap(mContext, photo,
                Net.getRunHost(mContext) + Net.SIGN(task.rid, uid, lat, lon),
                new VolleyListener(mContext) {

                    @Override
                    public void onCompleted() {
                        if (!mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        if (photo != null && photo.isRecycled()) {
                            photo.recycle();
                        }
                        // 签到成功
                        task.read = Constant.FINISH_YES;
                        if (task.isFreeSign()) {
                            task.place = locationStr;
                        }
                        // 签到成功， 减去1
                        String userNum = ((AppApplication) getApplicationContext())
                                .getUserInfo().account;
                        int unreadNum = SP.getIntSP(mContext, SP.DEFAULTCACHE, userNum + Task.CATEGORY_KEY_UNREAD_NUM, 0);
                        if (unreadNum > 0) {
                            SP.putIntSP(mContext, SP.DEFAULTCACHE, userNum + Task.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
                        }
                        disableSignButton(true);
                        setResult(RESULT_OK);
                    }

                    @Override
                    public void onErrorCode(int code) {                            // 签到失败
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                mContext);
                        builder.setTitle(R.string.message_tips);
                        builder.setMessage(R.string.task_signFail);
                        builder.show();
                    }
                });
    }

    @Override
    public void onReceivePoi(BDLocation arg0) {
    }
}
