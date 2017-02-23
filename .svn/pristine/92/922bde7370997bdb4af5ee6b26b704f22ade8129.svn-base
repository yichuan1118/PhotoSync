package com.metaisle.photosync.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metaisle.photosync.R;
import com.metaisle.photosync.app.SharePagerActivity;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.facebook.ShareTask;
import com.metaisle.photosync.lazyload.ImageLoader;

public class ShareAlbumAdapter extends CursorAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private ShareAlbumFragment mFragment;

	public ShareAlbumAdapter(ShareAlbumFragment fragment, Cursor c) {
		super(fragment.getActivity(), c,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mContext = fragment.getActivity();
		mInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(mContext);
		mFragment = fragment;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView album_cover = (ImageView) view.findViewById(R.id.album_cover);
		final TextView album_name = (TextView) view
				.findViewById(R.id.album_name);

		final Long album_id = cursor.getLong(cursor
				.getColumnIndex(AlbumTable.ALBUM_ID));
		final String name = cursor.getString(cursor
				.getColumnIndex(AlbumTable.ALBUM_NAME));

		album_name.setText(name);
		mImageLoader.DisplayImage(
				"https://graph.facebook.com/" + album_id
						+ "/picture?type=album&access_token="
						+ Prefs.facebook.getAccessToken(), album_cover);

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final SharePagerActivity activity = (SharePagerActivity) mFragment
						.getActivity();
				AlertDialog dialog = new AlertDialog.Builder(activity)
						.setTitle("Share these photos to [" + name + "]")
						.setPositiveButton("Share",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										String[] messages = activity.mToShareMessages
												.toArray(new String[activity.mToShareMessages
														.size()]);
										String[] urls = activity.mToShareSources
												.toArray(new String[activity.mToShareSources
														.size()]);
										new ShareTask(album_id, activity,
												messages).execute(urls);
										Toast.makeText(mContext,
												"Posting photos to Facebook.",
												Toast.LENGTH_LONG).show();
										activity.finish();
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.show();

			}
		});
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.fragment_share_album_item, null);
		return v;
	}
}
