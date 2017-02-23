package com.metaisle.photosync.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.service.MediaTrackingService;
import com.metaisle.photosync.service.UploadService;
import com.metaisle.profiler.CollectorService;

public class BootReceiver extends BroadcastReceiver {
	private SharedPreferences mDefaultPrefs;

	@Override
	public void onReceive(Context context, Intent intent) {

		mDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		boolean mEnableProfiling = mDefaultPrefs.getBoolean(
				Prefs.KEY_ENABLE_PROFILING, true);

		CameraMonitor.registerObservers(context.getApplicationContext());

		context.startService(new Intent(context, MediaTrackingService.class));

		if (mEnableProfiling) {
			context.startService(new Intent(context, CollectorService.class));
		}

		context.startService(new Intent(context, UploadService.class));
	}
}
