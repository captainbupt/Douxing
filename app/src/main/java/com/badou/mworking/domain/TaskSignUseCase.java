package com.badou.mworking.domain;

import android.text.TextUtils;

import com.badou.mworking.entity.user.UserInfo;
import com.badou.mworking.net.RestRepository;
import com.baidu.location.BDLocation;
import com.google.gson.annotations.SerializedName;

import java.io.File;

import rx.Observable;

public class TaskSignUseCase extends UseCase {

    String mRid;
    BDLocation mLocation;
    File mFile;
    String mQRCode;

    public TaskSignUseCase(String rid, BDLocation location, File file) {
        this.mRid = rid;
        this.mLocation = location;
        this.mFile = file;
    }

    public TaskSignUseCase(String rid, String qrcode) {
        this.mRid = rid;
        this.mQRCode = qrcode;
    }

    @Override
    protected Observable buildUseCaseObservable() {
        if (TextUtils.isEmpty(mQRCode)) {
            return RestRepository.getInstance().taskSign(UserInfo.getUserInfo().getUid(), mRid, mLocation.getLatitude(), mLocation.getLongitude(), mFile);
        } else {
            return RestRepository.getInstance().taskSign(new Body(UserInfo.getUserInfo().getUid(), mRid, mQRCode));
        }
    }

    public static class Body {
        @SerializedName("uid")
        String uid;
        @SerializedName("rid")
        String rid;
        @SerializedName("ar")
        String qr;

        public Body(String uid, String rid, String qr) {
            this.uid = uid;
            this.rid = rid;
            this.qr = qr;
        }
    }
}
