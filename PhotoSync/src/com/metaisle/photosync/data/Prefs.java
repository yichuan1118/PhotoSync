package com.metaisle.photosync.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.android.Facebook;

public class Prefs {
	public static SharedPreferences mPrefs;

	public static final boolean DEBUG = true;

	public static final String PREFS_NAME = "photosync.prefs";

	public static final String KEY_ACCESS_TOKEN = "access_token";
	public static final String KEY_EXPIRES_IN = "expires_in";
	
	public static final String KEY_LAST_REFRESH_TIME = "last_refresh_time";
	public static final String KEY_PRIVATE_ALBUM_ID = "private_album_id";

	public static final String KEY_USER_ID = "key_user_id";
	public static final String KEY_USER_NAME = "key_user_name";

	public static final String KEY_AUTO_UPLOAD_WIFI_ONLY = "auto_upload_wifi_only";
	public static final String KEY_ENABLE_TIMELINE = "key_enable_timeline";
	
	public static final String KEY_ENABLE_PROFILING = "key_enable_profiling";

	public static final String KEY_INVALIDATE = "key_invalidate";

	public static final String KEY_MILLI_PER_TIMESLOT = "KEY_MILLI_PER_TIMESLOT";
	public static final String KEY_LAST_UPLOAD_TIME = "KEY_LAST_UPLOAD_TIME";

	public static final String PHOTOSYNC_PRIVATE_ALBUM_NAME = "PhotoSync_PRIVATE";
	public static final String PHOTOSYNC_PRIVATE_ALBUM_PRIVACY = "SELF";

	//public static Facebook facebook = new Facebook("148927718573007");
	
	public static Facebook facebook = new Facebook("420229424658379");
	public static final String BROADCAST_FACEBOOK_UPLOAD_STARTED = "broadcast_facebook_upload_started";
	public static final String BROADCAST_FACEBOOK_UPLOAD_FINISHED = "broadcast_facebook_upload_finished";

	public static final int conf_version = 1; // configure file version

	public static final int MAX_DB_RECORDS = 1000;

	public static final int NEW_ALBUM_TASK_HANDLER_DONE = 0x90;
	public static final int NEW_ALBUM_TASK_HANDLER_ERROR = 0x91;

	public static SharedPreferences get(Context context) {
		if (mPrefs == null)
			mPrefs = context.getSharedPreferences(PREFS_NAME,
					Context.MODE_PRIVATE);
		return mPrefs;
	}
}