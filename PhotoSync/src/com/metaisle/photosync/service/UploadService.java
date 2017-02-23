package com.metaisle.photosync.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.facebook.UploadTask;
import com.metaisle.util.Util;

public class UploadService extends Service {
	public static final long FIVE_MIN_MILLI = 5 * 60 * 1000;
	public static long milli_per_timeslot;

	AlarmManager am;
	private SharedPreferences default_prefs;
	SharedPreferences mPrefs;

	@Override
	public void onCreate() {
		super.onCreate();

		default_prefs = PreferenceManager.getDefaultSharedPreferences(this);
		milli_per_timeslot = default_prefs.getLong(
				Prefs.KEY_MILLI_PER_TIMESLOT, FIVE_MIN_MILLI);

		mPrefs = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);

		am = (AlarmManager) getSystemService(ALARM_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Util.log("Upload service called, starting UploadTask");

		new UploadTask(this, false).execute();

		Intent i = new Intent(this, UploadService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);

		long next_checking = System.currentTimeMillis() + milli_per_timeslot;
		am.set(AlarmManager.RTC_WAKEUP, next_checking, pi);

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
