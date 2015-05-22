package com.badou.mworking.net;

public abstract class DownloadListener {
	
	public int totalSize = 0;
	public void setTotalSize(int size){
		this.totalSize = size;
		onGetTotalSize(totalSize);
	}
	public int getTotalSize(){
		return totalSize;
	}
	public abstract void onGetTotalSize(int totalSize);
	public abstract void onDownloadSizeChange(int downloadSize);
}
