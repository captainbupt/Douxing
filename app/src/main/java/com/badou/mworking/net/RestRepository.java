package com.badou.mworking.net;


import android.text.TextUtils;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.domain.CategoryCommentGetUseCase;
import com.badou.mworking.domain.CategoryDetailUseCase;
import com.badou.mworking.domain.CategoryUseCase;
import com.badou.mworking.domain.ChangePasswordUseCase;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.EMChatCreateGroupUseCase;
import com.badou.mworking.domain.EnrollUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.TaskSignUseCase;
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

import retrofit.RestAdapter;
import retrofit.mime.TypedString;
import rx.Observable;

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

    public Observable<BaseNetListEntity<Classification>> getClassification(String uid, String type) {
        return restApi.getClassification(AppApplication.SYSPARAM, AppApplication.appVersion, uid, type, "nest");
    }

    public <T> Observable<BaseNetEntity<CategoryOverall>> getCategory(String uid, String type, int tag, int begin, int pageNum, int done) {
        if (done == CategoryUseCase.TYPE_ALL) {
            return restApi.getCategoryNotice(AppApplication.SYSPARAM, AppApplication.appVersion, uid, type, tag, begin, pageNum, "");
        } else {
            return restApi.getCategoryNotice(AppApplication.SYSPARAM, AppApplication.appVersion, uid, type, tag, begin, pageNum, "", done == CategoryUseCase.TYPE_READ ? 1 : 0);
        }
    }

    public Observable<BaseNetListEntity<Train.TrainingCommentInfo>> getTrainCommentInfo(String uid, List<String> rids) {
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
            return restApi.taskSign(AppApplication.SYSPARAM, AppApplication.appVersion, uid, rid, latitude, longitude, file);
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
}
