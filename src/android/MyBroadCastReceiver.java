package com.disusered;


import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;

import com.disusered.WpsModel.Reciver;

import java.util.List;

public class MyBroadCastReceiver extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		String packageName = "huatech.university.newzzxy";
		final boolean isFileChanged[] = new boolean[]{false};

		if (intent.getExtras().getString(WpsModel.THIRD_PACKAGE).equals(packageName + "-FILESIGN")) {
			String s = intent.getAction();
//			if (s.equals(Reciver.ACTION_BACK)) {
//				resumeAppOnly(context);
//			} else

			if (s.equals(Reciver.ACTION_CLOSE)) {
				if (!isFileChanged[0]) {
					resumeAppOnly(context);
				}

			} else if (s.equals(Reciver.ACTION_SAVE)) {
				//文件修改后点保存按钮，会先后触发保存广播和关闭广播。
				// 保存广播触发后需要延迟500ms再执行上传文件操作，以防止wps还未保存完成（魅族小米有此问题）。
				//若文件还未上传完就收到了关闭广播并返回app，则文件会上传失败（原因未知，魅族，小米）。
				// 因此，收到保存广播，就置标志位。以此忽略掉紧跟着的关闭广播）3s后可以恢复正常
				isFileChanged[0] = true;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						isFileChanged[0] = false;
					}
				}, 3000);
				resumeApp(context, intent);
			}
		} else if (intent.getExtras().getString(WpsModel.THIRD_PACKAGE).equals(packageName + "-EIP")) {
			resumeAppOnly(context);
		}


	}


	/*eip 业务如 通知、会议使用wps打开后返回，什么都不做*/
	private void resumeAppOnly(Context context) {
		ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(100);
		for (ActivityManager.RunningTaskInfo runningTaskInfo : taskList) {
			if (runningTaskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
				activityManager.moveTaskToFront(runningTaskInfo.id, 0);
			}
		}
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


//		PackageManager packageManager = context.getPackageManager();
//		Intent newIntent = packageManager.getLaunchIntentForPackage(context.getPackageName());
//		context.getApplicationContext().startActivity(newIntent);


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


	}


}
