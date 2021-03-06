package com.metaisle.photosync.service;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.photosync.data.UploadTable;
import com.metaisle.photosync.facebook.UploadTask;
import com.metaisle.photosync.receiver.CameraMonitor;
import com.metaisle.photosync.util.JSONSharedPreferences;
import com.metaisle.util.Util;

public class MediaTrackingService extends Service {

	private static final String KEY_LAST_MEDIA_IDS = "key_last_media_ids";
	private JSONArray mLastMediaIDs = null;
	private static final String KEY_LAST_UPLOADED_MEDIA_IDS = "key_last_uploaded_media_ids";
	private JSONArray mLastUploadedMediaID = null;

	private ContentResolver resolver;
	private SharedPreferences mPrefs;

	private static final String[] PROJECTION_MAX_ID = { "MAX(_id)" };

	@Override
	public void onCreate() {
		super.onCreate();
		resolver = getContentResolver();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		try {
			mLastMediaIDs = JSONSharedPreferences.loadJSONArray(this,
					Prefs.PREFS_NAME, KEY_LAST_MEDIA_IDS);
			if (mLastMediaIDs.length() == 0) {
				updateLastID();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// First time tracking, check if we want to upload existing photos.
		// TODO: Dialog ask for upload existing photos?
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Util.log("onStartCommand");
		Util.log("mLastMediaIDs " + mLastMediaIDs);
		try {
			for (int i = 0; i < CameraMonitor.MEDIA_STORE_URIS.length; i++) {
				Uri uri = CameraMonitor.MEDIA_STORE_URIS[i];
				String last_id = String.valueOf(mLastMediaIDs.getLong(i));
				insertUpload(resolver, last_id, uri);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return START_STICKY;
	}

	public void insertUpload(ContentResolver resolver, String last_id, Uri uri) {
		Util.log("last_id " + last_id + " uri " + uri);
		Cursor cursor = resolver.query(uri, null,
				"_id > ? AND _data LIKE '%/DCIM/%'", new String[] { last_id },
				"_id");

		if (cursor == null || cursor.getCount() == 0) {
			// Util.log("No new photo found: empty or null cursor");
			return;
		} else {
			Util.log("New photo rows " + cursor.getCount());
		}

		cursor.moveToFirst();
		do {
			int _idColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID);

			long _id = cursor.getLong(_idColumn);

			int dataColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA);
			String filePath = cursor.getString(dataColumn);

			int mimeTypeColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.MIME_TYPE);
			String mimeType = cursor.getString(mimeTypeColumn);

			int oriColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
			int orientation = cursor.getInt(oriColumn);

			int latColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LATITUDE);
			double latitude = cursor.getDouble(latColumn);

			int lonColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.LONGITUDE);
			double longitude = cursor.getDouble(lonColumn);

			int dateColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_TAKEN);
			long date_taken = cursor.getLong(dateColumn);

			int sizeColumn = cursor
					.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.SIZE);
			long size = cursor.getLong(sizeColumn);

			ContentValues values = new ContentValues();
			values.put(UploadTable.MEDIASTORE_ID, _id);
			values.put(UploadTable.PATH, filePath);
			values.put(UploadTable.MIME_TYPE, mimeType);
			values.put(UploadTable.LATITUDE, latitude);
			values.put(UploadTable.LONGITUDE, longitude);
			values.put(UploadTable.DATE_TAKEN, date_taken);
			values.put(UploadTable.ORIENTATION, orientation);
			values.put(UploadTable.SIZE, size);
			values.put(UploadTable.RETRIED, 0);

			resolver.insert(Provider.UPLOAD_CONTENT_URI, values);

			Util.profile(this, "Photos.csv", "New photo, " + filePath + ", "
					+ size + ", " + mimeType + ", " + latitude + ", "
					+ longitude + ", " + date_taken);

		} while (cursor.moveToNext());
		cursor.close();

		updateLastID();

		Util.log(" has wifi_only "
				+ mPrefs.contains(Prefs.KEY_AUTO_UPLOAD_WIFI_ONLY));
		boolean wifi_only = mPrefs.getBoolean(Prefs.KEY_AUTO_UPLOAD_WIFI_ONLY,
				false);
		Util.log("wifi_only " + wifi_only);
		if (wifi_only) {
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			boolean isWifiConn = networkInfo.iscontextConnected();
			if (isWifiConn) {
				new UploadTask(getApplicationContex(t),false).execute();
			}
		} else {
			new UploadTask(getApplicationContext(),false).execute();
		}
	}

	private void updateLastID() {
		try {
			for (int i = 0; i < CameraMonitor.MEDIA_STORE_URIS.length; i++) {
				Cursro c = resolver.query(CameraMonitor.MEDIA_STORE_URIS[i],
						PROJECTION_MAX_ID, null, null, null);
				if (c != null) {
					c.moveToFirst();
					mLastMediaIDs.put(i, c.getLong(0));
					c.close();
				} else {
					mLastMediaIDs.put(i, -1);
				}
			}

			mLastUploadedMediaID = new JSONArray(mLastMediaIDs.toString());

			JSONSharedPreferences.saveJSONArray(this, Prefs.PREFS_NAME,
					KEY_LAST_MEDIA_IDS, mLastMediaIDs);
			JSONSharedPreferences.saveJSONArray(this, Prefs.PREFS_NAME,
					KEY_LAST_UPLOADED_MEDIA_IDS, mLastUploadedMediaID);

			Util.log("mLastMediaIDs " + mLastMediaIDs);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
