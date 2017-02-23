package com.metaisle.photosync.facebook;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ListView;

import com.facebook.android.Facebook;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.metaisle.photosync.data.PostTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.util.Util;

public class HomeFeedTask extends AsyncTask<Void, Void, Void> {
	private Context mContext;
	private Facebook facebook;
	// 2011-11-22T20:55:58+0000
	private SimpleDateFormat parser = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss+SSSS");
	private Calendar mCal = Calendar.getInstance();

	private String since = null;
	private String until = null;

	private PullToRefreshListView mPtrListView;
	private ListView mListView = null;
	private View mFooter;
	private boolean empty;

	public HomeFeedTask(Context context, Facebook facebook,
			PullToRefreshListView ptr, String since, String until) {
		mContext = context;
		this.facebook = facebook;
		this.since = since;
		this.until = until;
		mPtrListView = ptr;
	}

	public HomeFeedTask(Context context, Facebook facebook,
			PullToRefreshListView ptr, String since, String until, ListView lv,
			View footer) {
		mContext = context;
		this.facebook = facebook;
		this.since = since;
		this.until = until;
		mPtrListView = ptr;
		mListView = lv;
		mFooter = footer;
	}

	@Override
	protected Void doInBackground(Void... p) {
		if (!PreferenceManager.getDefaultSharedPreferences(mContext)
				.getBoolean(Prefs.KEY_ENABLE_TIMELINE, false)) {
			return null;
		}
		String graph = "me/home";
		Bundle params = new Bundle();
		params.putString("filter", "app_2305272732");
		if (since != null) {
			params.putString("since", since);
		} else if (until != null) {
			params.putString("until", until);
		}

		try {
			String response = facebook.request(graph, params);
			JSONObject resp = new JSONObject(response);
			JSONArray feeds = resp.getJSONArray("data");

			Util.log("Number of posts: " + feeds.length());
			if (feeds.length() == 0) {
				empty = true;
			} else {
				empty = false;
			}
			if (!empty && since == null && until == null) {
				JSONObject last_post = feeds.getJSONObject(feeds.length() - 1);
				String last_id = last_post.getString("id");
				Cursor c = mContext.getContentResolver().query(
						Provider.FEED_CONTENT_URI,
						new String[] { PostTable.POST_ID },
						PostTable.POST_ID + "=?", new String[] { last_id },
						null);
				if (c.getCount() == 0) {
					Util.log("Refresh not overlapping, remove old data to prevent gap.");
					mContext.getContentResolver().delete(
							Provider.FEED_CONTENT_URI, null, null);
				}
			}
			for (int i = 0; i < feeds.length(); i++) {
				Util.log("Post# " + i);
				JSONObject post = feeds.getJSONObject(i);

				// Util.log(post.toString(2));

				JSONObject from = post.getJSONObject("from");
				String link = post.getString("link");
				String album_id = link.split("&set=[a-z]+.")[1].split("[.]+")[0];

				// Util.log("link " + link);
				// Util.log("album_id " + album_id);

				Date created = parser.parse(post.getString("created_time"));
				Date updated = parser.parse(post.getString("updated_time"));

				ContentValues values = new ContentValues();
				values.put(PostTable.POST_ID, post.getString("id"));
				values.put(PostTable.FROM_ID, from.getString("id"));
				values.put(PostTable.FROM_NAME, from.getString("name"));
				if (!post.isNull(PostTable.MESSAGE)) {
					values.put(PostTable.MESSAGE, post.getString("message"));
				}
				if (!post.isNull(PostTable.STORY)) {
					values.put(PostTable.STORY, post.getString("story"));
				}
				values.put(PostTable.LINK, post.getString("link"));
				values.put(PostTable.ALBUM_ID, album_id);
				values.put(PostTable.PICTURE, post.getString("picture"));
				values.put(PostTable.PICTURE_ID, post.getString("object_id"));

				if (!post.isNull("comments")) {
					JSONObject comments = post.getJSONObject("comments");
					values.put(PostTable.COMMENTS_COUNT,
							comments.getInt("count"));
				} else {
					values.put(PostTable.COMMENTS_COUNT, 0);
				}

				if (!post.isNull("likes")) {
					JSONObject likes = post.getJSONObject("likes");
					values.put(PostTable.LIKES_COUNT, likes.getInt("count"));
				} else {
					values.put(PostTable.LIKES_COUNT, 0);
				}

				mCal.setTime(created);
				values.put(PostTable.CREATED_TIME, created.getTime());
				values.put(PostTable.CREATED_YEAR, mCal.get(Calendar.YEAR));
				values.put(PostTable.CREATED_MONTH, mCal.get(Calendar.MONTH));
				values.put(PostTable.CREATED_DAY,
						mCal.get(Calendar.DAY_OF_MONTH));

				mCal.setTime(updated);
				values.put(PostTable.UPDATED_TIME, updated.getTime());
				values.put(PostTable.UPDATED_YEAR, mCal.get(Calendar.YEAR));
				values.put(PostTable.UPDATED_MONTH, mCal.get(Calendar.MONTH));
				values.put(PostTable.UPDATED_DAY,
						mCal.get(Calendar.DAY_OF_MONTH));

				mContext.getContentResolver().insert(Provider.FEED_CONTENT_URI,
						values);
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
			Util.log("onPostExecute onRefreshComplete");
			mPtrListView.onRefreshComplete();
		}
		if (empty && mListView != null && mFooter != null) {
			mListView.setOnScrollListener(null);
			mListView.removeFooterView(mFooter);
		}
		super.onPostExecute(result);
	}
}
