package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badou.mworking.adapter.AskAnswerAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.net.bitmap.ImageViewLoader;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.ask.AskDetailPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.view.ask.AskDetailView;
import com.badou.mworking.widget.NoneResultView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 问答详情页面
 */
public class AskDetailActivity extends BaseBackActionBarActivity implements AskDetailView {

    private static final String KEY_ASK = "ask";

    AskAnswerAdapter mAnswerAdapter;

    PullToRefreshListView mContentListView;
    LinearLayout mBottomReplyLayout;  //回复

    NoneResultView mNoneResultView;
    AskDetailPresenter mPresenter;
    @Bind(R.id.subject_text_view)
    TextView mSubjectTextView;
    @Bind(R.id.time_text_view)
    TextView mTimeTextView;
    @Bind(R.id.content_text_view)
    TextView mContentTextView;
    @Bind(R.id.content_image_view)
    ImageView mContentImageView;
    @Bind(R.id.head_image_view)
    ImageView mHeadImageView;
    @Bind(R.id.name_text_view)
    TextView mNameTextView;
    @Bind(R.id.message_text_view)
    TextView mMessageTextView;
    @Bind(R.id.delete_text_view)
    TextView mDeleteTextView;
    ImageView mStoreImageView;

    public static Intent getIntent(Context context, String aid) {
        Intent intent = new Intent(context, AskDetailActivity.class);
        intent.putExtra(KEY_ASK, aid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionbarTitle(R.string.ask_title_detail);
        setContentView(R.layout.activity_ask_detail);
        initView();
        mPresenter = (AskDetailPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        if (!mReceivedIntent.hasExtra(KEY_ASK))
            finish();
        return new AskDetailPresenter(mContext, mReceivedIntent.getStringExtra(KEY_ASK));
    }

    /**
     * 初始化
     */
    private void initView() {
        mContentListView = (PullToRefreshListView) findViewById(R.id.content_list_view);
        mBottomReplyLayout = (LinearLayout) findViewById(R.id.reply_layout);

        mNoneResultView = new NoneResultView(mContext);
        mNoneResultView.setContent(-1, R.string.none_result_reply);
        mNoneResultView.setGravity(Gravity.CENTER_HORIZONTAL);
        mNoneResultView.setPadding(0, DensityUtil.getInstance().getOffsetXlarge(), 0, DensityUtil.getInstance().getOffsetXlarge());
        mContentListView.getRefreshableView().addFooterView(mNoneResultView, null, false);

        mContentListView.getRefreshableView().addHeaderView(getHeaderView(mContentListView.getRefreshableView()), null, false);

        mContentListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPresenter.refresh();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPresenter.loadMore();
            }
        });

        mBottomReplyLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.submitReply();
            }
        });
    }

    /**
     * 功能描述:发送回复TextView设置监听,pullToRefreshScrollView设置下拉刷新监听
     */
    private View getHeaderView(ViewGroup parentView) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.header_ask_detail, parentView, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void setData(Ask ask) {
        mStoreImageView = getDefaultImageView(mContext, ask.isStore() ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
        addTitleRightView(mStoreImageView, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStoreClicked();
            }
        });
        ImageViewLoader.setSquareImageViewResource(mContentImageView, R.drawable.icon_image_default, ask.getContentImageUrl(), getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));
        mAnswerAdapter = new AskAnswerAdapter(AskDetailActivity.this, ask.getAid(), ask.getCount(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.copy(getItem((int) v.getTag(R.id.tag_position)));
                return true;
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.praise(getItem((int) v.getTag()), (int) v.getTag());
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showFullImage(getItem((int) v.getTag()).getContentImageUrl());
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.submitReply(getItem((int) v.getTag(R.id.tag_position)));
            }
        });
        mContentListView.setAdapter(mAnswerAdapter);

        mSubjectTextView.setText(ask.getSubject());
        mContentTextView.setText(ask.getContent());
        mTimeTextView.append(TimeTransfer.long2StringDetailDate(mContext, ask.getCreateTime()));
        mNameTextView.setText(ask.getUserName());

        ImageViewLoader.setCircleImageViewResource(mHeadImageView, ask.getUserHeadUrl(), getResources().getDimensionPixelSize(R.dimen.icon_head_size_small));

        // 点击图片放大显示
        mContentImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showAskFullImage();
            }
        });

        mDeleteTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteAsk();
            }
        });

        mMessageTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.toMessage();
            }
        });

        if (!TextUtils.isEmpty(ask.getContentImageUrl()))
            ImageViewLoader.setSquareImageViewResource(mContentImageView, R.drawable.icon_image_default, ask.getContentImageUrl(), getResources().getDimensionPixelSize(R.dimen.icon_size_xlarge));
        else
            mContentImageView.setVisibility(View.GONE);
        if (ask.getUserName().equals("我")) {
            mMessageTextView.setVisibility(View.GONE);
        }
        if (ask.isDeletable()) {
            mDeleteTextView.setVisibility(View.VISIBLE);
        } else {
            mDeleteTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setReplyCount(int count) {
        mAnswerAdapter.setReplyCount(count);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, mPresenter.getResult());
        super.finish();
    }

    @Override
    public void showNoneResult() {
        mContentListView.getRefreshableView().addFooterView(mNoneResultView, null, false);
    }

    @Override
    public void hideNoneResult() {
        mContentListView.getRefreshableView().removeFooterView(mNoneResultView);
    }

    @Override
    public void disablePullUp() {
        mContentListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    public void enablePullUp() {
        mContentListView.setMode(PullToRefreshBase.Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        mContentListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mContentListView.setRefreshing();
        mContentListView.setMode(PullToRefreshBase.Mode.BOTH);
        showProgressBar();
    }

    @Override
    public boolean isRefreshing() {
        return mContentListView.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        mContentListView.onRefreshComplete();
        hideProgressBar();
    }

    @Override
    public void setData(List<Ask> data) {
        mAnswerAdapter.setList(data);
    }

    @Override
    public void addData(List<Ask> data) {
        mAnswerAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mAnswerAdapter.getCount();
    }

    @Override
    public int getAllCount() {
        return mAnswerAdapter.getRelyCount();
    }

    @Override
    public void setItem(int index, Ask item) {
        mAnswerAdapter.setItem(index, item);
    }

    @Override
    public Ask getItem(int index) {
        return mAnswerAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mAnswerAdapter.remove(index);
    }

    @Override
    public void setStore(boolean isStore) {
        mStoreImageView.setImageResource(isStore ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
    }
}
