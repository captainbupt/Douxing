package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.badou.mworking.adapter.ChatterTopicAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.ChatterTopic;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.EllipsizeTextView;
import com.badou.mworking.widget.MultiImageEditGridView;
import com.badou.mworking.widget.NoScrollListView;
import com.badou.mworking.widget.VideoImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述: 同事圈发送消息界面
 */
public class ChatterSubmitActivity extends BaseBackActionBarActivity {


    @Bind(R.id.content_edit_text)
    EditText mContentEditText;
    @Bind(R.id.url_title_text_view)
    EllipsizeTextView mUrlTitleTextView;
    @Bind(R.id.url_content_layout)
    LinearLayout mUrlContentLayout;
    @Bind(R.id.image_grid_view)
    MultiImageEditGridView mImageGridView;
    @Bind(R.id.video_image_view)
    VideoImageView mVideoImageView;
    @Bind(R.id.bottom_topic_layout)
    LinearLayout mBottomTopicLayout;
    @Bind(R.id.anonymous_check_box)
    CheckBox mAnonymousCheckBox;
    @Bind(R.id.bottom_anonymous_layout)
    LinearLayout mBottomAnonymousLayout;
    @Bind(R.id.bottom_photo_layout)
    LinearLayout mBottomPhotoLayout;
    @Bind(R.id.topic_list_view)
    ListView mTopicListView;

    private ChatterTopicAdapter mTopicAdapter;
    TextView mTopicConfirmTextView;
    EditText mTopicEditText;

