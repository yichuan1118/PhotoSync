package com.metaisle.profiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import com.metaisle.util.Util;

public class UploadStatisticFile {

	public static final String KEY_FTP_SERVER = "KEY_FTP_SERVER";
	public static final String KEY_FTP_USER = "KEY_FTP_USER";
	public static final String KEY_FTP_PASS = "KEY_FTP_PASS";
	public static String ftp_profile_server;
	public static String username;
	public static String password;

	private TelephonyManager mTelephonyManager;
	private static Context mContext;
	private String PhoneNumber = null;
	private String DeviceID = null;
	// private static final String PREFS_NAME = "PhotoSyncCollector";
	private static final String LAST_UPLOAD_DATE = "LastUploadTime";
	private static final String LAST_COPY2SD_DATE = "LastCopy2SDTime";
	private Date lastUpload;
	private Date lastCopy2SD;
	private NetworkStateReceiver mConnectionReceiver;
	private BatteryStateReceiver mBatteryStateReceiver;

	private static boolean isCharging;
	private static boolean isWifiConnected;
	private static boolean noConnectivity;
	protected static boolean mExternalStorageAvailable = false;
	protected static boolean mExternalStorageWriteable = false;
	private static boolean isCheckExternalStorage = false;
	protected static String state = Environment.getExternalStorageState();
	// private static final String NetworkStorage =
	// "http://photosyncprofiler.appspot.com/contextupload";

	private static final double MILLSECS_HALF_DAY = 1000 * 60 * 60 * 12;
	private static final double MILLSECS_ONE_HOUR = 1000 * 60 * 60;
	public static final long profilebackup_length = 1000 * 60 * 60 * 24;

	private static final String SDStorage = "/sdcard/PhotoSync/";

	private Handler mHandler = new Handler();
	private Runnable mRunner = new Runnable() {
		public void run() {
			Upload();
			mHandler.postDelayed(this, (long) MILLSECS_ONE_HOUR);
		}
	};

	/**
	 * Initial function 1. Acquire get phone number 3. Register connectivity
	 * listener
	 */
	public UploadStatisticFile(Context mContext) {

		IntentFilter aFilter;
		File path = new File(SDStorage);
		if(!path.exists())
		{
			path.mkdir();
		}
		this.mContext = mContext;

		// Initial
		mExternalStorageWriteable = mExternalStorageAvailable = isCharging = isWifiConnected = false;

		// Acquire phone number
		this.mTelephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		PhoneNumber = mTelephonyManager.getLine1Number();

		if (PhoneNumber == null)
			PhoneNumber = "Unknown";
		DeviceID = Secure.getString(mContext.getContentResolver(),
				Secure.ANDROID_ID);
		Util.log("PhoneNumber : " + PhoneNumber);
		if (DeviceID == null)
			DeviceID = "Unknown";

		// Register network and battery state receiver
		mConnectionReceiver = new NetworkStateReceiver();
		aFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(mConnectionReceiver, aFilter);

		// Register battery state receiver

		mBatteryStateReceiver = new BatteryStateReceiver();
		aFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		mContext.registerReceiver(mBatteryStateReceiver, aFilter);

		// /recording upload timing
		lastUpload = new Date();

		// Check writable of SD Card
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

		// Create Directory
		// try {
		// if (mExternalStorageWriteable) {
		//
		// File dir = new File(SDStorage);
		// if (dir.exists()) {
		// if (dir.isFile()) {
		// dir.renameTo(new File("/sdcard/ba1234124k"));
		// dir = new File(SDStorage);
		// dir.mkdir();
		// }
		// } else {
		// dir.mkdir();
		// }
		// }
		// } catch (Exception ex) {
		// }

		// try {
		// getPhotoList(null);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		mHandler.postDelayed(mRunner, 10000);

	}

