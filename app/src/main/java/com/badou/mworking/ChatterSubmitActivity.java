package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.model.MainIcon;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParameters;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.MultiImageEditGridView;

import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.LinearLayout;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * 功能描述: 同事圈发送消息界面
 */
public class ChatterSubmitActivity extends BaseBackActionBarActivity {

    private EditText mContentEditText;
    private MultiImageEditGridView mImageGridView;
    private LinearLayout mBottomTopicLayout;
    private LinearLayout mBottomAnonymousLayout;
    private LinearLayout mBottomPhotoLayout;
    private CheckBox mAnonymousCheckBox;

    private int mImageType = -1;
    private ImageChooser mImageChooser;

    public static final String ACT_NAME_TONG_SHI_QUAN = "tong_shi_quan";
    public static final String ACT_NAME_TONG_SHI_DETAIL = "tong_shi_quan_xiang_qing";
    public static final String KEY_QUESTION_VALUE = "QuestionActivity_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_submit);
        initView();
        initListener();
        // 设置图片
        setRightImage(R.drawable.button_title_send);
        setActionbarTitle("分享");
    }

    private void initView() {
        mContentEditText = (EditText) findViewById(R.id.et_activity_chatter_submit_content);
        mImageGridView = (MultiImageEditGridView) findViewById(R.id.miegv_activity_chatter_submit);
        mBottomTopicLayout = (LinearLayout) findViewById(R.id.ll_activity_chatter_submit_bottom_topic);
        mBottomAnonymousLayout = (LinearLayout) findViewById(R.id.ll_activity_chatter_submit_bottom_anonymous);
        mBottomPhotoLayout = (LinearLayout) findViewById(R.id.ll_activity_chatter_submit_bottom_photo);
        mAnonymousCheckBox = (CheckBox) findViewById(R.id.cb_activity_chatter_bottom_anonymous);
    }

    private void initListener() {
        mBottomTopicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mBottomAnonymousLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAnonymousCheckBox.setChecked(!mAnonymousCheckBox.isChecked());
            }
        });
        mBottomPhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageChooser.takeImage(getResources().getString(R.string.add_picture));
            }
        });
        mImageChooser = new ImageChooser(mContext, true, true, true, true);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChose(Bitmap bitmap, int type) {
                mImageType = type;
                mImageGridView.addImage(bitmap);
            }
        });
        mImageGridView.setOnImageDeleteListener(new MultiImageEditGridView.OnImageDeleteListener() {
            @Override
            public void onDelete(int position) {
                if (mImageType == ImageChooser.TYPE_VIDEO) {
                    // 清除视屏文件
                    File file = new File(FileUtils.getChatterVideoDir(mContext));
                    if (file.exists()) {
                        file.delete();
                    }
                }
                if (mImageGridView.getCount() == 0) {
                    mImageType = -1;
                }
            }
        });
    }

    @Override
    public void clickRight() {
        // 断网判断
        if (!NetUtils.isNetConnected(this)) {
            ToastUtil.showNetExc(mContext);
            return;
        }
        String content = mContentEditText.getText().toString().replaceAll("\\n", "")
                .trim();

        if (TextUtils.isEmpty(content)) {
            ToastUtil.showToast(mContext, R.string.question_content_null);
            return;
        }
        if (content.length() < 5) {
            ToastUtil.showToast(mContext, R.string.comment_tips_length);
            return;
        }
        /**发布内容**/
        publishQuestionShare(content, mImageGridView.getImages(), mImageType);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageChooser.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 发布问题/分享 内容
     *
     * @param content
     */
    private void publishQuestionShare(String content, final List<Object> bitmap, final int type) {
        // 提交提问内容
        ServiceProvider.doPublishQuestionShare(mContext, "share", content, mAnonymousCheckBox.isChecked() ? 1 : 0,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponse(Object arg0) {
                        JSONObject jObject = (JSONObject) arg0;
                        int errcode = jObject
                                .optInt(ResponseParams.QUESTION_ERRCODE);
                        if (errcode == Net.SUCCESS) {
                            // 获取questionid
                            String qid = jObject.optJSONObject(Net.DATA).optString("qid");
                            if (TextUtils.isEmpty(qid)) {
                                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                                return;
                            }
                            if (type == ImageChooser.TYPE_VIDEO) {
                                publishVideo(qid);
                            } else if (type == ImageChooser.TYPE_IMAGE) {
                                publicImage(qid, 0, bitmap);
                            } else {
                                System.out.println("Upload text success!!!!!!!!!!!");
                                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                                toChatterActivity();
                            }
                        } else {
                            ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                        ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                    }
                });
    }

    private void publicImage(final String qid, final int index, final List<Object> bitmapList) {
        ServiceProvider.doUploadImage(mContext, qid, index + 1, (Bitmap) bitmapList.get(index), new VolleyListener(mContext) {
            @Override
            public void onResponse(Object responseObject) {
                super.onResponse(responseObject);
                int errcode = ((JSONObject) responseObject)
                        .optInt(ResponseParams.QUESTION_ERRCODE);
                if (errcode == Net.SUCCESS) {
                    System.out.println("Upload image success!!!!!!!!!!!");
                    if (index == bitmapList.size() - 1) {
                        ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                        toChatterActivity();
                    } else {
                        publicImage(qid, index + 1, bitmapList);
                    }
                } else {
                    ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
            }
        });
    }

    /**
     * 功能描述: 上传视屏
     */
    private void publishVideo(String qid) {

        ServiceProvider.doUploadVideo(mContext, qid, FileUtils.getChatterVideoDir(mContext), new VolleyListener(mContext) {
            @Override
            public void onResponse(Object responseObject) {
                super.onResponse(responseObject);
                System.out.println("Upload video success!!!!!!!!!!!");
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                toChatterActivity();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
            }
        });

    }

    private void toChatterActivity() {
        Intent intent = new Intent(mContext, ChatterActivity.class);
        intent.putExtra(BaseBackActionBarActivity.KEY_TITLE, MainIcon.getMainIcon(mContext, RequestParameters.CHK_UPDATA_PIC_CHATTER, R.drawable.button_chatter, R.string.module_default_title_chatter).name);
        startActivity(intent);
    }

}
