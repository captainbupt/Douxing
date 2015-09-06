package com.badou.mworking.presenter;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.R;
import com.badou.mworking.database.MessageCenterResManager;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.factory.ResourceIntentFactory;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.view.BaseView;
import com.badou.mworking.view.MessageCenterView;

import java.util.ArrayList;
import java.util.List;

public class MessageCenterPresenter extends Presenter {

    MessageCenterView mMessageCenterView;

    public MessageCenterPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mMessageCenterView = (MessageCenterView) v;
        List<MessageCenter> messageCenterList = MessageCenterResManager.getAllItem();
        if (messageCenterList == null || messageCenterList.size() == 0) {
            mMessageCenterView.showNoneResult();
        } else {
            mMessageCenterView.hideNoneResult();
            mMessageCenterView.setData(MessageCenterResManager.getAllItem());
        }
    }

    public void toDetailPage(int position, MessageCenter messageCenter) {
        deleteItem(position, messageCenter);
        Intent intent = ResourceIntentFactory.getIntentFromMessage(mContext, messageCenter);
        if (intent == null) {
            ToastUtil.showToast(mContext, R.string.category_unsupport_type);
        } else {
            mContext.startActivity(intent);
        }
    }

    public void deleteItem(int position, MessageCenter messageCenter) {
        mMessageCenterView.removeItem(position);
        MessageCenterResManager.deleteItem(messageCenter);
        if (mMessageCenterView.getDataCount() == 0) {
            mMessageCenterView.showNoneResult();
        }
    }

    public void clear() {
        MessageCenterResManager.deleteAll();
        mMessageCenterView.setData(new ArrayList<MessageCenter>());
    }
}