    private int mImageType = -1;
    private ImageChooser mImageChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_submit);
        ButterKnife.bind(this);
        initView();
        initListener();
        // 设置图片
        setRightImage(R.drawable.button_title_send, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send();
            }
        });
        setActionbarTitle(R.string.chatter_title_right);
    }

    private void initView() {
        View header = LayoutInflater.from(mContext).inflate(R.layout.layout_chatter_topic_header, mTopicListView, false);
        mTopicConfirmTextView = (TextView) header.findViewById(R.id.topic_confirm_text_view);
        mTopicEditText = (EditText) header.findViewById(R.id.topic_edit_text);
        mTopicListView.addHeaderView(header);
    }

    @OnClick(R.id.bottom_topic_layout)
    void onTopicClicked(){

    }



    private void initListener() {
        mBottomTopicLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTopicScrollView.getVisibility() == View.VISIBLE) {
                    mTopicScrollView.setVisibility(View.GONE);
                    mContentEditText.setEnabled(true);
                } else {
                    mTopicScrollView.setVisibility(View.VISIBLE);
                    mContentEditText.setEnabled(false);
                }
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
                addImage();
            }
        });
        mImageGridView.setAddOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addImage();
            }
        });
        mImageChooser = new ImageChooser(mContext, true, true, false, true);
        mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
            @Override
            public void onImageChosen(Bitmap bitmap, int type) {
                if (type == ImageChooser.TYPE_VIDEO) {
                    mVideoImageView.setVisibility(View.VISIBLE);
                    mVideoImageView.setData(bitmap, null);
                    mImageGridView.clear();
                    mImageGridView.setVisibility(View.GONE);
                } else {
                    mImageGridView.setVisibility(View.VISIBLE);
                    mImageGridView.addImage(bitmap);
                    mVideoImageView.clear();
                    mVideoImageView.setVisibility(View.GONE);
                }
                mImageType = type;
            }
        });
        mImageChooser.setOnOperationClickListener(new ImageChooser.OnOperationClickListener() {
            @Override
            public boolean onOperationClick(int type) {
                if (type == ImageChooser.TYPE_IMAGE && mImageGridView.isMax()) {
                    ToastUtil.showToast(mContext, R.string.chatter_submit_max_image);
                }
                return true;
            }
        });
        mTopicAdapter = new ChatterTopicAdapter(mContext);
        mTopicConfirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = mTopicEditText.getText().toString().replace("#", "").replace(" ", "").trim();
                onTopicSelected(topic);
            }
        });
        mTopicListView.setOnItemClickListener(new NoScrollListView.OnNoScrollItemClickListener() {
            @Override
            public void onItemClick(View v, int position, long id) {
                ChatterTopic chatterTopic = (ChatterTopic) mTopicAdapter.getItem(position);
                onTopicSelected(chatterTopic.key);
            }
        });
        mTopicListView.setAdapter(mTopicAdapter);
        mVideoImageView.setOnImageDeleteListener(new VideoImageView.OnImageDeleteListener() {
            @Override
            public void onDelete() {
                mImageGridView.setVisibility(View.VISIBLE);
                mVideoImageView.clear();
                mVideoImageView.setVisibility(View.GONE);
                mImageType = -1;
            }
        });
        getTopicList();
    }

    private void addImage() {
        mTopicListView.setVisibility(View.GONE);
        mImageChooser.takeImage(getResources().getString(R.string.add_picture));
    }

    private void onTopicSelected(String content) {
        String temp = mContentEditText.getText().toString();
/*        Pattern pattern = Pattern.compile("#[\\s\\S]*#");
        Matcher matcher = pattern.matcher(temp);
        matcher.replaceFirst("");*/
        mContentEditText.setText("#" + content + "#" + temp.replaceFirst("#[\\s\\S]*#", ""));
        mTopicScrollView.setVisibility(View.GONE);
        mContentEditText.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (mTopicListView.getVisibility() == View.VISIBLE) {
            mTopicScrollView.setVisibility(View.GONE);
            mContentEditText.setEnabled(true);
            return;
        }
        super.onBackPressed();
    }

    public void send() {
        // 断网判断
        if (!NetUtils.isNetConnected(this)) {
            ToastUtil.showToast(mContext, R.string.error_service);
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
        if (mImageType == ImageChooser.TYPE_IMAGE && mImageGridView.getCount() > 1) {
            publishQuestionShare(content, mImageGridView.getImages(), mImageType);
        } else if (mImageType == ImageChooser.TYPE_VIDEO) {
            List<Object> tmpList = new ArrayList<>();
            tmpList.add(mVideoImageView.getBitmap());
            publishQuestionShare(content, tmpList, mImageType);
        } else {
            publishQuestionShare(content, null, mImageType);
        }
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
    private void publishQuestionShare(String content, final List<Object> bitmapList, final int type) {
        mProgressDialog.setContent(R.string.progress_tips_send_ing);
        mProgressDialog.show();
        Bitmap bitmap = null;
        if (bitmapList != null && bitmapList.size() == 1)
            bitmap = (Bitmap) bitmapList.get(0);
        // 提交提问内容
        ServiceProvider.doPublishQuestionShare(mContext, "share", content, bitmap, mAnonymousCheckBox.isChecked() ? 1 : 0,
                new VolleyListener(mContext) {

                    @Override
                    public void onResponseSuccess(JSONObject response) {
                        // 获取questionid
                        String qid = response.optJSONObject(Net.DATA).optString("qid");
                        if (TextUtils.isEmpty(qid)) {
                            ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                            return;
                        }
                        if (type == ImageChooser.TYPE_VIDEO) {
                            publishVideo(qid);
                        } else if (type == ImageChooser.TYPE_IMAGE) {
                            publicImage(qid, 0, bitmapList);
                        } else {
                            ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                            toChatterActivity();
                        }
                    }

                    @Override
                    public void onErrorCode(int code) {
                        ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        super.onErrorResponse(error);
                    }
                });
    }

    private void publicImage(final String qid, final int index, final List<Object> bitmapList) {
        ServiceProvider.doUploadImage(mContext, qid, index + 1, (Bitmap) bitmapList.get(index), new VolleyListener(mContext) {

            @Override
            public void onResponseSuccess(JSONObject response) {
                if (index == bitmapList.size() - 1) {
                    mProgressDialog.dismiss();
                    ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                    toChatterActivity();
                } else {
                    publicImage(qid, index + 1, bitmapList);
                }
            }

            @Override
            public void onErrorCode(int code) {
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
            public void onResponseSuccess(JSONObject response) {
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_success);
                toChatterActivity();
            }

            @Override
            public void onErrorCode(int code) {
                ToastUtil.showToast(mContext, R.string.tongShiQuan_submit_fail);
            }

            @Override
            public void onCompleted() {
                mProgressDialog.dismiss();
            }
        });

    }

    private void toChatterActivity() {
        setResult(RESULT_OK);
        finish();
    }

    private void getTopicList() {
        ServiceProvider.doGetTopicList(mContext, new VolleyListener(mContext) {

            @Override
            public void onResponseSuccess(JSONObject response) {
                JSONObject datas = response.optJSONObject(Net.DATA);
                if (datas == null) {
                    return;
                }
                List<Object> topicList = new ArrayList<>();
                Iterator it = datas.keys();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    String value = datas.optString(key);
                    topicList.add(new ChatterTopic(key, Long.parseLong(value) * 1000));
                }
                mTopicAdapter.setList(topicList);
            }
        });
    }

}
