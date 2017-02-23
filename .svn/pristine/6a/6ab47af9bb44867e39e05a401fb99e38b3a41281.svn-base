package com.metaisle.profiler.collector.eventdriven;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.metaisle.profiler.collector.EventDrivenCollector;
import com.metaisle.util.Util;

public class Connectivity extends EventDrivenCollector {

	private static final String TAG = "Connectivity";
	private NetworkStateReceiver mConnectionReceiver;
	private WifiManager myWifiManager;

	public Connectivity(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		myWifiManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	protected void _Start() {
		// TODO Auto-generated method stub
		Util.log( "_Start()");
		IntentFilter aFilter = new IntentFilter(
				ConnectivityManager.CONNECTIVITY_ACTION);
		mConnectionReceiver = new NetworkStateReceiver();
		mContext.registerReceiver(mConnectionReceiver, aFilter);

	}

	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(mConnectionReceiver);
	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
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

	private class NetworkStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				boolean noConnectivity = intent.getBooleanExtra(
						ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				NetworkInfo aNetworkInfo = (NetworkInfo) intent
						.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if (!noConnectivity) {
					// mIsConnected = true;
					if (aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
						// Handle connected case - 3G
						WriteLineWithTime(aNetworkInfo.getState() + ","
								+ aNetworkInfo.getTypeName() + ","
								+ aNetworkInfo.getSubtypeName());
						// networkStatus = aNetworkInfo.getSubtypeName() +
						// "_CONNECTED";
					} else if (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						// Handle connected case - WIFI
						WifiInfo myWifiInfo = myWifiManager.getConnectionInfo();
						Util.log( "WIFI");
						Util.log( "" + myWifiInfo.getRssi());
						WriteLineWithTime(aNetworkInfo.getState() + ","
								+ aNetworkInfo.getTypeName() + ","
								+ myWifiInfo.getBSSID() + ","
								+ myWifiInfo.getSSID() + ","
								+ myWifiInfo.getRssi() + ","
								+ myWifiInfo.getLinkSpeed());

                                                // -----------------------------------------------------------
						final String BROADCAST_ACTION = "profile_data";
						Intent mintent = new Intent();
						mintent.setAction(BROADCAST_ACTION);
						mintent.putExtra("type", 1);
						mintent.putExtra("time", System.currentTimeMillis());
						mintent.putExtra("value",
								String.valueOf(myWifiInfo.getRssi()));
						context.sendBroadcast(mintent);
                                                // -----------------------------------------------------------
					}
				} else {
					WriteLineWithTime(aNetworkInfo.getState() + ","
							+ aNetworkInfo.getTypeName());
				}
			} catch (Exception ex) {
			}
		}

	}
}
