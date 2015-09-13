package com.badou.mworking;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.badou.mworking.adapter.MessageCenterAdapter;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.presenter.MessageCenterPresenter;
import com.badou.mworking.presenter.Presenter;
import com.badou.mworking.view.MessageCenterView;
import com.badou.mworking.widget.NoneResultView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageCenterActivity extends BaseBackActionBarActivity implements MessageCenterView {

    @Bind(R.id.content_list_view)
    ListView mContentListView;
    @Bind(R.id.none_result_view)
    NoneResultView mNoneResultView;
    MessageCenterAdapter mContentAdapter;

    MessageCenterPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_center);
        setActionbarTitle(R.string.title_name_message_center);
        ButterKnife.bind(this);
        initData();
        mPresenter = (MessageCenterPresenter) super.mPresenter;
        mPresenter.attachView(this);
    }

    @Override
    public Presenter getPresenter() {
        return new MessageCenterPresenter(mContext);
    }

    private void initData() {
        // 点击事件已经集成到adapter中
        mContentAdapter = new MessageCenterAdapter(mContext, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.deleteItem((Integer) v.getTag(), mContentAdapter.getItem((Integer) v.getTag()));
            }
        });
        mContentListView.setAdapter(mContentAdapter);
        mContentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mPresenter.toDetailPage(i, (MessageCenter) adapterView.getAdapter().getItem(i));
            }
        });
        setRightImage(R.drawable.button_title_bar_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.clear();
            }
        });
    }


    @Override
    public void showNoneResult() {
        mNoneResultView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoneResult() {
        mNoneResultView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void disablePullUp() {

    }

    @Override
    public void enablePullUp() {

    }

    @Override
    public void startRefreshing() {

    }

    @Override
    public boolean isRefreshing() {
        return false;
    }

    @Override
    public void refreshComplete() {

    }

    @Override
    public void setData(List<MessageCenter> data) {
        mContentAdapter.setList(data);
    }

    @Override
    public void addData(List<MessageCenter> data) {
        mContentAdapter.addList(data);
    }

    @Override
    public int getDataCount() {
        return mContentAdapter.getCount();
    }

    @Override
    public void setItem(int index, MessageCenter item) {
        mContentAdapter.setItem(index, item);
    }

    @Override
    public MessageCenter getItem(int index) {
        return mContentAdapter.getItem(index);
    }

    @Override
    public void removeItem(int index) {
        mContentAdapter.remove(index);
    }
}
