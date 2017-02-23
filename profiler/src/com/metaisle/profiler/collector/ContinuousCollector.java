package com.metaisle.profiler.collector;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

import com.metaisle.util.Util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public abstract class ContinuousCollector extends BasicCollector {

	protected int sampleRate;
	protected Sensor mSensor;
	protected SensorManager mSensorManager;
	protected SensorActivity mSensorEventListener;
	protected boolean isSupport = false;
	protected StringBuffer sb;
	protected ContinuousReading last_reading;
	public static final int count = 200;
	public static final Object UpdateLock = new Object();

	//
	private Calendar LastScan = null;
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final double THIRTY_MINUTES = 1000 * 60 * 30;

	public class ContinuousReading {
		public LinkedList<Float>[] dataList;
		public int MaxReading;
		public int MinReading;

		public ContinuousReading(int numType, int max, int min) {
			this.dataList = new LinkedList[numType];
			for (int i = 0; i < numType; i++) {
				this.dataList[i] = new LinkedList<Float>();
			}
			this.MaxReading = max;
			this.MinReading = min;
		}

		public int count() {
			return dataList[0].size();
		}

		public void add(float[] data) {
			synchronized (UpdateLock) {
				if (dataList[0].size() > count + 2) {
					for (int i = 0; i < dataList.length; i++) {
						dataList[i].removeFirst();
					}
				}

				for (int i = 0; i < dataList.length; i++) {
					dataList[i].addLast(data[i]);
				}
			}
		}
	}

	public ContinuousCollector(String filename, Context mContext)
			throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		sb = new StringBuffer();
		mSensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		sampleRate = SensorManager.SENSOR_DELAY_NORMAL; //
		mSensorEventListener = new SensorActivity();
	}

	public void SetSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	@Override
	protected void _Start() {
		if (LastScan == null) {
			LastScan = Calendar.getInstance();
			LastScan.setTimeInMillis(0);
		}

		double delta = ((double) (System.currentTimeMillis() - LastScan
				.getTimeInMillis()) / THIRTY_MINUTES);

		Util.log("LastScan:" + LastScan);
		Util.log("start Acc");
		Util.log("delta:" + delta);

		if (delta < 1) {
			return;
		} else {
			LastScan.setTimeInMillis(System.currentTimeMillis());
		}

		if (isSupport == true)
			mSensorManager.registerListener(mSensorEventListener, mSensor,
					sampleRate);
	}

	public float GetMaxRange() {
		return mSensor.getMaximumRange();
	}

	@Override
	protected void _Stop() {
		if (isSupport == true)
			mSensorManager.unregisterListener(mSensorEventListener);
	}

	@Override
	public void Pause() {
		running = false;
		_Stop();
	}

	@Override
	public boolean isSupported() {
		return isSupport;
	}

	@Override
	public Object GetReading() {
		return last_reading;
	}

	protected class SensorActivity extends Activity implements
			SensorEventListener {

		protected void onResume() {
			super.onResume();
			mSensorManager.registerListener(this, mSensor, sampleRate);
		}

		protected void onPause() {
			super.onPause();
			mSensorManager.unregisterListener(this);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {

			sb.setLength(0);

			if (last_reading == null) {
				int num = getNumEvent();
				if (num < 0)
					num = event.values.length;
				last_reading = new ContinuousReading(num,
						(int) event.sensor.getMaximumRange() / 2,
						(int) -event.sensor.getMaximumRange() / 2);
			}

			last_reading.add(event.values);
			sb.append(event.timestamp);
			for (float f : event.values) {
				sb.append("," + f);
			}
			try {
				// Log.d(TAG, sb.toString());
				WriteLine(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	protected String GetExtension() {
		// TODO Auto-generated method stub
		return "csv";
	}

	public abstract String[] GetReadingNames();

	public int getNumEvent() {
		return -1;
	}

}
