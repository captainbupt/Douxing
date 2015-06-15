package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.MultiImageEditGridView;

import org.json.JSONObject;

/**
 * 问答页面
 */
public class AskAnswerSubmitActivity extends BaseBackActionBarActivity {

    public static final String KEY_AID = "aid";

    private String mAid = "";
    private EditText mContentEditText;
    private MultiImageEditGridView mImageGridView;
    private LinearLayout mPhotoLinearLayout;
    private ImageChooser mImageChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_answer_submit);
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化
     */
    private void initView() {
        mContentEditText = (EditText) findViewById(R.id.et_activity_ask_answer_submit_content);
        mImageGridView = (MultiImageEditGridView) findViewById(R.id.miegv_activity_ask_answer_submit);
        mPhotoLinearLayout = (LinearLayout) findViewById(R.id.ll_activity_ask_answer_photo);
    }

    private void initListener() {
        mPhotoLinearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageChooser.takeImage(getResources().getString(R.string.add_picture));
            }
        });
        mImageChooser = new ImageChooser(mContext, true, true, true);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChose(Bitmap bitmap, int type) {
                mImageGridView.addImage(bitmap);
            }
        });
    }

    private void initData() {
        mProgressDialog.setContent(R.string.progress_tips_submit_ing);
        mAid = mReceivedIntent.getStringExtra(KEY_AID);
        setRightImage(R.drawable.button_title_send);
        setActionbarTitle("回答");
    }


    @Override
    public void clickRight() {
        super.clickRight();
        sendReply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageChooser.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendReply() {
        mProgressDialog.show();
        String content = mContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(mContext, R.string.ask_answer_tip_empty);
            return;
        } else if (content.length() < 5) {
            ToastUtil.showToast(mContext, R.string.ask_answer_tip_little);
            return;
        }
        Bitmap bitmap = null;
        if (mImageGridView.getCount() > 0)
            bitmap = (Bitmap) mImageGridView.getImages().get(0);
        // 提交提问内容
        ServiceProvider.doPublishAnswer(mContext, content, mAid, bitmap,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onCompleted() {
                        if (!mActivity.isFinishing()) {
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }

}

