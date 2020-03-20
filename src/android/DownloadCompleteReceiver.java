package com.disusered;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class DownloadCompleteReceiver extends BroadcastReceiver {

	public static String getRealFilePath( final Context context, final Uri uri ) {
		if ( null == uri ) return null;
		final String scheme = uri.getScheme();
		String data = null;
		if ( scheme == null )
			data = uri.getPath();
		else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
			data = uri.getPath();
		} else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
			Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
			if ( null != cursor ) {
				if ( cursor.moveToFirst() ) {
					int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
					if ( index > -1 ) {
						data = cursor.getString( index );
					}
				}
				cursor.close();
			}
		}
		return data;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		//下载结束  取消遮罩
		final Intent downloadIntent = new Intent("endDownloadFile");
		LocalBroadcastManager.getInstance(context).sendBroadcastSync(downloadIntent);

		if (intent != null) {
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
				Uri uri = manager.getUriForDownloadedFile(completeDownloadId);
				Log.d("UriForDownloadedFile:{}", uri + "");
				if (uri != null) {

					Intent handleIntent = new Intent();
					Bundle bundle = new Bundle();

					bundle.putString(WpsModel.OPEN_MODE, WpsModel.OpenMode.EDIT_MODE); // 打开模式

					bundle.putBoolean(WpsModel.SEND_CLOSE_BROAD, true); // 关闭时是否发送广播
					bundle.putBoolean(WpsModel.SEND_SAVE_BROAD, true);
					bundle.putBoolean(WpsModel.HOMEKEY_DOWN, true);
					bundle.putBoolean(WpsModel.BACKKEY_DOWN, true);
					bundle.putString(WpsModel.THIRD_PACKAGE, context.getPackageName()+'oa');
					bundle.putBoolean(WpsModel.CLEAR_TRACE, true);// 清除打开记录

					bundle.putBoolean("IsShowView", false);// 是否显示wps界面


					handleIntent.setData(null);
					String filePath = getRealFilePath(context, uri);
					handleIntent.putExtra("FILEPATH",filePath);
					handleIntent.putExtra("OpenFile",filePath);


					handleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK|
							Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

					handleIntent.setAction(Intent.ACTION_VIEW);
					handleIntent.setClassName(WpsModel.PackageName.PACKAGENAME, WpsModel.ClassName.NORMAL);

					handleIntent.putExtras(bundle);
					try {
						context.startActivity(handleIntent);
					} catch (ActivityNotFoundException e) {
						Toast.makeText(context, "请先安装wps", Toast.LENGTH_SHORT).show();
					}
				}
			} else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
				long[] ids = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
				//点击通知栏取消下载
				manager.remove(ids);
				Toast.makeText(context, "已经取消下载", Toast.LENGTH_SHORT).show();

			}
		}
	}


}