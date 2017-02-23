package com.metaisle.photosync.facebook;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.metaisle.photosync.data.Prefs;

public class DeleteTask extends AsyncTask<String, Void, Void> {
	private Context mContext;
	private String mAlbumID;

	public DeleteTask(Context context, String album_id) {
		mContext = context;
		mAlbumID = album_id;
	}

	@Override
	protected Void doInBackground(String... params) {
		for (String id : params) {
			try {
				Bundle b = new Bundle();
				String response = Prefs.facebook.request(String.valueOf(id), b,
						"DELETE");

				if (!response.equals("true")) {
					throw new IllegalStateException("return false");
				}
			} catch (Exception e) {
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mContext, "Error while delete.",
								Toast.LENGTH_LONG).show();
					}
				});
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		new SingleAlbumPhotosTask(mContext, String.valueOf(mAlbumID), null)
				.execute();
		Toast.makeText(mContext, "Photos deleted", Toast.LENGTH_LONG).show();
	}
}
