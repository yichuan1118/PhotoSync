package com.metaisle.photosync.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.metaisle.photosync.R;
import com.metaisle.photosync.app.PhotoByDateActivity;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.lazyload.ImageLoader;

public class AlbumsAdapter extends CursorAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private AlbumsFragment mFragment;

	public AlbumsAdapter(AlbumsFragment fragment, Cursor c) {
		super(fragment.getActivity(), c,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mContext = fragment.getActivity();
		mInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(mContext);
		mFragment = fragment;
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		View v = mInflater.inflate(R.layout.fragment_share_album_item, null);
		return v;
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
				Intent i = new Intent(mContext, PhotoByDateActivity.class);
				i.putExtra(PhotoByDateFragment.KEY_ALBUM_ID, album_id);
				mContext.startActivity(i);
			}
		});
	}

}
