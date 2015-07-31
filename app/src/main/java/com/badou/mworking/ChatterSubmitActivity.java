package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badou.mworking.adapter.ChatterTopicAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.chatter.ChatterTopic;
import com.badou.mworking.entity.chatter.UrlContent;
import com.badou.mworking.presenter.chatter.ChatterSubmitPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.util.ImageChooser;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.chatter.ChatterSubmitView;
import com.badou.mworking.widget.ChatterUrlView;
import com.badou.mworking.widget.MultiImageEditGridView;
import com.badou.mworking.widget.VideoImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 功能描述: 同事圈发送消息界面
 */
public class ChatterSubmitActivity extends BaseBackActionBarActivity implements ChatterSubmitView {

    private static final String KEY_URL = "url";


    ChatterTopicAdapter mTopicAdapter;
    TextView mTopicConfirmTextView;
    EditText mTopicEditText;

    ImageChooser mImageChooser;
    ChatterSubmitPresenter mPresenter;
    @Bind(R.id.content_edit_text)
    EditText mContentEditText;
    @Bind(R.id.url_left_text_view)
    TextView mUrlLeftTextView;
    @Bind(R.id.url_right_image_view)
    ImageView mUrlRightImageView;
    @Bind(R.id.url_content_layout)
    ChatterUrlView mUrlContentLayout;
    @Bind(R.id.url_layout)
    RelativeLayout mUrlLayout;
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
    @Bind(R.id.bottom_photo_image_view)
    ImageView mBottomPhotoImageView;
    @Bind(R.id.bottom_photo_text_view)
    TextView mBottomPhotoTextView;
    @Bind(R.id.bottom_photo_layout)
    LinearLayout mBottomPhotoLayout;
    @Bind(R.id.topic_list_view)
    ListView mTopicListView;

    public static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, ChatterSubmitActivity.class);
        intent.putExtra(KEY_URL, url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatter_submit);
        ButterKnife.bind(this);
        initView();
        initListener();
        this.mPresenter = (ChatterSubmitPresenter) super.mPresenter;
        mPresenter.attachView(this);
        mPresenter.setUrl(mReceivedIntent.getStringExtra(KEY_URL));
    }

    @Override
    public Presenter getPresenter() {
        return new ChatterSubmitPresenter(mContext);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(KEY_URL) && mPresenter != null) {
            mPresenter.setUrl(intent.getStringExtra(KEY_URL));
        }
        mReceivedIntent = intent;
    }

    private void initView() {
        setActionbarTitle(R.string.chatter_submit_title_share);
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
    void onUrlLayoutClicked() {
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
    public void setUrlContent(UrlContent urlContent) {
        mUrlContentLayout.setData(urlContent);
    }

    @Override
    public void setModeUrl() {
        mImageGridView.setVisibility(View.INVISIBLE);
        mBottomPhotoLayout.setEnabled(false);
        mUrlContentLayout.setVisibility(View.VISIBLE);
        mBottomPhotoTextView.setTextColor(0xffc6c6c6);
        mBottomPhotoImageView.setImageResource(R.drawable.icon_bottom_photo_disable);
        mUrlRightImageView.setEnabled(true);
        mUrlRightImageView.setImageResource(R.drawable.button_chatter_submit_url_cancel);
        mUrlRightImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancelUrlSharing();
            }
        });
    }

    @Override
    public void setModeNormal() {
        clearBitmap();
        mImageGridView.setVisibility(View.VISIBLE);
        mBottomPhotoLayout.setEnabled(true);
        mUrlRightImageView.setImageResource(R.drawable.chatter_submit_arrow_url);
        mBottomPhotoTextView.setTextColor(getResources().getColor(R.color.color_text_black));
        mBottomPhotoImageView.setImageResource(R.drawable.icon_bottom_photo);
        mUrlContentLayout.setVisibility(View.GONE);
        mUrlRightImageView.setEnabled(false);
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
