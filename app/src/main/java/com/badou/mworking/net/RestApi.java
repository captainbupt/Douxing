package com.badou.mworking.net;

import com.badou.mworking.domain.CategoryCommentGetUseCase;
import com.badou.mworking.domain.CategoryDetailUseCase;
import com.badou.mworking.domain.ChangePasswordUseCase;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.EnrollUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.CategorySearchOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.comment.CategoryComment;
import com.badou.mworking.entity.comment.CommentOverall;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.user.UserInfo;

import java.io.File;
import java.util.List;

import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

public interface RestApi {

    String PARAMS_SYSTEM = "sys";
    String PARAMS_VERSION = "ver";
    String PARAMS_UID = "uid";

    @POST("/chgpwd")
    Observable<BaseNetEntity> changePassword(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body ChangePasswordUseCase.Body body);

    @POST("/login")
    Observable<BaseNetEntity<UserInfo>> login(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body LoginUseCase.Body body);

    @POST("/chkupd")
    Observable<BaseNetEntity<MainData>> checkUpdate(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("screen") String screen, @retrofit.http.Body CheckUpdateUseCase.Body body);

    @GET("/gettaglist")
    Observable<BaseNetListEntity<Classification>> getClassification(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("fmt") String format);

    @GET("/sync_v2")
    <T> Observable<BaseNetEntity<CategoryOverall>> getCategoryNotice(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("tag") int tag, @Query("begin") int begin, @Query("limit") int pageNum, @Query("key") String key);

    @GET("/sync_v2")
    <T> Observable<BaseNetEntity<CategoryOverall>> getCategoryNotice(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("tag") int tag, @Query("begin") int begin, @Query("limit") int pageNum, @Query("key") String key, @Query("done") int done);

    @POST("/getmc2")
    Observable<BaseNetListEntity<Train.TrainingCommentInfo>> getTrainCommentInfo(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @retrofit.http.Body List<String> rids);

    @POST("/getcomment")
    Observable<BaseNetEntity<CommentOverall<CategoryComment>>> getCategoryComment(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body CategoryCommentGetUseCase.Body body);

    @POST("/comment")
    Observable<BaseNetEntity> sendCategoryComment(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @retrofit.http.Body String comment);

    @POST("/comment")
    Observable<BaseNetEntity> sendCategoryComment(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @Query("whom") String whom, @retrofit.http.Body String comment);

    @POST("/viewres")
    Observable<BaseNetEntity<CategoryDetail>> getCategoryDetail(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body CategoryDetailUseCase.Body body);

    @GET("/search")
    Observable<BaseNetEntity<CategorySearchOverall>> getSearchResult(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query("uid") String uid, @Query("key") String key);

    @GET("/markread")
    Observable<BaseNetEntity> markRead(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid);

    @POST("/delstore")
    Observable<BaseNetEntity> deleteStore(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body StoreUseCase.Body body);

    @POST("/addstore")
    Observable<BaseNetEntity> addStore(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body StoreUseCase.Body body);

    @POST("/checkin_v2")
    Observable<BaseNetEntity> taskSign(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @Query("lat") double latitude, @Query("lon") double longitude, @retrofit.http.Body File file);

    @POST("/checkin_v2")
    Observable<BaseNetEntity> taskSign(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @Query("lat") double latitude, @Query("lon") double longitude);

    @POST("/enroll")
    Observable<BaseNetEntity> enroll(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body EnrollUseCase.Body body);
}
