package com.metaisle.profiler.collector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;

import com.metaisle.util.Util;

public abstract class PeriodicalCollector extends BasicCollector {

	protected int dutyCycle;
	protected Thread mThread;
	public PeriodicalCollector(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
		
		this.dutyCycle = 5000; // in ms
	}

	@Override
	public void Start() throws FileNotFoundException
	{
		if(this.isSupported() == false)
			return;
		
		if (this.isEnable == false)
			return;
		
		
		Util.log( this.filename + " Start");
		running = true;
		

		File file = mContext.getFileStreamPath(filename + "." + GetExtension());
		if(!file.exists())
			Initialize();
			_Start();
		
		if(isEnableThread() == true){
			mThread = new Thread(new RunnableThread());
			mThread.start();
		}
		
	}
	
	@Override
	public void Stop()
	{
		running = false;
		try{
			_Stop();
		}catch(Exception ex){}

		if(mThread != null){
			try{
				mThread.interrupt();
				mThread = null;
			}catch(Exception ex){}
		}
		
		try
		{
			if(mBufferedWriter != null)
			{
				mBufferedWriter.flush();
				mBufferedWriter.close();
			}
			
			if(fOut != null)
			{
				fOut.flush();
				fOut.close();
			}
		}
		catch(IOException ex)
		{
			System.out.println(ex.getMessage());
		}
		finally
		{
			fOut = null;
			mBufferedWriter = null;
		}
	}
	
	public void SetDutyCycle(int dutyCycle)
	{
		this.dutyCycle = dutyCycle;
	}
	
	protected class RunnableThread implements Runnable {
		

		public RunnableThread() {
			// TODO Auto-generated constructor stub
		}

		public void run() {
			// TODO Auto-generated method stub
			while(running == true)
			{
				try {
					Util.log( "Get Reading");
					GetReading();
					Thread.sleep(dutyCycle);
				} catch (Exception e) {
					// TODO Auto-generated catch block
				} 
			}
		}
	}
	
	@Override
	public void Pause()
	{
		running = false;
		if(mThread != null){
			try{
				mThread.interrupt();
				mThread = null;
			}catch(Exception ex){}
		}
	}

	public abstract boolean isEnableThread();
}
