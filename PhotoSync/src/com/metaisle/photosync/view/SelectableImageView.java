package com.metaisle.photosync.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.metaisle.photosync.R;

public class SelectableImageView extends ImageView {
	private Context mContext;
	private boolean mSelected;
	private Drawable mSelectedDrawable;

	public SelectableImageView(Context context) {
		super(context);
		init(context);
	}

	public SelectableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SelectableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		mContext = context;
		mSelected = false;
		Resources res = mContext.getApplicationContext().getResources();
		mSelectedDrawable = res.getDrawable(R.drawable.list_focused_holo);
	}

	public void setSelected(boolean selected) {
		mSelected = selected;
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int i = canvas.getSaveCount();
		canvas.save();
		super.onDraw(canvas);
		canvas.restoreToCount(i);

		if (mSelected) {
			mSelectedDrawable.setBounds(0, 0, canvas.getWidth(),
					canvas.getHeight());

			mSelectedDrawable.draw(canvas);
		}
	}

}
