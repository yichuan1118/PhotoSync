package com.metaisle.profiler.collector.eventdriven;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.metaisle.profiler.collector.EventDrivenCollector;
import com.metaisle.util.Util;

public class Cellular extends EventDrivenCollector {

	private TelephonyManager mTelephonyManager;
	private MyPhoneStateListener mMyPhoneStateListener;
	private final String TAG = "Cellular";

	public Cellular(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		mMyPhoneStateListener = new MyPhoneStateListener();

		this.mTelephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);

	}

	@Override
	public void _Start() {
		mTelephonyManager.listen(mMyPhoneStateListener,
				PhoneStateListener.LISTEN_CELL_LOCATION
						| PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_CALL_STATE
						| PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
						| PhoneStateListener.LISTEN_DATA_ACTIVITY
						| PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR
						| PhoneStateListener.LISTEN_SERVICE_STATE);
	}

	@Override
	public void _Stop() {
		mTelephonyManager.listen(mMyPhoneStateListener,
				PhoneStateListener.LISTEN_NONE);
	}

	private class MyPhoneStateListener extends PhoneStateListener {

		public void onCallForwardingIndicatorChanged(boolean cfi) {

			try {
				WriteLineWithTime("CallForward," + cfi);
			} catch (Exception ex) {
			}
		}

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			try {
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					WriteLineWithTime("idle");
					Util.log( "Call state idle");
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					WriteLineWithTime("ringing," + incomingNumber);
					Util.log( "Call state ringing");
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					WriteLineWithTime("offhook," + incomingNumber);
					Util.log( "Call state offhook");
					break;
				default:
					break;
				}
			} catch (Exception ex) {
			}
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			super.onCellLocationChanged(location);
			try {
				if (location instanceof GsmCellLocation) {
					GsmCellLocation gloc = (GsmCellLocation) location;
					WriteLineWithTime("GSM," + gloc.getCid());
					Util.log( "GSM Cell id = " + gloc.getCid());
				} else if (location instanceof CdmaCellLocation) {
					CdmaCellLocation gloc = (CdmaCellLocation) location;
					WriteLineWithTime("CDMA" + gloc.getBaseStationId());
					Util.log( "CDMA Cell id = " + gloc.getBaseStationId());
				}
			} catch (Exception ex) {
			}

		}

		@Override
		public void onDataConnectionStateChanged(int state, int networkType) {
			try {
				WriteLineWithTime("DataActivity," + networkType + "," + state);
			} catch (Exception ex) {
			}
		}

		@Override
		public void onMessageWaitingIndicatorChanged(boolean mwi) {
			try {
				WriteLineWithTime("Message," + mwi);
			} catch (Exception ex) {
			}
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			try {
				WriteLineWithTime("ServiceState," + serviceState.getState());
			} catch (Exception ex) {
			}
		}

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			int mStrength;
			try {
				if (signalStrength.isGsm()) {
					mStrength = signalStrength.getGsmSignalStrength();
					//WriteLineWithTime("Signal," +String.valueOf(mStrength));
					// ---------------------------------------------------------
					final String BROADCAST_ACTION = "profile_data";
					Intent mintent = new Intent();
					mintent.setAction(BROADCAST_ACTION);
					if (mStrength != 99) {
						WriteLineWithTime("Signal,"
								+ String.valueOf(-113 + 2 * mStrength));
						mintent.putExtra("value",
								String.valueOf(-113 + 2 * mStrength));
					} else {
						WriteLineWithTime("Signal," + String.valueOf(0));
						mintent.putExtra("value", String.valueOf(0));
					}
					Util.log( "Signal Strength=" + String.valueOf(-113 + 2 * mStrength));

					mintent.putExtra("type", 0);
					mintent.putExtra("time", System.currentTimeMillis());
					mContext.sendBroadcast(mintent);
					// ---------------------------------------------------------
				} else {
					int strength = -1;
					if (signalStrength.getEvdoDbm() < 0)
						strength = signalStrength.getEvdoDbm();
					else if (signalStrength.getCdmaDbm() < 0)
						strength = signalStrength.getCdmaDbm();
					if (strength < 0) {
						// convert to asu
						// mStrength = Math.round((strength + 113f) / 2f);
						mStrength = strength;
						WriteLineWithTime("Signal," + String.valueOf(mStrength));

					// ---------------------------------------------------------						Util.log( "Signal Strength=" + mStrength);
						final String BROADCAST_ACTION = "profile_data";
						Intent mintent = new Intent();
						mintent.setAction(BROADCAST_ACTION);
						mintent.putExtra("type", 0);
						mintent.putExtra("time", System.currentTimeMillis());
						mintent.putExtra("value", String.valueOf(mStrength));
						mContext.sendBroadcast(mintent);
					// ---------------------------------------------------------
					}
				}
			} catch (Exception ex) {
			}
		}
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
}
