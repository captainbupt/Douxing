package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.UserCenterPresenter;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.view.UserCenterView;
import com.badou.mworking.widget.LevelTextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述: 个人中心页面
 */
public class UserCenterActivity extends BaseNoTitleActivity implements UserCenterView {

    @Bind(R.id.head_image_view)
    ImageView mHeadImageView;
    @Bind(R.id.level_text_view)
    LevelTextView mLevelTextView;
    @Bind(R.id.name_text_view)
    TextView mNameTextView;
    @Bind(R.id.about_image_view)
    ImageView mAboutImageView;
    @Bind(R.id.back_image_view)
    ImageView mBackImageView;
    @Bind(R.id.study_percent_text_view)
    TextView mStudyPercentTextView;
    @Bind(R.id.study_progress_bar)
    ProgressBar mStudyProgressBar;
    @Bind(R.id.study_progress_layout)
    LinearLayout mStudyProgressLayout;
    @Bind(R.id.exam_score_text_view)
    TextView mExamScoreTextView;
    @Bind(R.id.my_exam_layout)
    LinearLayout mMyExamLayout;
    @Bind(R.id.chatter_number_text_view)
    TextView mChatterNumberTextView;
    @Bind(R.id.my_chatter_layout)
    LinearLayout mMyChatterLayout;
    @Bind(R.id.message_number_text_view)
    TextView mMessageNumberTextView;
    @Bind(R.id.my_message_layout)
    LinearLayout mMyMessageLayout;
    @Bind(R.id.store_number_text_view)
    TextView mStoreNumberTextView;
    @Bind(R.id.my_store_layout)
    LinearLayout mMyStoreLayout;
    @Bind(R.id.my_account_layout)
    LinearLayout mMyAccountLayout;
    @Bind(R.id.service_layout)
    LinearLayout mServiceLayout;
    @Bind(R.id.user_center_linear)
    LinearLayout mUserCenterLinear;

    ImageChooser mImageChooser;

    UserCenterPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        ButterKnife.bind(this);
        disableSwipeBack();
        mPresenter = (UserCenterPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new UserCenterPresenter(mContext);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_bottom);
    }

    @OnClick(R.id.head_image_view)
    void onHeadClicked() {
        mPresenter.changeUserHead();
    }

    @OnClick(R.id.level_text_view)
    void onLevelClicked() {
        mPresenter.checkLevel();
    }

    @OnClick(R.id.back_image_view)
    public void onBackPressedClicked() {
        super.onBackPressed();
    }

    @OnClick(R.id.study_progress_layout)
    void onStudyProgressClicked() {
        mPresenter.toMyStudy();
    }

    @OnClick(R.id.my_exam_layout)
    void onExamProgressClicked() {
        mPresenter.toMyExam();
    }

    @OnClick(R.id.my_chatter_layout)
    void onChatterClicked() {
        mPresenter.toMyChatter();
    }

    @OnClick(R.id.my_message_layout)
    void onMessageClicked() {
        mPresenter.toMyChat();
    }

    @OnClick(R.id.my_store_layout)
    void onStoreClicked() {
        mPresenter.toMyStore();
    }

    @OnClick(R.id.my_account_layout)
    void onAccountClicked() {
        mPresenter.toMyAccount();
    }

    @OnClick(R.id.service_layout)
    void onServiceClicked() {
        mPresenter.toService();
    }

    @OnClick(R.id.about_image_view)
    void onAboutUsClicked() {
        mPresenter.toAboutUs();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageChooser.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置分数 学习进度
     */
    public void setData(UserDetail userDetail) {
        if (!TextUtils.isEmpty(userDetail.getHeadimg()))
            setHeadImage(userDetail.getHeadimg());
        // 用户信息
        String strScore = mContext.getResources().getString(R.string.text_score);
        String strPingJunFen = mContext.getResources().getString(R.string.user_center_exam_average);
        mNameTextView.setText(userDetail.getName() + "\n" + userDetail.getDpt());
        mExamScoreTextView.setText(strPingJunFen + userDetail.getScore() + strScore);

        // 学习进度
        int study = userDetail.getStudyTotal();
        int training = userDetail.getTrainingTotal();
        mStudyPercentTextView.setText(study + "/" + training);
        int s = study * 100;
        if (training != 0) {
            int progress = s / training;
            mStudyProgressBar.setProgress(progress);
        }

        // 同事圈
        mChatterNumberTextView.setText(userDetail.getAsk() + getResources().getString(R.string.chatter_num));
        mLevelTextView.setLevel(userDetail.getLevel());
        int nmsg = userDetail.getNmsg();
        if (nmsg > 0) {
            mMessageNumberTextView.setVisibility(View.VISIBLE);
            mMessageNumberTextView.setText(nmsg + "");
        } else {
            mMessageNumberTextView.setVisibility(View.GONE);
        }
        int storeNumber = userDetail.getStore();
        mStoreNumberTextView.setText(storeNumber + getResources().getString(R.string.chatter_num));

    }

    @Override
    public void takeImage() {
        if (mImageChooser == null) {
            mImageChooser = new ImageChooser(mContext, true, true, true);
            mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
                @Override
                public void onImageChosen(Bitmap bitmap, int type) {
                    mPresenter.onImageSelected(bitmap);
                }
            });
        }
        mImageChooser.takeImage(getString(R.string.uc_dialog_title_settingHead));
    }

    @Override
    public void setHeadImage(String url) {
        ImageViewLoader.setCircleImageViewResource(mHeadImageView, url, getResources().getDimensionPixelSize(R.dimen.user_center_image_head_size));
    }
}
