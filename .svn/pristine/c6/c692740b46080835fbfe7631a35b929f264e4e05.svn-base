package com.metaisle.profiler.collector.periodical;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;

public class NeighborCellCollector extends PeriodicalCollector {
	protected static final String TAG = "Neighbor Cell";
	private TelephonyManager mTelephonyManager;

	public NeighborCellCollector(String filename, Context mContext)
			throws IOException {
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
		Util.log( "start collect");
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
		this.mTelephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		List<NeighboringCellInfo> NeighboringList = mTelephonyManager
				.getNeighboringCellInfo();
		try {
			WriteLineWithTime("");
			Util.log( "NeighboringList.size():" + NeighboringList.size());
			for (int i = 0; i < NeighboringList.size(); i++) {

				String dBm;
				int rssi = NeighboringList.get(i).getRssi();
				if (rssi == NeighboringCellInfo.UNKNOWN_RSSI) {
					dBm = "-1";
				} else {
					dBm = String.valueOf(-113 + 2 * rssi);
				}

				WriteLine(String.valueOf(NeighboringList.get(i).getCid()) + ","
						+ String.valueOf(NeighboringList.get(i).getLac()) + ","
						+ dBm);// CID,LAC,RSSI
				Util.log( String.valueOf(NeighboringList.get(i).getCid())
						+ "," + String.valueOf(NeighboringList.get(i).getLac())
						+ "," + dBm);
			}

		}

		catch (IOException e) {
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
