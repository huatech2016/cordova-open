package com.disusered;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.disusered.WpsModel.Reciver;

public class MyBroadCastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = "PACKAGE-NAME-TO-REPLACE";

		if (intent.getExtras().getString(WpsModel.THIRD_PACKAGE).equals(packageName + "-FILESIGN")) {
			String s = intent.getAction();
			if (s.equals(Reciver.ACTION_BACK)) {
				resumeAppOnly(context);
			} else if (s.equals(Reciver.ACTION_CLOSE)) {
				resumeApp(context, intent);
			} else if (s.equals(Reciver.ACTION_SAVE)) {
				resumeApp(context, intent);
			}
		} else if (intent.getExtras().getString(WpsModel.THIRD_PACKAGE).equals(packageName + "-EIP")) {
			resumeAppOnly(context);
		}

	}


	/*eip 业务如 通知、会议使用wps打开后返回，什么都不做*/
	private void resumeAppOnly(Context context) {
		PackageManager packageManager = context.getPackageManager();
		Intent newIntent = packageManager.getLaunchIntentForPackage(context.getPackageName());
		context.getApplicationContext().startActivity(newIntent);
	}

	/*
	公文签批，打开返回后可能需要上传文件
	 */
	private void resumeApp(Context context, Intent intent) {
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
