package com.metaisle.profiler.collector.continuous;

import java.io.IOException;
import java.util.List;

import com.metaisle.profiler.collector.ContinuousCollector;

import android.content.Context;
import android.hardware.Sensor;


public class Proximity extends ContinuousCollector {

	protected static final String TAG = "Proximity";
	

	public Proximity(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		

		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY);
		if(list != null && list.size() > 0){
			isSupport = true;
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	        WriteLine("Timestamp,Accuracy,Distance(cm)");
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
		return new String[]{"cm"};
	}

	@Override
	public int getNumEvent(){
		return 1;
	}
}
