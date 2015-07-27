package com.badou.mworking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.badou.mworking.presenter.ChatterSubmitPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.ChatterSubmitView;
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
public class ChatterSubmitActivity extends BaseBackActionBarActivity implements ChatterSubmitView {


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
    @Bind(R.id.anonymous_check_box)
    CheckBox mAnonymousCheckBox;
    @Bind(R.id.topic_list_view)
    ListView mTopicListView;

    ChatterTopicAdapter mTopicAdapter;
    TextView mTopicConfirmTextView;
    EditText mTopicEditText;

    ImageChooser mImageChooser;
    ChatterSubmitPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_submit);
        ButterKnife.bind(this);
        initView();
        initListener();
        this.mPresenter = (ChatterSubmitPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new ChatterSubmitPresenter(mContext);
    }

    private void initView() {
        setActionbarTitle(R.string.chatter_title_right);
        View header = LayoutInflater.from(mContext).inflate(R.layout.layout_chatter_topic_header, mTopicListView, false);
        mTopicConfirmTextView = (TextView) header.findViewById(R.id.topic_confirm_text_view);
        mTopicEditText = (EditText) header.findViewById(R.id.topic_edit_text);
        mTopicListView.addHeaderView(header);
        mTopicAdapter = new ChatterTopicAdapter(mContext);
        mTopicListView.setAdapter(mTopicAdapter);
    }

    @OnClick(R.id.bottom_topic_layout)
    void onTopicClicked() {
        mPresenter.showTopic();
    }

    @OnClick(R.id.bottom_anonymous_layout)
    void onAnonymousClicked() {
        mPresenter.setAnonymous();
    }

    @OnClick(R.id.bottom_photo_layout)
    void onPhotoClicked() {
        mPresenter.takeImage();
    }

    @OnClick(R.id.url_layout)
    void onUrlLayoutClicked(){
        mPresenter.showUrlTip();
    }

    private void initListener() {
        // 设置图片
        setRightImage(R.drawable.button_title_send, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.send(mContentEditText.getText().toString());
            }
        });

        mImageGridView.setAddOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.takeImage();
            }
        });
        mTopicConfirmTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic = mTopicEditText.getText().toString().replace("#", "").replace(" ", "").trim();
                mPresenter.onTopicConfirmed(topic);
            }
        });
        mTopicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatterTopic chatterTopic = (ChatterTopic) parent.getAdapter().getItem(position);
                mPresenter.onTopicClicked(chatterTopic);
            }
        });
        mVideoImageView.setOnImageDeleteListener(new VideoImageView.OnImageDeleteListener() {
            @Override
            public void onDelete() {
                mPresenter.onVideoDeleted();
            }
        });
    }

    @Override
    public void setTopicListVisibility(boolean isVisible) {
        if (!isVisible) {
            mTopicListView.setVisibility(View.GONE);
            mContentEditText.setEnabled(true);
        } else {
            mTopicListView.setVisibility(View.VISIBLE);
            mContentEditText.setEnabled(false); // 防止点击到后面的EditText上
        }
    }

    @Override
    public void setAnonymousCheckBox(boolean isChecked) {
        mAnonymousCheckBox.setChecked(isChecked);
    }

    @Override
    public void takeImage() {
        if (mImageChooser == null) {
            mImageChooser = new ImageChooser(mContext, true, true, false, true);
            mImageChooser.setOnImageChosenListener(new ImageChooser.OnImageChosenListener() {
                @Override
                public void onImageChosen(Bitmap bitmap, int type) {
                    mPresenter.onImageSelected(bitmap, type == ImageChooser.TYPE_VIDEO);
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
        }
        mImageChooser.takeImage(getResources().getString(R.string.add_picture));
    }

    @Override
    public void addImage(Bitmap bitmap) {
        mImageGridView.addImage(bitmap);
    }

    @Override
    public void addVideo(Bitmap bitmap, String path) {
        mVideoImageView.setData(bitmap, null);
    }

    @Override
    public int getMaxImageCount() {
        return mImageGridView.getMaxImageCount();
    }

    @Override
    public List<Bitmap> getCurrentBitmap() {
        if (mImageGridView.getVisibility() == View.VISIBLE) {
            return mImageGridView.getImages();
        } else if (mVideoImageView.getVisibility() == View.VISIBLE) {
            return new ArrayList<Bitmap>() {{
                add(mVideoImageView.getBitmap());
            }};
        } else {
            return null;
        }
    }

    @Override
    public void clearBitmap() {
        mImageGridView.clear();
        mVideoImageView.clear();
    }

    public void onTopicSelected(String content) {
        String temp = mContentEditText.getText().toString();
        mContentEditText.setText("#" + content + "#" + temp.replaceFirst("#[\\s\\S]*#", ""));
    }

    @Override
    public void setImageMode(boolean isVideo) {
        if (isVideo) {
            mVideoImageView.setVisibility(View.VISIBLE);
            mImageGridView.clear();
            mImageGridView.setVisibility(View.GONE);
        } else {
            mImageGridView.setVisibility(View.VISIBLE);
            mVideoImageView.clear();
            mVideoImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTopicSynchronized(List<ChatterTopic> topics) {
        mTopicAdapter.setList(topics);
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void send() {
        mPresenter.send(mContentEditText.getText().toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mImageChooser.onActivityResult(requestCode, resultCode, data);
    }

}
