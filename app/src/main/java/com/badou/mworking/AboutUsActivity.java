package com.badou.mworking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.util.ToastUtil;
import com.loopj.android.http.RangeFileAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述: 关于我们
 */
public class AboutUsActivity extends BaseBackActionBarActivity {

    @InjectView(R.id.tv_info)
    TextView tvInfo;
    @InjectView(R.id.cb_push)
    CheckBox cbPush;
    @InjectView(R.id.cb_save)
    CheckBox cbSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(mContext.getResources().getString(
                R.string.title_name_about));
        setContentView(R.layout.activity_about_us);
        ButterKnife.inject(this);
        initData();
    }

    private void initData() {
        cbSave.setChecked(SPHelper.getSaveInternetOption());
        cbPush.setChecked(SPHelper.getClosePushOption());
        tvInfo.setText(mContext.getResources().getString(
                R.string.app_name)
                + AppApplication.appVersion);
    }

    @OnClick(R.id.ll_check_update)
    void checkUpdate() {
        mProgressDialog.setTitle(R.string.message_tips);
        mProgressDialog.setContent(R.string.action_update_check_ing);
        mProgressDialog.show();
        ServiceProvider.doCheckUpdate(mContext, null, new VolleyListener(mContext) {

                    @Override
                    public void onCompleted() {
                        if (!mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        JSONObject data = response.optJSONObject(Net.DATA);
                        JSONObject newver = data
                                .optJSONObject(RequestParameters.CHK_UPDATA_PIC_NEWVER);
                        boolean hasNew = newver
                                .optInt(ResponseParameters.CHECKUPDATE_NEW) == 1;
                        if (hasNew) {
                            final String info = newver
                                    .optString(ResponseParameters.CHECKUPDATE_INFO);
                            final String url = newver
                                    .optString(ResponseParameters.CHECKUPDATE_URL);
                            new AlertDialog.Builder(mContext)
                                    .setTitle(R.string.main_tips_update_title).setMessage(info)
                                    .setPositiveButton(R.string.about_btn_update,
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    mProgressDialog.setTitle(R.string.action_update_download_ing);
                                                    mProgressDialog.show();
                                                    ServiceProvider.doUpdateMTraning(mActivity, url, new RangeFileAsyncHttpResponseHandler(new File("update.apk")) { // 仅仅是借用该接口
                                                        @Override
                                                        public void onProgress(long bytesWritten, long totalSize) {
                                                            if (mProgressDialog.getMax() != (int) totalSize)
                                                                mProgressDialog.setMax((int) totalSize);
                                                            mProgressDialog.setProgress((int) bytesWritten);
                                                        }

                                                        @Override
                                                        public void onSuccess(int statusCode, Header[] headers, File file) {
                                                            mProgressDialog.dismiss();
                                                            Intent intent = new Intent();
                                                            intent.setAction(Intent.ACTION_VIEW);
                                                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                        }

                                                        @Override
                                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                                                            ToastUtil.showNetExc(mContext);
                                                            mProgressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            }).setNegativeButton(R.string.text_cancel, null)
                                    .create().show();
                        } else {
                            ToastUtil.showToast(mContext, R.string.result_update_check_noneed);
                        }
                    }
                }

        );
    }

    // 清除缓存
    @OnClick(R.id.ll_clear_cache)
    void clearCache() {
        startActivity(new Intent(mContext, ClearCacheActivity.class));
    }

    // 常见问题
    @OnClick(R.id.ll_faq)
    void frequentQuestion() {
        Intent intent1 = new Intent(mContext,
                TipsWebView.class);
        intent1.putExtra(BackWebActivity.KEY_URL,
                Net.getRunHost(AboutUsActivity.this) + Net.FAQ);
        intent1.putExtra(TipsWebView.KEY_TITLE, getResources().getString(R.string.about_us_frequent_question));
        startActivity(intent1);
    }

    // 联系我们
    @OnClick(R.id.ll_contact)
    void contactUs() {
        new AlertDialog.Builder(mContext).setMessage(R.string.about_tips_phone)
                .setPositiveButton(R.string.about_btn_tophone,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                Intent intent = new Intent(
                                        "android.intent.action.CALL",
                                        Uri.parse("tel:4008233773"));
                                startActivity(intent);
                            }
                        }).setNegativeButton(R.string.text_cancel, null).show();
    }

    //是否显示图片开关
    @OnCheckedChanged(R.id.cb_save)
    void saveInternetOption(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            SPHelper.setSaveInternetOption(true);
        } else {
            SPHelper.setSaveInternetOption(false);
        }
    }

    //是否开启推送开关
    @OnCheckedChanged(R.id.cb_push)
    void closePushOption(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            SPHelper.setClosePushOption(true);
            JPushInterface.stopPush(getApplicationContext());   //推送关闭
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.cancel(mContext);
        } else {
            SPHelper.setClosePushOption(false);
            JPushInterface.resumePush(getApplicationContext());    //推送打开
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.OpenTimer(mContext);
        }
    }
}
