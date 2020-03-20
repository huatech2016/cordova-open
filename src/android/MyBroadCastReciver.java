package com.disusered;


import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.disusered.WpsModel.Reciver;

import org.bsc.cordova.CDVBroadcaster;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class MyBroadCastReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

//		FileUtils.setAppendFile("intent.getAction():" + intent.getAction());
//		FileUtils.setAppendFile("ThirdPackage:" + intent.getStringExtra("ThirdPackage"));
//		FileUtils.setAppendFile("ThirdPartyPackage:" + intent.getStringExtra("ThirdPartyPackage"));
//		FileUtils.setAppendFile("CurrentPath:" + intent.getStringExtra("CurrentPath"));
//		FileUtils.setAppendFile("SaveAs:" + intent.getStringExtra("SaveAs"));


		if (intent.getExtras().getString(WpsModel.THIRD_PACKAGE).equals("huatech.gov.renda")) {
			String s = intent.getAction();
//			if (s.equals(Reciver.ACTION_BACK_PRO)) {
//				redirectMyApp(context, intent);
//
//			} else if (s.equals(Reciver.ACTION_CLOSE)) {
//				redirectOnly(context, intent);//E人有本可以正常返回，此处不需要，其它机型有返回不了的情况
//			} else if (s.equals(Reciver.ACTION_HOME_PRO)) {
//
//			} else

			if (s.equals(Reciver.ACTION_SAVE_PRO)) {
				redirectMyApp(context, intent);
			}
		}
	}


	private void redirectMyApp(Context context, Intent intent) {
//      个人版逻辑
//		String str = intent.getExtras().getString("CloseFile");
//		int downloadId = Integer.parseInt(str.substring(str.lastIndexOf("/") + 1));
//
//		String fileUrl = null;
//		String fileName = null;
//		DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//		DownloadManager.Query query = new DownloadManager.Query();
//		query.setFilterById(downloadId);
//		Cursor c = manager.query(query);
//
//		if (c.moveToFirst()) {
//			//获取文件下载路径
//			///storage/emulated/0/Android/data/huatech.gov.renda/files/123-99.doc
//			fileUrl = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
//			if (fileUrl != null) {
//				fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
//			}
//		}
		//专业版逻辑
		//发广播，开始上传文件
//		String currentPath = intent.getStringExtra("CurrentPath");
//		String fileName = currentPath.substring(currentPath.lastIndexOf("/") + 1);
//
//		if (fileName == null) {
//			return;
//		}

		//发广播，开始上传文件
		String currentPath = intent.getStringExtra("OpenFile");
		String fileName = currentPath.substring(currentPath.lastIndexOf("/") + 1);

		if (fileName == null) {
			return;
		}
		final Intent uploadIntent = new Intent("startUploadFile");
		final Bundle bundle = new Bundle();


		bundle.putString("fileName", fileName);
		uploadIntent.putExtras(bundle);
		LocalBroadcastManager.getInstance(context).sendBroadcastSync(uploadIntent);


		PackageManager packageManager = context.getPackageManager();
		Intent newIntent = packageManager.getLaunchIntentForPackage(context.getPackageName());
		context.getApplicationContext().startActivity(newIntent);
	}



}
