package com.badou.mworking;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.badou.mworking.base.AppApplication;
import com.badou.mworking.base.BaseBackActionBarActivity;
import com.badou.mworking.net.Net;
import com.badou.mworking.net.ResponseParams;
import com.badou.mworking.net.ServiceProvider;
import com.badou.mworking.net.volley.VolleyListener;
import com.badou.mworking.util.FileUtils;
import com.badou.mworking.util.ToastUtil;
import com.badou.mworking.widget.SwipeBackLayout;
import com.badou.mworking.widget.WaitProgressDialog;
import com.umeng.analytics.MobclickAgent;

import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.app.ProgressDialog;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * @author 葛建锋
 * 问答提问页面
 */
public class PutQuestionActivity extends BaseBackActionBarActivity implements OnClickListener{
	
	private ProgressDialog mProgressDialog;
	private EditText tiwenEt;
	private ImageView ivQuestionImg; // 图片上传
	private ImageView ivQuestionImgdel; // 图片删除
	
	/* 组件 */
	private String[] items = new String[2];
	private Bitmap photo = null;
	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 3;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.putquestionactivity);
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
	 *  初始化
	 */
	private void init(){
		mProgressDialog = new WaitProgressDialog(mContext,
				R.string.exam_action_submit_ing);
		tiwenEt = (EditText) findViewById(R.id.tiwen_et);
		// 设置图片
		setRightImage(R.drawable.around_send);
		setActionbarTitle("提问");
		ivQuestionImg = (ImageView) findViewById(R.id.iv_tiwen_image);
		ivQuestionImgdel = (ImageView) findViewById(R.id.iv_tiwen_image_delete);
		
		ivQuestionImgdel.setOnClickListener(this);
		ivQuestionImg.setOnClickListener(this);
		
		items[0] = mContext.getResources().getString(R.string.choose_sd_Pic);
		items[1] = mContext.getResources().getString(R.string.choose_camera);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_tiwen_image:
			showDialog();
			break;
		case R.id.iv_tiwen_image_delete:  
			new AlertDialog.Builder(this)
					.setTitle(R.string.message_tips)
					.setMessage(R.string.tip_delete_confirmation)
					.setPositiveButton(R.string.text_ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									photo = null;
									ivQuestionImg.setImageBitmap(null);
									ivQuestionImg
											.setBackgroundResource(R.drawable.icon_question_camera);
									ivQuestionImgdel.setVisibility(View.GONE);
								}
							}).setNegativeButton(R.string.text_cancel, null).show();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.add_picture)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: // 选择本地图片
							chooseImgNative();
							break;
						case 1: // 拍照           
							tackPhoto();
							break;
							default :
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
			case IMAGE_REQUEST_CODE:     //打开相册
				OpenAlbum(data);		
				break;
			case CAMERA_REQUEST_CODE:    //拍照存储
				cameraStore();
				break;
			case RESULT_REQUEST_CODE:   //剪切完成
				cutCompleted(data);
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * 功能描述: 打开相册
	 */
	private void OpenAlbum(Intent data){
		Uri originalUri = data.getData(); // 获得图片的uri
		String path = getPath(PutQuestionActivity.this,originalUri);
		BitmapFactory.Options option1 = new BitmapFactory.Options();
		option1.inSampleSize = 2;
		photo = BitmapFactory.decodeFile(path, option1);
		// 这里直接取到Bitmap对象，然后显示出来即可
		ivQuestionImg.setBackgroundColor(Color.TRANSPARENT); // 设置背景为透明，消掉图片
		ivQuestionImg.setImageBitmap(photo);
		ivQuestionImgdel.setVisibility(View.VISIBLE);		
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
	 * 功能描述: 拍照存储
	 */
	private void cameraStore(){
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
                photo = Bitmap.createBitmap(bitmap, 0, 0, width, height,  
                		matrix, true);// 从新生成图片  
                ivQuestionImg.setBackgroundColor(Color.TRANSPARENT); // 设置背景为透明，消掉图片
                ivQuestionImg.setImageBitmap(photo);	
                ivQuestionImgdel.setVisibility(View.VISIBLE);	
		} else {
			Toast.makeText(this, R.string.save_camera_fail,
					Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 功能描述: 剪切完成
	 */
	private void cutCompleted(Intent data){
		// 这里直接取到Bitmap对象，然后显示出来即可
		Bundle extras = data.getExtras();
		photo = extras.getParcelable("data");
		ivQuestionImg.setBackgroundColor(Color.TRANSPARENT); // 设置背景为透明，消掉图片
		ivQuestionImg.setImageBitmap(photo);
		ivQuestionImgdel.setVisibility(View.VISIBLE);
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
	 * 功能描述:选择本地图片
	 */
	private void chooseImgNative(){
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
	}
	
	/**
	 * 功能描述: 拍照
	 */
	private void tackPhoto(){
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
	}
	
	@Override
	public void clickRight() {
		super.clickRight();
		sendWenDaContent();
	}
	
  	private void sendWenDaContent() {
  		String content = tiwenEt.getText().toString();
  		if(TextUtils.isEmpty(content)){
			ToastUtil.showToast(mContext, "提问内容不能为空");
			return;
  		}else if(content.length()<5){
			ToastUtil.showToast(mContext, "提问内容不少于5个字");
  			return;
  		}
  		// 提交提问内容
  		ServiceProvider.doPublishAsk(mContext,content, photo,
  				new VolleyListener(mContext) {

  					@Override
  					public void onResponse(Object arg0) {
  						if (arg0 == null) {
  							return;
  						}
  						if (arg0 instanceof JSONObject) {
  							JSONObject jObject = (JSONObject) arg0;
  							int errcode = jObject
  									.optInt(ResponseParams.QUESTION_ERRCODE);
  							if (errcode == Net.LOGOUT) {
  								AppApplication.logoutShow(mContext);
  								return;
  							}
  							if (errcode == 0) {
  								WenDActivity.ISREBOOLEANWENDALIST = true;
  								PutQuestionActivity.this.finish();
  							} else {
								ToastUtil.showToast(mContext, "提交失败");
  							}
  						}
  					}
  					
  					@Override
  					public void onErrorResponse(VolleyError error) {
  						super.onErrorResponse(error);
  						if (null != mProgressDialog && mContext != null
  								&& !mActivity.isFinishing()) {
  							mProgressDialog.dismiss();
  						}
  					}
  				});
  	}
}
