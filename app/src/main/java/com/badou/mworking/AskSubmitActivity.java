package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.badou.mworking.base.BaseActionBarActivity;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.MultiImageEditGridView;

import org.json.JSONObject;

/**
 * 问答提问页面
 */
public class AskSubmitActivity extends BaseBackActionBarActivity {

    private EditText mSubjectEditView;
    private EditText mDescriptionEditView;
    private MultiImageEditGridView mImageGridView;
    private TextView mBottomPhotoTextView;
    private ImageChooser mImageChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_submit);
        setActionbarTitle(getResources().getString(R.string.ask_title_right));
        initView();
        initListener();
    }

    /**
     * 初始化
     */
    private void initView() {
        setRightImage(R.drawable.button_title_send, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendWenDaContent();
            }
        });
        mSubjectEditView = (EditText) findViewById(R.id.et_activity_ask_submit_subject);
        mDescriptionEditView = (EditText) findViewById(R.id.et_activity_ask_submit_description);
        mImageGridView = (MultiImageEditGridView) findViewById(R.id.miegv_activity_ask_submit);
        mBottomPhotoTextView = (TextView) findViewById(R.id.tv_activity_ask_submit_photo);
    }

    private void initListener() {
        mBottomPhotoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageChooser.takeImage(getResources().getString(R.string.add_picture));
            }
        });
        mImageGridView.setAddOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageChooser.takeImage(getResources().getString(R.string.add_picture));
            }
        });
        mImageChooser = new ImageChooser(mContext, true, true, false);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChose(Bitmap bitmap, int type) {
                mImageGridView.addImage(bitmap);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageChooser.onActivityResult(requestCode, resultCode, data);
    }

    private void sendWenDaContent() {
        String subject = mSubjectEditView.getText().toString();
        String content = mDescriptionEditView.getText().toString();
        if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(content)) {
            ToastUtil.showToast(mContext, R.string.ask_answer_tip_empty);
            return;
        } else if (content.replace(" ", "").length() < 5 || subject.replace(" ", "").length() < 5) {
            ToastUtil.showToast(mContext, R.string.ask_answer_tip_little);
            return;
        }
        Bitmap bitmap = null;

        if (mImageGridView.getImages() != null && mImageGridView.getImages().size() >= 1)
            bitmap = (Bitmap) mImageGridView.getImages().get(0);
        // 提交提问内容
        ServiceProvider.doPublishAsk(mContext, subject, content, bitmap,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        Intent intent = new Intent(mContext, AskActivity.class);
                        intent.putExtra(BaseActionBarActivity.KEY_TITLE,
                                MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_ASK, R.drawable.button_ask, R.string.module_default_title_ask).name);
                        startActivity(intent);
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
