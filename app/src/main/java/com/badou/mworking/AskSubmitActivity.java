package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.ask.AskSubmitPresenter;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.view.ask.AskSubmitView;
import com.badou.mworking.widget.MultiImageEditGridView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问答提问页面
 */
public class AskSubmitActivity extends BaseBackActionBarActivity implements AskSubmitView {

    @Bind(R.id.subject_text_view)
    EditText mSubjectTextView;
    @Bind(R.id.content_text_view)
    EditText mContentTextView;
    @Bind(R.id.image_grid_view)
    MultiImageEditGridView mImageGridView;
    @Bind(R.id.submit_photo_text_view)
    TextView mSubmitPhotoTextView;

    AskSubmitPresenter mPresenter;
    ImageChooser mImageChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_submit);
        ButterKnife.bind(this);
        setActionbarTitle(getResources().getString(R.string.ask_title_right));
        initView();
        mPresenter = (AskSubmitPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new AskSubmitPresenter(mContext);
    }

    /**
     * 初始化
     */
    private void initView() {
        setRightImage(R.drawable.button_title_send, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.publishAsk(mSubjectTextView.getText().toString(), mContentTextView.getText().toString(), mImageGridView.getImages());
            }
        });
        mSubmitPhotoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.takeImage();
            }
        });
        mImageGridView.setAddOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.takeImage();
            }
        });
        mImageChooser = new ImageChooser(mContext, true, true, false);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChosen(Bitmap bitmap, int type) {
                mPresenter.onImageSelected(bitmap);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageChooser.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void addImage(Bitmap bitmap) {
        mImageGridView.setImage(0, bitmap);
    }

    @Override
    public void takeImage() {
        mImageChooser.takeImage(getResources().getString(R.string.add_picture));
    }

    @Override
    protected void onDestroy() {
        mImageGridView.clear();
        super.onDestroy();
    }
}
