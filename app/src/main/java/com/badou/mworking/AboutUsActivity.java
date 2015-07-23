package com.badou.mworking;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.presenter.AboutUsPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.view.AboutUsView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * 功能描述: 关于我们
 */
public class AboutUsActivity extends BaseBackActionBarActivity implements AboutUsView {

    @Bind(R.id.version_text_view)
    TextView mVersionTextView;
    @Bind(R.id.push_check_box)
    CheckBox mPushCheckBox;
    @Bind(R.id.save_check_box)
    CheckBox mSaveCheckBox;

    AboutUsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(mContext.getResources().getString(R.string.title_name_about));
        setContentView(R.layout.activity_about_us);
        ButterKnife.bind(this);
        mPresenter = (AboutUsPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new AboutUsPresenter(mContext);
    }

    @OnClick(R.id.check_update_layout)
    void checkUpdate() {
        mPresenter.checkUpdate();
    }

    // 清除缓存
    @OnClick(R.id.clear_cache_layout)
    void clearCache() {
        mPresenter.clearCache();
    }

    // 常见问题
    @OnClick(R.id.faq_layout)
    void frequentQuestion() {
        mPresenter.frequentQuestion();
    }

    // 联系我们
    @OnClick(R.id.contact_layout)
    void contactUs() {
        mPresenter.contactUs();
    }

    //是否显示图片开关
    @OnCheckedChanged(R.id.save_check_box)
    void saveInternetOption(CompoundButton buttonView, boolean isChecked) {
        mPresenter.saveInternetOption(isChecked);
    }

    //是否开启推送开关
    @OnCheckedChanged(R.id.push_check_box)
    void closePushOption(CompoundButton buttonView, boolean isChecked) {
        mPresenter.closePushOption(isChecked);
    }

    @Override
    public void setSaveMode(boolean isSave) {
        mSaveCheckBox.setChecked(SPHelper.getSaveInternetOption());
    }

    @Override
    public void setClosePushMode(boolean isClosed) {
        mPushCheckBox.setChecked(SPHelper.getClosePushOption());
    }

    @Override
    public void setVersion(String version) {
        mVersionTextView.setText(mContext.getResources().getString(R.string.app_name) + version);
    }
}
