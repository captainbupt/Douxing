package com.badou.mworking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.AlarmUtil;
import com.badou.mworking.util.SPUtil;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

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
        cbSave.setChecked(SPUtil.getSaveInternetOption(mContext));
        cbPush.setChecked(SPUtil.getClosePushOption(mContext));
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
                                                    ServiceProvider
                                                            .doUpdateMTraning(
                                                                    mActivity, url);
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
            SPUtil.setSaveInternetOption(mContext, true);
        } else {
            SPUtil.setSaveInternetOption(mContext, false);
        }
    }

    //是否开启推送开关
    @OnCheckedChanged(R.id.cb_push)
    void closePushOption(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            SPUtil.setClosePushOption(mContext, true);
            JPushInterface.stopPush(getApplicationContext());   //推送关闭
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.cancel(mContext);
        } else {
            SPUtil.setClosePushOption(mContext, false);
            JPushInterface.resumePush(getApplicationContext());    //推送打开
            AlarmUtil alarmUtil = new AlarmUtil();
            alarmUtil.OpenTimer(mContext);
        }
    }
}
