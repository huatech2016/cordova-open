package com.disusered;


import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

import app.App;

public class DownloadStatusQueryReciver extends BroadcastReceiver {
	private DownloadManager downloadManager;

	@Override
	public void onReceive(Context context, Intent intent) {


		String s = intent.getAction();

		String orderType = intent.getStringExtra("orderType");
		long downloadId = intent.getExtras().getInt("downloadId");

		if (orderType.equals("order-query")) {
			downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
			queryStatus(downloadId, downloadManager, context);

		} else if (orderType.equals("order-remove")) {
			downloadManager.remove(downloadId);
		}else if(orderType.equals("fileSign-success"))
		{

			moveAppToFront(context);
		}


	}


	private void moveAppToFront(Context context) {
		ActivityManager activityManager = (ActivityManager) (context.getSystemService(Context.ACTIVITY_SERVICE));
		List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(100);
		for (ActivityManager.RunningTaskInfo runningTaskInfo : taskList){
			if (runningTaskInfo.topActivity.getPackageName().equals(context.getPackageName())){
				activityManager.moveTaskToFront(runningTaskInfo.id, 0);
			}
		}
	}


	private void queryStatus(long id, DownloadManager downloadManager, Context context) {

		boolean isNeedDownloadAgain = true;

		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterById(id);
		Cursor cursor = downloadManager.query(query);
		if (cursor != null && cursor.moveToFirst()) {
			int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
			int status = cursor.getInt(columnIndex);
			int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
			int reason = cursor.getInt(columnReason);

			switch (status) {
				case DownloadManager.STATUS_FAILED:
					switch (reason) {
						case DownloadManager.ERROR_CANNOT_RESUME:
							//some possibly transient error occurred but we can't resume the download
							break;
						case DownloadManager.ERROR_DEVICE_NOT_FOUND:
							//no external storage device was found. Typically, this is because the SD card is not mounted
							break;
						case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
							//the requested destination file already exists (the download manager will not overwrite an existing file)
							break;
						case DownloadManager.ERROR_FILE_ERROR:
							//a storage issue arises which doesn't fit under any other error code
							break;
						case DownloadManager.ERROR_HTTP_DATA_ERROR:
							//an error receiving or processing data occurred at the HTTP level
							break;
						case DownloadManager.ERROR_INSUFFICIENT_SPACE://sd卡满了
							//here was insufficient storage space. Typically, this is because the SD card is full
							break;
						case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
							//there were too many redirects
							break;
						case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
							//an HTTP code was received that download manager can't handle
							break;
						case DownloadManager.ERROR_UNKNOWN:
							//he download has completed with an error that doesn't fit under any other error code
							break;
					}
					isNeedDownloadAgain = true;

					final Intent endIntent = new Intent("endDownloadFile");
					endIntent.putExtra("reason", "下载出错，请重试");
					LocalBroadcastManager.getInstance(context).sendBroadcastSync(endIntent);
//					AlertUtil.alert("开始重新下载更新!", mContext);
					break;
				case DownloadManager.STATUS_PAUSED:

					switch (reason) {
						case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
							//the download exceeds a size limit for downloads over the mobile network and the download manager is waiting for a Wi-Fi connection to proceed

							break;
						case DownloadManager.PAUSED_UNKNOWN:
							//the download is paused for some other reason
							break;
						case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
							//the download is waiting for network connectivity to proceed
							break;
						case DownloadManager.PAUSED_WAITING_TO_RETRY:
							//the download is paused because some network error occurred and the download manager is waiting before retrying the request
							break;
					}
					//isNeedDownloadAgain = false;

//					AlertUtil.alert("下载已暂停，请继续下载！", mContext);
					break;
				case DownloadManager.STATUS_PENDING:
					//the download is waiting to start
					//isNeedDownloadAgain = false;
//					AlertUtil.alert("更新正在下载！", mContext);
					break;
				case DownloadManager.STATUS_RUNNING:
					//the download is currently running
					//isNeedDownloadAgain = false;
//					AlertUtil.alert("更新正在下载！", mContext);
					break;
				case DownloadManager.STATUS_SUCCESSFUL:
					//the download has successfully completed
					///isNeedDownloadAgain = false;
//					installApk(id, downloadManager, mContext);
					break;
			}

		}
		//return isNeedDownloadAgain;
	}


}
