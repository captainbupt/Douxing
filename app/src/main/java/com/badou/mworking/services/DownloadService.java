package com.badou.mworking.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.badou.mworking.IDownloadService;
import com.badou.mworking.util.MyIntents;

/**
 * 类:  <code> DownloadService </code>
 * 功能描述: 
 * 创建人: 葛建锋
 * 创建日期: 2013-12-2 下午2:52:16
 * 开发环境: JDK6.0
 */
public class DownloadService extends Service {

    private DownloadManager mDownloadManager;

    @Override
    public IBinder onBind(Intent intent) {

        return new DownloadServiceImpl();
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mDownloadManager = new DownloadManager(this);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if(intent == null){
        	return;
        }
        if (intent.getAction().equals("com.badou.mworking.services.IDownloadService")) {
        	
            int type = intent.getIntExtra(MyIntents.TYPE, -1);
            String url;
            switch (type) {
                case MyIntents.Types.START:       // 开始下载  
                    if (!mDownloadManager.isRunning()) {
                        mDownloadManager.startManage();
                    } else {
                        mDownloadManager.reBroadcastAddAllTask();
                    }
                    break;
                case MyIntents.Types.ADD:       //添加下载
                    url = intent.getStringExtra(MyIntents.URL);
                    if (!TextUtils.isEmpty(url) && !mDownloadManager.hasTask(url)) {
                        mDownloadManager.addTask(url);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private class DownloadServiceImpl extends IDownloadService.Stub {

        @Override
        public void startManage() throws RemoteException {
            mDownloadManager.startManage();
        }

        @Override
        public void addTask(String url) throws RemoteException {
            mDownloadManager.addTask(url);
        }

        @Override
        public void pauseTask(String url) throws RemoteException {

        }

        @Override
        public void deleteTask(String url) throws RemoteException {

        }

        @Override
        public void continueTask(String url) throws RemoteException {

        }
    }
}
