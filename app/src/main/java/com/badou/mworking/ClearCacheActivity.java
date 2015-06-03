/* 
 * 文件名: ClearCacheActivity.java
 * 包路径: com.badou.mworking
 * 创建描述  
 *        创建人：葛建锋
 *        创建日期：2015年1月22日 下午1:54:47
 *        内容描述：
 * 修改描述  
 *        修改人：葛建锋 
 *        修改日期：2015年1月22日 下午1:54:47 
 *        修改内容:
 * 版本: V1.0   
 */
package com.badou.mworking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.util.FileUtils;

import java.io.File;

/**
 * 功能描述: 清除缓存页面
 */
public class ClearCacheActivity extends BaseBackActionBarActivity{

	private TextView mTrainingCacheTextView;  //微培训缓存大小
	private TextView mChatterCacheTextView; //同事圈缓存大小
	private TextView mTrainingDeleteTextView; //微培训缓存删除
	private TextView mChatterDeleteTextView; //微培训缓存删除

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clear_cache);
		setActionbarTitle(getResources().getString(R.string.about_us_clear_cache));
		initView();
		initListener();
	}
	
	/**
	 * 功能描述:初始化
	 */
	private void initView(){
		mTrainingCacheTextView = (TextView) findViewById(R.id.weipeixun_cache_tv);
		mChatterCacheTextView = (TextView) findViewById(R.id.tongshiquan_cache_tv);
		mTrainingDeleteTextView = (TextView) findViewById(R.id.weipeixun_cache_del);
		mChatterDeleteTextView = (TextView) findViewById(R.id.tongshiquan_cache_del);

		String trainingFileStr = FileUtils.getTrainCacheDir(mContext);//微培训缓存路径
		String chatterFileStr = FileUtils.getTongSHQDir(mContext); // 同事圈缓存路径

		mTrainingCacheTextView.setText(getCacheSize(trainingFileStr));
		mChatterCacheTextView.setText(getCacheSize(chatterFileStr));

	}

	private void initListener(){
		mTrainingDeleteTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				File weipeixunFile = new File(FileUtils.getTrainCacheDir(mContext));
				showClearCacheDilog("确定清除吗？清除之后，微培训课件需要重新下载",weipeixunFile,"w");
			}
		});
		mChatterDeleteTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				File tongSHQFile = new File(FileUtils.getTongSHQDir(mContext));
				showClearCacheDilog("确定清除吗？清除之后，同事圈视屏需要重新下载",tongSHQFile,"t");
			}
		});
	}
	
	/**
	 * 功能描述: 清除缓存dilog
	 * @param message 提醒内容
	 * @param file
	 */
	private void showClearCacheDilog(String message,final File file,final String modules){
		new AlertDialog.Builder(this)
		.setTitle("提示").setMessage(message)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								FileUtils.deleteDir(file);
								if("w".equals(modules)){
									mTrainingCacheTextView.setText("0.0M");
								}else if("t".equals(modules)){
									mChatterCacheTextView.setText("0.0M");
								}
							}
						}).setNegativeButton("取消", null)
				.create().show();
	}
	
	
	/**
	 * 功能描述: 获取缓存大小
	 */
	private String getCacheSize(String fileStr){
		try {
			File weiPeiXunFile = new File(fileStr);
			Long fileSize = FileUtils.getFileSize(weiPeiXunFile);
			return (float)(Math.round((float)fileSize / 1024 / 1024 * 10))/ 10 + "M";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
