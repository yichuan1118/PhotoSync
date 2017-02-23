package com.metaisle.util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Base64;
import android.util.Log;

import com.metaisle.profiler.BuildConfig;

public class Util {

	public static boolean log(String msg) {
		if (BuildConfig.DEBUG) {
			StackTraceElement ste = new Throwable().fillInStackTrace()
					.getStackTrace()[1];
			String fullname = ste.getClassName();
			String name = fullname.substring(fullname.lastIndexOf('.'));
			String tag = name + "." + ste.getMethodName();
			Log.v(tag, msg);
			return true;
		} else
			return false;
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public static boolean isOnWifi(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();

		if (networkInfo != null
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else {
			return false;
		}
		// ConnectivityManager connManager = (ConnectivityManager) context
		// .getSystemService(Context.CONNECTIVITY_SERVICE);
		// NetworkInfo mWifi = connManager
		// .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		//
		// if (mWifi.isConnected()) {
		// return true;
		// }
		// return false;
	}

	public static boolean isCharging(Context context) {
		Intent intent = context.registerReceiver(null, new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED));
		int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		return plugged == BatteryManager.BATTERY_PLUGGED_AC
				|| plugged == BatteryManager.BATTERY_PLUGGED_USB;
	}

	public static void profile(final Context context, final String filename,
			final String text) {
		new Thread() {
			@Override
			public void run() {
				FileOutputStream fOut;
				try {
					fOut = context
							.openFileOutput(filename, Context.MODE_APPEND);
					BufferedWriter bw = new BufferedWriter(
							new OutputStreamWriter(fOut));
					bw.write("" + System.currentTimeMillis() + ", " + text
							+ "\n");
					bw.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public static String EncodeBase64(String orig) {
		// String to be encoded with Base64
		// Sending side
		byte[] data = null;
		try {
			data = orig.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		return Base64.encodeToString(data, Base64.DEFAULT);
	}

}
