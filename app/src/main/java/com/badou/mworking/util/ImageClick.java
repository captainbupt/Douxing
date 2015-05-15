package com.badou.mworking.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.style.ClickableSpan;
import android.view.View;

import java.io.File;

public class ImageClick extends ClickableSpan{


	private String url;
	private Context context;

	public ImageClick(Context context, String url) {
		this.context = context;
		this.url = url;
	}

	@Override
	public void onClick(View widget) {
		String imageName = new MD5().getMD5ofStr(url);
		String sdcardPath =context.getExternalFilesDir(
				Environment.DIRECTORY_PICTURES).getAbsolutePath()
				+ File.separator + "exam"; // 获取SDCARD的路径
		// 最终图片保持的地址
		String savePath = sdcardPath + "/" + imageName
				+ ".jpg";
		File file = new File(savePath);
		if (file.exists()) {
			// 处理点击事件，开启一个新的activity来处理显示图片

			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "image/*");
			context.startActivity(intent);
		}
	}

}
