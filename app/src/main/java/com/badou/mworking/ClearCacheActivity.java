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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.badou.mworking.util.FileUtils;
import com.badou.mworking.widget.SwipeBackLayout;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

/**
 * 类:  <code> ClearCacheActivity </code>
 * 功能描述: 清除缓存页面
 * 创建人:  葛建锋
 * 创建日期: 2015年1月22日 下午1:54:47
 * 开发环境: JDK7.0
 */
public class ClearCacheActivity extends Activity implements OnClickListener{
	
	private TextView txtActionbarTitle;   //标题
	private TextView weipeixunCacheTv;  //微培训缓存大小
	private TextView tongshiquanCacheTv; //同事圈缓存大小
	private TextView weipeixunCacheDel; //微培训缓存删除
	private TextView tongshiquanCacheDel; //微培训缓存删除
	
	private ImageView ivActionbarLeft;
	
	private LinearLayout weipeixunCacheLay; //微培训清除缓存
	private LinearLayout tongshiquanLay;
	
	private String weiPeiXunFileStr = "";   //微培训缓存路径
	private String tongSHQFileStr = "";  // 同事圈缓存路径

	private SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clearcacheactivity);
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		init();
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 功能描述:初始化
	 */
	private void init(){
		ivActionbarLeft = (ImageView) findViewById(R.id.iv_actionbar_left);
		txtActionbarTitle = (TextView) findViewById(R.id.txt_actionbar_title);
		weipeixunCacheLay = (LinearLayout) findViewById(R.id.weipeixun_cache_lay);
		tongshiquanLay = (LinearLayout) findViewById(R.id.tongshiquan_lay);
		weipeixunCacheTv = (TextView) findViewById(R.id.weipeixun_cache_tv);
		tongshiquanCacheTv = (TextView) findViewById(R.id.tongshiquan_cache_tv);
		weipeixunCacheDel = (TextView) findViewById(R.id.weipeixun_cache_del);
		tongshiquanCacheDel = (TextView) findViewById(R.id.tongshiquan_cache_del);
		weipeixunCacheLay.setOnClickListener(this);
		tongshiquanLay.setOnClickListener(this);
		ivActionbarLeft.setOnClickListener(this);
		weipeixunCacheDel.setOnClickListener(this);
		tongshiquanCacheDel.setOnClickListener(this);
		txtActionbarTitle.setText(getResources().getString(R.string.clear_cache));
		
		weiPeiXunFileStr = FileUtils.getTrainCacheDir(this);
		tongSHQFileStr = FileUtils.getTongSHQDir(this);
		
		weipeixunCacheTv.setText(getCacheSize(weiPeiXunFileStr));
		tongshiquanCacheTv.setText(getCacheSize(tongSHQFileStr));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_left:
			ClearCacheActivity.this.finish();
			break;
		case R.id.tongshiquan_lay:
			break;
		case R.id.weipeixun_cache_lay:
			break;
		case R.id.tongshiquan_cache_del:
			File tongSHQFile = new File(tongSHQFileStr);
			showClearCacheDilog("确定清除吗？清除之后，同事圈视屏需要重新下载",tongSHQFile,"t");
			break;
		case R.id.weipeixun_cache_del:
			File weipeixunFile = new File(weiPeiXunFileStr);
			showClearCacheDilog("确定清除吗？清除之后，微培训课件需要重新下载",weipeixunFile,"w");
			break;
		default:
			break;
		}
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
									weipeixunCacheTv.setText("0.0M");
								}else if("t".equals(modules)){
									tongshiquanCacheTv.setText("0.0M");
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
