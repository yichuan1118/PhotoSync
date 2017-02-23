package com.metaisle.photosync.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.metaisle.photosync.fragment.AlbumsFragment;
import com.metaisle.photosync.fragment.HomeFeedFragment;
import com.metaisle.photosync.fragment.PhotoByDateFragment;
import com.metaisle.photosync.fragment.UploadQueueFragment;

public class PhotoPagerAdapter extends FragmentStatePagerAdapter {
	private static final String[] mFragmentTitles = { "Private", "Public",
			"Queue", "Timeline" };
	private boolean mEnableTimeline;

	public static final int POSITION_PRIVATE_FRAGMENT = 0;
	public static final int POSITION_ALBUMS_FRAGMENT = 1;
	public static final int POSITION_UPLOADS_FRAGMENT = 2;
	public static final int POSITION_TIMELINE_FRAGMENT = 3;

	public PhotoPagerAdapter(SherlockFragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
	}

	public void setEnableTimeline(boolean enable) {
		mEnableTimeline = enable;
	}

	@Override
	public int getCount() {
		if (mEnableTimeline) {
			return 4;
		} else {
			return 3;
		}
	}

	@Override
	public Fragment getItem(int position) {
		SherlockFragment f = null;
		switch (position) {
		case POSITION_PRIVATE_FRAGMENT: {
			f = new PhotoByDateFragment();
			break;
		}
		case POSITION_ALBUMS_FRAGMENT: {
			f = new AlbumsFragment();
			break;
		}
		case POSITION_UPLOADS_FRAGMENT: {
			f = new UploadQueueFragment();
			break;
		}
		case POSITION_TIMELINE_FRAGMENT: {
			if (mEnableTimeline) {
				f = new HomeFeedFragment();
			} else {
				f = null;
			}
			break;
		}
		}

		return f;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentTitles[position];
	}
}
