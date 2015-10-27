package com.badou.mworking.factory;

import android.content.Context;
import android.content.Intent;

import com.badou.mworking.AskDetailActivity;
import com.badou.mworking.ChatterDetailActivity;
import com.badou.mworking.R;
import com.badou.mworking.entity.MessageCenter;
import com.badou.mworking.entity.Store;
import com.badou.mworking.util.ToastUtil;

public class ResourceIntentFactory {

    public static Intent getIntentFromStore(Context context, Store store) {
        if (store.getType() == Store.TYPE_NOTICE || store.getType() == Store.TYPE_TRAINING || store.getType() == Store.TYPE_EXAM
                || store.getType() == Store.TYPE_TASK || store.getType() == Store.TYPE_SHELF || store.getType() == Store.TYPE_ENTRY
                || store.getType() == Store.TYPE_PLAN || store.getType() == Store.TYPE_SURVEY) {
            return CategoryIntentFactory.getIntent(context, Store.getCategoryTypeFromStore(store.getType()), store.getSid());
        } else if (store.getType() == Store.TYPE_CHATTER) {
            return ChatterDetailActivity.getIntent(context, store.getSid());
        } else if (store.getType() == Store.TYPE_ASK) {
            return AskDetailActivity.getIntent(context, store.getSid());
        } else {
            return null;
        }
    }

    public static Intent getIntentFromMessage(Context context, MessageCenter message) {
        if (message.getType().equals(MessageCenter.TYPE_NOTICE) || message.getType().equals(MessageCenter.TYPE_EXAM)
                || message.getType().equals(MessageCenter.TYPE_TRAINING) || message.getType().equals(MessageCenter.TYPE_TASK)
                || message.getType().equals(MessageCenter.TYPE_SHELF) || message.getType().equals(MessageCenter.TYPE_ENTRY)
                || message.getType().equals(MessageCenter.TYPE_PLAN) || message.getType().equals(MessageCenter.TYPE_SURVEY)) {
            System.out.println("type: " + message.getType() + ", type int" + message.getCategoryType());
            return CategoryIntentFactory.getIntent(context, message.getCategoryType(), message.getAdd(), true, null);
        } else if (message.getType().equals(MessageCenter.TYPE_CHATTER)) {
            return ChatterDetailActivity.getIntent(context, message.getAdd());
        } else if (message.getType().equals(MessageCenter.TYPE_ASK)) {
            return AskDetailActivity.getIntent(context, message.getAdd());
        } else {
            return null;
        }
    }
}
