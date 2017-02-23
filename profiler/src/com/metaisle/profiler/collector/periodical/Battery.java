package com.metaisle.profiler.collector.periodical;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;

public class Battery extends PeriodicalCollector {
	protected static final String TAG = "Battery Collect";
	private Intent batteryStatus;
	private IntentFilter ifilter;
	public static double batteryLevel = -1;
	private String battery;

	public Battery(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void _Start() {
		// TODO Auto-generated method stub
		GetReading();
	}

	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryStatus = mContext.getApplicationContext().registerReceiver(null,
				ifilter);
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		float batteryPct = level / (float) scale;
		battery = "" + (batteryPct * 100);
		Util.log( battery);
		final String BROADCAST_ACTION = "profile_data";
		Intent mintent = new Intent();
		mintent.setAction(BROADCAST_ACTION);
		mintent.putExtra("type", 3);
		mintent.putExtra("time", System.currentTimeMillis());
		mintent.putExtra("value", String.valueOf(battery));
		mContext.sendBroadcast(mintent);
		try {
			WriteLineWithTime("battery level," + battery);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

}