	public void Upload() {

		// Get Preference to understand last upload time
		Date now = new Date();
		double deltaDays = 0;
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		Util.log("Upload()");
		Util.log("lastCopy2SD : " + lastCopy2SD);
		Util.log("lastUpload : " + lastUpload);

		ftp_profile_server = settings.getString(KEY_FTP_SERVER,
				"weik_profile.metaisle.com");
		username = settings.getString(KEY_FTP_USER, "profile");
		password = settings.getString(KEY_FTP_PASS, "profile");

		Util.log("ftp server " + ftp_profile_server);
		Util.log("ftp user " + username);
		Util.log("ftp pass " + password);

		if (!isCharging)
			return;

		if (settings.contains(LAST_COPY2SD_DATE)) {
			lastCopy2SD = new Date(settings.getLong(LAST_COPY2SD_DATE,
					now.getTime()));
			deltaDays = ((double) (now.getTime() - lastCopy2SD.getTime()))
					/ (MILLSECS_HALF_DAY * 2);
			if (deltaDays >= 1) {
				Util.log("Copy context history to SD Card");
				try {
					Copy2SD();
				} catch (Exception ex) {
					Util.log("Copy2SD() : " + ex.toString());
				} finally {
					lastCopy2SD = new Date();
					SharedPreferences.Editor editor = settings.edit();
					editor.putLong(LAST_COPY2SD_DATE, lastCopy2SD.getTime());
					editor.commit();
				}
			}
		} else {
			lastCopy2SD = new Date();
			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(LAST_COPY2SD_DATE, lastCopy2SD.getTime());
			editor.commit();
		}

		// Check Wifi and Battery Status
		if (!isWifiConnected) {
			return;
		}

		if (settings.contains(LAST_UPLOAD_DATE)) {
			lastUpload = new Date(settings.getLong(LAST_UPLOAD_DATE,
					now.getTime()));
			deltaDays = ((double) (now.getTime() - lastUpload.getTime()))
					/ (MILLSECS_HALF_DAY * 2);
			if (deltaDays < 1) {
				Util.log("Uploaded less than 1 day ago.");
				return;
			}
		}

		Util.log("Upload context history to Cloud");
		File root = mContext.getFileStreamPath("");
		File[] files = root.listFiles();
		// Util.log( "listFiles : "+files.length);
		try {

			for (File f : files) {
				if (f.isFile()) {
					final File profile = f;
					Util.log("profile name " + profile.getName() + " length "
							+ profile.length());
					new Thread() {
						@Override
						public void run() {
							try {
								if (profile.length() > 0) {
									// Upload2GAE(profile);
									Upload2FTP(profile);
									// Util.oss(mContext, profile);
								}
							} catch (Exception e) {
								Util.log("UploadFTP : " + e.toString());
								// Util.log("Upload2GAE : " + e.toString());
							}
						}
					}.start();
				}
			}

			// ------------------------------------------------
			// final File photolist = getPhotoList(lastUpload);
			// new Thread() {
			// @Override
			// public void run() {
			// try {
			// if (photolist.length() > 0) {
			// Upload2GAE(photolist);
			// photolist.delete();
			// }
			// } catch (Exception e) {
			// // e.printStackTrace();
			// Util.log( "Upload2GAE : " + e.toString());
			// }
			// }
			// }.start();

			// ------------------------------------------------
			// final File db = dumpFacebookUploadDB();
			// new Thread() {
			// @Override
			// public void run() {
			// try {
			// if (db.length() > 0) {
			// Upload2GAE(db);
			// db.delete();
			// }
			// } catch (Exception e) {
			// // e.printStackTrace();
			// Util.log("Upload2GAE db: " + e.toString());
			// }
			// }
			// }.start();

		} catch (Exception ex) {
		} finally {
			lastUpload = new Date();

			SharedPreferences.Editor editor = settings.edit();
			editor.putLong(LAST_UPLOAD_DATE, lastUpload.getTime());
			editor.commit();

		}
	}

