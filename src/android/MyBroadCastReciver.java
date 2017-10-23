package com.disusered;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.disusered.WpsModel.Reciver;

public class MyBroadCastReciver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		String s = intent.getAction();
		if (s.equals(Reciver.ACTION_BACK)) {
			//System.out.println(Reciver.ACTION_BACK);
			redirectMyApp(context);

		} else if (s.equals(Reciver.ACTION_CLOSE)) {
			redirectMyApp(context);



		} else if (s.equals(Reciver.ACTION_HOME)) {
			System.out.println(Reciver.ACTION_HOME);


		} else if (s.equals(Reciver.ACTION_SAVE)) {
			//System.out.println(Reciver.ACTION_SAVE);
			redirectMyApp(context);

		}

	}

	private  void redirectMyApp(Context context)
	{
		PackageManager packageManager = context.getPackageManager();
		Intent newIntent = new Intent();
		// 这里面的值是你要跳转app的包名，你跳转的清单文件里的package名
		newIntent = packageManager.getLaunchIntentForPackage(context.getPackageName());
		context.getApplicationContext().startActivity(newIntent);
	}

}
