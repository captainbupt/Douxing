package com.badou.mworking;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.Task;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.RequestParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.PicImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.SP;
import com.badou.mworking.util.TimeTransfer;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.badou.mworking.widget.WaitProgressDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.umeng.analytics.MobclickAgent;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 类:  <code> SignActivity </code>
 * 功能描述:  任务签到页面
 * 创建人:  葛建锋
 * 创建日期: 2014年7月18日 下午9:37:30
 * 开发环境: JDK7.0
 */
public class SignActivity extends BaseNoTitleActivity implements OnClickListener,BDLocationListener,OnGetGeoCoderResultListener{
	
	public static final String VALUE_URL = "url";
	public static final String INTENT_TASK = "detailTask";
	private static final int CAMERA_REQUEST_CODE = 1; // 请求码
	public static boolean isSignSuccess = false;      //是否签到成功
	
	private TextView tvTaskDesc;
	private TextView tvTaskTime;
	private TextView tvTaskAdd;
	private TextView tvSignTaskPassed;      //任务已过期
	private TextView llSignSmile;
	private TextView llSignConfirm;
	private TextView actionbarTitleTv;
	
	private ImageView ivBack;
	private ImageView ivActionbarRight;
	private ImageView ivAddPic;   //添加照片
	private ImageView showLocImg; //显示图片
	
	private LinearLayout llSignConfirmOrIgnor;
	private LinearLayout layoutCarmea;
	
	private Task task;
	private Context mContext = this;
	private Bitmap photo = null;
	private long taskDetailStartline;
	private ProgressDialog mProgressDialog;
	private String[] items = new String[2];  //组件 
	private Marker mMarkerA;
	public LocationClient mLocationClient;
	private Boolean isSign = false; //是否签到， 区别是否是否是首次进入需要显示地图
	private Boolean isFreeSign = false;       //自由签到   否 false 是 true
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private BDLocation signLocation = null;   //爆粗签到的location值
	private String locationStr="";
	
	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
	
	private SwipeBackLayout layout;
	
