package com.metaisle.photosync.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.metaisle.photosync.R;
import com.metaisle.photosync.data.AlbumTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.photosync.facebook.AlbumListTask;

public class AlbumsFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor> {
	private SharedPreferences mPrefs;
	private AlbumsAdapter mAdapter;
	private PullToRefreshListView mPtrListView;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = getActivity().getSharedPreferences(Prefs.PREFS_NAME,
				Context.MODE_PRIVATE);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), Provider.ALBUM_CONTENT_URI,
				new String[] { AlbumTable._ID, AlbumTable.ALBUM_NAME,
						AlbumTable.ALBUM_ID, AlbumTable.COVER_ID }, null, null,
				AlbumTable.UPDATED_TIME + " DESC");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_share_album, null);
		mPtrListView = (PullToRefreshListView) v
				.findViewById(R.id.fragment_share_album_ptr);
		mAdapter = new AlbumsAdapter(AlbumsFragment.this, null);
		mListView = mPtrListView.getRefreshableView();
		mListView.setAdapter(mAdapter);
		getLoaderManager().initLoader(0, null, this);

		mPtrListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (Prefs.facebook != null && Prefs.facebook.isSessionValid()) {
					new AlbumListTask(getActivity(), mPtrListView).execute();
				} else {
					mPtrListView.onRefreshComplete();
				}

			}
		});

		return v;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
	}

}
