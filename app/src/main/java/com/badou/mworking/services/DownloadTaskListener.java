package com.badou.mworking.services;

/**
 * 类:  <code> DownloadTaskListener </code>
 * 功能描述:  下载监听器
 * 创建人: 葛建锋
 * 创建日期: 2013-12-2 上午11:40:43
 * 开发环境: JDK6.0
 */
public interface DownloadTaskListener {

	/**
	 * 更新下载进度条
	 * */
    public void updateProcess(DownloadTask task);
    
    /**
     * 结束下载
     * */
    public void finishDownload(DownloadTask task);

    /**
     * 下载之前
     * */
    public void preDownload(DownloadTask task);

    /**
     * 下载出错
     * */
    public void errorDownload(DownloadTask task, Throwable error);
}
