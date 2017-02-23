package com.metaisle.photosync.facebook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Time;
import au.com.bytecode.opencsv.CSVReader;

import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.photosync.data.UploadTable;
import com.metaisle.photosync.util.Utility;
import com.metaisle.util.Util;

public class UploadTask extends AsyncTask<Void, Void, Void> {
	private static final int MAX_RETRIES = 3;
	private Context mContext;
	private ContentResolver resolver;
	private long mPrivateAlbumID = -1;

	private SharedPreferences default_prefs;
	SharedPreferences mPrefs;
	private boolean mUserRequest;

	public static boolean running = false;

	public UploadTask(Context context, boolean userRequset) {
		mContext = context;
		mPrivateAlbumID = mContext.getSharedPreferences(Prefs.PREFS_NAME,
				Context.MODE_PRIVATE).getLong(Prefs.KEY_PRIVATE_ALBUM_ID, -1);

		default_prefs = PreferenceManager.getDefaultSharedPreferences(mContext);

		mPrefs = mContext.getSharedPreferences(Prefs.PREFS_NAME,
				Context.MODE_PRIVATE);
		mUserRequest = userRequset;
	}

	public boolean lookup(long start_time) {
		String sdcard = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String file = "lookup.csv";

		try {

			if (!new File(sdcard + File.separator + file).exists()) {
				return true;
			}

			CSVReader reader = new CSVReader(new FileReader(sdcard
					+ File.separator + file));
			List<String[]> table = reader.readAll();
			reader.close();

			int num_timeslot = table.size();
			long milli_per_timeslot = 24 * 60 * 60 * 1000L / num_timeslot;

			default_prefs.edit()
					.putLong(Prefs.KEY_MILLI_PER_TIMESLOT, milli_per_timeslot)
					.commit();

			Calendar c = Calendar.getInstance();
			long now = c.getTimeInMillis();
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			long midnight = c.getTimeInMillis();
			int start_timeslot = (int) ((midnight - start_time) / milli_per_timeslot);
			int now_timeslot = (int) ((midnight - now) / milli_per_timeslot);

			Util.log("start_timeslot " + start_timeslot
					+ " (midnight - start_time) " + (midnight - start_time));
			Util.log("now_timeslot " + now_timeslot + " (midnight - now) "
					+ (midnight - now));

			double threshold = Double
					.parseDouble(table.get(start_timeslot)[now_timeslot]);

			float last_thru;
			if (Util.isOnWifi(mContext)) {
				last_thru = default_prefs.getFloat("LAST_WIFI_THRU",
						Float.MAX_VALUE);
			} else {
				last_thru = default_prefs.getFloat("LAST_MOBILE_THRU",
						Float.MAX_VALUE);
			}

			if (last_thru > threshold) {
				return true;
			} else {
				return false;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	@Override
	protected Void doInBackground(Void... p) {

		if (!Util.isOnline(mContext)) {
			return null;
		}

		boolean wifi_only = mPrefs.getBoolean(Prefs.KEY_AUTO_UPLOAD_WIFI_ONLY,
				false);
		if (wifi_only && !Util.isOnWifi(mContext)) {
			return null;
		}

		resolver = mContext.getContentResolver();
		Cursor cursor = resolver.query(Provider.UPLOAD_CONTENT_URI, null,
				UploadTable.FINISH_TIME + " is null AND " + UploadTable.RETRIED
						+ "<" + MAX_RETRIES, null, UploadTable.DATE_TAKEN);

		if (cursor == null || cursor.getCount() == 0
				|| !Prefs.facebook.isSessionValid() || mPrivateAlbumID == -1) {
			return null;
		}

		//
		if (running) {
			return null;
		}
		running = true;

		while (cursor.moveToNext()) {

			String path = cursor.getString(cursor
					.getColumnIndex(UploadTable.PATH));
			int orientation = cursor.getInt(cursor
					.getColumnIndex(UploadTable.ORIENTATION));
			String mime_type = cursor.getString(cursor
					.getColumnIndex(UploadTable.MIME_TYPE));
			int mediastore_id = cursor.getInt(cursor
					.getColumnIndex(UploadTable.MEDIASTORE_ID));

			double latitude = cursor.getLong(cursor
					.getColumnIndex(UploadTable.LATITUDE));
			double longitude = cursor.getLong(cursor
					.getColumnIndex(UploadTable.LONGITUDE));
			long date_taken = cursor.getLong(cursor
					.getColumnIndex(UploadTable.DATE_TAKEN));
			long size = cursor.getLong(cursor.getColumnIndex(UploadTable.SIZE));
			long start = cursor.getLong(cursor
					.getColumnIndex(UploadTable.START_TIME));
			long finish = cursor.getLong(cursor
					.getColumnIndex(UploadTable.FINISH_TIME));

			if (!mUserRequest && !lookup(date_taken)) {
				continue;
			}

			ContentValues values = new ContentValues();
			values.put(UploadTable.START_TIME, System.currentTimeMillis());

			Util.log("Uri with path "
					+ Uri.withAppendedPath(Provider.UPLOAD_CONTENT_URI, ""
							+ mediastore_id));
			resolver.update(
					Uri.withAppendedPath(Provider.UPLOAD_CONTENT_URI, ""
							+ mediastore_id), values, null, null);

			Bundle params = new Bundle();
			byte[] photo;
			try {
				File f = new File(path);
				Util.log(path + " " + f.exists());
				if (!f.exists()) {
					values = new ContentValues();
					values.put(UploadTable.FINISH_TIME, -1);
					resolver.update(Uri.withAppendedPath(
							Provider.UPLOAD_CONTENT_URI, "" + mediastore_id),
							values, null, null);
					running = false;
					return null;
				}
				photo = Utility.scaleImage(mContext, path, orientation,
						mime_type);

				if (photo == null) {
					values = new ContentValues();
					values.put(UploadTable.FINISH_TIME, -1);
					resolver.update(Uri.withAppendedPath(
							Provider.UPLOAD_CONTENT_URI, "" + mediastore_id),
							values, null, null);
					running = false;
					return null;
				}

				params.putByteArray("source", photo);

				// ------------------------------------------------------------
				String response = Prefs.facebook.request(mPrivateAlbumID
						+ "/photos", params, "POST");

				Util.log("upload " + response);
				// ------------------------------------------------------------

				Intent photo_upload = new Intent();
				photo_upload.setAction("photo_upload_start");
				photo_upload.putExtra("isuploading", true);
				photo_upload.putExtra("time", System.currentTimeMillis());
				mContext.sendBroadcast(photo_upload);

				values = new ContentValues();
				if (!response.equals("false")) {

					photo_upload.putExtra("isuploading", false);
					photo_upload.putExtra("photosize", photo.length);
					photo_upload.putExtra("time", System.currentTimeMillis());
					mContext.sendBroadcast(photo_upload);

					values.put(UploadTable.FINISH_TIME,
							System.currentTimeMillis());
					resolver.update(Uri.withAppendedPath(
							Provider.UPLOAD_CONTENT_URI, "" + mediastore_id),
							values, null, null);
				} else {
					// int retried = cursor.getInt(cursor
					// .getColumnIndex(UploadTable.RETRIED));
					// values.put(UploadTable.RETRIED, retried + 1);
					// resolver.update(Uri.withAppendedPath(
					// Provider.UPLOAD_CONTENT_URI, "" + mediastore_id),
					// values, null, null);

					values = new ContentValues();
					values.putNull(UploadTable.START_TIME);
					resolver.update(Uri.withAppendedPath(
							Provider.UPLOAD_CONTENT_URI, "" + mediastore_id),
							values, null, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
				values = new ContentValues();
				values.putNull(UploadTable.START_TIME);
				resolver.update(
						Uri.withAppendedPath(Provider.UPLOAD_CONTENT_URI, ""
								+ mediastore_id), values, null, null);
			}

			Util.profile(mContext, "Photos.csv", "Upload photo, " + path + ", "
					+ size + ", " + mime_type + ", " + latitude + ", "
					+ longitude + ", " + date_taken + ", " + start + ", "
					+ finish);
			
			cursor.close();
			cursor = resolver.query(Provider.UPLOAD_CONTENT_URI, null,
					UploadTable.FINISH_TIME + " is null AND " + UploadTable.RETRIED
							+ "<" + MAX_RETRIES, null, UploadTable.DATE_TAKEN);
		}
		running = false;
		return null;
	}

}
