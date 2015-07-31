package com.badou.mworking.domain.emchat;

import com.badou.mworking.database.EMChatResManager;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.emchat.ContactList;
import com.badou.mworking.entity.emchat.Department;
import com.badou.mworking.entity.emchat.EMChatEntity;
import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.BaseNetEntity;
import com.badou.mworking.net.RestRepository;
import com.google.gson.annotations.SerializedName;

import rx.Observable;
import rx.functions.Func1;

public class EmchatListGetUseCase extends UseCase {
    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().getContactList(new Body(UserInfo.getUserInfo().getUid())).map(new Func1<BaseNetEntity<ContactList>, BaseNetEntity<ContactList>>() {
            @Override
            public BaseNetEntity<ContactList> call(BaseNetEntity<ContactList> contactListBaseNetEntity) {
                if (contactListBaseNetEntity.getErrcode() == 0) {
                    ContactList data = contactListBaseNetEntity.getData();
                    for (Department root : data.getDepartmentList()) {
                        setParent(root);
                    }
                    EMChatResManager.insertContacts(data.getUserList());
                    EMChatResManager.insertDepartments(data.getDepartmentList());
                    EMChatResManager.insertRoles(data.getRoleList());
                    EMChatEntity.getInstance().setContactList(null);
                    EMChatEntity.getInstance().getContactList();
                }
                return contactListBaseNetEntity;
            }
        });
    }

    public void setParent(Department parent) {
        for (Department son : parent.getSonList()) {
            son.setParent(parent.getParent());
            setParent(son);
        }
    }

    public static class Body {
        @SerializedName("uid")
        String uid;

        public Body(String uid) {
            this.uid = uid;
        }
    }
}
