package com.metaisle.profiler.collector.periodical;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;

public class NetworkTraffic extends PeriodicalCollector {
	private static final String TAG = "NetworkLogger";
	private long mobile_tx_temp, wifi_tx_temp;

	private ActivityManager mActivityManager;

	private SharedPreferences default_prefs;

	public NetworkTraffic(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		mobile_tx_temp = 0;
		wifi_tx_temp = 0;
		// TODO Auto-generated constructor stub
		mActivityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);

		default_prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
	}

	public void measureThroughput() {
		// ----------------------------------------------------------------
		// generate network flow
		new Thread() {
			@Override
			public void run() {
				long mobile_tx = 0;
				long wifi_tx = 0;
				try {
					// Calendar start = Calendar.getInstance();
					long old_mobile_tx = android.net.TrafficStats
							.getMobileTxBytes();
					long old_wifi_tx = android.net.TrafficStats
							.getTotalTxBytes() - old_mobile_tx;

					double time = datasend("nmsl.cs.nthu.edu.tw", 5051, 32);
					// Calendar end = Calendar.getInstance();
					long new_mobile_tx = android.net.TrafficStats
							.getMobileTxBytes();
					long new_wifi_tx = android.net.TrafficStats
							.getTotalTxBytes() - new_mobile_tx;

					if (new_mobile_tx < old_mobile_tx) {
						mobile_tx = new_mobile_tx;
					} else {
						mobile_tx = new_mobile_tx - old_mobile_tx;
					}

					if (new_wifi_tx < old_wifi_tx) {
						wifi_tx = new_wifi_tx;
					} else {
						wifi_tx = new_wifi_tx - old_wifi_tx;
					}
					double wifi_thru = 0;
					// double time = (double) (end.getTimeInMillis() - start
					// .getTimeInMillis()) / 1000;
					if (time > 0)
						wifi_thru = ((double) wifi_tx / 128) / time;

					double mobile_thru = 0;

					if (time > 0)
						mobile_thru = ((double) mobile_tx / 128) / time;

					WriteLineWithTime("thru, " + String.valueOf(mobile_thru)
							+ "," + String.valueOf(wifi_thru));
					Util.log("thrughput : " + String.valueOf(mobile_thru) + ","
							+ String.valueOf(wifi_thru));
					Util.log("size : " + mobile_tx / 1024 + "," + wifi_tx
							/ 1024);
					Util.log("time : " + time);

					default_prefs.edit()
							.putFloat("LAST_MOBILE_THRU", (float) mobile_thru)
							.putFloat("LAST_WIFI_THRU", (float) wifi_thru)
							.commit();

				} catch (Exception ex) {
				}

			}
		}.start();
	}

	/*
	 * private static long MobileRxBytes, MobileTxBytes; private static long
	 * MobileRxPackets, MobileTxPackets; private static long TotalRxBytes,
	 * TotalTxBytes; private static long TotalRxPackets, TotalTxPackets;
	 */
	public void recordProcessNetStatistic(int pid) {
	}

	/*
	 * We are going to record following items 1. Mobile RX, TX bytes 2. Mobile
	 * RX, TX packets 3. Total RX, TX packets bytes 4. Total RX, TX packets
	 * packets
	 */
	public void recordNetworkStatistic() {

		long mobile_tx = 0;
		long wifi_tx = 0;
		try {
			long new_mobile_tx = android.net.TrafficStats.getMobileTxBytes();
			long new_wifi_tx = android.net.TrafficStats.getTotalTxBytes()
					- new_mobile_tx;

			if (mobile_tx_temp == 0) {
				mobile_tx_temp = new_mobile_tx;
				mobile_tx = 0;
			} else if (new_mobile_tx < mobile_tx_temp) {
				mobile_tx = new_mobile_tx;
				mobile_tx_temp = new_mobile_tx;
			} else {
				mobile_tx = new_mobile_tx - mobile_tx_temp;
				mobile_tx_temp = new_mobile_tx;
			}

			if (wifi_tx_temp == 0) {
				wifi_tx_temp = new_wifi_tx;
				wifi_tx = 0;
			} else if (new_wifi_tx < wifi_tx_temp) {
				wifi_tx = new_wifi_tx;
				wifi_tx_temp = new_wifi_tx;
			} else {
				wifi_tx = new_wifi_tx - wifi_tx_temp;
				wifi_tx_temp = new_wifi_tx;
			}

			WriteLineWithTime(mobile_tx + "," + wifi_tx);

			// ----------------------------------------------------------------
			WriteLineWithTime("");
			WriteLine(android.net.TrafficStats.getMobileRxBytes() + ","
					+ android.net.TrafficStats.getMobileRxPackets() + ","
					+ android.net.TrafficStats.getMobileTxBytes() + ","
					+ android.net.TrafficStats.getMobileTxPackets() + ","
					+ android.net.TrafficStats.getTotalRxBytes() + ","
					+ android.net.TrafficStats.getTotalRxPackets() + ","
					+ android.net.TrafficStats.getTotalTxBytes() + ","
					+ android.net.TrafficStats.getTotalTxPackets());

			List<RunningAppProcessInfo> mylist = mActivityManager
					.getRunningAppProcesses();

			for (RunningAppProcessInfo info : mylist) {
				WriteLine(info.processName + "," + info.uid + ","
						+ android.net.TrafficStats.getUidRxBytes(info.uid)
						+ ","
						+ android.net.TrafficStats.getUidTxBytes(info.uid));
			}

		} catch (Exception ex) {
		}
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
		long total = 0;
		// TODO Auto-generated method stub
		try {
			total = android.net.TrafficStats.getMobileRxPackets();
			if (total > 0)
				total++;
			return true;
		} catch (NoClassDefFoundError e) {
		}

		return false;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		recordNetworkStatistic();
		measureThroughput();
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

	private double datasend(String ip, int port, int data_size) {
		double time = 0;
		try {
			SocketAddress socketAddress = new InetSocketAddress(ip, port);
			Socket clientSocket = new Socket();
			clientSocket.connect(socketAddress, 10 * 1000);
			char[] out = new char[1024];
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					clientSocket.getOutputStream()));
			Calendar start = Calendar.getInstance();
			for (int i = 0; i < data_size; i++) {
				bw.write(out);
			}
			bw.flush();
			// bw.write("end");
			// bw.flush();
			bw.close();
			clientSocket.close();
			Calendar end = Calendar.getInstance();
			time = (double) (end.getTimeInMillis() - start.getTimeInMillis()) / 1000;
		} catch (IOException e) {
			Util.log("socket execption : " + e.toString());
			return 0;
		}
		return time;
	}
}
