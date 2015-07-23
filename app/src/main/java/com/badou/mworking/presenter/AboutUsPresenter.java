package com.badou.mworking.presenter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.widget.CompoundButton;

import com.badou.mworking.BackWebActivity;
import com.badou.mworking.ClearCacheActivity;
import com.badou.mworking.R;
import com.badou.mworking.TipsWebView;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.main.NewVersion;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.DialogUtil;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.AboutUsView;
import com.badou.mworking.view.BaseView;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;

import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class AboutUsPresenter extends Presenter {

    AboutUsView mAboutUsView;

    public AboutUsPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mAboutUsView = (AboutUsView) v;
        initData();
    }

    private void initData() {
        mAboutUsView.setSaveMode(SPHelper.getSaveInternetOption());
        mAboutUsView.setClosePushMode(SPHelper.getClosePushOption());
        mAboutUsView.setVersion(AppApplication.appVersion);
    }

    public void checkUpdate() {
        mAboutUsView.showProgressDialog(R.string.action_update_check_ing);
        CheckUpdateUseCase useCase = new CheckUpdateUseCase(mContext);
        useCase.execute(new BaseSubscriber<MainData>(mContext) {
            @Override
            public void onResponseSuccess(MainData data) {
                if (data.getNewVersion().hasNewVersion()) {
                    DialogUtil.apkUpdate(mContext, mAboutUsView, data.getNewVersion());
                } else {
                    ToastUtil.showToast(mContext, R.string.result_update_check_noneed);
                }
            }

            @Override
            public void onCompleted() {
                mAboutUsView.hideProgressDialog();
            }
        });
    }

    // 清除缓存
    public void clearCache() {
        mContext.startActivity(new Intent(mContext, ClearCacheActivity.class));
    }

    // 常见问题
    public void frequentQuestion() {
        Intent intent1 = new Intent(mContext, TipsWebView.class);
        intent1.putExtra(BackWebActivity.KEY_URL, Net.getRunHost() + Net.FAQ);
        intent1.putExtra(TipsWebView.KEY_TITLE, mContext.getResources().getString(R.string.about_us_frequent_question));
        mContext.startActivity(intent1);
    }

    // 联系我们
    public void contactUs() {
        new AlertDialog.Builder(mContext).setMessage(R.string.about_tips_phone)
                .setPositiveButton(R.string.about_btn_tophone,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:4008233773"));
                                mContext.startActivity(intent);
                            }
                        }).setNegativeButton(R.string.text_cancel, null).show();
    }

    //是否显示图片开关
    public void saveInternetOption(boolean isChecked) {
        SPHelper.setSaveInternetOption(isChecked);
    }

    //是否开启推送开关
    public void closePushOption(boolean isChecked) {
        if (isChecked) {
            SPHelper.setClosePushOption(true);
            JPushInterface.stopPush(mContext.getApplicationContext());   //推送关闭
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.cancel(mContext);
        } else {
            SPHelper.setClosePushOption(false);
            JPushInterface.resumePush(mContext.getApplicationContext());    //推送打开
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.OpenTimer(mContext);
        }
    }
}
