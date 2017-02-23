package com.metaisle.photosync.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.metaisle.photosync.facebook.UploadTask;
import com.metaisle.util.Util;

public class ConnectivityReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Util.log("Connectivity Change.");
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();

		if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			Util.log("Wifi connected");
			Util.profile(context, "upload_debug.csv", "Wifi connected");

			new UploadTask(context, false).execute();
		} else if (networkInfo != null) {
		} else {
		}

	}

}
