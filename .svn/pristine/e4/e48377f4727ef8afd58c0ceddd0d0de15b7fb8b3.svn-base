package com.metaisle.profiler.collector.continuous;

import java.io.IOException;
import java.util.List;

import com.metaisle.profiler.collector.ContinuousCollector;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;


public class Gyroscope extends ContinuousCollector {
	
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float timestamp;
	protected static final String TAG = "Gyroscope";
	
	public Gyroscope(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		

		List<Sensor> list = mSensorManager.getSensorList(Sensor.TYPE_GYROSCOPE);
		if(list != null && list.size() > 0){
			isSupport = true;
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
	        WriteLine("Timestamp,Accuracy,X,Y,Z");
		}
		//mSensorEventListener = new MySensorActivity();
	}
	
	/*
	protected class MySensorActivity extends SensorActivity {


	     public void onSensorChanged(SensorEvent event) {

	    	 try {
	 		sb = new StringBuffer();
	 		
	 		last_reading.add(event.values);
	 		
	    	 sb.append(event.timestamp + ","  + event.accuracy);
	    	 
	    	 if (timestamp != 0) {
	              final float dT = (event.timestamp - timestamp) * NS2S;
	              sb.append("," + event.values[0] * dT);
	              sb.append("," + event.values[1] * dT);
	              sb.append("," + event.values[2] * dT);
	          }
	          timestamp = event.timestamp;
	          
	    		 Log.d(TAG, sb.toString());
				WriteLine(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	     }
	 }
*/
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

