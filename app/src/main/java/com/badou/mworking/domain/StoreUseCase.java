package com.badou.mworking.domain;

import android.content.Context;

import com.badou.mworking.R;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.StoreItem;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.net.RestRepository;
import com.badou.mworking.view.StoreItemView;
import com.google.gson.annotations.SerializedName;

import rx.Observable;

public class StoreUseCase extends UseCase {

    private boolean isAdd;
    private String sid;
    private String type;

    public StoreUseCase(String sid, String type) {
        this.sid = sid;
        this.type = type;
    }

    public StoreUseCase() {
    }

    public void setData(Store store) {
        this.sid = store.getSid();
        this.type = Store.getTypeString(store.getType());
        this.isAdd = false;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().modifyStore(new Body(UserInfo.getUserInfo().getUid(), sid, type), isAdd);
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("sid")
        String sid;
        @SerializedName("type")
        String type;

        public Body(String uid, String sid, String type) {
            this.uid = uid;
            this.sid = sid;
            this.type = type;
        }
    }

    public void onStoreClicked(Context context, final StoreItemView itemView, final StoreItem item) {
        if (item == null) {
            itemView.showToast(R.string.message_wait);
            return;
        }
        if (item.isStore()) {
            itemView.showProgressDialog(R.string.progress_tips_delete_store_ing);
        } else {
            itemView.showProgressDialog(R.string.progress_tips_store_ing);
        }

        setIsAdd(!item.isStore());
        execute(new BaseSubscriber<BaseNetEntity>(context) {
            @Override
            public void onResponseSuccess(BaseNetEntity data) {
                boolean isStore = !item.isStore();
                item.setStore(isStore);
                itemView.setStore(isStore);
                itemView.showToast(isStore ? R.string.store_add_success : R.string.store_cancel_success);
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                itemView.hideProgressDialog();
            }
        });
    }
}
