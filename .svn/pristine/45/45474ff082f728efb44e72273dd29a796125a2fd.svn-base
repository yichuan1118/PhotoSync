package com.metaisle.profiler.collector.periodical;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;

public class WiFiCollector extends PeriodicalCollector {
	WifiManager mWifiManager;
	wifiScanReceiver mReceiver;
	protected static final String TAG = "WiFi Collector";
	private boolean isStart = false;
	private boolean OriWifiState = false;
	private static final double THIRTY_MINUTES = 1000 * 60 * 30;
	private Calendar LastScan = null;

	public WiFiCollector(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		mWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);

		mReceiver = new wifiScanReceiver();

		// WriteLine("Time,SSID,Level,Capability,Frequency,BSSID");
		// mWifiManager.setWifiEnabled(true);

		// this.dutyCycle = 10 * 60 * 1000;
	}

	@Override
	protected void _Start() {
		// TODO Auto-generated method stub

		if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
				|| mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING)
			OriWifiState = true;
		else
			OriWifiState = false;

		if (LastScan == null) {
			Util.log("LastScan:" + LastScan);
			Util.log("start scan WiFi");
			LastScan = Calendar.getInstance();
			LastScan.setTimeInMillis(System.currentTimeMillis());
//			mWifiManager.setWifiEnabled(true);
			GetReading();
			mContext.registerReceiver(mReceiver, new IntentFilter(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		}
		double delta = ((double) (System.currentTimeMillis() - LastScan
				.getTimeInMillis()) / THIRTY_MINUTES);
		if (delta >= 1) {
			Util.log("LastScan:" + LastScan);
			Util.log("delta:" + delta);
			Util.log("start scan WiFi");
			LastScan.setTimeInMillis(System.currentTimeMillis());
//			mWifiManager.setWifiEnabled(true);
			GetReading();
			mContext.registerReceiver(mReceiver, new IntentFilter(
					WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		}
	}

	@Override
	public void _Stop() {
		isStart = false;
		mContext.unregisterReceiver(mReceiver);
	}

	@Override
	public boolean isSupported() {
		return true;
	}

	private class wifiScanReceiver extends BroadcastReceiver {
		List<ScanResult> wifiList;

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				wifiList = mWifiManager.getScanResults();
				if (wifiList != null) {
					WriteLineWithTime("");
					for (ScanResult s : wifiList) {
						Util.log(s.SSID + "," + s.level + "," + s.capabilities
								+ "," + s.frequency + "," + s.BSSID);
						// SSID,Level,Capability,Frequency,BSSID

						WriteLine(s.SSID + "," + s.level + "," + s.capabilities
								+ "," + s.frequency + "," + s.BSSID);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void Pause() {
		running = false;
		if (mThread != null) {
			try {
				mThread.interrupt();
				mThread = null;
			} catch (Exception ex) {
			}
		}
//		mWifiManager.setWifiEnabled(OriWifiState);

	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		isStart = true;

		mWifiManager.startScan();
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

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}
}
