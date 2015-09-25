package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.presenter.ask.AskDetailPresenter;
import com.badou.mworking.util.DensityUtil;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.UriUtil;
import com.badou.mworking.view.ask.AskDetailView;
import com.badou.mworking.widget.DividerItemDecoration;
import com.badou.mworking.widget.HeaderViewRecyclerAdapter;
import com.badou.mworking.widget.NoneResultView;
import com.badou.mworking.widget.VerticalSpaceItemDecoration;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 问答详情页面
 */
public class AskDetailActivity extends BaseBackActionBarActivity implements AskDetailView {

    private static final String KEY_ASK = "ask";

    AskAnswerAdapter mAnswerAdapter;

    AskDetailPresenter mPresenter;
    @Bind(R.id.subject_text_view)
    TextView mSubjectTextView;
    @Bind(R.id.time_text_view)
    TextView mTimeTextView;
    @Bind(R.id.content_text_view)
    TextView mContentTextView;
    @Bind(R.id.content_image_view)
    SimpleDraweeView mContentImageView;
    @Bind(R.id.head_image_view)
    SimpleDraweeView mHeadImageView;
    @Bind(R.id.name_text_view)
    TextView mNameTextView;
    @Bind(R.id.message_text_view)
    TextView mMessageTextView;
    @Bind(R.id.delete_text_view)
    TextView mDeleteTextView;
    ImageView mStoreImageView;

    RecyclerView mContentListView;
    NoneResultView mNoneResultView;
    PtrClassicFrameLayout mPtrClassicFrameLayout;
    LinearLayout mReplyLayout;


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
        mContentListView = (RecyclerView) findViewById(R.id.content_list_view);
        mReplyLayout = (LinearLayout) findViewById(R.id.reply_layout);
        mPtrClassicFrameLayout = (PtrClassicFrameLayout) findViewById(R.id.ptr_classic_frame_layout);

        mPtrClassicFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                mPresenter.loadMore();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.refresh();
            }
        });

        mReplyLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.submitReply();
            }
        });

        mContentListView.setLayoutManager(new LinearLayoutManager(mContext));
        mContentListView.addItemDecoration(new DividerItemDecoration(mContext));
    }

    private View getHeaderView(ViewGroup parentView) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.header_ask_detail, parentView, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private View getFooterView() {
        NoneResultView noneResultView = new NoneResultView(mContext);
        noneResultView.setContent(-1, R.string.none_result_reply);
        noneResultView.setGravity(Gravity.CENTER_HORIZONTAL);
        noneResultView.setPadding(0, DensityUtil.getInstance().getOffsetXlarge(), 0, DensityUtil.getInstance().getOffsetXlarge());
        noneResultView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return noneResultView;
    }

    @Override
    public void setData(Ask ask) {
        mStoreImageView = getDefaultImageView(mContext, ask.isStore() ? R.drawable.button_title_store_checked : R.drawable.button_title_store_unchecked);
        addTitleRightView(mStoreImageView, new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onStoreClicked();
            }
        });
        mAnswerAdapter = new AskAnswerAdapter(AskDetailActivity.this, ask.getAid(), ask.getCount(), new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mPresenter.copy(getItem((int) v.getTag()));
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
                mPresenter.submitReply(getItem((int) v.getTag()));
            }
        });
        HeaderViewRecyclerAdapter headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mAnswerAdapter);
        headerViewRecyclerAdapter.addHeaderView(getHeaderView(mContentListView));
        mNoneResultView = (NoneResultView) getFooterView();
        headerViewRecyclerAdapter.addFooterView(mNoneResultView);
        mContentListView.setAdapter(headerViewRecyclerAdapter);

        mSubjectTextView.setText(ask.getSubject());
        mContentTextView.setText(ask.getContent());
        mTimeTextView.append(TimeTransfer.long2StringDetailDate(mContext, ask.getCreateTime()));
        mNameTextView.setText(ask.getUserName());
        mHeadImageView.setImageURI(UriUtil.getHttpUri(ask.getUserHeadUrl()));

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
            mContentImageView.setImageURI(UriUtil.getHttpUri(ask.getContentImageUrl()));
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
        mNoneResultView.setVisibility(View.GONE);
    }

    @Override
    public void hideNoneResult() {
        mNoneResultView.setVisibility(View.GONE);
    }

    @Override
    public void disablePullUp() {
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.REFRESH);
    }

    @Override
    public void enablePullUp() {
        mPtrClassicFrameLayout.setMode(PtrFrameLayout.Mode.BOTH);
    }

    @Override
    public void startRefreshing() {
        mPtrClassicFrameLayout.autoRefresh();
        showProgressBar();
    }

    @Override
    public boolean isRefreshing() {
        return mPtrClassicFrameLayout.isRefreshing();
    }

    @Override
    public void refreshComplete() {
        mPtrClassicFrameLayout.refreshComplete();
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
        return mAnswerAdapter.getItemCount();
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