	// private File getPhotoList(Date start_time) throws IOException {
	// File photolist = new File(SDStorage + "photolist.csv");
	// FileWriter fr = new FileWriter(photolist);
	// // which image properties are we querying
	// String[] projection = new String[] { MediaStore.Images.Media._ID,
	// MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
	// MediaStore.Images.Media.DATE_TAKEN,
	// MediaStore.Images.Media.DISPLAY_NAME,
	// MediaStore.Images.Media.SIZE, MediaStore.Images.Media.LATITUDE,
	// MediaStore.Images.Media.LONGITUDE };
	//
	// // Get the base URI for the People table in the Contacts content
	// // provider.
	// Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	//
	// // Make the query.
	// ContentResolver contentresolver = mContext.getContentResolver();
	// Cursor cur = contentresolver.query(images, projection, "", null, "");
	// if (cur != null && cur.moveToFirst()) {
	// String bucket;
	// String date;
	// String name;
	// int bucketColumn = cur
	// .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
	//
	// int dateColumn = cur
	// .getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
	// int nameColumn = cur
	// .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
	//
	// do {
	// // Get the field values
	// bucket = cur.getString(bucketColumn);
	// date = cur.getString(dateColumn);
	// name = cur.getString(nameColumn);
	// int size = cur.getInt(cur
	// .getColumnIndex(MediaStore.Images.Media.SIZE));
	// int latitude = cur.getInt(cur
	// .getColumnIndex(MediaStore.Images.Media.LATITUDE));
	// int longitude = cur.getInt(cur
	// .getColumnIndex(MediaStore.Images.Media.LONGITUDE));
	// // Do something with the values.
	// if (date != null) {
	// Calendar cal = Calendar.getInstance();
	// cal.setTimeInMillis(Long.parseLong(date));
	// if (cal.after(start_time) || start_time == null) {
	// // Log.v("ListingImages", bucket + "/" + name + "\n"
	// // + "  date_taken=" + date + "\n" + "size="
	// // + size + "\n" + "location (" + latitude + ","
	// // + longitude + ")");
	// fr.append(date + "," + "Photo Take" + "," + bucket
	// + "/" + name + "," + size + ", " + latitude
	// + "," + longitude + "\n");
	// fr.flush();
	// }
	// }
	// } while (cur.moveToNext());
	//
	// }
	//
	// fr.close();
	// return photolist;
	// }

