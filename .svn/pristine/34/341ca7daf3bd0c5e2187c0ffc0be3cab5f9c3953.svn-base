package com.metaisle.photosync.facebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;

import com.facebook.android.Facebook;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.PhotoTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.util.Util;

public class AllAlbumsTask extends AsyncTask<Void, Void, Void> {
	private Context mContext;
	private Facebook facebook;
	private PullToRefreshListView mPtrListView;

	private final SimpleDateFormat mDateParser = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss+SSSS");
	private Calendar mCal = Calendar.getInstance();

	public AllAlbumsTask(Context context, Facebook facebook,
			PullToRefreshListView ptr) {
		mContext = context;
		this.facebook = facebook;
		mPtrListView = ptr;
		// Util.log("mPtrListView " + mPtrListView);
	}

	@Override
	protected Void doInBackground(Void... p) {
		try {
			String response = facebook.request("me/albums");
			JSONObject resp = new JSONObject(response);
			JSONArray albums = resp.getJSONArray("data");

			Util.log("me/albums: " + albums.length());

			// Clear DBs.
			mContext.getContentResolver().delete(Provider.ALBUM_CONTENT_URI,
					null, null);
			mContext.getContentResolver().delete(Provider.PHOTO_CONTENT_URI,
					null, null);

			long private_album_id = -1;
			for (int i = 0; i < albums.length(); i++) {
				JSONObject album = albums.getJSONObject(i);

				Date created = mDateParser.parse(album
						.getString("created_time"));
				Date updated = mDateParser.parse(album
						.getString("updated_time"));

				ContentValues values = new ContentValues();
				values.put(AlbumTable.ALBUM_ID, album.getLong("id"));
				values.put(AlbumTable.FROM_ID, album.getJSONObject("from")
						.getLong("id"));
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
				values.put(AlbumTable.CAN_UPLOAD,
						album.getBoolean("can_upload"));

				mContext.getContentResolver().insert(
						Provider.ALBUM_CONTENT_URI, values);

				// ------------------------------------------------------------
				// Get photos for this album

				String response_photos = facebook.request(""
						+ album.getLong("id") + "/photos");
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
							photo.optInt("position"));

					mCal.setTime(updated_photo);
					values_photo.put(PhotoTable.UPDATED_TIME,
							updated_photo.getTime());
					values_photo.put(PhotoTable.UPDATED_YEAR,
							mCal.get(Calendar.YEAR));
					values_photo.put(PhotoTable.UPDATED_MONTH,
							mCal.get(Calendar.MONTH));
					values_photo.put(PhotoTable.UPDATED_DAY,
							mCal.get(Calendar.DAY_OF_MONTH));

					values_photo.put(PhotoTable.ALBUM_ID, album.getLong("id"));

					mContext.getContentResolver().insert(
							Provider.PHOTO_CONTENT_URI, values_photo);

				}

				//
				// ------------------------------------------------------------

				if (album.getString("name").equals(
						Prefs.PHOTOSYNC_PRIVATE_ALBUM_NAME)
						&& album.getBoolean("can_upload")) {
					SharedPreferences mPrefs = mContext.getSharedPreferences(
							Prefs.PREFS_NAME, Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = mPrefs.edit();
					editor.putLong(Prefs.KEY_PRIVATE_ALBUM_ID,
							album.getLong("id"));
					editor.commit();
					private_album_id = album.getLong("id");
				}

			}

			if (private_album_id == -1) {
				Bundle params = new Bundle();
				params.putString("name", Prefs.PHOTOSYNC_PRIVATE_ALBUM_NAME);
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("value",
							Prefs.PHOTOSYNC_PRIVATE_ALBUM_PRIVACY);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				params.putString("privacy", jsonObject.toString());
				response = facebook.request("me/albums", params, "POST");
				JSONObject r = new JSONObject(response);
				String id = r.getString("id");
				Util.log("Create private album " + id);
				SharedPreferences mPrefs = mContext.getSharedPreferences(
						Prefs.PREFS_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = mPrefs.edit();
				editor.putLong(Prefs.KEY_PRIVATE_ALBUM_ID, Long.parseLong(id));
				editor.commit();
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
