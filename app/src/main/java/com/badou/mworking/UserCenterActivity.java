package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.UserCenterPresenter;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.UriUtil;
import com.badou.mworking.view.UserCenterView;
import com.badou.mworking.widget.LevelTextView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述: 个人中心页面
 */
public class UserCenterActivity extends BaseNoTitleActivity implements UserCenterView {

    @Bind(R.id.head_image_view)
    SimpleDraweeView mHeadImageView;
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
    @Bind(R.id.store_number_text_view)
    TextView mStoreNumberTextView;
    @Bind(R.id.title_text_view)
    TextView mTitleTextView;
    @Bind(R.id.credit_text_view)
    TextView mCreditTextView;
    @Bind(R.id.my_store_layout)
    LinearLayout mMyStoreLayout;
    @Bind(R.id.audit_text_view)
    TextView mAuditTextView;
    @Bind(R.id.audit_layout)
    LinearLayout mAuditLayout;
    @Bind(R.id.audit_layout_divider)
    View mAuditDivider;

    ImageChooser mImageChooser;
    UserCenterPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        ButterKnife.bind(this);
        mTitleTextView.setVisibility(View.INVISIBLE);
        if (!UserInfo.getUserInfo().getAccount().startsWith("admin@")) {
            mAuditLayout.setVisibility(View.GONE);
            mAuditDivider.setVisibility(View.GONE);
        }
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

    @OnClick(R.id.survey_layout)
    void onSurveyClicked() {
        mPresenter.toSurvey();
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

    @OnClick(R.id.credit_layout)
    void onCreditClicked() {
        mPresenter.toMyCredit();
    }

    @OnClick(R.id.audit_layout)
    void onAuditClicked() {
        mPresenter.toMyAudit();
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
        int storeNumber = userDetail.getStore();
        mStoreNumberTextView.setText(storeNumber + getResources().getString(R.string.chatter_num));
        mCreditTextView.setText(userDetail.getCredit() + getResources().getString(R.string.credit_num));
        mAuditTextView.setText(userDetail.getAudit() + getResources().getString(R.string.audit_num));
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
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromCache(UriUtil.getHttpUri(url));
        mHeadImageView.setImageURI(UriUtil.getHttpUri(url));
    }

    @Override
    public void setHeadImage(Bitmap bitmap) {
        GenericDraweeHierarchy hierarchy = mHeadImageView.getHierarchy();
        hierarchy.setPlaceholderImage(new BitmapDrawable(mContext.getResources(), bitmap));
        mHeadImageView.setHierarchy(hierarchy);
    }
}
