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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.ActionMode;
import com.metaisle.photosync.R;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.photosync.data.UploadTable;

public class UploadQueueFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor>, OnLongClickListener {

	public SharedPreferences mPrefs;
	public UploadQueueAdapter mAdapter;
	public ActionMode mActionMode;
	private GridView mGridView;

	private LinearLayout mMainLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = getActivity().getSharedPreferences(Prefs.PREFS_NAME,
				Context.MODE_PRIVATE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mMainLayout = (LinearLayout) inflater.inflate(
				R.layout.fragment_gridview, null);
		mAdapter = new UploadQueueAdapter(getActivity(), null);
		// mAdapter.setOnLongClickListener(this);
		mGridView = (GridView) mMainLayout.findViewById(R.id.gridview);
		mGridView.setAdapter(mAdapter);
		mGridView.setColumnWidth(mAdapter.mThumbnailSize);
		mGridView.setNumColumns(mAdapter.mItemsPerRow);

		getLoaderManager().initLoader(0, null, this);

		return mMainLayout;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		return new CursorLoader(getActivity(), Provider.UPLOAD_CONTENT_URI,
				new String[] { UploadTable._ID, UploadTable.MEDIASTORE_ID,
						UploadTable.FINISH_TIME }, null, null,
				UploadTable.DATE_TAKEN + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	// public ActionMode.Callback mActionModeCallback = new
	// ActionMode.Callback() {
	//
	// @Override
	// public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	// MenuInflater inflater = mode.getMenuInflater();
	// inflater.inflate(R.menu.upload_context_menu, menu);
	// return true;
	// }
	//
	// @Override
	// public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	// return false;
	// }
	//
	// @Override
	// public boolean onActionItemClicked(final ActionMode mode, MenuItem item)
	// {
	// switch (item.getItemId()) {
	// case R.id.menu_upload:
	// Util.log("mToShare " + mAdapter.mToShare.keySet());
	// if (mAdapter.mToShare.size() > 0) {
	// Intent i = new Intent(getActivity(),
	// SharePagerActivity.class);
	//
	// ArrayList<String> pictures = new ArrayList<String>();
	// Set<String> keyset = mAdapter.mToShare.keySet();
	// for (String k : keyset) {
	// pictures.add(mAdapter.mToShare.get(k).picture);
	// }
	//
	// i.putExtra(SharePagerActivity.KEY_TO_SHARE_SOURCE,
	// mAdapter.mToShareSource);
	// i.putExtra(SharePagerActivity.KEY_TO_SHARE_PICTURE,
	// pictures);
	// startActivity(i);
	// mode.finish();
	// }
	// return true;
	//
	// case R.id.menu_delete:
	// Util.log("To delete " + mAdapter.mToShare.keySet());
	// if (mAdapter.mToShare.size() > 0) {
	// AlertDialog dialog = new AlertDialog.Builder(getActivity())
	// .setTitle("Delete photos")
	// .setMessage("Are you sure?")
	// .setPositiveButton("Yes",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(
	// DialogInterface dialog,
	// int which) {
	// String[] ids = new String[mAdapter.mToShare
	// .size()];
	// Set<String> keyset = mAdapter.mToShare
	// .keySet();
	// int i = 0;
	// for (String k : keyset) {
	// ids[i++] = mAdapter.mToShare
	// .get(k).id;
	// }
	// Util.log("delete "
	// + Arrays.toString(ids));
	// new DeleteTask(getActivity(),
	// String.valueOf(mAlbumID))
	// .execute(ids);
	// mode.finish();
	// }
	// })
	// .setNegativeButton("Cancel",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(
	// DialogInterface dialog,
	// int which) {
	// dialog.dismiss();
	// }
	// }).create();
	// dialog.show();
	// }
	// return true;
	// default:
	// return false;
	// }
	// }
	//
	// @Override
	// public void onDestroyActionMode(ActionMode mode) {
	// mActionMode = null;
	// mAdapter.mToShare.clear();
	// mAdapter.mToShareSource.clear();
	// mAdapter.notifyDataSetChanged();
	// mAdapter.mSelectMode = false;
	// }
	//
	// };

	@Override
	public boolean onLongClick(View v) {
		// if (!mAdapter.mSelectMode) {
		// mAdapter.mSelectMode = true;
		// mActionMode = getSherlockActivity().startActionMode(
		// mActionModeCallback);
		// MediaTracker mt = (MediaTracker) v.getTag();
		// mAdapter.mToShare.put(mt.source, mt);
		// SelectableImageView siv = (SelectableImageView) v;
		// siv.setSelected(true);
		// mAdapter.notifyDataSetChanged();
		// return true;
		// }
		return false;
	}

}