package com.badou.mworking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseNoTitleActivity;
import com.badou.mworking.model.user.UserDetail;
import com.badou.mworking.net.LVUtil;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.bitmap.BitmapLruCache;
import com.badou.mworking.net.bitmap.CircleImageListener;
import com.badou.mworking.net.volley.MyVolley;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.BitmapUtil;
import com.badou.mworking.util.Constant;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.NetUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.badou.mworking.widget.WaitProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * 类: <code> UserCenterActivity </code> 功能描述: 个人中心页面 创建人: 葛建锋 创建日期: 2014年7月15日
 * 下午3:59:23 开发环境: JDK7.0
 */
public class UserCenterActivity extends BaseNoTitleActivity {

	private int headWidth;
	private int headHeight;
	private String uid = "";
	private String finalImgPath;

	/* 组件 */
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 3;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	public static final String KEY_USERINFO = "UserCenter_INFO";
	private UserCenterActivity mActivity = this;
	private Context mContext = this;
	private ImageView ivUserHeadIcon;
	private TextView postsNumTv;     // 我的圈帖子数量
	private TextView lvTv;   	//等级
	private Bitmap headBmp;
	private ProgressDialog mProgressDialog;
	private Bundle bundle;
	private String imgCaheUrl = "";
	private Bitmap photo;
	private TextView chatNumTv;   //聊天未读数量
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		items[0] = mContext.getResources().getString(R.string.choose_sd_Pic);
		items[1] = mContext.getResources().getString(R.string.choose_camera);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
		setContentView(R.layout.activity_user_center);
		mProgressDialog = new WaitProgressDialog(mContext,
				R.string.user_detail_download_ing);
		bundle = new Bundle();
		initView();
		mProgressDialog.show();
		// 获取用户详情
		ServiceProvider.doOptainUserDetail(mContext,uid,new VolleyListener(
				mContext) {
			@Override
			public void onErrorResponse(VolleyError error) {
				super.onErrorResponse(error);
				if (null != mProgressDialog && mContext != null
						&& !mActivity.isFinishing())
					mProgressDialog.dismiss();
			}

			@Override
			public void onResponse(Object arg0) {
				if (null != mProgressDialog && mContext != null
						&& !mActivity.isFinishing())
					mProgressDialog.dismiss();
				JSONObject jsonObject = (JSONObject) arg0;
				int code = jsonObject.optInt(Net.CODE);
				if (code == Net.LOGOUT) {
					AppApplication.logoutShow(mContext);
					return;
				}
				if (code != Net.SUCCESS) {
					return;
				}
				JSONObject jObject = null;
				try {
					jObject = new JSONObject(jsonObject.optString(Net.DATA));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (jObject == null) {
					return;
				}
				
				UserDetail userDetail = new UserDetail(jObject);
				imgCaheUrl = userDetail.getHeadimg();
				bundle.putSerializable(KEY_USERINFO, userDetail);
				setViewValue(userDetail);
				postsNumTv.setText(userDetail.getAsk()+"条");
				LVUtil.setTextViewBg(lvTv, userDetail.getCircle_lv());
				int nmsg = userDetail.getNmsg();
				if(nmsg>0){
					chatNumTv.setVisibility(View.VISIBLE);
					chatNumTv.setText(userDetail.getNmsg()+"");
					// 如果是两位数的话，换一个背景
					if (nmsg>9) {
						chatNumTv.setBackgroundResource(R.drawable.icon_chat_unread_long);
					} else {
						chatNumTv.setBackgroundResource(R.drawable.icon_chat_unread);
					}
				}
			}

		});
	}

	// Press the back button in mobile phone
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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
	protected void onDestroy() {
		mProgressDialog.dismiss();
		super.onDestroy();
	}

