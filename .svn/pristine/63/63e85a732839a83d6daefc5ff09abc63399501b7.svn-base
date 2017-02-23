package com.metaisle.photosync.facebook;

import java.text.SimpleDateFormat;
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

import com.facebook.android.Facebook;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.util.Util;

public class AlbumListTask extends AsyncTask<Void, Void, Void> {
	private Context mContext;
	private Facebook facebook;
	private PullToRefreshListView mPtrListView;

	private final SimpleDateFormat mDateParser = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss+SSSS");

	public AlbumListTask(Context context, PullToRefreshListView ptr) {
		mContext = context;
		this.facebook = Prefs.facebook;
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
		if (mPtrListView != null) {
			mPtrListView.onRefreshComplete();
		}
		super.onPostExecute(result);
	}
}
