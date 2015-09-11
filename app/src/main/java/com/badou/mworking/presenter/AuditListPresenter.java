package com.badou.mworking.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.badou.mworking.domain.AuditGetUrlUseCase;
import com.badou.mworking.domain.AuditGetUseCase;
import com.badou.mworking.domain.AuditSetUseCase;
import com.badou.mworking.domain.UseCase;
import com.badou.mworking.entity.Audit;
import com.badou.mworking.net.BaseSubscriber;
import com.badou.mworking.util.SPHelper;
import com.badou.mworking.view.AuditListView;
import com.badou.mworking.view.BaseView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

public class AuditListPresenter extends ListPresenter<Audit> {

    AuditGetUseCase mAuditGetUseCase;
    AuditListView mAuditListView;

    public AuditListPresenter(Context context) {
        super(context);
    }

    @Override
    public void attachView(BaseView v) {
        mAuditListView = (AuditListView) v;
        super.attachView(v);
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<Audit>>() {
        }.getType();
    }

    @Override
    protected String getCacheKey() {
        return null;
    }

    @Override
    protected UseCase getRefreshUseCase(int pageIndex) {
        if (mAuditGetUseCase == null)
            mAuditGetUseCase = new AuditGetUseCase();
        return mAuditGetUseCase;
    }

    @Override
    public void toDetailPage(Audit data) {

    }

    public void setAudit(final int position, boolean isIn) {
        mAuditListView.showProgressDialog();
        Audit audit = mAuditListView.getItem(position);
        new AuditSetUseCase(audit.getPhone(), isIn).execute(new BaseSubscriber(mContext) {
            @Override
            public void onResponseSuccess(Object data) {
                mAuditListView.removeItem(position);
                if (mAuditListView.getDataCount() == 0) {
                    mAuditListView.showNoneResult();
                }
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                mAuditListView.hideProgressDialog();
            }
        });
    }

    public void addAudit() {
        mAuditListView.showProgressDialog();
        new AuditGetUrlUseCase().execute(new BaseSubscriber<AuditGetUrlUseCase.Response>(mContext) {
            @Override
            public void onResponseSuccess(AuditGetUrlUseCase.Response data) {
                shareToWechat(data.getUrl());
            }

            @Override
            public void onError(Throwable e) {
                mAuditListView.hideProgressDialog();
                super.onError(e);
            }
        });
    }

    public void shareToWechat(String url) {
        mAuditListView.hideProgressDialog();
        if (TextUtils.isEmpty(url)) {
            return;
        }
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setTitle("管理员邀请你加入" + SPHelper.getLoginContent());
        sp.setImageUrl(SPHelper.getLoginUrl());
        sp.setText("");
        sp.setUrl(url);
        sp.setShareType(Platform.SHARE_WEBPAGE);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.share(sp);
    }
}
