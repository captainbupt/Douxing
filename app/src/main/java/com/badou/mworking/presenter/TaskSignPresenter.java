package com.badou.mworking.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.badou.mworking.CategoryListActivity;
import com.badou.mworking.R;
import com.badou.mworking.domain.TaskSignUseCase;
import com.badou.mworking.entity.category.Category;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.TaskSignView;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.io.File;

public class TaskSignPresenter extends CategoryBasePresenter implements BDLocationListener {

    private boolean isSign = false; //是否签到， 否表示显示我的位置

    private Bitmap mPhoto = null;
    public LocationClient mLocationClient;

    TaskSignView mTaskSignView;

    public TaskSignPresenter(Context context, String rid) {
        super(context, Category.CATEGORY_TASK, rid);
    }

    @Override
    public void attachView(BaseView v) {
        super.attachView(v);
        mTaskSignView = (TaskSignView) v;
    }

    @Override
    public void setData(CategoryDetail categoryDetail) {
        super.setData(categoryDetail);
        initlocation();
        if (mCategoryDetail.getContent().getC() == 1) {// 已签到
            mTaskSignView.setStatus(TaskSignView.STATUS_SIGN);
        } else {
            if (mCategoryDetail.getTask().isOffline()) { // 已过期
                mTaskSignView.setStatus(TaskSignView.STATUS_OFFLINE);
            } else { // 未签到
                mTaskSignView.setStatus(TaskSignView.STATUS_UNSIGN);
            }
        }
    }

    public void onImageChosen(Bitmap bitmap) {
        if (bitmap != null) {
            mPhoto = bitmap;
            mTaskSignView.showProgressDialog(R.string.sign_action_sign_ing);
            startLocation(true);
        }
    }

    /**
     * 功能描述:  初始化定位数据
     */
    private void initlocation() {
        mLocationClient = new LocationClient(mContext);
        mLocationClient.registerLocationListener(this);
        // 定位初始化
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocationClient.setLocOption(option);
        if (mCategoryDetail.getTask().isFreeSign()) {
            startLocation(false);
        }
    }

    public void startSign() {
        //无网络状态下不允许点击
        if (!NetUtils.isNetConnected(mContext)) {
            mCategoryBaseView.showToast(R.string.error_service);
            return;
        }
        long timeNow = System.currentTimeMillis();
        if (mCategoryDetail.getTask().getStartline() > timeNow) {
            ToastUtil.showToast(mContext, R.string.task_notStart);
            return;
        }
        if (mCategoryDetail.getTask().isPhoto()) {
            mTaskSignView.takeImage();
        } else {
            mTaskSignView.showProgressDialog(R.string.sign_action_sign_ing);
            startLocation(true);
        }
    }

    public void toMyPosition() {
        startLocation(false);
    }

    private void startLocation(boolean isSign) {
        this.isSign = isSign;
        mLocationClient.start();
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        mLocationClient.stop();
        if (location == null || String.valueOf(location.getLatitude()).equals(4.9E-324) || String.valueOf(location.getLongitude()).equals(4.9E-324)) {
            mTaskSignView.showToast(R.string.task_get_gps_fail);
            mTaskSignView.hideProgressDialog();
            return;
        }
        if (!isSign) {
            mTaskSignView.setLocation(location);
        } else {
            isSign = false;
            taskSign(location);
        }
    }

    private void taskSign(final BDLocation location) {
        if (location == null) {
            return;
        }
        File file = null;
        if (mPhoto != null) {
            String filePath = mContext.getExternalCacheDir().getPath() + File.separator + "tmp.jpg";
            FileUtils.writeBitmap2SDcard(mPhoto, filePath);
            file = new File(filePath);
        }
        new TaskSignUseCase(mRid, location, file).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                if (mPhoto != null && mPhoto.isRecycled()) {
                    mPhoto.recycle();
                }
                mTaskSignView.setStatus(TaskSignView.STATUS_SIGN);
                mTaskSignView.showToast(R.string.task_sign_success);
                mCategoryDetail.getContent().setC(1);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                mTaskSignView.hideProgressDialog();
            }

            @Override
            public void onErrorCode(int code) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