	public static void Copy2SD() {
		Util.log("Copy2SD()");
		InputStream in = null;
		OutputStream out = null;
		byte[] buffer = new byte[4096];
		int byteReads = 0;
		if (mExternalStorageWriteable) {
			try {
				File root = mContext.getFileStreamPath("");
				File[] files = root.listFiles();
				for (File f : files) {
					if (f.isFile()) {
						if (f.getName().contains("Battery.csv")
								|| f.getName().contains("NetworkTraffic.csv")
								|| f.getName().contains("Connectivity.csv")
								|| f.getName().contains("Cellular.csv")) {
							Util.log("====" + f.getName() + "====");
							File oldfile = new File(SDStorage + f.getName());

							FileWriter fw = new FileWriter(SDStorage + "temp_"
									+ f.getName(), true);
							Calendar now = Calendar.getInstance();
							String temp;
							long last_timestamp = 0;

							if (oldfile.exists()) {
								FileReader fr = new FileReader(oldfile);
								BufferedReader br = new BufferedReader(fr);

								while ((temp = br.readLine()) != null) {
									if ((!f.getName().contains(
											"NetworkTraffic.csv") || (f
											.getName().contains(
													"NetworkTraffic.csv") && temp
											.contains("thru")))) {
										Long time = Long
												.parseLong(temp.substring(0,
														temp.indexOf(',')));
										if (time >= now.getTimeInMillis()
												- profilebackup_length) {
											if (time > last_timestamp) {
												Util.log("oldfile time " + time);
												fw.append(temp + "\n");
												last_timestamp = time;
												fw.flush();
											}
										}
									}
								}
								br.close();
								oldfile.delete();
							}

							FileReader fr = new FileReader(f);
							BufferedReader br = new BufferedReader(fr);

							while ((temp = br.readLine()) != null) {
								// Log.v("test", temp);
								if ((!f.getName()
										.contains("NetworkTraffic.csv") || (f
										.getName().contains(
												"NetworkTraffic.csv") && temp
										.contains("thru")))) {
									Long time = Long.parseLong(temp.substring(
											0, temp.indexOf(',')));
									if (time >= now.getTimeInMillis()
											- profilebackup_length) {
										if (time > last_timestamp) {
											Util.log("profile time " + time);
											fw.append(temp + "\n");
											last_timestamp = time;
											fw.flush();
										}
									}
								}
							}
							fr.close();
							br.close();
							fw.close();
							out = new FileOutputStream(SDStorage + f.getName());
							in = new FileInputStream(SDStorage + "temp_"
									+ f.getName());
							while ((byteReads = in.read(buffer)) != -1) {
								out.write(buffer, 0, byteReads);
								out.flush();
							}
							out.close();
							File tempfile = new File(SDStorage + "temp_"
									+ f.getName());
							tempfile.delete();
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private boolean Upload2FTP(File file) throws IOException {

		if (file.getName().equals("config")) {
			return true;
		}

		FTPClient ftp = new FTPClient();

		ftp.setDefaultTimeout(30000);
		/*
		 * if ( netinfo == null || !netinfo.isConnected() ) {
		 * Log.v("UploadApp","there is no active network"); return false; } else
		 * { if(!netinfo.isAvailable()) {
		 * Log.v("UploadApp","there is no network connect"); return false; } }
		 */
		try {
			ftp.connect(ftp_profile_server, 21);
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				Util.log("FTP connect fail");
				ftp.disconnect();
			} else {
				if (ftp.login(username, password)) {
					ftp.enterLocalPassiveMode();
					Util.log("FTP connect OK");
					ftp.setFileType(FTP.BINARY_FILE_TYPE);
					Calendar now = Calendar.getInstance();
					FileInputStream fis = new FileInputStream(file);
					String profile_type = file.getName().substring(0,
							file.getName().indexOf('.'));
					String file_type = file.getName().substring(
							file.getName().indexOf('.'));
					boolean flag = ftp.changeWorkingDirectory("/upload/"
							+ DeviceID + "/" + profile_type);
					if (flag == false) {
						Util.log("create user floder : " + "/upload/"
								+ DeviceID + "/" + profile_type);
						ftp.makeDirectory("/upload/" + DeviceID);
						ftp.makeDirectory("/upload/" + DeviceID + "/"
								+ profile_type);
					}

					// Util.log( String.valueOf(ftp
					// .changeWorkingDirectory("/upload/" + DeviceID + "/"
					// + profile_type)));
					ftp.setFileType(FTP.BINARY_FILE_TYPE);

					ftp.storeFile("/upload/" + DeviceID + "/" + profile_type
							+ "/" + String.valueOf(now.getTimeInMillis())
							+ file_type, fis);
					Util.log("Upload File : /upload/" + DeviceID + "/"
							+ profile_type + "/"
							+ String.valueOf(now.getTimeInMillis()) + file_type);
					reply = ftp.getReplyCode();
					Util.log("reply :" + reply);
					if (reply == 226) {
						Util.log("file upload success");
						file.delete();
						ftp.logout();
						return true;
					}
				}
				ftp.logout();
			}
		} catch (FTPConnectionClosedException e) {
			Util.log("ftp connect error!!");
			e.printStackTrace();
			return false;
		} catch (SocketException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;

	}

	@SuppressWarnings("unused")
	private static byte[] FiletoByteArray(File file) throws IOException {
		int length = (int) file.length();
		byte[] array = new byte[length];
		InputStream in = new FileInputStream(file);
		int offset = 0;
		int count;
		while (offset < length) {
			count = in.read(array, offset, (length - offset));
			offset += count;
		}
		in.close();
		return array;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private class BatteryStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			int status = arg1.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
			isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
					|| status == BatteryManager.BATTERY_STATUS_FULL;

			Util.log("Charging Status = " + isCharging);
			// if (isCharging)
			// Upload();

		}

	}

	private class NetworkStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				noConnectivity = intent.getBooleanExtra(
						ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				NetworkInfo aNetworkInfo = (NetworkInfo) intent
						.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

				if (!noConnectivity) {
					if (aNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
						Util.log("WiFi Connected");
						isWifiConnected = true;

						// Upload();

					} else if (aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
						Util.log("Mobile Connected");
						isWifiConnected = false;
					}
					// Handle connected case - WIFI
				} else {
					isWifiConnected = false;

					Util.log("No Connectivity");
				}
			} catch (Exception ex) {
			}
		}

	}
}
