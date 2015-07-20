package com.badou.mworking.domain;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;

import java.util.List;

import rx.Observable;

public class EMChatCreateGroupUseCase extends UseCase {

    String name;
    String desc;
    String msg;
    List<String> members;

    public EMChatCreateGroupUseCase(String name, String desc, String msg, List<String> members) {
        this.name = name;
        this.desc = desc;
        this.msg = msg;
        this.members = members;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        return RestRepository.getInstance().createEMChatGroup(new Body(UserInfo.getUserInfo().getUid(), name, desc, msg, members));
    }

    public static class Body {
        String uid;
        String name;
        String desc;
        String msg;
        List<String> members;

        public Body(String uid, String name, String desc, String msg, List<String> members) {
            this.uid = uid;
            this.name = name;
            this.desc = desc;
            this.msg = msg;
            this.members = members;
        }
    }

    public static class Response {
        String groupid;

        public String getGroupid() {
            return groupid;
        }
    }

}
