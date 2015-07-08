package com.badou.mworking.net;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.user.UserInfo;

import org.json.JSONObject;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;
import rx.Observable;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RestApi {

    String PARAMS_SYSTEM = "sys";
    String PARAMS_VERSION = "ver";
    String PARAMS_UID = "uid";

    /*    @POST("/chgpwd")
        Observable<BaseNetEntity<>> changePassword(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Field(PARAMS_UID) String uid, @Field("oldpwd") String oldPassword, @Field("newpwd") String newPassword);*/
    @POST("/login")
    Observable<BaseNetEntity<UserInfo>> login(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body LoginUseCase.Login login);

    @POST("/chkupd")
    Observable<BaseNetEntity<MainData>> checkUpdate(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("screen") String screen, @Body CheckUpdateUseCase.UpdateInfo updateInfo);
}
