package com.badou.mworking.net;

import com.badou.mworking.domain.ChangePasswordUseCase;
import com.badou.mworking.domain.CheckUpdateUseCase;
import com.badou.mworking.domain.EMChatCreateGroupUseCase;
import com.badou.mworking.domain.ExperienceInfoUseCase;
import com.badou.mworking.domain.LoginUseCase;
import com.badou.mworking.domain.ResetPasswordUseCase;
import com.badou.mworking.domain.StoreUseCase;
import com.badou.mworking.domain.VerificationMessageUseCase;
import com.badou.mworking.domain.ask.AskDeleteUseCase;
import com.badou.mworking.domain.ask.AskListUseCase;
import com.badou.mworking.domain.ask.AskPublishUseCase;
import com.badou.mworking.domain.ask.AskReplyGetUseCase;
import com.badou.mworking.domain.ask.AskReplyPraiseUseCase;
import com.badou.mworking.domain.ask.AskReplySendUseCase;
import com.badou.mworking.domain.ask.AskUseCase;
import com.badou.mworking.domain.category.CategoryCommentGetUseCase;
import com.badou.mworking.domain.category.CategoryDetailUseCase;
import com.badou.mworking.domain.category.EnrollUseCase;
import com.badou.mworking.domain.category.SurveyStatusUseCase;
import com.badou.mworking.domain.category.TaskSignUseCase;
import com.badou.mworking.domain.chatter.ChatterListUseCase;
import com.badou.mworking.domain.chatter.ChatterPublishUseCase;
import com.badou.mworking.domain.chatter.ChatterReplyDeleteUseCase;
import com.badou.mworking.domain.chatter.ChatterReplyGetUseCase;
import com.badou.mworking.domain.chatter.ChatterReplySendUseCase;
import com.badou.mworking.domain.chatter.UrlContentUseCase;
import com.badou.mworking.domain.emchat.EmchatListGetUseCase;
import com.badou.mworking.domain.emchat.EmchatRegisterUseCase;
import com.badou.mworking.entity.Ask;
import com.badou.mworking.entity.Store;
import com.badou.mworking.entity.category.CategoryBase;
import com.badou.mworking.entity.category.CategoryDetail;
import com.badou.mworking.entity.category.CategoryOverall;
import com.badou.mworking.entity.category.CategorySearchOverall;
import com.badou.mworking.entity.category.Classification;
import com.badou.mworking.entity.category.Train;
import com.badou.mworking.entity.chatter.Chatter;
import com.badou.mworking.entity.chatter.ChatterHotOverall;
import com.badou.mworking.entity.chatter.UrlContent;
import com.badou.mworking.entity.comment.CategoryComment;
import com.badou.mworking.entity.comment.ChatterComment;
import com.badou.mworking.entity.comment.CommentOverall;
import com.badou.mworking.entity.emchat.ContactList;
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

public interface OpenRestApi {
    @POST("/eapi")
    Observable<BaseNetEntity> sendExperienceInfo(@Body ExperienceInfoUseCase.Body body);

    @POST("/sapi")
    Observable<BaseNetEntity<LinkedTreeMap<String, Integer>>> getSurveyStatus(@Body SurveyStatusUseCase.Body body);
}
