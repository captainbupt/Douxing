package com.badou.mworking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.badou.mworking.base.BaseFragmentActivity;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * 类:  <code> PhotoActivity </code>
 * 功能描述: 同事圈图片点击之后法放大查看页面
 * 创建人:  葛建锋
 * 创建日期: 2014年7月21日 上午10:04:34
 * 开发环境: JDK7.0
 */
public class PhotoActivity extends BaseFragmentActivity {
	
	public static final String MODE_PICZOMM = "pic_zoom";
	private PhotoView photoView;
	private Context mContext;
	private ImageView downImg; //图片下载
	
	private Bitmap bitmap; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoview);
		mContext = PhotoActivity.this;
		photoView = (PhotoView) findViewById(R.id.photoView);
		downImg = (ImageView) findViewById(R.id.img_down);
		try {
			Intent intent = getIntent();
			String imgUrl = intent.getStringExtra(MODE_PICZOMM);
			String imgpath = intent.getStringExtra("filePath");
			if(imgUrl!=null&&!imgUrl.equals("")){
				bitmap = BitmapLruCache.getBitmapLruCache().getBitmap(imgUrl);
				if (null != bitmap ) {
					photoView.setImageBitmap(bitmap);
				} else {
					MyVolley.getImageLoader().get(
							imgUrl,
							new PicImageListener(mContext,
									photoView, imgUrl));
				}
			}else{
				if(imgpath!=null&&!imgpath.equals("")){
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inSampleSize = 2;
					Bitmap bitmap = BitmapFactory.decodeFile(imgpath, option);
					photoView.setImageBitmap(bitmap);
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
			
		//设置点击监听
		photoView.setOnViewTapListener(new OnViewTapListener() {
			@Override
			public void onViewTap(View view, float x, float y) {
				if (PhotoActivity.this!=null) {
					PhotoActivity.this.finish();
				}
			}
		});
		
		// 下载图片
		downImg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String fileStr = FileUtils.getSDPath(PhotoActivity.this)+"douxing/";
				File file = new File(fileStr);
				if(!file.exists()){
					file.mkdirs();
				}
				if(bitmap!=null){
					String fileName = System.currentTimeMillis()+".png"; 
					FileUtils.writeBitmap2SDcard(bitmap, fileStr+fileName);
					ToastUtil.showToast(PhotoActivity.this,"图片下载成功");
					// 下载完毕之后，发送广播扫描文件，否则的话，相册里面不能及时看到
					Uri uri = Uri.fromFile(new File(fileStr+fileName));
					Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri);
					PhotoActivity.this.sendBroadcast(localIntent);
				}
			}
		});
	}
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void finish() {
		overridePendingTransition(0, R.anim.base_slide_right_out);
		super.finish();
	} 
}
