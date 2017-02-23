package com.metaisle.photosync.facebook;

import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.metaisle.photosync.data.Prefs;
import com.metaisle.util.Util;

public class ShareTask extends AsyncTask<String, Void, Void> {

	private long mAlbumID;
	private Context mContext;

	private String[] mMessages = null;

	// public ShareTask(long album_id, Context context) {
	// mAlbumID = album_id;
	// mContext = context;
	// }

	public ShareTask(long album_id, Context context, String[] messages) {
		mAlbumID = album_id;
		mContext = context;
		mMessages = messages;
	}

	@Override
	protected Void doInBackground(String... urls) {
		if (urls.length == 0) {
			Util.log("Nothing to Share?");
			return null;
		}
		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];

			String msg = null;
			if (mMessages != null && mMessages.length > i) {
				msg = mMessages[i];
			}

			Util.log("url " + url + Prefs.facebook.isSessionValid());
			Bundle params = new Bundle();
			params.putString("url", url);
			if (msg != null) {
				params.putString("message", msg);
			}
			try {
				String resp = Prefs.facebook.request(mAlbumID + "/photos",
						params, "POST");
				Util.log(resp);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
		Toast.makeText(mContext, "Photos shared", Toast.LENGTH_LONG).show();
	}

}