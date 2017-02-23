package com.metaisle.photosync.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.metaisle.photosync.R;
import com.metaisle.photosync.data.UploadTable;

public class UploadQueueAdapter extends CursorAdapter implements
		OnClickListener {
	private LayoutInflater mInflater;

	public int mThumbnailSize;
	public int mItemsPerRow;
	public int mSpacing;

	public UploadQueueAdapter(Context context, Cursor c) {
		super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		mInflater = LayoutInflater.from(context);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService("window"))
				.getDefaultDisplay().getMetrics(displayMetrics);
		mThumbnailSize = PhotoByDateAdapter.fillThumbnailSizes(context,
				displayMetrics);
		mItemsPerRow = displayMetrics.widthPixels / mThumbnailSize;
		mSpacing = (displayMetrics.widthPixels - mItemsPerRow * mThumbnailSize)
				/ (displayMetrics.widthPixels / mThumbnailSize - 1);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		long mid = cursor.getLong(cursor
				.getColumnIndex(UploadTable.MEDIASTORE_ID));
		Bitmap bmp = MediaStore.Images.Thumbnails.getThumbnail(
				context.getContentResolver(), mid,
				MediaStore.Images.Thumbnails.MICRO_KIND, null);
		ImageView v = (ImageView) view.findViewById(R.id.thumb);
		v.setImageBitmap(bmp);

		if (!cursor.isNull(cursor.getColumnIndex(UploadTable.FINISH_TIME))) {
			ImageView badge = (ImageView) view.findViewById(R.id.badge);
			badge.setImageResource(R.drawable.ic_action_upload_finish);
		}

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		FrameLayout photoItem = (FrameLayout) mInflater.inflate(
				R.layout.fragment_uploadqueue_item, parent, false);
		ViewGroup.LayoutParams lp = photoItem.getLayoutParams();
		lp.height = mThumbnailSize;
		lp.width = mThumbnailSize;
		return photoItem;
	}
}
