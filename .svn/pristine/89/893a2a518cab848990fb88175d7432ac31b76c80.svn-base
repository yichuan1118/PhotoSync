package com.metaisle.photosync.facebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.format.Time;

import com.facebook.android.Facebook;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.PhotoTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.util.Util;

public class AllPhotosTask extends AsyncTask<Void, Void, Void> {
	private Context mContext;
	private Facebook facebook;
	private PullToRefreshListView mPtrListView;

	private final SimpleDateFormat mDateParser = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss+SSSS");
	private Calendar mCal = Calendar.getInstance();

	public AllPhotosTask(Context context, PullToRefreshListView ptr) {
		mContext = context;
		this.facebook = Prefs.facebook;
		mPtrListView = ptr;
	}

	@Override
	protected Void doInBackground(Void... p) {
		try {

			Cursor cursor = mContext.getContentResolver().query(
					Provider.ALBUM_CONTENT_URI,
					new String[] { AlbumTable.ALBUM_ID },
					null, null, null);

			if (cursor == null || cursor.getCount() == 0) {
				return null;
			}

			// Clear DBs.
			mContext.getContentResolver().delete(Provider.PHOTO_CONTENT_URI,
					null, null);

			while (cursor.moveToNext()) {

				long album_id = cursor.getLong(cursor
						.getColumnIndex(AlbumTable.ALBUM_ID));

				// ------------------------------------------------------------
				// Get photos for this album

				String response_photos = facebook.request("" + album_id
						+ "/photos");
				JSONObject resp_photos = new JSONObject(response_photos);

				// Util.log("response_photos " + response_photos);
				JSONArray photos = resp_photos.getJSONArray("data");

				for (int j = 0; j < photos.length(); j++) {
					JSONObject photo = photos.getJSONObject(j);

					Date created_photo = mDateParser.parse(photo
							.getString("created_time"));
					Date updated_photo = mDateParser.parse(photo
							.getString("updated_time"));

					ContentValues values_photo = new ContentValues();

					values_photo.put(PhotoTable.PHOTO_ID, photo.getLong("id"));
					values_photo.put(PhotoTable.FROM_ID,
							photo.getJSONObject("from").getLong("id"));
					values_photo.put(PhotoTable.PHOTO_NAME,
							photo.optString("name"));
					values_photo.put(PhotoTable.PICTURE,
							photo.getString("picture"));
					values_photo.put(PhotoTable.SOURCE,
							photo.getString("source"));
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

					values_photo.put(PhotoTable.POSITION,
							photo.getInt("position"));

					mCal.setTime(updated_photo);
					values_photo.put(PhotoTable.UPDATED_TIME,
							updated_photo.getTime());
					values_photo.put(PhotoTable.UPDATED_YEAR,
							mCal.get(Calendar.YEAR));
					values_photo.put(PhotoTable.UPDATED_MONTH,
							mCal.get(Calendar.MONTH));
					values_photo.put(PhotoTable.UPDATED_DAY,
							mCal.get(Calendar.DAY_OF_MONTH));

					values_photo.put(PhotoTable.ALBUM_ID, album_id);

					mContext.getContentResolver().insert(
							Provider.PHOTO_CONTENT_URI, values_photo);

				}

				//
				// ------------------------------------------------------------
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (mContext instanceof Activity) {
				((Activity) mContext).runOnUiThread(new FacebookErrorToast(
						mContext));
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		SharedPreferences prefs = mContext.getSharedPreferences(
				Prefs.PREFS_NAME, Context.MODE_PRIVATE);
		Time now = new Time();
		now.setToNow();
		prefs.edit().putLong(Prefs.KEY_LAST_REFRESH_TIME, now.toMillis(false))
				.commit();
		Util.log("last refresh " + now);

		if (mPtrListView != null) {
			mPtrListView.onRefreshComplete();
		}
		super.onPostExecute(result);
	}
}
