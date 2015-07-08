package com.badou.mworking.net;


import com.badou.mworking.base.AppApplication;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.main.MainData;
import com.badou.mworking.entity.user.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
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

/*    public Observable<String> changePassword(String oldPassword, String newPassword) {
        return restApi.changePassword(AppApplication.SYSPARAM, AppApplication.appVersion, UserInfo.getUserInfo().getUid(), oldPassword, newPassword);
    }*/

    public Observable<BaseNetEntity<UserInfo>> login(LoginUseCase.Login login) {
        return restApi.login(AppApplication.SYSPARAM, AppApplication.appVersion, login);
    }

    public Observable<BaseNetEntity<MainData>> checkUpdate(String uid, String screen, CheckUpdateUseCase.UpdateInfo updateInfo) {
        return restApi.checkUpdate(AppApplication.SYSPARAM, AppApplication.appVersion, uid, screen, updateInfo);
    }

}
