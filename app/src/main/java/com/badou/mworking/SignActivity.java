package com.badou.mworking;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseStatisticalActionBarActivity;
import com.badou.mworking.model.category.Task;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.badou.mworking.widget.WaitProgressDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;

import org.holoeverywhere.app.AlertDialog;
import org.json.JSONObject;

/**
 * 功能描述:  任务签到页面
 */
public class SignActivity extends BaseStatisticalActionBarActivity implements BDLocationListener {

    public static final String KEY_TASK = "task";
    private static final int CAMERA_REQUEST_CODE = 1;

    private TextView mBeginDateTextView;
    private TextView mBeginTimeTextView;
    private TextView mEndDateTextView;
    private TextView mEndTimeTextView;
    private TextView mDescriptionTextView;
    private TextView mLocationTextView;
    private TextView mSignTextView;


    private Task task;
    private Bitmap photo = null;
    public LocationClient mLocationClient;
    private Boolean isSign = false; //是否签到， 区别是否是否是首次进入需要显示地图
    private BDLocation signLocation = null;   //爆粗签到的location值
    private String locationStr = "";

    private ImageChooser mImageChooser;

    // 初始化全局 bitmap 信息，不用时及时 recycle
    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        //页面滑动关闭
        layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
        layout.attachToActivity(this);
        mProgressDialog = new WaitProgressDialog(mContext,
                R.string.sign_action_sign_ing);
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
                    isSign = true;
                    mProgressDialog.show();
                    mLocationClient.start();
                }
            }
        });
        mImageChooser = new ImageChooser(mContext, false, true, false);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChose(Bitmap bitmap) {
                if (bitmap != null) {
                    photo = bitmap;
                    isSign = true;
                    mProgressDialog.show();
                    mLocationClient.start();
                }
            }
        });
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
        mBeginTimeTextView.setText(TimeTransfer.long2StringTimeHour(beginTime));
        mEndDateTextView.setText(TimeTransfer.long2StringDateUnit(endTime));
        mEndTimeTextView.setText(TimeTransfer.long2StringTimeHour(endTime));

        if (finish) {// 已签到
            disableSignButton(R.string.category_signed);
        } else {
            if (task.isOffline()) { // 已过期
                disableSignButton(R.string.category_expired);
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
        if (task.isFreeSign()) {
            mLocationClient.start();
        } else {
            LatLng location = new LatLng(task.latitude, task.longitude);
            MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(location);
            MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(16);
            SupportMapFragment map = (SupportMapFragment) (getSupportFragmentManager()
                    .findFragmentById(R.id.map));
            OverlayOptions ooA = new MarkerOptions().position(location).icon(bdA).draggable(false);
            map.getBaiduMap().addOverlay(ooA);
            map.getBaiduMap().setMapStatus(u1);
            map.getBaiduMap().setMapStatus(u2);
        }
    }

    private void disableSignButton(int resId) {
        mSignTextView.setBackgroundResource(R.drawable.background_button_activity_task_sign_disable);
        mSignTextView.setEnabled(false);
        mSignTextView.setText(resId);
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
            ToastUtil.showToast(SignActivity.this, R.string.task_get_gps_fail);
            if (!mActivity.isFinishing()) {
                mProgressDialog.dismiss();
            }
            return;
        }
        if (!isSign) {
            LatLng location1 = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(location1);
            MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(18);
            SupportMapFragment map = (SupportMapFragment) (getSupportFragmentManager()
                    .findFragmentById(R.id.map));
            OverlayOptions ooA = new MarkerOptions().position(location1).icon(bdA).draggable(false);
            map.getBaiduMap().addOverlay(ooA);
            map.getBaiduMap().setMapStatus(u1);
            map.getBaiduMap().setMapStatus(u2);
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
                    public void onResponse(Object responseObject) {
                        if (photo != null && photo.isRecycled()) {
                            photo.recycle();
                        }
                        if (!mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                        JSONObject jsonObject = (JSONObject) responseObject;
                        if (jsonObject == null) {
                            ToastUtil.showToast(SignActivity.this, getResources().getString(R.string.error_service));
                            return;
                        }
                        int errcode = jsonObject
                                .optInt(RequestParams.ERRCODE);
                        if (errcode == 0) {
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
                            disableSignButton(R.string.category_signed);
                            setResult(RESULT_OK, null);
                        } else {
                            // 签到失败
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    mContext);
                            builder.setTitle(R.string.message_tips);
                            builder.setMessage(R.string.task_signFail);

                            builder.setPositiveButton(
                                    R.string.text_ok,
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface arg0,
                                                int arg1) {
                                            arg0.dismiss();
                                        }
                                    }).show();
                        }
                    }
                });
    }

    @Override
    public void onReceivePoi(BDLocation arg0) {
    }
}
