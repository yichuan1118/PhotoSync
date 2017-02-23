package com.metaisle.profiler.collector.eventdriven;

import java.io.IOException;

import com.metaisle.profiler.collector.EventDrivenCollector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMS extends EventDrivenCollector {
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String TAG = "SMS";
	private IntentFilter mIntentFilter;
	private SMSReceiver mSMSReceiver;

	public SMS(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(SMS_RECEIVED);
		mSMSReceiver = new SMSReceiver();
	}

	@Override
	protected void _Start() {
		// TODO Auto-generated method stub
		mContext.registerReceiver(mSMSReceiver, mIntentFilter);
	}

	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub
		mContext.unregisterReceiver(mSMSReceiver);
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

	private class SMSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			// ---get the SMS message passed in---
			try {
				Bundle bundle = intent.getExtras();
				SmsMessage[] msgs = null;
				String str = "";
				if (bundle != null) {
					// ---retrieve the SMS message received---
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						WriteLineWithTime(msgs[i].getTimestampMillis() + ","
								+ msgs[i].getOriginatingAddress());
					}
				}
			} catch (Exception ex) {
			}
		}

	}
}
