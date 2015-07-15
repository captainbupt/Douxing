package com.badou.mworking.net;


import com.badou.mworking.base.AppApplication;
import com.badou.mworking.domain.CategoryUseCase;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.user.UserInfo;

import java.util.List;

import retrofit.RestAdapter;
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

/*    public Observable<String> changePassword(String oldPassword, String newPassword) {
        return restApi.changePassword(AppApplication.SYSPARAM, AppApplication.appVersion, UserInfo.getUserInfo().getUid(), oldPassword, newPassword);
    }*/

    public Observable<BaseNetEntity<UserInfo>> login(LoginUseCase.Login login) {
        return restApi.login(AppApplication.SYSPARAM, AppApplication.appVersion, login);
    }

    public Observable<BaseNetEntity<MainData>> checkUpdate(String uid, String screen, CheckUpdateUseCase.UpdateInfo updateInfo) {
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

}
