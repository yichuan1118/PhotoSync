package com.metaisle.profiler.collector.continuous;

import java.io.IOException;
import java.util.List;

import com.metaisle.profiler.collector.ContinuousCollector;


import android.content.Context;
import android.hardware.Sensor;


public class Accelerometer extends ContinuousCollector {

	public static final String TAG = "Accelerometer";
	
	public Accelerometer(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		
		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if(list != null && list.size() > 0){
			isSupport = true;
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	        WriteLine("Timestamp,Accuracy,X,Y,Z");
		}
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
	public String[] GetReadingNames() {
		// TODO Auto-generated method stub
		return new String[]{"X", "Y", "Z"};
	}
}
