package com.metaisle.photosync.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.metaisle.photosync.R;
import com.metaisle.photosync.app.SharePagerActivity;
import com.metaisle.photosync.lazyload.ImageLoader;
import com.metaisle.util.Util;

public class SharePhotoAdapter extends BaseAdapter {
	private List<String> mToSharePicture;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;

	private int mThumbnailSize;
	private SharePhotoFragment mFragment;

	public SharePhotoAdapter(SharePhotoFragment fragment) {
		mFragment = fragment;
		mToSharePicture = ((SharePagerActivity) fragment.getActivity()).mToSharePictures;
		mInflater = (LayoutInflater) fragment.getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mImageLoader = new ImageLoader(fragment.getActivity());

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) fragment.getActivity().getSystemService("window"))
				.getDefaultDisplay().getMetrics(displayMetrics);
		mThumbnailSize = PhotoByDateAdapter.fillThumbnailSizes(
				fragment.getActivity(), displayMetrics);
	}

	@Override
	public int getCount() {
		return mToSharePicture.size();
	}

	@Override
	public Object getItem(int position) {
		return mToSharePicture.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout ll;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_share_photo_item,
					parent, false);

			ll = (LinearLayout) convertView;

			ImageView iv = (ImageView) ll.findViewById(R.id.photo);
			LayoutParams params = iv.getLayoutParams();
			params.width = mThumbnailSize;
			params.height = mThumbnailSize;
			iv.setLayoutParams(params);

			final EditText et = (EditText) ll.findViewById(R.id.message);
			et.setImeOptions(EditorInfo.IME_ACTION_DONE);
			et.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					Util.log("position " + position + " change to "
							+ s.toString());
					ArrayList<String> messages = ((SharePagerActivity) mFragment
							.getActivity()).mToShareMessages;
					while (messages.size() < position + 1) {
						messages.add(null);
					}
					messages.set(position, s.toString());
					Util.log("messages "
							+ ((SharePagerActivity) mFragment.getActivity()).mToShareMessages
									.toString());
				}
			});

			ll.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					InputMethodManager imm = (InputMethodManager) v
							.getContext().getSystemService(
									Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					return false;
				}
			});
		}

		ll = (LinearLayout) convertView;

		ImageView iv = (ImageView) ll.findViewById(R.id.photo);
		mImageLoader.DisplayImage(mToSharePicture.get(position), iv);

		return ll;
	}
}
