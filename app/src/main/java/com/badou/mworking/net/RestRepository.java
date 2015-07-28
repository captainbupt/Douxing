package com.badou.mworking.net;


import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.domain.category.CategoryCommentGetUseCase;
import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.domain.category.CategoryUseCase;
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
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.chatter.ChatterHotOverall;
import com.badou.mworking.entity.chatter.ChatterTopic;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.CategorySearchOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.chatter.UrlContent;
import com.badou.mworking.entity.comment.CategoryComment;
import com.badou.mworking.entity.comment.ChatterComment;
import com.badou.mworking.entity.comment.CommentOverall;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.user.UserDetail;
import com.badou.mworking.entity.user.UserInfo;
import com.google.gson.internal.LinkedTreeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
import rx.Observable;
import rx.functions.Func1;

public class RestRepository {

    public RestApi restApi;
    private static RestRepository restRepository;

    public static RestRepository getInstance() {
        if (restRepository == null)
            restRepository = new RestRepository();
        return restRepository;
    }

    public RestRepository() {
        RestAdapter restApiAdapter = new RestAdapter.Builder()
                .setEndpoint("http://115.28.138.79/badou")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                        //.setConverter(new StringConverter())
                .build();
        restApi = restApiAdapter.create(RestApi.class);
    }

    public Observable<BaseNetEntity> changePassword(ChangePasswordUseCase.Body body) {
        return restApi.changePassword(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity<UserInfo>> login(LoginUseCase.Body login) {
        return restApi.login(AppApplication.SYSPARAM, AppApplication.appVersion, login);
    }

    public Observable<BaseNetEntity<MainData>> checkUpdate(String uid, String screen, CheckUpdateUseCase.Body updateInfo) {
        return restApi.checkUpdate(AppApplication.SYSPARAM, AppApplication.appVersion, uid, screen, updateInfo);
    }

    public Observable<BaseNetEntity<List<Classification>>> getClassification(String uid, String type) {
        return restApi.getClassification(AppApplication.SYSPARAM, AppApplication.appVersion, uid, type, "nest");
    }

    public <T> Observable<BaseNetEntity<CategoryOverall>> getCategory(String uid, String type, int tag, int begin, int pageNum, int done) {
        if (done == CategoryUseCase.TYPE_ALL) {
            return restApi.getCategoryNotice(AppApplication.SYSPARAM, AppApplication.appVersion, uid, type, tag, begin, pageNum, "");
        } else {
            return restApi.getCategoryNotice(AppApplication.SYSPARAM, AppApplication.appVersion, uid, type, tag, begin, pageNum, "", done == CategoryUseCase.TYPE_READ ? 1 : 0);
        }
    }

    public Observable<BaseNetEntity<List<Train.TrainingCommentInfo>>> getTrainCommentInfo(String uid, List<String> rids) {
        return restApi.getTrainCommentInfo(AppApplication.SYSPARAM, AppApplication.appVersion, uid, rids);
    }

    public Observable<BaseNetEntity<CommentOverall<CategoryComment>>> getCategoryComment(CategoryCommentGetUseCase.Body body) {
        return restApi.getCategoryComment(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity> sendCategoryComment(String uid, String rid, String whom, String comment) {
        if (TextUtils.isEmpty(whom)) {
            return restApi.sendCategoryComment(AppApplication.SYSPARAM, AppApplication.appVersion, uid, rid, new TypedString(comment));
        } else {
            return restApi.sendCategoryComment(AppApplication.SYSPARAM, AppApplication.appVersion, uid, rid, whom, new TypedString(comment));
        }
    }

    public Observable<BaseNetEntity<CategoryDetail>> getCategoryDetail(CategoryDetailUseCase.Body body) {
        return restApi.getCategoryDetail(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity<CategorySearchOverall>> getSearchResult(String uid, String key) {
        return restApi.getSearchResult(AppApplication.SYSPARAM, AppApplication.appVersion, uid, key == null ? "" : key.replace(" ", ""));
    }

    public Observable<BaseNetEntity> markRead(String uid, String rid) {
        return restApi.markRead(AppApplication.SYSPARAM, AppApplication.appVersion, uid, rid);
    }

    public Observable<BaseNetEntity> modifyStore(StoreUseCase.Body body, boolean isAdd) {
        if (isAdd) {
            return restApi.addStore(AppApplication.SYSPARAM, AppApplication.appVersion, body);
        } else {
            return restApi.deleteStore(AppApplication.SYSPARAM, AppApplication.appVersion, body);
        }
    }

    public Observable<BaseNetEntity> taskSign(String uid, String rid, double latitude, double longitude, File file) {
        if (file == null) {
            return restApi.taskSign(AppApplication.SYSPARAM, AppApplication.appVersion, uid, rid, latitude, longitude);
        } else {
            return restApi.taskSign(AppApplication.SYSPARAM, AppApplication.appVersion, uid, rid, latitude, longitude, new TypedFile("image/jpg", file));
        }
    }

    public Observable<BaseNetEntity> taskSign(TaskSignUseCase.Body body) {
        return restApi.taskSign(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity> enroll(EnrollUseCase.Body body) {
        return restApi.enroll(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity<EMChatCreateGroupUseCase.Response>> createEMChatGroup(EMChatCreateGroupUseCase.Body body) {
        return restApi.createEMChatGroup(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity<UserDetail>> getUserDetail(String uid) {
        return restApi.getUserDetail(AppApplication.SYSPARAM, AppApplication.appVersion, uid);
    }

    public Observable<BaseNetEntity> setUserHead(String uid, File file) {
        return restApi.setUserHead(AppApplication.SYSPARAM, AppApplication.appVersion, uid, new TypedFile("image/jpg", file));
    }

    public Observable<BaseNetEntity<ChatterPublishUseCase.Response>> publishChatter(ChatterPublishUseCase.Body body) {
        return restApi.publishChatter(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity<List<ChatterTopic>>> getTopicList(String uid) {
        return restApi.getTopicList(AppApplication.SYSPARAM, AppApplication.appVersion, uid).map(new Func1<BaseNetEntity<LinkedTreeMap>, BaseNetEntity<List<ChatterTopic>>>() {
            @Override
            public BaseNetEntity<List<ChatterTopic>> call(BaseNetEntity<LinkedTreeMap> linkedTreeMapBaseNetEntity) {
                List<ChatterTopic> topicList = new ArrayList<>();
                LinkedTreeMap<String, String> data = linkedTreeMapBaseNetEntity.getData();
                for (String key : data.keySet()) {
                    topicList.add(new ChatterTopic(key, Long.parseLong(data.get(key))));
                }
                return new BaseNetEntity<List<ChatterTopic>>(linkedTreeMapBaseNetEntity.getErrcode(), topicList);
            }
        });
    }

    public Observable<BaseNetEntity> publishChatterImage(String uid, String qid, int index, File imgFile) {
        return restApi.publicChatterImage(AppApplication.SYSPARAM, AppApplication.appVersion, uid, qid, index, new TypedFile("image/jpg", imgFile));
    }

    public Observable<BaseNetEntity> publishChatterVideo(String uid, String qid, File imgFile) {
        return restApi.publicChatterVideo(AppApplication.SYSPARAM, AppApplication.appVersion, uid, qid, new TypedFile("image/jpg", imgFile));
    }

    public Observable<BaseNetEntity> publicChatterUrl(String uid, String qid, String url) {
        return restApi.publicChatterUrl(AppApplication.SYSPARAM, AppApplication.appVersion, uid, qid, new TypedString(url));
    }

    public Observable<BaseNetEntity<List<Chatter>>> getChatterList(ChatterListUseCase.Body body, String topic, boolean isUser) {
        Observable<BaseNetEntity<ChatterListUseCase.Response>> responseObservable;
        if (isUser) {
            responseObservable = restApi.getChatterListUser(AppApplication.SYSPARAM, AppApplication.appVersion, body);
        } else if (TextUtils.isEmpty(topic)) {
            responseObservable = restApi.getChatterList(AppApplication.SYSPARAM, AppApplication.appVersion, body);
        } else {
            responseObservable = restApi.getChatterList(AppApplication.SYSPARAM, AppApplication.appVersion, body.getUid(), topic, body.getPageNum(), body.getItemNum());
        }
        return responseObservable.map(new Func1<BaseNetEntity<ChatterListUseCase.Response>, BaseNetEntity<List<Chatter>>>() {
            @Override
            public BaseNetEntity<List<Chatter>> call(BaseNetEntity<ChatterListUseCase.Response> responseBaseNetEntity) {
                return new BaseNetEntity<List<Chatter>>(responseBaseNetEntity.getErrcode(), responseBaseNetEntity.getData().getChatterList());
            }
        });
    }

    public Observable<BaseNetEntity<UrlContent>> parseUrlContent(UrlContentUseCase.Body body, final String url) {
        return restApi.parseUrlContent(AppApplication.SYSPARAM, AppApplication.appVersion, body).map(new Func1<BaseNetEntity<UrlContent>, BaseNetEntity<UrlContent>>() {
            @Override
            public BaseNetEntity<UrlContent> call(BaseNetEntity<UrlContent> urlContentBaseNetEntity) {
                if (urlContentBaseNetEntity.getData() != null)
                    urlContentBaseNetEntity.getData().setUrl(url);
                return urlContentBaseNetEntity;
            }
        });
    }

    public Observable<BaseNetEntity<Chatter>> getChatter(String uid, String qid) {
        return restApi.getChatter(AppApplication.SYSPARAM, AppApplication.appVersion, uid, qid);
    }

    public Observable<BaseNetEntity<CommentOverall<ChatterComment>>> getChatterReply(ChatterReplyGetUseCase.Body body) {
        return restApi.getChatterReply(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity> deleteChatter(String uid, String qid) {
        return restApi.deleteChatter(AppApplication.SYSPARAM, AppApplication.appVersion, uid, qid);
    }

    public Observable<BaseNetEntity> sendChatterReply(ChatterReplySendUseCase.Body body) {
        return restApi.replyChatter(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity> sendChatterReplyAt(ChatterReplySendUseCase.Body body) {
        return restApi.replyChatterAt(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity> deleteChatterReply(ChatterReplyDeleteUseCase.Body body) {
        return restApi.deleteChatterReply(AppApplication.SYSPARAM, AppApplication.appVersion, body);
    }

    public Observable<BaseNetEntity> praiseChatter(String uid, String qid) {
        return restApi.praiseChatter(AppApplication.SYSPARAM, AppApplication.appVersion, uid, qid);
    }

    public Observable<BaseNetEntity<ChatterHotOverall>> getChatterHotList(String uid, int pageNum, int itemNum) {
        return restApi.getChatterHotList(AppApplication.SYSPARAM, AppApplication.appVersion, uid, pageNum, itemNum);
    }

}
