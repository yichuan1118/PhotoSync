package com.metaisle.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

public class WlanScanner extends BroadcastReceiver {

	private AlarmManager alarmMgr;
	private PendingIntent pendingIntent;

	private static WifiLock wifiLock;
	private static WakeLock wakeLock;

	public static void lock() {
		try {
			wakeLock.acquire();
			wifiLock.acquire();
		} catch (Exception e) {
			Log.e("WlanSilencer", "Error getting Lock: " + e.getMessage());
		}
	}

	public static void unlock() {
		if (wakeLock.isHeld())
			wakeLock.release();
		if (wifiLock.isHeld())
			wifiLock.release();
	}

	public WlanScanner() {
	} // called by the AlarmManager

	// this constructor is invoked by WlanLockService (see next code snippet)
	public WlanScanner(Context context, int timeoutInSeconds) {

		// initialise the locks
		wifiLock = ((WifiManager) context
				.getSystemService(Context.WIFI_SERVICE)).createWifiLock(
				WifiManager.WIFI_MODE_SCAN_ONLY, "WlanSilencerScanLock");
		wakeLock = ((PowerManager) context
				.getSystemService(Context.POWER_SERVICE)).newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "WlanSilencerWakeLock");

		alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		// use this class as the receiver
		Intent intent = new Intent(context, WlanScanner.class);
		// create a PendingIntent that can be passed to the AlarmManager
		pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		// create a repeating alarm, that goes of every x seconds
		// AlarmManager.ELAPSED_REALTIME_WAKEUP = wakes up the cpu only
		alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime(), timeoutInSeconds * 1000,
				pendingIntent);
	}

	// stop the repeating alarm
	public void stop() {
		alarmMgr.cancel(pendingIntent);
	}

	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.v("WlanSilencer", "Alarm received");
		WifiManager connManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (connManager.isWifiEnabled()) {
			lock(); // lock wakeLock and wifiLock, then scan.
			// unlock() is then called at the end of the onReceive function of
			// WifiStateReciever
			connManager.startScan();
		}
	}

}
