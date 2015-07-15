package com.badou.mworking.net;

import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.user.UserInfo;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.POST;
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

    @GET("/gettaglist")
    Observable<BaseNetListEntity<Classification>> getClassification(@Query((PARAMS_SYSTEM)) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("fmt") String format);

    @GET("/sync_v2")
    <T> Observable<BaseNetEntity<CategoryOverall>> getCategoryNotice(@Query((PARAMS_SYSTEM)) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("tag") int tag, @Query("begin") int begin, @Query("limit") int pageNum, @Query("key") String key);

    @GET("/sync_v2")
    <T> Observable<BaseNetEntity<CategoryOverall>> getCategoryNotice(@Query((PARAMS_SYSTEM)) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("tag") int tag, @Query("begin") int begin, @Query("limit") int pageNum, @Query("key") String key, @Query("done") int done);

    @POST("/getmc2")
    Observable<BaseNetListEntity<Train.TrainingCommentInfo>> getTrainCommentInfo(@Query((PARAMS_SYSTEM)) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Body List<String> rids);
}
