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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.metaisle.photosync.R;
import com.metaisle.photosync.data.PostTable;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.photosync.facebook.HomeFeedTask;
import com.metaisle.util.Util;

public class HomeFeedFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor>, OnScrollListener {
	public SharedPreferences mPrefs;
	public ActionMode mActionMode;
	private PullToRefreshListView mPtrListView;
	private ListView mListView;
	private HomeFeedAdapter mAdapter;

	private LinearLayout mHomeFeedLayout;
	public View mFooterView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = getActivity().getSharedPreferences(Prefs.PREFS_NAME,
				Context.MODE_PRIVATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mHomeFeedLayout = (LinearLayout) inflater.inflate(
				R.layout.fragment_home_feed, null);
		mPtrListView = (PullToRefreshListView) mHomeFeedLayout
				.findViewById(R.id.home_feed_ptr_list);
		mAdapter = new HomeFeedAdapter(getActivity(), null);
		mListView = mPtrListView.getRefreshableView();
		mFooterView = inflater
				.inflate(R.layout.fragment_home_feed_footer, null);
		mListView.addFooterView(mFooterView);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(this);

		getLoaderManager().initLoader(0, null, this);

		mPtrListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				Util.log("onRefresh logged in "
						+ Prefs.facebook.isSessionValid());
				if (Prefs.facebook != null && Prefs.facebook.isSessionValid()) {
					new HomeFeedTask(getActivity(), Prefs.facebook,
							mPtrListView, null, null).execute();
					if (mListView.getFooterViewsCount() == 0) {
						mListView.addFooterView(mFooterView);
					}
					mListView.setOnScrollListener(HomeFeedFragment.this);
				} else {
					mPtrListView.onRefreshComplete();
				}
			}
		});

		return mHomeFeedLayout;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), Provider.FEED_CONTENT_URI,
				new String[] { PostTable._ID, PostTable.FROM_ID,
						PostTable.STORY, PostTable.MESSAGE,
						PostTable.FROM_NAME, PostTable.CREATED_TIME,
						PostTable.PICTURE_ID }, null, null,
				PostTable.CREATED_TIME + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
		if (mAdapter.getCursor() == null) {
			Util.log("after swap cursor, adapter cursor null");
		} else {
			Util.log("after swap cursor " + mAdapter.getCursor().toString());
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (totalItemCount == 0) {
			return;
		}

		boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
		if (loadMore) {
			Util.log("Load more");
			Cursor c = mAdapter.getCursor();

			if (c != null && c.getCount() > 0) {
				Util.log("firstVisibleItem " + firstVisibleItem
						+ " visibleItemCount " + visibleItemCount
						+ " totalItemCount " + totalItemCount);
				c.moveToLast();
				// Util.log("created index "
				// + c.getColumnIndex(PostTable.CREATED_TIME));
				// Util.log("cursor count " + c.getCount());
				int created = (int) (c.getLong(c
						.getColumnIndex(PostTable.CREATED_TIME)) / 1000);
				String until = String.valueOf(created);
				Util.log("load more, until " + until);
				new HomeFeedTask(getActivity(), Prefs.facebook, mPtrListView,
						null, until, mListView, mFooterView).execute();
			} else {
				Util.log("cursor is null");
			}

		}

	}
}
