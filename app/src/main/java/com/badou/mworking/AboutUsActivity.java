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
import com.badou.mworking.util.SP;
import com.badou.mworking.util.SPUtil;
import com.badou.mworking.util.ToastUtil;

import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 功能描述: 关于我们
 */
public class AboutUsActivity extends BaseBackActionBarActivity {

    private TextView mInfoTextView;
    private CheckBox mShowPictureCheckBox;  //是否显示图片
    private CheckBox mPushCheckBox;    //是否推送提醒

    private LinearLayout mUpdateLinearLayout;    // 检查更新
    private LinearLayout mClearCacheLinearLayout;  //缓存管理
    private LinearLayout mFrequentlyQuestionLinearLayout; //常见问题
    private LinearLayout mContactUsLinearLayout;  //联系我们

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(mContext.getResources().getString(
                R.string.title_name_about));
        setContentView(R.layout.activity_about_us);
        initView();
        initData();
        initListener();
    }

    protected void initView() {
        mInfoTextView = (TextView) findViewById(R.id.tv_user_setting_info);
        mShowPictureCheckBox = (CheckBox) findViewById(R.id.cb_about_us_save);
        mPushCheckBox = (CheckBox) findViewById(R.id.cb_about_us_push);
        mUpdateLinearLayout = (LinearLayout) findViewById(R.id.ll_about_us_check_update);
        mClearCacheLinearLayout = (LinearLayout) findViewById(R.id.ll_about_us_clear_cache);
        mFrequentlyQuestionLinearLayout = (LinearLayout) findViewById(R.id.ll_about_us_faq);
        mContactUsLinearLayout = (LinearLayout) findViewById(R.id.ll_about_us_contact);
    }

    private void initData() {
        mShowPictureCheckBox.setChecked(SPUtil.getSaveInternetOption(mContext));
        mPushCheckBox.setChecked(SPUtil.getPushOption(mContext));
        mInfoTextView.setText(mContext.getResources().getString(
                R.string.app_name)
                + AppApplication.appVersion);
    }

    private void initListener() {
        // 检测更新
        mUpdateLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUpdate(false);
            }
        });

        // 清除缓存
        mClearCacheLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, ClearCacheActivity.class));
            }
        });

        // 常见问题
        mFrequentlyQuestionLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(mContext,
                        TipsWebView.class);
                intent1.putExtra(BackWebActivity.KEY_URL,
                        Net.getRunHost(AboutUsActivity.this) + Net.FAQ);
                intent1.putExtra(TipsWebView.KEY_TITLE, getResources().getString(R.string.about_us_frequent_question));
                startActivity(intent1);
            }
        });

        // 联系我们
        mContactUsLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setMessage(R.string.about_tips_phone
                        )
                        .setPositiveButton(
                                R.string.about_btn_tophone,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        Intent intent = new Intent(
                                                "android.intent.action.CALL",
                                                Uri.parse("tel:4008233773"));
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                                    }
                                }).setNegativeButton(R.string.text_cancel, null)
                        .create().show();
            }
        });

        //是否显示图片开关
        mShowPictureCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SP.putBooleanSP(mContext, SP.DEFAULTCACHE, "pic_show", true);
                } else {
                    SP.putBooleanSP(mContext, SP.DEFAULTCACHE, "pic_show", false);
                }
            }
        });

        //是否开启推送开关
        mPushCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    SPUtil.setPushOption(mContext, false);
                    JPushInterface.resumePush(getApplicationContext());    //推送打开
                    AlarmUtil alarmUtil = new AlarmUtil();
                    alarmUtil.OpenTimer(mContext);
                } else {
                    SPUtil.setPushOption(mContext, true);
                    JPushInterface.stopPush(getApplicationContext());   //推送关闭
                    AlarmUtil alarmUtil = new AlarmUtil();
                    alarmUtil.cancel(mContext);
                }
            }
        });
    }

    private void checkUpdate(boolean isAuto) {
        if (!isAuto) {
            mProgressDialog.setTitle(R.string.message_tips);
            mProgressDialog.setContent(R.string.action_update_check_ing);
            mProgressDialog.show();
        }
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
}
