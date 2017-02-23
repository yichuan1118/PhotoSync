package com.metaisle.photosync.facebook;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.android.Facebook;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.util.Util;

public class NewAlbumTask extends AsyncTask<Void, Void, String> {
	private Context mContext;
	private String mName;
	private String mPrivacy;

	private Handler mHandler = null;

	private Facebook facebook;

	private final SimpleDateFormat mDateParser = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss+SSSS");

	public NewAlbumTask(Context context, String name, String privacy) {
		mContext = context;
		mName = name;
		mPrivacy = privacy;
	}

	public NewAlbumTask(Context context, String name, String privacy,
			Handler handler) {
		this(context, name, privacy);
		mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		facebook = Prefs.facebook;
		Util.log("facebook " + facebook.isSessionValid());
	}

	@Override
	protected String doInBackground(Void... voids) {

		Bundle params = new Bundle();
		params.putString("name", mName);

		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("value", mPrivacy);
			params.putString("privacy", jsonObject.toString());

			Util.log("name " + mName + " mPrivacy " + mPrivacy);
			String response = facebook.request("me/albums", params, "POST");
			JSONObject r = new JSONObject(response);
			String id = r.getString("id");

			Util.log("create album returns " + response);

			response = facebook.request(id);
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
			return id;
		} catch (Exception e) {
			if (mHandler != null)
				mHandler.sendEmptyMessage(Prefs.NEW_ALBUM_TASK_HANDLER_ERROR);
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if (mHandler != null)
			mHandler.sendEmptyMessage(Prefs.NEW_ALBUM_TASK_HANDLER_DONE);
		super.onPostExecute(result);
	}
}
