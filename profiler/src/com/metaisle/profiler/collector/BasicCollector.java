package com.metaisle.profiler.collector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import android.content.Context;
import android.os.Environment;

import com.metaisle.util.Util;

public abstract class BasicCollector {

	protected BufferedWriter mBufferedWriter;
	protected FileOutputStream fOut;
	protected String TAG = null;
	protected boolean running;
	protected boolean isWrite;
	protected Context mContext;
	protected String filename;
	protected static boolean mExternalStorageAvailable = false;
	protected static boolean mExternalStorageWriteable = false;
	private static boolean isCheckExternalStorage = false;
	protected static String state = Environment.getExternalStorageState();
	protected boolean isEnable = false;
	protected boolean isCreateWriter = true; // Default we will create file
												// writer
	private static BasicCollector ThisCollector;

	public BasicCollector() {
		// Do nothing, sometimes we need get instance without parameter
	}

	public BasicCollector(String filename, Context mContext) {
		if (isCheckExternalStorage == false) {
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				// We can read and write the media
				mExternalStorageAvailable = mExternalStorageWriteable = true;
			} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				// We can only read the media
				mExternalStorageAvailable = true;
				mExternalStorageWriteable = false;
			} else {
				// Something else is wrong. It may be one of many other states,
				// but all we need
				// to know is we can neither read nor write
				mExternalStorageAvailable = mExternalStorageWriteable = false;
			}
			isCheckExternalStorage = true;
		}

		this.filename = filename;
		this.mContext = mContext;
		this.isWrite = true;
		this.TAG = this.GetTAG();
		running = false;
		ThisCollector = this;
	}

	public static BasicCollector GetCollector() {
		return ThisCollector;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void SetWritable(boolean b) {
		this.isWrite = b;
	}
	public void Flush() throws IOException{

		if (isWrite == true && mBufferedWriter != null) {
			mBufferedWriter.flush();
		}
	}

	public void SetEnable(boolean arg1) throws FileNotFoundException {
		this.isEnable = arg1;
		if (arg1 == true) {

		}
	}

	public void Initialize() throws FileNotFoundException {


		if(this.isSupported() == false)
			return;
		
		if (this.isEnable == false)
			return;
		
		if (isCreateWriter == true) {
			try{
				if(mBufferedWriter != null){
					mBufferedWriter.flush();
					mBufferedWriter.close();
				}
			}catch(Exception e){}
			
			if (isAppend())
				fOut = mContext.openFileOutput(filename + "." + GetExtension(),
						Context.MODE_APPEND);
			else
				fOut = mContext.openFileOutput(filename + "." + GetExtension(),
						Context.MODE_PRIVATE);
			mBufferedWriter = new BufferedWriter(new OutputStreamWriter(fOut));
		}
		/*
		 * //Create the file if(isCreateWriter == true &&
		 * mExternalStorageWriteable == true) { File root =
		 * Environment.getExternalStorageDirectory(); File dir = new
		 * File(root.getAbsolutePath() + "/philip"); dir.mkdir(); File f = new
		 * File(dir, filename + "." + GetExtension());
		 * 
		 * try { fOut = new FileOutputStream(f); } catch (FileNotFoundException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * OutputStreamWriter osw = new OutputStreamWriter(fOut);
		 * 
		 * mBufferedWriter = new BufferedWriter(osw); }
		 */

	}

	protected boolean isAppend() {
		return true;
	}

	public void Start() throws Exception{

		if(this.isSupported() == false)
			return;
		
		if (this.isEnable == false)
			return;
		
		
		Util.log( this.filename + " Start");
		running = true;
		

		try {
		File file = mContext.getFileStreamPath(filename + "." + GetExtension());
		if(!file.exists())
			Initialize();
			_Start();
		} catch (Exception ex) {
		}
	}

	public void Stop() {

		running = false;

		try {
			_Stop();
		} catch (Exception ex) {
		}

		try {
			if (mBufferedWriter != null) {
				mBufferedWriter.flush();
				mBufferedWriter.close();
			}

			if (fOut != null) {
				fOut.flush();
				fOut.close();
			}
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} finally {
			fOut = null;
			mBufferedWriter = null;
		}
	}

	protected void WriteLineWithTime(final String text) throws IOException {
		if (isWrite == true && mBufferedWriter != null) {
			Date d = new Date();
			if(text == null || text.length() == 0)
				mBufferedWriter.write(d.getTime() + "");
			else
				mBufferedWriter.write(d.getTime() + "," + text);
			mBufferedWriter.newLine();
			mBufferedWriter.flush();
		}
	}

	protected void WriteLine(final String text) throws IOException {
		if (isWrite == true && mBufferedWriter != null) {
			mBufferedWriter.write(text);
			mBufferedWriter.newLine();
		}
	}

	protected void Write(byte[] data) throws IOException {
		if (isWrite == true && fOut != null) {
			fOut.write(data);
		}
	}

	public abstract void Pause();

	protected abstract void _Start();

	protected abstract void _Stop();

	public abstract boolean isSupported();

	public abstract Object GetReading();

	protected abstract String GetTAG();

	protected abstract String GetExtension();

}
