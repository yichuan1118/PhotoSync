package com.metaisle.photosync.fragment;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.metaisle.photosync.R;
import com.metaisle.photosync.app.PhotoViewActivity;
import com.metaisle.photosync.data.PhotoTable;
import com.metaisle.photosync.lazyload.ImageLoader;
import com.metaisle.photosync.view.SelectableImageView;
import com.metaisle.util.Util;

public class PhotoByDateAdapter extends CursorAdapter implements
		OnClickListener {

	private LayoutInflater mInflater;

	private int mThumbnailSize;
	private int mItemsPerRow;
	private int mSpacing;

	public ArrayList<DisplayRow> mListRows;

	private SparseIntArray mCursorPosToRowIndex;

	private ImageLoader imageloader;

	private Context mContext;

	public Map<String, MediaTracker> mToShare;
	public ArrayList<String> mToShareSource;

	public boolean mSelectMode = false;
	private OnLongClickListener mOnLongClickListener = null;

	public PhotoByDateAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mContext = context;
		mInflater = LayoutInflater.from(context);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService("window"))
				.getDefaultDisplay().getMetrics(displayMetrics);
		mThumbnailSize = fillThumbnailSizes(context, displayMetrics);
		mItemsPerRow = displayMetrics.widthPixels / mThumbnailSize;
		mSpacing = (displayMetrics.widthPixels - mItemsPerRow * mThumbnailSize)
				/ (displayMetrics.widthPixels / mThumbnailSize - 1);

		// Util.log("mItemsPerRow " + mItemsPerRow);
		// Util.log("mSpacing " + mSpacing);

		mListRows = new ArrayList<PhotoByDateAdapter.DisplayRow>();
		imageloader = new ImageLoader(context);
		mCursorPosToRowIndex = new SparseIntArray();

		mToShare = new HashMap<String, MediaTracker>();
		mToShareSource = new ArrayList<String>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// if (convertView != null) {
		// Util.log("convertView.getId() " + convertView.getId());
		// }
		if (position > mListRows.size()) {
			return null;
		} else {
			// Util.log("position " + position);

			DisplayRow row = mListRows.get(position);

			mCursor.moveToPosition(position);

			View v;
			if (convertView == null) {
				v = newView(mContext, mCursor, parent);
			} else if ((row.isDivider && convertView.getId() == R.id.photo_row_divider)
					|| (!row.isDivider && convertView.getId() == R.id.photo_row)) {
				v = convertView;
			} else {
				v = newView(mContext, mCursor, parent);
			}
			bindView(v, mContext, mCursor);
			return v;
		}

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int rowIndex = cursor.getPosition();
		// Util.log("view.getId() " + view.getId());
		DisplayRow currentRow = mListRows.get(rowIndex);
		if (currentRow.isDivider) {
			if (view.getId() == R.id.photo_row_divider) {
				// reuse
				// Util.log("reuse divider");
				TextView tv = (TextView) view.findViewById(R.id.date);
				tv.setText(new DateFormatSymbols().getMonths()[currentRow.month]);
			} else {
				Util.log("isDivider, not FrameLayout");
			}
		} else {
			if (view.getId() == R.id.photo_row) {
				// Util.log("reuse row");
				LinearLayout ll = (LinearLayout) view;

				for (int i = 0; i < ll.getChildCount(); i++) {
					View v = ll.getChildAt(i);
					SelectableImageView iv = (SelectableImageView) v
							.findViewById(R.id.album_view_photo);
					if (i < currentRow.pictures.size()) {
						String source = currentRow.sources.get(i);
						String picture = currentRow.pictures.get(i);
						String id = currentRow.ids.get(i);
						MediaTracker mt = new MediaTracker(source, picture, id);

						iv.setTag(mt);
						if (mToShareSource.contains(mt.source)) {
							// Util.log("ImageView is selected");
							iv.setSelected(true);
						} else {
							iv.setSelected(false);
						}
						imageloader.DisplayImage(picture, iv);
						iv.setOnLongClickListener(mOnLongClickListener);
					} else {
						iv.setImageBitmap(null);
						// iv.setImageResource(android.R.color.transparent);
					}
				}

				for (int i = 0; i < currentRow.sources.size(); i++) {
					String source = currentRow.sources.get(i);
					String picture = currentRow.pictures.get(i);
					String id = currentRow.ids.get(i);
					MediaTracker mt = new MediaTracker(source, picture, id);

					FrameLayout photoItem = (FrameLayout) ll.getChildAt(i);
					ImageView iv = (ImageView) photoItem
							.findViewById(R.id.album_view_photo);

					iv.setTag(mt);

					iv.setOnClickListener(this);
					imageloader.DisplayImage(picture, iv);
				}
			} else {
				Util.log("Not isDivider, not LinearLayout");
			}
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// Util.log("cursor.getPosition() " + cursor.getPosition());

		int rowIndex = cursor.getPosition();

		DisplayRow row = mListRows.get(rowIndex);
		if (row.isDivider) {
			View v = mInflater.inflate(R.layout.photo_row_divider, null);
			// Util.log("isDivider ");
			return v;
		} else {
			LinearLayout ll = (LinearLayout) mInflater.inflate(
					R.layout.photo_row, null);
			ll.removeAllViews();
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 1);
			lp.setMargins(this.mSpacing / 2, 0, this.mSpacing / 2, 0);
			for (int i = 0; i < mItemsPerRow; i++) {
				FrameLayout photoItem = (FrameLayout) mInflater.inflate(
						R.layout.photo_row_item, null);
				photoItem.setLayoutParams(lp);
				photoItem.getLayoutParams().width = mThumbnailSize;
				photoItem.getLayoutParams().height = mThumbnailSize;
				ll.addView(photoItem);
			}
			return ll;
		}
	}

	@Override
	public Cursor swapCursor(Cursor cursor) {

		mListRows = new ArrayList<PhotoByDateAdapter.DisplayRow>();
		mCursorPosToRowIndex = new SparseIntArray();

		int currentMonth = -1;
		DisplayRow currentRow = new DisplayRow();
		if (cursor != null) {
			// Util.log("cursor.getCount() " + cursor.getCount());
			if (cursor.moveToFirst()) {
				do {
					int month = cursor.getInt(cursor
							.getColumnIndex(PhotoTable.CREATED_MONTH));
					String picture = cursor.getString(cursor
							.getColumnIndex(PhotoTable.PICTURE));
					String source = cursor.getString(cursor
							.getColumnIndex(PhotoTable.SOURCE));
					String photo_id = String.valueOf(cursor.getLong(cursor
							.getColumnIndex(PhotoTable.PHOTO_ID)));

					// add a divider, add new row
					if (currentMonth != month) {
						// Util.log("now is Month " + month);

						currentMonth = month;
						currentRow = new DisplayRow();
						currentRow.isDivider = true;
						currentRow.month = currentMonth;
						mListRows.add(currentRow);

						// Util.log("new divider, mListRow " +
						// mListRows.size());

						currentRow = new DisplayRow();
						currentRow.isDivider = false;
						currentRow.month = currentMonth;
						mListRows.add(currentRow);

						// Util.log("row after divider, mListRow "
						// + mListRows.size());
					}

					// row full, add new row
					else if (currentRow.sources.size() == mItemsPerRow) {
						currentRow = new DisplayRow();
						currentRow.isDivider = false;
						currentRow.month = currentMonth;
						mListRows.add(currentRow);

						// Util.log("full new row, mListRow " +
						// mListRows.size());
					}

					currentRow.sources.add(source);
					currentRow.pictures.add(picture);
					currentRow.ids.add(photo_id);

					mCursorPosToRowIndex.put(cursor.getPosition(),
							mListRows.size() - 1);
					// Util.log("cursor " + cursor.getPosition()
					// + ", ListRows index " + (mListRows.size() - 1));

					cursor.moveToNext();
				} while (!cursor.isAfterLast());
			}
		}

		// Util.log("total list rows " + mListRows.size());
		return super.swapCursor(cursor);
	}

	@Override
	public int getCount() {
		// Util.log("mListRows.size() " + mListRows.size());
		return mListRows.size();
	}

	public static int fillThumbnailSizes(Context context,
			DisplayMetrics displayMetrics) {
		int screen_width = displayMetrics.widthPixels;
		int screen_height = displayMetrics.heightPixels;
		// Util.log("screen_width " + screen_width);
		// Util.log("screen_height " + screen_height);
		Resources res = context.getResources();
		int photo_size = res.getDimensionPixelOffset(R.dimen.album_photo_width);
		// Util.log("photo_size " + photo_size);
		int virtical_spacing = res
				.getDimensionPixelOffset(R.dimen.album_grid_vertical_spacing);
		// Util.log("virtical_spacing " + virtical_spacing);
		int photos_only_width = screen_width - virtical_spacing
				* (screen_width / photo_size - 1);
		int thumb_width = photos_only_width / (photos_only_width / photo_size);

		// Util.log("thumb_width " + thumb_width);

		int photos_only_height = screen_height - virtical_spacing
				* (screen_height / photo_size - 1);
		int thumb_height = photos_only_height
				/ (photos_only_height / photo_size);

		// Util.log("thumb_height " + thumb_height);

		return Math.max(thumb_width, thumb_height);
	}

	public static class DisplayRow {
		int month = -1;
		public ArrayList<String> sources = new ArrayList<String>();
		public ArrayList<String> pictures = new ArrayList<String>();
		public ArrayList<String> ids = new ArrayList<String>();
		public boolean isDivider = false;
	}

	@Override
	public void onClick(View v) {
		MediaTracker mt = (MediaTracker) v.getTag();

		SelectableImageView siv = (SelectableImageView) v;
		if (mSelectMode) {
			if (mToShareSource.contains(mt.source)) {
				mToShare.remove(mt.source);
				mToShareSource.remove(mt.source);
				Util.log("Remove from to share: " + mToShareSource.size());
				siv.setSelected(false);
				notifyDataSetChanged();
			} else {
				mToShare.put(mt.source, mt);
				mToShareSource.add(mt.source);
				Util.log("Added to share: " + mToShareSource.size());
				siv.setSelected(true);
				notifyDataSetChanged();
			}

		} else {
			Intent i = new Intent(mContext, PhotoViewActivity.class);
			i.putExtra("source", mt.source);
			mContext.startActivity(i);
		}
	}

	public void setOnLongClickListener(OnLongClickListener listener) {
		mOnLongClickListener = listener;
	}

	public static class MediaTracker {
		public MediaTracker(String s, String p, String id) {
			source = s;
			picture = p;
			this.id = id;
		}

		public String source;
		public String picture;
		public String id;
	}

}
