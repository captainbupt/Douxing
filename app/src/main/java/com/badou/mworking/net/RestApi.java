package com.badou.mworking.net;

import com.badou.mworking.domain.ask.AskDeleteUseCase;
import com.badou.mworking.domain.ask.AskListUseCase;
import com.badou.mworking.domain.ask.AskPublishUseCase;
import com.badou.mworking.domain.ask.AskReplyGetUseCase;
import com.badou.mworking.domain.ask.AskReplyPraiseUseCase;
import com.badou.mworking.domain.ask.AskReplySendUseCase;
import com.badou.mworking.domain.ask.AskUseCase;
import com.badou.mworking.domain.category.CategoryCommentGetUseCase;
import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.domain.ChangePasswordUseCase;
import com.badou.mworking.domain.chatter.ChatterListUseCase;
import com.badou.mworking.domain.chatter.ChatterReplyDeleteUseCase;
import com.badou.mworking.domain.chatter.ChatterReplyGetUseCase;
import com.badou.mworking.domain.chatter.ChatterReplySendUseCase;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.EMChatCreateGroupUseCase;
import com.badou.mworking.domain.category.EnrollUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.domain.chatter.ChatterPublishUseCase;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.category.TaskSignUseCase;
import com.badou.mworking.domain.chatter.UrlContentUseCase;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.CategorySearchOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.chatter.ChatterHotOverall;
import com.badou.mworking.entity.chatter.UrlContent;
import com.badou.mworking.entity.comment.CategoryComment;
import com.badou.mworking.entity.comment.ChatterComment;
import com.badou.mworking.entity.comment.CommentOverall;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
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
    Observable<BaseNetEntity<List<Classification>>> getClassification(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("fmt") String format);

    @GET("/sync_v2")
    <T> Observable<BaseNetEntity<CategoryOverall>> getCategoryNotice(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("tag") int tag, @Query("begin") int begin, @Query("limit") int pageNum, @Query("key") String key);

    @GET("/sync_v2")
    <T> Observable<BaseNetEntity<CategoryOverall>> getCategoryNotice(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("type") String type, @Query("tag") int tag, @Query("begin") int begin, @Query("limit") int pageNum, @Query("key") String key, @Query("done") int done);

    @POST("/getmc2")
    Observable<BaseNetEntity<List<Train.TrainingCommentInfo>>> getTrainCommentInfo(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @retrofit.http.Body List<String> rids);

    @POST("/getcomment")
    Observable<BaseNetEntity<CommentOverall<CategoryComment>>> getCategoryComment(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body CategoryCommentGetUseCase.Body body);

    @POST("/comment")
    Observable<BaseNetEntity> sendCategoryComment(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @retrofit.http.Body TypedString comment);

    @POST("/comment")
    Observable<BaseNetEntity> sendCategoryComment(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @Query("whom") String whom, @retrofit.http.Body TypedString comment);

    @POST("/credit")
    Observable<BaseNetEntity> rateCategory(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @Query("credit") int credit);

    @POST("/viewres")
    Observable<BaseNetEntity<CategoryDetail>> getCategoryDetail(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body CategoryDetailUseCase.Body body);

    @GET("/search")
    Observable<BaseNetEntity<CategorySearchOverall>> getSearchResult(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query("uid") String uid, @Query("key") String key);

    @GET("/markread")
    Observable<BaseNetEntity> markRead(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid);

    @GET("/getstore")
    Observable<BaseNetEntity<List<Store>>> getStoreList(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("page_no") int pageNum, @Query("item_per_page") int itemNum);

    @POST("/delstore")
    Observable<BaseNetEntity> deleteStore(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body StoreUseCase.Body body);

    @POST("/addstore")
    Observable<BaseNetEntity> addStore(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body StoreUseCase.Body body);

    @POST("/checkin_v2")
    Observable<BaseNetEntity> taskSign(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @Query("lat") double latitude, @Query("lon") double longitude, @retrofit.http.Body TypedFile file);

    @POST("/checkin_v2")
    Observable<BaseNetEntity> taskSign(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("rid") String rid, @Query("lat") double latitude, @Query("lon") double longitude);

    @POST("/checkin_qr")
    Observable<BaseNetEntity> taskSign(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body TaskSignUseCase.Body body);

    @POST("/enroll")
    Observable<BaseNetEntity> enroll(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body EnrollUseCase.Body body);

    @POST("/genhxgrp")
    Observable<BaseNetEntity<EMChatCreateGroupUseCase.Response>> createEMChatGroup(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @retrofit.http.Body EMChatCreateGroupUseCase.Body body);

    @POST("/viewusr")
    Observable<BaseNetEntity<UserDetail>> getUserDetail(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid);

    @POST("/setimg")
    Observable<BaseNetEntity> setUserHead(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Body TypedFile imgFile);

    @POST("/publish")
    Observable<BaseNetEntity<ChatterPublishUseCase.Response>> publishChatter(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body ChatterPublishUseCase.Body body);

    @GET("/getTopicList")
    Observable<BaseNetEntity<LinkedTreeMap>> getTopicList(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid);

    @POST("/pubphoto")
    Observable<BaseNetEntity> publicChatterImage(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("qid") String qid, @Query("idx") int index, @Body TypedFile imgFile);

    @POST("/pubweburl")
    Observable<BaseNetEntity> publicChatterUrl(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("qid") String qid, @Body TypedString url);

    @POST("/pubvideo")
    Observable<BaseNetEntity> publicChatterVideo(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("qid") String qid, @Body TypedFile videoFile);

    @POST("/getpublish")
    Observable<BaseNetEntity<ChatterListUseCase.Response>> getChatterList(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body ChatterListUseCase.Body body);

    @GET("/getTopicInfo")
    Observable<BaseNetEntity<ChatterListUseCase.Response>> getChatterList(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("topic") String topic, @Query("page_no") int page_no, @Query("item_per_page") int item_per_page);

    @POST("/viewqas")
    Observable<BaseNetEntity<ChatterListUseCase.Response>> getChatterListUser(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body ChatterListUseCase.Body body);

    @POST("/parseurl")
    Observable<BaseNetEntity<UrlContent>> parseUrlContent(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body UrlContentUseCase.Body body);

    @GET("/getonepub")
    Observable<BaseNetEntity<Chatter>> getChatter(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("qid") String qid);

    @GET("/delpublish")
    Observable<BaseNetEntity> deleteChatter(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("qid") String qid);

    @POST("/getreply")
    Observable<BaseNetEntity<CommentOverall<ChatterComment>>> getChatterReply(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body ChatterReplyGetUseCase.Body body);

    @POST("/reply")
    Observable<BaseNetEntity> replyChatter(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body ChatterReplySendUseCase.Body body);

    @POST("/replyat")
    Observable<BaseNetEntity> replyChatterAt(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body ChatterReplySendUseCase.Body body);

    @POST("/delreply")
    Observable<BaseNetEntity> deleteChatterReply(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body ChatterReplyDeleteUseCase.Body body);

    @GET("/setCredit")
    Observable<BaseNetEntity> praiseChatter(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("qid") String qid);

    @GET("/getdaren")
    Observable<BaseNetEntity<ChatterHotOverall>> getChatterHotList(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Query(PARAMS_UID) String uid, @Query("page_no") int pageNum, @Query("item_per_page") int itemNum);

    @POST("/getask")
    Observable<BaseNetEntity<List<Ask>>> getAskList(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body AskListUseCase.Body body);

    @POST("/pubask")
    Observable<BaseNetEntity<AskPublishUseCase.Response>> publishAsk(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body AskPublishUseCase.Body body);

    @POST("/getoneask")
    Observable<BaseNetEntity<Ask>> getAsk(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body AskUseCase.Body body);

    @POST("/getanswer")
    Observable<BaseNetEntity<List<Ask>>> getAskReply(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body AskReplyGetUseCase.Body body);

    @POST("/delask")
    Observable<BaseNetEntity> deleteAsk(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body AskDeleteUseCase.Body body);

    @POST("/pollanswer")
    Observable<BaseNetEntity> praiseAnswer(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body AskReplyPraiseUseCase.Body body);

    @POST("/pubanswer")
    Observable<BaseNetEntity> replyAsk(@Query(PARAMS_SYSTEM) String system, @Query(PARAMS_VERSION) String version, @Body AskReplySendUseCase.Body body);
}
