package com.metaisle.profiler.collector.periodical;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.metaisle.profiler.collector.PeriodicalCollector;

import android.content.Context;


public class CPUUsageCollector extends PeriodicalCollector{
	private static final String TAG = "CPU Usage";
	public CPUUsageCollector(String filename, Context mContext)
			throws IOException {
		super(filename, mContext);
		// TODO Auto generated constructor stub
	}

	int usage = 0;
	int ThreadTime = 3000;
	
	private float readUsage() {
		float usage = 0;
		long cpu1, cpu2, idle1, idle2;
		cpu1 = cpu2 = idle1 = idle2 = 0;
	    try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        
	        String[] toks = load.split("\\s+");

	        idle1 = Long.parseLong(toks[4]);
	        
	        for(int i=1; i<9&&i <toks.length;i++){
	        	if( i == 4)
	        		continue;
	        	cpu1 += Long.parseLong(toks[i]);
	        }
	        
	        try {
	            Thread.sleep(1000);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();
	        
	        toks = load.split("\\s+");

	        idle2 = Long.parseLong(toks[4]);
	        
	        for(int i=1; i<9&&i <toks.length;i++){
	        	if( i == 4)
	        		continue;
	        	cpu2 += Long.parseLong(toks[i]);
	        }
	        
	        usage =  (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
	        WriteLineWithTime(String.valueOf(usage));
	        return usage;

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return usage;
	}
	
	@Override
	protected void _Start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		return readUsage();
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
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return true;
	}

}
