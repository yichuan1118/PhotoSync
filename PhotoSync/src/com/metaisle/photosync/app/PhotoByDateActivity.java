package com.metaisle.photosync.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.metaisle.photosync.fragment.PhotoByDateFragment;

public class PhotoByDateActivity extends SherlockFragmentActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Bundle b = new Bundle();
			b.putLong(PhotoByDateFragment.KEY_ALBUM_ID, getIntent()
					.getLongExtra(PhotoByDateFragment.KEY_ALBUM_ID, 0));
			getSupportFragmentManager()
					.beginTransaction()
					.add(android.R.id.content,
							Fragment.instantiate(this,
									PhotoByDateFragment.class.getName(), b))
					.commit();
		}

	}
}
