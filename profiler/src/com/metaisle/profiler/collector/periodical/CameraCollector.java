package com.metaisle.profiler.collector.periodical;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Environment;
import android.view.SurfaceView;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;


public class CameraCollector extends PeriodicalCollector{

	private Camera mCamera;
	protected static final String TAG = "Camera Collector";
	private MyShutterCallback mShutterCallback;
	private RawPictureCallback mRawPictureCallback;
	private JpegPictureCallback mJpegPictureCallback;

	private Runnable GetPicture;
	
	private File dir;
	private String ext;
	private String prename;
	private int numOfImage;
	
	//In camera, we create a directory for that
	public CameraCollector(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		
		
		// TODO Auto-generated constructor stub
		mShutterCallback = new MyShutterCallback();
		mRawPictureCallback = new RawPictureCallback();
		mJpegPictureCallback = new JpegPictureCallback();
		
		
		if(mExternalStorageWriteable == true)
		{
			File root = Environment.getExternalStorageDirectory();
			dir = new File(root.getAbsolutePath() + "/Camera/");
			if(dir.isDirectory() == false || dir.exists() == false)
			{
				if(dir.mkdirs() == false)
					throw new IOException("Can't create directory : " + dir.getAbsolutePath());
			}
			
			//Split the filename into name and ext
			if(filename.lastIndexOf('.') > 0)
			{
				prename = filename.substring(0, filename.lastIndexOf('.'));
				ext = filename.substring(filename.lastIndexOf('.'), filename.length());
			}
			else
			{
				prename = filename;
				ext = ".jpg";
			}
			ext = ".jpg";
			numOfImage = 1;
		}
		isCreateWriter = false;
		GetPicture = new RunnableThread();
	}

	@Override
	protected void _Stop()
	{
		mCamera.stopPreview();
		closeCamera();
	}
	
	@Override
	public Object GetReading()
	{
		//We will using time as 
		mCamera.takePicture(mShutterCallback, mRawPictureCallback, mJpegPictureCallback);
		return null;
	}
	
	@Override
	public void Start()
	{
		if(this.isSupported() == false)
			return;
		
		if(this.isEnable == false)
			return;
		
		Util.log( this.filename + " Start");
		running = true;

		//_Start();
		//CollectorService.DoHandler(GetPicture, dutyCycle);
		
	}

	private class MyShutterCallback implements ShutterCallback 
	{
		public void onShutter() {
			Util.log( "onShutter'd");
		}
	}
	
	private class RawPictureCallback implements PictureCallback 
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Util.log( "onPictureTaken - raw");
		}
	}

	private class JpegPictureCallback implements PictureCallback 
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Util.log( "onPictureTaken - jpeg");
			
			try {

				if(isWrite == true && mExternalStorageWriteable == true)
				{
					//we have make a new image
					fOut = new FileOutputStream(new File(dir.getAbsolutePath() + "/" + prename + "_" + numOfImage + ext));
					numOfImage++;
				}
				
				Write(data);
				
				if(mExternalStorageWriteable == true)
				{
					fOut.flush();
					fOut.close();
					fOut = null;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//CollectorService.DoHandler(GetPicture, dutyCycle);
		}
	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected String GetTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	protected void _Start() {
		// TODO Auto-generated method stub

	}
	
	private void closeCamera(){

		//Do nothing
		if(mCamera != null)
			mCamera.release();
	}
	
	private void openCamera(){
		mCamera = Camera.open();
		Camera.Parameters parameters = mCamera.getParameters();
		
		parameters.setPictureFormat(PixelFormat.JPEG);
		parameters.setPreviewSize(200, 200);
		mCamera.setParameters(parameters);
		
		SurfaceView view = new SurfaceView(mContext);
		try {
			mCamera.setPreviewDisplay(view.getHolder());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mCamera.startPreview();		
	}
	
	@Override
	protected String GetExtension() {
		// TODO Auto-generated method stub
		return "";
	}
	
	protected class RunnableThread implements Runnable {
			
	
			public RunnableThread() {
				// TODO Auto-generated constructor stub
			}
	
			public void run() {
				// TODO Auto-generated method stub
				if(running == true)
				{
					try {
						openCamera();
						GetReading();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}


}
