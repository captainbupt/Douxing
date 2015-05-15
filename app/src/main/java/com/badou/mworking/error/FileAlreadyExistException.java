package com.badou.mworking.error;

/**
 * 类:  <code> FileAlreadyExistException </code>
 * 功能描述: 文件已存在异常
 * 创建人: 葛建锋
 * 创建日期: 2013-11-29 下午3:26:45
 * 开发环境: JDK6.0
 */
public class FileAlreadyExistException extends DownloadException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 功能描述:
     * @param message
     */
    public FileAlreadyExistException(String message) {
        super(message);
    }

}
