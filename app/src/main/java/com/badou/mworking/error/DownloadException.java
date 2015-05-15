package com.badou.mworking.error;

/**
 * 类:  <code> DownloadException </code>
 * 功能描述: 处理下载异常
 * 创建人: 葛建锋
 * 创建日期: 2013-11-29 下午3:27:20
 * 开发环境: JDK6.0
 */
public class DownloadException extends Exception {
    private static final long serialVersionUID = 1L;

    private String mExtra;

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(String message, String extra) {
        super(message);
        mExtra = extra;
    }

    public String getExtra() {
        return mExtra;
    }
}
