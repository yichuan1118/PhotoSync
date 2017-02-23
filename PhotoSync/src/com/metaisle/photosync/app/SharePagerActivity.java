package com.metaisle.photosync.app;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.metaisle.photosync.R;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.facebook.NewAlbumTask;
import com.metaisle.util.Util;
import com.viewpagerindicator.PageIndicator;

public class SharePagerActivity extends SherlockFragmentActivity {
	public static final String KEY_TO_SHARE_SOURCE = "key_to_share_source";
	public static final String KEY_TO_SHARE_PICTURE = "key_to_share_picture";
	public static final String KEY_TO_SHARE_MESSAGE = "key_to_share_message";

	private ViewPager mPager;
	private PageIndicator mIndicator;

	private SharePagerAdapter mAdapter;

	public ArrayList<String> mToShareSources;
	public ArrayList<String> mToSharePictures;
	public ArrayList<String> mToShareMessages;

	private MenuItem mNewAlbumMenu;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_share_pager);

		if (savedInstanceState != null) {
			mToShareMessages = savedInstanceState
					.getStringArrayList(KEY_TO_SHARE_MESSAGE);
		}
		if (mToShareMessages == null) {
			mToShareMessages = new ArrayList<String>();
		}

		mToShareSources = getIntent().getStringArrayListExtra(
				KEY_TO_SHARE_SOURCE);
		mToSharePictures = getIntent().getStringArrayListExtra(
				KEY_TO_SHARE_PICTURE);

		mPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new SharePagerAdapter(this);
		mPager.setAdapter(mAdapter);

		mIndicator = (PageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(new PagerChangeListener());

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putStringArrayList(KEY_TO_SHARE_MESSAGE, mToShareMessages);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// -------------------------------------------------------------------------
		mNewAlbumMenu = menu.add("New Album");
		mNewAlbumMenu.setIcon(R.drawable.content_new_picture);
		mNewAlbumMenu.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		mNewAlbumMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				final Dialog dialog = new Dialog(SharePagerActivity.this);
				dialog.setContentView(R.layout.dialog_new_album);
				dialog.setTitle("Create new Album on Facebook");

				Rect displayRectangle = new Rect();
				Window window = SharePagerActivity.this.getWindow();
				window.getDecorView().getWindowVisibleDisplayFrame(
						displayRectangle);

				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
				lp.copyFrom(dialog.getWindow().getAttributes());
				lp.width = WindowManager.LayoutParams.MATCH_PARENT;
				lp.height = (int) (displayRectangle.width() * 0.9f);
				dialog.getWindow().setAttributes(lp);

				final EditText et = (EditText) dialog
						.findViewById(R.id.edit_name);

				final ProgressBar pro = (ProgressBar) dialog
						.findViewById(R.id.progress);
				pro.setVisibility(View.INVISIBLE);

				String[] array_spinner = new String[3];
				array_spinner[0] = "EVERYONE";
				array_spinner[1] = "ALL_FRIENDS";
				array_spinner[2] = "SELF";
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						SharePagerActivity.this,
						android.R.layout.simple_spinner_item, array_spinner);
				final Spinner sp = (Spinner) dialog
						.findViewById(R.id.spinner_privacy);
				sp.setAdapter(adapter);

				// --------------------------------
				// Dismiss dialog from NewAlbumTask.
				mHandler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						Util.log("got message");
						switch (msg.what) {
						case Prefs.NEW_ALBUM_TASK_HANDLER_DONE:
							dialog.dismiss();
							break;
						case Prefs.NEW_ALBUM_TASK_HANDLER_ERROR:
							Toast.makeText(SharePagerActivity.this,
									"Create album error", Toast.LENGTH_LONG)
									.show();
						}
						super.handleMessage(msg);
					}
				};

				Button create = (Button) dialog
						.findViewById(R.id.create_button);
				create.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new NewAlbumTask(SharePagerActivity.this, et.getText()
								.toString(), sp.getSelectedItem().toString(),
								mHandler).execute();
						pro.setVisibility(View.VISIBLE);
					}
				});

				Button cancel = (Button) dialog
						.findViewById(R.id.cancel_button);
				cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
				return true;
			}
		});

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Util.log("current item " + mPager.getCurrentItem() + " pos "
				+ SharePagerAdapter.POSITION_ALBUM_FRAGMENT);
		if (mPager.getCurrentItem() == SharePagerAdapter.POSITION_ALBUM_FRAGMENT) {
			mNewAlbumMenu.setVisible(true);
		} else {
			mNewAlbumMenu.setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private class PagerChangeListener extends
			ViewPager.SimpleOnPageChangeListener {
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			super.onPageScrolled(position, positionOffset, positionOffsetPixels);
			invalidateOptionsMenu();
		}
	}
}
