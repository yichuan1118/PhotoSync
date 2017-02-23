package com.metaisle.photosync.facebook;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.facebook.android.Facebook;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.util.Util;

public class MeTask extends AsyncTask<Void, Void, Void> {
	private Context mContext;
	private Facebook mFacebook;

	private String user_id;
	private String user_name;

	public MeTask(Context context, Facebook facebook) {
		mContext = context;
		mFacebook = facebook;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String response;
		try {
			response = mFacebook.request("me");
			JSONObject resp = new JSONObject(response);
			user_id = resp.getString("id");
			user_name = resp.getString("name");
		} catch (Exception e) {
			if (mContext instanceof Activity) {
				((Activity) mContext).runOnUiThread(new FacebookErrorToast(
						mContext));
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		SharedPreferences prefs = mContext.getSharedPreferences(
				Prefs.PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit().putString(Prefs.KEY_USER_ID, user_id)
				.putString(Prefs.KEY_USER_NAME, user_name).commit();
		Util.log("username " + user_name + " userid " + user_id);

		new AllAlbumsTask(mContext, mFacebook, null).execute();
		new HomeFeedTask(mContext, mFacebook, null, null, null).execute();
	}

}
