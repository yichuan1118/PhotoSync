package com.metaisle.profiler.collector.periodical;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;


public class Microphone extends PeriodicalCollector {
	protected static final String TAG = "Microphone";

	private MediaRecorder mediaRecorder;
	int num_file;
	
	
	
	public Microphone(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		
		isCreateWriter = false;
	}

	@Override
	protected void _Start() {
		num_file = 0;
		//mediaRecorder = new MediaRecorder();
	}	
	
		
	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub
		try{
			//mediaRecorder.release();
		}catch(Exception ex){}
		
	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		try {
			num_file++;

			mediaRecorder = new MediaRecorder();
			if(mExternalStorageWriteable == true)
			{
				File root = Environment.getExternalStorageDirectory();
				File dir = new File(root.getAbsolutePath() + "/philip/sound");
				dir.mkdir();
				File f = new File(dir, filename + "_" + num_file + "." + GetExtension());
				
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
				if(isWrite==true)
					mediaRecorder.setOutputFile(f.getAbsolutePath());
			}
			mediaRecorder.prepare();
			mediaRecorder.start();
			
			Thread.sleep(dutyCycle);

			mediaRecorder.stop();

			mediaRecorder.release();
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Util.log( "Collect once");
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
		return "3gpp";
	}

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}

}