	private void initView() {
		// 获取用户的uid
		try {
			uid = ((AppApplication) mContext.getApplicationContext())
					.getUserInfo().getUserId();
			// 根据uid拿到用户头像的路径
			finalImgPath = mActivity.getExternalFilesDir(
					Environment.DIRECTORY_PICTURES).getAbsolutePath()
					+ File.separator + uid + ".png";
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		postsNumTv = (TextView) findViewById(R.id.posts_num_tv);
		chatNumTv = (TextView) findViewById(R.id.chat_num_tv);
		// actionbar home 操作，返回主界面
		ImageView ivHome = (ImageView) findViewById(R.id.iv_actionbar_left);
		ivHome.setImageResource(R.drawable.title_bar_back_normal);
		ivHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserCenterActivity.this != null) {
					finish();
					overridePendingTransition(0, R.anim.base_slide_right_out);
				}
			}
		});
		// 个人中心的设置操作
		ImageView ivSettings = (ImageView) findViewById(R.id.iv_actionbar_right);
		ivSettings.setVisibility(View.GONE);
		ivSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity,
						AccountManageActivity.class);
				mActivity.startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
		// 设置actionbar标题
		((TextView) findViewById(R.id.txt_actionbar_title)).setText(getIntent().getStringExtra(MainGridActivity.KEY_TITLE_NAME));
		headWidth = getResources().getDimensionPixelSize(
				R.dimen.user_center_image_head_size);
		headHeight = headWidth;
		// 用户头像
		ivUserHeadIcon = (ImageView) findViewById(R.id.ivUserSecondHeadIcon);
		ivUserHeadIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 判断是否联网，如果没有联网的话，不进行拍照和相册选取图片的操作
				if (!NetUtils.isNetConnected(UserCenterActivity.this)) {
					showToast(R.string.error_service);
					return;
				}
				showDialog();
			}
		});
		lvTv = (TextView) findViewById(R.id.lv_tv); 
		lvTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				chankanLV();
			}
		});
		// 我的学习
		((RelativeLayout) findViewById(R.id.llUserSecondStudyProgress))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								MyStudyProgressAct.class);
						intent.putExtras(bundle);
						mActivity.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}
				});
		// 进入我的圈页面
		((LinearLayout) findViewById(R.id.llUserMyGroup))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								AroundUserActivity.class);
						mActivity.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}
				});

		// 进入我的考试
		((RelativeLayout) findViewById(R.id.llUserSecondExam))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								MyExamAct.class);
						intent.putExtras(bundle);
						mActivity.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}
				});

		// 进入我的直通车
		((LinearLayout) findViewById(R.id.chat_linear))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								ChattingActivity.class);
						mActivity.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}
			});
		// 进入我的账号
		((LinearLayout) findViewById(R.id.llUserMyAccount))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mActivity,
								AccountManageActivity.class);
						mActivity.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
					}
				});
		 // 进入关于我们
		((ImageView)findViewById(R.id.iv_about)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(mActivity,
						AboutUsActivity.class);
				mActivity.startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});
	}

	/**
	 * 功能描述: 从文件中获取用户头像
	 * 
	 * @param path
	 * @return
	 */
	private Bitmap getUserIconFromFile(String path) {
		if (!FileUtils.hasSdcard()) {
			return null;
		}
		return BitmapUtil.decodeSampledBitmapFromFile(path, headWidth,
				headHeight);
	}

	/**
	 * 功能描述: 设置用户头像
	 * 
	 * @param uid
	 * @param imgUrl
	 */
	private void setUserIcon(final String uid, final String imgUrl) {
		if (headBmp != null && !headBmp.isRecycled()) {
			headBmp.recycle();
			headBmp = null;
		}
		if (!TextUtils.isEmpty(imgUrl))
			headBmp = BitmapLruCache.getBitmapLruCache()
					.getCircleBitmap(imgUrl);
		headBmp = BitmapUtil.getCirlBitmp(headBmp,
				headWidth, headHeight);
		if (headBmp != null) {
			ivUserHeadIcon.setImageBitmap(headBmp);
			return;
		}
		headBmp = BitmapUtil.getCirlBitmp(getUserIconFromFile(finalImgPath),
				headWidth, headHeight);
		if (headBmp != null) {
			ivUserHeadIcon.setImageBitmap(headBmp);
			BitmapLruCache.getBitmapLruCache().putCircleBitmap(imgUrl, headBmp);
			return;
		} else {
			MyVolley.getImageLoader().get(
					imgUrl,
					new CircleImageListener(mContext, imgUrl, ivUserHeadIcon,
							headWidth, headHeight));
		}
	}

	/**
	 * 设置分数 学习进度
	 * @param userDetail
	 */
	private void setViewValue(UserDetail userDetail) {
		String strScore = mContext.getResources().getString(R.string.text_score);
		String strPingJunFen = mContext.getResources().getString(R.string.ucneter_text_pingJunFen);
				
		if (userDetail != null) {
			setUserIcon(uid, userDetail.getHeadimg());
			if (!TextUtils.isEmpty(userDetail.getName())) {
				((TextView) findViewById(R.id.tvActivityUserSecondName))
						.setText(userDetail.getName());
			}
			if (!TextUtils.isEmpty(userDetail.getDpt())) {
				((TextView) findViewById(R.id.tvActivityUserSecondPart))
						.setText(userDetail.getDpt());
			}
			if (!TextUtils.isEmpty(String.valueOf(userDetail.getScore()))) {
				((TextView) findViewById(R.id.tvUserSecondAvrScoreUser))
						.setText(strPingJunFen + String.valueOf(userDetail.getScore())
								+ strScore);
			}
			/**学习总进度**/
			if (!TextUtils.isEmpty(String.valueOf(userDetail.getStudy_total()))
					&& !TextUtils.isEmpty(String.valueOf(userDetail
							.getTraining_total()))) {
				((TextView) findViewById(R.id.studyTotalPercent))
						.setText(String.valueOf(userDetail.getStudy_total())
								+ "/"
								+ String.valueOf(userDetail.getTraining_total()));
				int study = userDetail.getStudy_total();
				int training = userDetail.getTraining_total();
				int s = study * 100;
				if (training != 0) {
					int progress = s / training;
					ProgressBar pbTotalBar = (ProgressBar) findViewById(R.id.proBarTotalUser);
					pbTotalBar.setProgress(progress);
				}
			}
		}
	}

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle(R.string.uc_dialog_title_settingHead)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: // 选择本地图片
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型

							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							intentFromGallery
									.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
							break;
						case 1: // 拍照
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (FileUtils.hasSdcard()) {
								File file = new File(mActivity
										.getExternalCacheDir()
										.getAbsolutePath()
										+ File.separator + "temp.png");
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
							break;
						}
					}
				})
				.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				if (data != null)
					startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (FileUtils.hasSdcard()) {
					File file = new File(mActivity.getExternalCacheDir()
							.getAbsolutePath() + File.separator + "temp.png");
					BitmapFactory.Options option = new BitmapFactory.Options();
					option.inSampleSize = 2;
					Bitmap bitmap = BitmapFactory.decodeFile(file.toString(), option); //根据Path读取资源图片 
					Matrix matrix = new Matrix();  
	                    int width = bitmap.getWidth();  
	                    int height = bitmap.getHeight();  
	                    matrix.preRotate(readPictureDegree(file.toString()));
	                    Bitmap bitmap2 = Bitmap.createBitmap(bitmap, 0, 0, width, height,  
	                    		matrix, true);// 从新生成图片  
	                    FileUtils.writeBitmap2SDcard(bitmap2,
	                    		file.toString());
					startPhotoZoom(Uri.fromFile(file));
				} else {
					Toast.makeText(UserCenterActivity.this, R.string.save_camera_fail,
							Toast.LENGTH_LONG).show();
				}
				break;
			case RESULT_REQUEST_CODE:
				if (data != null) {
					getImageToView(data);
				}
				break;
			case RESULT_CANCELED:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		
		String filepath = getPath(UserCenterActivity.this,uri);
		Uri newUri = Uri.fromFile(new File(filepath));
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(newUri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 3);
		intent.putExtra("aspectY", 3);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("return-data", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * Get a file path from a Uri. This will get the the path for Storage Access
	 * Framework Documents, as well as the _data field for the MediaStore and
	 * other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @author paulburke
	 */
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {
	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
	                null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int column_index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(column_index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			final Bitmap photo = extras.getParcelable("data");
			if (photo != null) {
				mProgressDialog.setTitle(R.string.user_detail_icon_upload_ing);
				mProgressDialog.show();
				ServiceProvider.doUpdateBitmap(mContext, photo,
						Net.getRunHost(mContext) + Net.UPDATE_HEAD_ICON(uid),
						new VolleyListener(mContext) {

							@Override
							public void onResponse(Object responseObject) {
								Bitmap headbitmap = BitmapLruCache.getBitmapLruCache().get(imgCaheUrl);
								if(headbitmap!=null&&!headbitmap.isRecycled()){
									headbitmap.recycle();
								}
								if (null != mProgressDialog && mContext != null
										&& !mActivity.isFinishing())
									mProgressDialog.dismiss();
								try {
									int code = ((JSONObject) responseObject)
											.optInt(Net.CODE);
									if (code==Net.LOGOUT) {
										AppApplication.logoutShow(mContext);
										return;
									}
									if (code != Net.SUCCESS) {
										ToastUtil.showToast(
												mContext,
												mActivity
														.getString(R.string.error_service));
										return;
									} else {
										FileUtils.writeBitmap2SDcard(photo,
												finalImgPath);
										photo.recycle();
										setUserIcon(uid, null);
										ToastUtil
												.showToast(
														mContext,
														R.string.user_detail_icon_upload_success);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
			} else {
				ToastUtil.showToast(mContext,
						R.string.user_detail_icon_upload_failed);
			}
		}
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
       
	/**
	* 功能描述: 等级查看
	*/
	public void chankanLV(){
	   if(lvTv!=null){
	       lvTv.setOnClickListener(new OnClickListener() {
	           
	           @Override
	           public void onClick(View arg0) {
	               String userId = ((AppApplication) mContext.getApplicationContext())
	                       .getUserInfo().getUserId();
	               Intent intent = new Intent(mContext, BackWebActivity.class);
	               intent.putExtra("title", "等级介绍");
	               intent.putExtra(BackWebActivity.VALUE_URL,Constant.LV_URL+userId);
	               startActivity(intent);
	           }
	       });
	   }
}
}
