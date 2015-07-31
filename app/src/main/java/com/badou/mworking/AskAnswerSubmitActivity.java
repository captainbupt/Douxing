package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.ask.AskReplyPresenter;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.view.ask.AskReplyView;
import com.badou.mworking.widget.MultiImageEditGridView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问答页面
 */
public class AskAnswerSubmitActivity extends BaseBackActionBarActivity implements AskReplyView {

    private static final String KEY_AID = "aid";

    @Bind(R.id.content_edit_text)
    EditText mContentEditText;
    @Bind(R.id.image_grid_view)
    MultiImageEditGridView mImageGridView;
    @Bind(R.id.photo_text_view)
    TextView mPhotoTextView;

    ImageChooser mImageChooser;

    AskReplyPresenter mPresenter;

    public static Intent getIntent(Context context, String aid) {
        Intent intent = new Intent(context, AskAnswerSubmitActivity.class);
        intent.putExtra(KEY_AID, aid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(R.string.ask_title_reply);
        setContentView(R.layout.activity_ask_answer_submit);
        ButterKnife.bind(this);
        initListener();
        mPresenter = (AskReplyPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new AskReplyPresenter(mContext, mReceivedIntent.getStringExtra(KEY_AID));
    }

    private void initListener() {
        mPhotoTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.takeImage();
            }
        });
        mImageGridView.setAddOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.takeImage();
            }
        });
        setRightImage(R.drawable.button_title_send, new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.sendReply(mContentEditText.getText().toString(), mImageGridView.getImages());
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
        if (mImageChooser == null) {
            mImageChooser = new ImageChooser(mContext, true, true, false);
            mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
                @Override
                public void onImageChosen(Bitmap bitmap, int type) {
                    mPresenter.onImageSelected(bitmap);
                }
            });
        }
        mImageChooser.takeImage(getResources().getString(R.string.add_picture));
    }
}

