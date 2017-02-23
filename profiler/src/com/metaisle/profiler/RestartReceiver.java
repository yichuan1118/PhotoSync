package com.metaisle.profiler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.metaisle.util.Util;

public class RestartReceiver extends BroadcastReceiver {
	public static final String CUSTOM_INTENT = "PhotoSync.Profiler.RESTART_SERVICE";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Util.log("Incoming Alarm Receiver by " + intent.getAction());

		try {
			context.startService(new Intent(context, CollectorService.class));
		} catch (Exception e) {
		}
	}

}
