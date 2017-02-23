package com.metaisle.profiler.collector.eventdriven;

import java.io.IOException;
import java.util.Set;

import com.metaisle.profiler.collector.EventDrivenCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class ActionMiscellaneous extends EventDrivenCollector{

	private ScreenStatusReceiver mScreenReceiver;
	private boolean ScreenStatus = false;
	private IntentFilter mIntentFilter;
	private static final String TAG = "SystemActions"; 
	
	public ActionMiscellaneous(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		mScreenReceiver = new ScreenStatusReceiver();		
		mIntentFilter = new IntentFilter();
		
		//Including all broadcast intent
		mIntentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_ANSWER);
//		mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
//		mIntentFilter.addAction(Intent.ACTION_BATTERY_LOW);
//		mIntentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
		mIntentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
		mIntentFilter.addAction(Intent.ACTION_CAMERA_BUTTON);
		mIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		mIntentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
//		mIntentFilter.addAction(Intent.ACTION_DATE_CHANGED);
//		mIntentFilter.addAction(Intent.ACTION_DEVICE_STORAGE_LOW);
//		mIntentFilter.addAction(Intent.ACTION_DEVICE_STORAGE_OK);
//		mIntentFilter.addAction(Intent.ACTION_DOCK_EVENT);
//		mIntentFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
//		mIntentFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
//		mIntentFilter.addAction(Intent.ACTION_GTALK_SERVICE_CONNECTED);
//		mIntentFilter.addAction(Intent.ACTION_GTALK_SERVICE_DISCONNECTED);
		mIntentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
//		mIntentFilter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_MAIN);
//		mIntentFilter.addAction(Intent.ACTION_MANAGE_PACKAGE_STORAGE);
//		mIntentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_BUTTON);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_CHECKING);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_NOFS);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTABLE);
		mIntentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		mIntentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		mIntentFilter.addAction(Intent.ACTION_PACKAGE_RESTARTED);
		mIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
		mIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		mIntentFilter.addAction(Intent.ACTION_PROVIDER_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_REBOOT);
		mIntentFilter.addAction(Intent.ACTION_SHUTDOWN);
		mIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		mIntentFilter.addAction(Intent.ACTION_UID_REMOVED);
		mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
		mIntentFilter.addAction(Intent.ACTION_USER_PRESENT);
	}
	
	@Override
	protected void _Start() {
		// TODO Auto-generated method stub
		mContext.registerReceiver(mScreenReceiver, mIntentFilter);
	}

	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(mScreenReceiver);
	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		return ScreenStatus;
	}

	@Override
	protected String GetTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	protected String GetExtension() {
		// TODO Auto-generated method stub
		return "csv";
	}

	public class ScreenStatusReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				StringBuilder sr = new StringBuilder();
				Bundle bundle = intent.getExtras();
				Set<String> KeySet = bundle.keySet();
				for(String s : KeySet)
				{
					if(bundle.get(s)!= null){
						try{
							sr.append(",");
							sr.append(s + "," + bundle.get(s));
						}catch(Exception ex){}
					}
				}
				WriteLineWithTime(intent.getAction() + "," +sr.toString());
				if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
					ScreenStatus = false;
				}
				else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
					ScreenStatus = true;
				}
			}catch(Exception ex){}
		}
	}

}
