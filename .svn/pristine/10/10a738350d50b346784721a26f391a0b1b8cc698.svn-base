package com.metaisle.profiler.collector.continuous;

import java.io.IOException;
import java.util.List;

import com.metaisle.profiler.collector.ContinuousCollector;

import android.content.Context;
import android.hardware.Sensor;


public class MagneticField extends ContinuousCollector {

	protected static final String TAG = "MagneticField";

	public MagneticField(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		

		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		if(list != null && list.size() > 0){
			isSupport = true;
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	        WriteLine("Timestamp,Accuracy,X(uT),Y(uT),Z(uT)");
		}
	}

	@Override
	protected String GetTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	public String[] GetReadingNames() {
		// TODO Auto-generated method stub
		return new String[]{"X", "Y", "Z"};
	}
}
