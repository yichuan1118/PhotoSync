package com.metaisle.profiler.collector.periodical;

import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;


public class ActivityCollector extends PeriodicalCollector{
	
	private ActivityManager mActivityManager;
	private static final String TAG = "Activity Collector";
	private final int maxTaskNum = 16;

	public ActivityCollector(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		//sBuilder = new StringBuilder();
	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		return scanActivity();
	}

	@Override
	protected String GetTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}
	
	
	private List<RunningAppProcessInfo> scanActivity(){
		String s;
		try{
			List<RunningAppProcessInfo> mylist = mActivityManager.getRunningAppProcesses();
			
			WriteLineWithTime("");
			for(RunningAppProcessInfo info : mylist){
				s = info.processName + "," + info.pid + "," + info.importance + "," + info.lru;
				WriteLine(s);
				/*
				sBuilder.setLength(0);
				sBuilder.append(info.processName);
				sBuilder.append(",");
				sBuilder.append(info.pid);
				sBuilder.append(",");
				sBuilder.append(info.importance);
				sBuilder.append(",");
				sBuilder.append(info.lru);
				WriteLine(sBuilder.toString());
				*/
				Util.log( s);
			}
			return mylist;
		}catch(Exception ex){}
		return null;
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
	protected String GetExtension() {
		// TODO Auto-generated method stub
		return "csv";
	}

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}


}