	public static boolean ISSLIDEABLE = true;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);
		SignActivity.ISSLIDEABLE = true;
		//页面滑动关闭
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.base, null);
		layout.attachToActivity(this);
		mProgressDialog = new WaitProgressDialog(mContext,
				R.string.sign_action_sign_ing);
		items[0] = mContext.getResources().getString(R.string.choose_sd_Pic);
		items[1] = mContext.getResources().getString(R.string.choose_camera);
		initView();
		initlocation();
		
		if(task.latitude==0||task.longitude==0){
			isFreeSign = true;
			mLocationClient.start();
		}else{
			LatLng location = new LatLng(task.latitude, task.longitude);
			MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(location);
			MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(16);
			SupportMapFragment map = (SupportMapFragment) (getSupportFragmentManager()
					.findFragmentById(R.id.map));
			OverlayOptions ooA = new MarkerOptions().position(location).icon(bdA).draggable(false);
			mMarkerA = (Marker) (map.getBaiduMap().addOverlay(ooA));
			map.getBaiduMap().setMapStatus(u1);
			map.getBaiduMap().setMapStatus(u2);
			map.getBaiduMap().setOnMapTouchListener(new OnMapTouchListener() {
				
				@Override
				public void onTouch(MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_MOVE:
						SignActivity.ISSLIDEABLE = false;
						break;
					case MotionEvent.ACTION_UP:
						SignActivity.ISSLIDEABLE = true;
						break;
					}
				}
			});
		}
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
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
	 * 功能描述:  初始化定位数据
	 */
	private void initlocation(){
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(this);
		// 定位初始化
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
	}

	protected void initView() {
		actionbarTitleTv = (TextView) findViewById(R.id.txt_actionbar_title);
		actionbarTitleTv.setText(getIntent().getStringExtra("title")+"");
		ivBack = (ImageView) findViewById(R.id.iv_actionbar_left);
		showLocImg = (ImageView)findViewById(R.id.show_loc_img);
		
		// 是否是管理员
		boolean isAdmin = ((AppApplication) getApplicationContext())
				.getUserInfo().isAdmin;
		ivActionbarRight = (ImageView) findViewById(R.id.iv_actionbar_right);
		// 是否显示统计图标
		if(isAdmin){
			ivActionbarRight.setBackgroundResource(R.drawable.admin_tongji);
			ivActionbarRight.setVisibility(View.VISIBLE);
			ivActionbarRight.setOnClickListener(this);
		}else{
			ivActionbarRight.setVisibility(View.GONE);
		}
	
		ivBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		llSignSmile = (TextView) findViewById(R.id.llSignSmile);
		// 签到任务取消或者忽略
		llSignConfirm = (TextView) findViewById(R.id.llSignConfirm);
		llSignConfirm.setOnClickListener(this);
		llSignConfirmOrIgnor = (LinearLayout) findViewById(R.id.llSignSignOrIgnor);
		tvSignTaskPassed = (TextView) findViewById(R.id.tvSignTaskPassed);
		tvTaskDesc = (TextView) findViewById(R.id.tvSignTaskDescrb);
		tvTaskTime = (TextView) findViewById(R.id.tvSignInTime);
		tvTaskAdd = (TextView) findViewById(R.id.tvSignTaskAdd);
		
		layoutCarmea = (LinearLayout) this.findViewById(R.id.ll_camrea);
		ivAddPic = (ImageView) this.findViewById(R.id.iv_question_image);
		ivAddPic.setOnClickListener(this);
		task = (Task) getIntent().getBundleExtra(INTENT_TASK).getSerializable(
				INTENT_TASK);
		
		if (task != null) {
			boolean finish = task.isRead();
			String comment = task.comment;
			if (comment == null || comment.equals("")) {
				tvTaskDesc.setText(mContext.getResources().getString(R.string.text_null));
			} else {
				tvTaskDesc.setText(comment);
			}
			String place = task.place;
			taskDetailStartline = task.startline;      //任务开始时间
			long taskDetailDeadline = task.deadline;      //任务结束时间
			
			long timeNow = System.currentTimeMillis();
			// 已签到
			if (finish) {
				llSignConfirm.setEnabled(false);
				llSignSmile.setVisibility(View.VISIBLE);
				llSignConfirmOrIgnor.setVisibility(View.GONE);
				tvSignTaskPassed.setVisibility(View.GONE);
			}else {
				// 已过期
				if(task.getOffline()){
					llSignConfirm.setEnabled(false);
					tvSignTaskPassed.setVisibility(View.VISIBLE);
					llSignConfirmOrIgnor.setVisibility(View.GONE);
					llSignSmile.setVisibility(View.GONE);
					task.read = Constant.FINISH_YES;
				// 未签到
				}else{
					llSignConfirm.setEnabled(true);
					llSignConfirmOrIgnor.setVisibility(View.VISIBLE);
					tvSignTaskPassed.setVisibility(View.GONE);
					llSignSmile.setVisibility(View.GONE);
				}
			}
			if (taskDetailDeadline > 0) {
				String taskTime = TimeTransfer.long2StringDetailDate(mContext,taskDetailStartline)+"——"+TimeTransfer.long2StringDetailDate(mContext,taskDetailDeadline);
				tvTaskTime.setText(taskTime);
			}
			if (place == null || place.equals("")) {
				tvTaskAdd.setText(mContext.getResources().getString(R.string.text_null));
			} else {
				tvTaskAdd.setText(place);
			}
			if (task.photo == 0 || task.isRead()) {
				layoutCarmea.setVisibility(View.GONE);
				
				File file = new File(mActivity
						.getExternalCacheDir()
						.getAbsolutePath()
						+ File.separator + task.rid+".png");
				if(file.exists()){
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inSampleSize = 2;
					Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), option); //根据Path读取资源图片 
					showLocImg.setImageBitmap(bitmap);
				}else{
					String imgUrl = task.photoUrl;
					if(!TextUtils.isEmpty(imgUrl)){
						MyVolley.getImageLoader().get(
								task.photoUrl,
								new PicImageListener(mContext, showLocImg, task.photoUrl));
					}
				}
				showLocImg.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(task!=null&&task.photo == 0){
							return;
						}
						File file = new File(mActivity.getExternalCacheDir().getAbsolutePath()+ File.separator + task.rid+".png");
						Intent intent = new Intent(mContext, PhotoActivity.class);
						intent.putExtra(PhotoActivity.MODE_PICZOMM, task.photoUrl);
						intent.putExtra("filePath", file.toString());
						((Activity)mContext).startActivity(intent);
					}
				});
			}else {
				layoutCarmea.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.llSignConfirm:
			//无网络状态下不允许点击
			if (NetUtils.isNetConnected(mContext)) {
				ToastUtil.showNetExc(mContext);
				return;
			}
			long timeNow = System.currentTimeMillis();
			if(taskDetailStartline>timeNow){
				ToastUtil.showToast(this, R.string.task_notStart);
				return;
			}
			if (task.photo==1&&photo == null) {
				ToastUtil.showToast(this, R.string.sign_needUpload);
				return;
			}
			isSign = true;
			mProgressDialog.show();
			mLocationClient.start();
			break;
		case R.id.iv_question_image:
			takePhoto();
			break;
		case R.id.iv_actionbar_right:
			String titleStr = getResources().getString(R.string.statistical_data);
			String uid = ((AppApplication) getApplicationContext()).getUserInfo().userId;
			String url = Net.getRunHost(SignActivity.this)+Net.getTongji(uid,task.rid);
			Intent intent = new Intent();
			intent.setClass(SignActivity.this, BackWebActivity.class);
			intent.putExtra(BackWebActivity.VALUE_URL,url);
			intent.putExtra(BackWebActivity.VALUE_TITLE,titleStr);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case CAMERA_REQUEST_CODE:
				if (FileUtils.hasSdcard()) {
					File file = new File(mActivity.getExternalCacheDir()
							.getAbsolutePath() + File.separator + task.rid+".png");
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inSampleSize = 2;
					//图片取出后压缩
					Bitmap bitmap =  BitmapUtil.compressImage(BitmapFactory.decodeFile(file.toString(), option));
					Matrix matrix = new Matrix();  
	                    int width = bitmap.getWidth();  
	                    int height = bitmap.getHeight();  
	                    matrix.preRotate(readPictureDegree(file.toString()));
	                    photo = Bitmap.createBitmap(bitmap, 0, 0, width, height,  
	                    		matrix, true);// 从新生成图片  
	                    if(bitmap.isRecycled()){
	                    	bitmap.recycle();
	                    }
	                    FileUtils.writeBitmap2SDcard(photo,
	                    		file.toString());
	                    ivAddPic.setBackgroundColor(Color.TRANSPARENT); // 设置背景为透明，消掉图片
						ivAddPic.setImageBitmap(photo);	
				} else {
					Toast.makeText(this, R.string.save_camera_fail,
							Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onReceiveLocation(BDLocation location) { 
		mLocationClient.stop();
		if(location==null||String.valueOf(location.getLatitude()).equals(4.9E-324)||String.valueOf(location.getLongitude()).equals(4.9E-324)){
			ToastUtil.showToast(SignActivity.this, R.string.task_get_gps_fail);
			if (null != mProgressDialog
					&& mContext != null
					&& !mActivity.isFinishing()) {
				mProgressDialog.dismiss();
			}
			return;
		}
		if(isFreeSign&&!isSign){
			LatLng location1 = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(location1);
			MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(18);
			SupportMapFragment map = (SupportMapFragment) (getSupportFragmentManager()
					.findFragmentById(R.id.map));
			OverlayOptions ooA = new MarkerOptions().position(location1).icon(bdA).draggable(false);
			mMarkerA = (Marker) (map.getBaiduMap().addOverlay(ooA));
			map.getBaiduMap().setMapStatus(u1);
			map.getBaiduMap().setMapStatus(u2);
		}else{
			isSign = false;
			if(isFreeSign){
				signLocation = location;
				LatLng ptCenter = new LatLng(location.getLatitude(), location.getLongitude());
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()    
				.location(ptCenter));
			}else{
				getImageToView(location);
			}
		}
	}
	
	private void getImageToView(final BDLocation location) {
		if(location == null){
			return;
		}
		String lat = String.valueOf(location.getLatitude());
		String lon = String.valueOf(location.getLongitude());
		String uid = ((AppApplication) getApplicationContext())
				.getUserInfo().userId;
				ServiceProvider.doUpdateBitmap(mContext, photo,
						Net.getRunHost(mContext) + Net.SIGN(task.rid, uid, lat,lon),
						new VolleyListener(mContext) {
							@Override
							public void onResponse(Object responseObject) {
							  if(photo!=null&&photo.isRecycled()){
								  photo.recycle();
			                    }
								if (null != mProgressDialog
										&& mContext != null
										&& !mActivity.isFinishing()) {
									mProgressDialog.dismiss();
								}
								JSONObject jsonObject = (JSONObject) responseObject;
								if(jsonObject == null){
									ToastUtil.showToast(SignActivity.this, getResources().getString(R.string.error_service));
									return;
								}
								int errcode = jsonObject
										.optInt(RequestParams.ERRCODE);
								if (errcode == 0) {
									// 签到成功
									task.read = Constant.FINISH_YES;
									if(isFreeSign){
										task.place = locationStr;
									}
									// 签到成功， 减去1
									String userNum = ((AppApplication) getApplicationContext())
											.getUserInfo().account;
									int unreadNum = SP.getIntSP(mContext,SP.DEFAULTCACHE, userNum+Task.CATEGORY_KEY_UNREAD_NUM, 0);
									if (unreadNum > 0 ) {
										SP.putIntSP(mContext,SP.DEFAULTCACHE, userNum+Task.CATEGORY_KEY_UNREAD_NUM, unreadNum - 1);
									}
									
									llSignConfirmOrIgnor
											.setVisibility(View.GONE);
									tvSignTaskPassed
											.setVisibility(View.GONE);
									llSignSmile.setVisibility(View.VISIBLE);
									SignActivity.this.finish();
									// 设置签到成功
									SignActivity.isSignSuccess = true;
								} else {
									// 签到失败
									AlertDialog.Builder builder = new AlertDialog.Builder(
											mContext);
									builder.setTitle(R.string.message_tips);
									builder.setMessage(R.string.task_signFail);

									builder.setPositiveButton(
											R.string.text_ok,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													arg0.dismiss();
												}
											}).show();
								}		
							}
						});
	}
	
	@Override
	public void onReceivePoi(BDLocation arg0) {
	}
	
	
	/**
	 * 显示选择对话框
	 */
	private void takePhoto() {
		Intent intentFromCapture = new Intent(
				MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可以用，可用进行存储
		if (FileUtils.hasSdcard()) {
			File file = new File(mActivity
					.getExternalCacheDir()
					.getAbsolutePath()
					+ File.separator + task.rid+".png");
			if (file.exists())
				file.delete();
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

			intentFromCapture.putExtra(
					MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(file));
		}
		intentFromCapture
				.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intentFromCapture,
				CAMERA_REQUEST_CODE);
		overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}
	
	
	 /** 
     * 读取图片属性：旋转的角度 
     * @param path 图片绝对路径 
     * @return degree旋转的角度 
     */  
       public static int readPictureDegree(String path) {  
           int degree  = 0;  
           try {  
                   ExifInterface exifInterface = new ExifInterface(path);  
                   int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
                   switch (orientation) {  
                   case ExifInterface.ORIENTATION_ROTATE_90:  
                           degree = 90;  
                           break;  
                   case ExifInterface.ORIENTATION_ROTATE_180:  
                           degree = 180;  
                           break;  
                   case ExifInterface.ORIENTATION_ROTATE_270:  
                           degree = 270;  
                           break;  
                   }  
           } catch (IOException e) {  
                   e.printStackTrace();  
           }  
           return degree;  
       }

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			return;
		}
		getImageToView(signLocation);
		locationStr = result.getAddress();
		tvTaskAdd.setText(locationStr);
	}  
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SignActivity.ISSLIDEABLE = true;

	}
}
