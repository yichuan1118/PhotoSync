package com.metaisle.photosync.facebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.PhotoTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.util.Util;

public class SingleAlbumPhotosTask extends AsyncTask<Void, Void, Void> {
	private Context mContext;
	private String mAlbumID;

	private final SimpleDateFormat mDateParser = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss+SSSS");
	private Calendar mCal = Calendar.getInstance();

	String mAfter64 = null;
	private Handler mHandler;
	private PullToRefreshBase mPtr;

	public SingleAlbumPhotosTask(Context context, String album_id,
			PullToRefreshBase ptr) {
		mContext = context;
		mAlbumID = album_id;
		mPtr = ptr;
	}

	public SingleAlbumPhotosTask(Context context, String album_id,
			String after64, Handler handler) {
		this(context, album_id, null);

		mAfter64 = after64;
		mHandler = handler;
	}

	@Override
	protected Void doInBackground(Void... voids) {

		String response;
		try {
			response = Prefs.facebook.request("" + mAlbumID);
			JSONObject album = new JSONObject(response);
			Date created = mDateParser.parse(album.getString("created_time"));
			Date updated = mDateParser.parse(album.getString("updated_time"));

			ContentValues values = new ContentValues();
			values.put(AlbumTable.ALBUM_ID, album.getLong("id"));
			values.put(AlbumTable.FROM_ID,
					album.getJSONObject("from").getLong("id"));
			values.put(AlbumTable.FROM_NAME, album.getJSONObject("from")
					.getString("name"));
			values.put(AlbumTable.ALBUM_NAME, album.getString("name"));
			values.put(AlbumTable.COVER_ID, album.optLong("cover_photo"));
			values.put(AlbumTable.PRIVACY, album.getString("privacy"));
			if (!album.isNull("count")) {
				values.put(AlbumTable.COUNT, album.getInt("count"));
			}
			values.put(AlbumTable.TYPE, album.getString("type"));
			values.put(AlbumTable.CREATED_TIME, created.getTime());
			values.put(AlbumTable.UPDATED_TIME, updated.getTime());
			values.put(AlbumTable.CAN_UPLOAD, album.getBoolean("can_upload"));

			mContext.getContentResolver().insert(Provider.ALBUM_CONTENT_URI,
					values);

			// ------------------------------------------------------------

			if (mAfter64 == null) {
				// Clear DBs.
				mContext.getContentResolver().delete(
						Provider.PHOTO_CONTENT_URI, PhotoTable.ALBUM_ID + "=?",
						new String[] { mAlbumID });
				response = Prefs.facebook.request("" + mAlbumID + "/photos");
			} else {
				Util.log("after " + mAfter64);
				Bundle b = new Bundle();
				b.putString("limit", "25");
				b.putString("after", mAfter64);
				response = Prefs.facebook.request("" + mAlbumID + "/photos", b);
			}

			JSONObject resp = new JSONObject(response);
			JSONArray photos = resp.getJSONArray("data");

			// Util.log("album " + album_id + ": " + photos.length());

			for (int j = 0; j < photos.length(); j++) {
				JSONObject photo = photos.getJSONObject(j);

				Date created_photo = mDateParser.parse(photo
						.getString("created_time"));
				Date updated_photo = mDateParser.parse(photo
						.getString("updated_time"));

				ContentValues values_photo = new ContentValues();

				values_photo.put(PhotoTable.PHOTO_ID, photo.getLong("id"));
				values_photo.put(PhotoTable.FROM_ID, photo
						.getJSONObject("from").getLong("id"));
				values_photo
						.put(PhotoTable.PHOTO_NAME, photo.optString("name"));
				values_photo
						.put(PhotoTable.PICTURE, photo.getString("picture"));
				values_photo.put(PhotoTable.SOURCE, photo.getString("source"));
				values_photo.put(PhotoTable.HEIGHT, photo.getInt("height"));
				values_photo.put(PhotoTable.WIDTH, photo.getInt("width"));

				mCal.setTime(created_photo);
				values_photo.put(PhotoTable.CREATED_TIME,
						created_photo.getTime());
				values_photo.put(PhotoTable.CREATED_YEAR,
						mCal.get(Calendar.YEAR));
				values_photo.put(PhotoTable.CREATED_MONTH,
						mCal.get(Calendar.MONTH));
				values_photo.put(PhotoTable.CREATED_DAY,
						mCal.get(Calendar.DAY_OF_MONTH));

				values_photo.put(PhotoTable.POSITION, photo.optInt("position"));
				

				mCal.setTime(updated_photo);
				values_photo.put(PhotoTable.UPDATED_TIME,
						updated_photo.getTime());
				values_photo.put(PhotoTable.UPDATED_YEAR,
						mCal.get(Calendar.YEAR));
				values_photo.put(PhotoTable.UPDATED_MONTH,
						mCal.get(Calendar.MONTH));
				values_photo.put(PhotoTable.UPDATED_DAY,
						mCal.get(Calendar.DAY_OF_MONTH));

				values_photo.put(PhotoTable.ALBUM_ID, mAlbumID);

				mContext.getContentResolver().insert(
						Provider.PHOTO_CONTENT_URI, values_photo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (mHandler != null)
			mHandler.sendEmptyMessage(0);
		if (mPtr != null)
			mPtr.onRefreshComplete();
		super.onPostExecute(result);
	}
}
