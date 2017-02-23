package com.metaisle.photosync.fragment;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.metaisle.photosync.R;
import com.metaisle.photosync.data.PostTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.lazyload.ImageLoader;

public class HomeFeedAdapter extends CursorAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader avatarImageloader;
	private ImageLoader pictureImageloader;

	public HomeFeedAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		avatarImageloader = new ImageLoader(mContext);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService("window"))
				.getDefaultDisplay().getMetrics(displayMetrics);

		pictureImageloader = new ImageLoader(mContext,
				displayMetrics.widthPixels - 40);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.fragment_home_feed_item, null);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView avatar_view = (ImageView) view
				.findViewById(R.id.home_feed_avatar);
		TextView story_view = (TextView) view
				.findViewById(R.id.home_feed_story);
		TextView message_view = (TextView) view
				.findViewById(R.id.home_feed_message);
		TextView time_view = (TextView) view.findViewById(R.id.home_feed_time);
		ImageView image_view = (ImageView) view
				.findViewById(R.id.home_feed_image);

		Long user_id = cursor.getLong(cursor.getColumnIndex(PostTable.FROM_ID));
		String avatar_url = "http://graph.facebook.com/" + user_id + "/picture";
		avatarImageloader.DisplayImage(avatar_url, avatar_view);

		// Util.log("user_id " + user_id);

		int message_index = cursor.getColumnIndex(PostTable.MESSAGE);
		if (!cursor.isNull(message_index)) {
			String message = cursor.getString(message_index);
			message_view.setText(message);
			story_view.setText(cursor.getString(cursor
					.getColumnIndex(PostTable.FROM_NAME)));
			// Util.log("message " + message);
		} else {
			message_view.setText(null);
		}

		int story_index = cursor.getColumnIndex(PostTable.STORY);
		if (!cursor.isNull(story_index)) {
			String story = cursor.getString(story_index);
			story_view.setText(story);
			// Util.log("story " + story);
		} else {
			String name = cursor.getString(cursor
					.getColumnIndex(PostTable.FROM_NAME));
			story_view.setText(name);
		}

		long created_time = cursor.getLong(cursor
				.getColumnIndex(PostTable.CREATED_TIME));
		String ago_str = DateUtils.getRelativeTimeSpanString(created_time)
				.toString();
		time_view.setText(ago_str);

		// String picture_url = cursor.getString(cursor
		// .getColumnIndex(FeedTable.PICTURE));
		long picture_id = cursor.getLong(cursor
				.getColumnIndex(PostTable.PICTURE_ID));
		// Util.log("picture id" + picture_id);
		String url = "https://graph.facebook.com/" + picture_id
				+ "/picture?type=normal&access_token="
				+ Prefs.facebook.getAccessToken();
		pictureImageloader.DisplayImage(url, image_view);
	}
}
