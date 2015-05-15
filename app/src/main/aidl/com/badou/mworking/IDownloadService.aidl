package com.badou.mworking;

interface IDownloadService {
	
	void startManage();
	
	void addTask(String url);
	
	void pauseTask(String url);
	
	void deleteTask(String url);
	
	void continueTask(String url);
}
