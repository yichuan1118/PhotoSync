package com.metaisle.photosync.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.metaisle.photosync.fragment.ShareAlbumFragment;
import com.metaisle.photosync.fragment.SharePhotoFragment;

public class SharePagerAdapter extends FragmentStatePagerAdapter {
	public static final int POSITION_ALBUM_FRAGMENT = 1;
	private static final String[] mFragmentTitles = { "Photos", "Share to" };
	public SharePhotoFragment mPhotoFragment;
	public ShareAlbumFragment mAlbumFragment;

	// private SherlockFragmentActivity mActivity;

	public SharePagerAdapter(SherlockFragmentActivity activity) {
		super(activity.getSupportFragmentManager());
		// mActivity = activity;
	}

	@Override
	public Fragment getItem(int position) {
		SherlockFragment f = null;
		switch (position) {
		case 0: {
			mPhotoFragment = new SharePhotoFragment();
			f = mPhotoFragment;
			break;
		}
		case 1: {
			mAlbumFragment = new ShareAlbumFragment();
			f = mAlbumFragment;
			break;
		}
		}

		return f;
	}

	@Override
	public int getCount() {
		return mFragmentTitles.length;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitles[position];
	}
}
