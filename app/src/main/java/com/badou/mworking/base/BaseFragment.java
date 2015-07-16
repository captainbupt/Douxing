package com.badou.mworking.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.WaitProgressDialog;

public class BaseFragment extends Fragment {
    protected Context mContext;
    protected Activity mActivity;
    protected WaitProgressDialog mProgressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = activity;
        mProgressDialog = new WaitProgressDialog(mActivity);
    }

    public void showToast(int resId) {
        ToastUtil.showToast(mContext, resId);
    }

    public void showToast(String message) {
        ToastUtil.showToast(mContext, message);
    }

    public boolean onBackPressed() {
        return false;
    }

    public void showProgressDialog(int resId) {
        mProgressDialog.setContent(resId);
        mProgressDialog.show();
    }

    public void showProgressDialog(String message) {
        mProgressDialog.setContent(message);
        mProgressDialog.show();
    }

    public void showProgressDialog() {
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        mProgressDialog.dismiss();
    }

}
