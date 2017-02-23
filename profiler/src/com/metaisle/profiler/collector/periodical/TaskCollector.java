package com.metaisle.profiler.collector.periodical;

import java.io.IOException;
import java.util.List;

import com.metaisle.profiler.collector.PeriodicalCollector;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;


public class TaskCollector extends PeriodicalCollector{
	
	private ActivityManager mActivityManager;
	private static final String TAG = "Task Collector";
	private final int maxTaskNum = 16;

	public TaskCollector(String filename, Context mContext) throws IOException {
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
	
	
	private List<ActivityManager.RunningTaskInfo> scanActivity(){
		String s;
		try{
			List<ActivityManager.RunningTaskInfo> runningTasks = mActivityManager.getRunningTasks(maxTaskNum);
			
			int actCount = 1;
			WriteLineWithTime("");
			for(ActivityManager.RunningTaskInfo taskInfo:runningTasks){
				//ex: 1, 
				s =  actCount + "," + taskInfo.baseActivity.getClassName() + "," + taskInfo.numRunning + "," + taskInfo.numActivities;
				if(taskInfo.numRunning > 1){
					s += "," + taskInfo.topActivity.getClassName();
				}
				WriteLine(s);
				actCount++;
			}
			return runningTasks;
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
