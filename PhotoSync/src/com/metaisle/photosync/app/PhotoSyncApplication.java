package com.metaisle.photosync.app;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.receiver.CameraMonitor;
import com.metaisle.photosync.service.MediaTrackingService;
import com.metaisle.photosync.service.UploadService;
import com.metaisle.profiler.CollectorService;
import com.metaisle.util.Util;

public class PhotoSyncApplication extends Application {
	private SharedPreferences mPrefs;
	private SharedPreferences mDefaultPrefs;

	@Override
	public void onCreate() {
		super.onCreate();

		mPrefs = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);

		mDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean mEnableProfiling = mDefaultPrefs.getBoolean(
				Prefs.KEY_ENABLE_PROFILING, true);

		SharedPreferences dprefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		dprefs.edit().putString("KEY_FTP_SERVER", "nmsl.cs.nthu.edu.tw")
				.putString("KEY_FTP_USER", "nthu")
				.putString("KEY_FTP_PASS", "nthu").commit();

		CameraMonitor.registerObservers(getApplicationContext());
		startService(new Intent(this, MediaTrackingService.class));
		if (mEnableProfiling) {
			startService(new Intent(this, CollectorService.class));
		}
		startService(new Intent(this, UploadService.class));
	}
}
