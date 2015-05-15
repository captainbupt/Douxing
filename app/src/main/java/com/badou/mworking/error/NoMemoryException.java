package com.badou.mworking.error;

/**
 * 类:  <code> NoMemoryException </code>
 * 功能描述: 内存不足异常
 * 创建人: 葛建锋
 * 创建日期: 2013-11-29 下午3:31:31
 * 开发环境: JDK6.0
 */
public class NoMemoryException extends DownloadException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 功能描述:
     * @param message
     */
    public NoMemoryException(String message) {
        super(message);
    }

}
