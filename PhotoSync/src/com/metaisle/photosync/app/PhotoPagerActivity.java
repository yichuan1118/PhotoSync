package com.metaisle.photosync.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.metaisle.photosync.R;
import com.metaisle.photosync.data.Prefs;
import com.metaisle.photosync.data.Provider;
import com.metaisle.photosync.data.UploadTable;
import com.metaisle.photosync.facebook.AllAlbumsTask;
import com.metaisle.photosync.facebook.MeTask;
import com.metaisle.photosync.fragment.PhotoByDateFragment;
import com.metaisle.photosync.fragment.UploadQueueFragment;
import com.metaisle.profiler.ConfigFileManager;
import com.metaisle.util.Util;
import com.viewpagerindicator.PageIndicator;

public class PhotoPagerActivity extends SherlockFragmentActivity {
	private SharedPreferences mPrefs;
	private SharedPreferences mDefaultPrefs;
	private long lastRefreshTime = -1;

	private PhotoPagerAdapter mAdapter;
	private ViewPager mPager;
	private PageIndicator mIndicator;

	private Button mLoginButton;

	public static final int CODE_FACEBOOK_AUTH = 202;
	private ConfigFileManager conf;
	private MenuItem mShareMenuItem;
	private MenuItem mClearFinishedUploadsMenuItem;
	private boolean mEnableTimeline;

	// private MenuItem mUploadMenuItem;
	// private MenuItem mQueueMenuItem;
	// private boolean isuploading;
	// private uploadlistener uploadeceiver = new uploadlistener();
	// private Handler icon_refresh;
	//
	// private class uploadlistener extends BroadcastReceiver {
	// Handler upload_icon_delay = new Handler();
	// long start_time = 0;
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// isuploading = intent.getExtras().getBoolean("isuploading");
	// Util.log("photo upload intent : " + isuploading);
	// final long time = intent.getExtras().getLong("time");
	// final int photosize = intent.getExtras().getInt("photosize");
	//
	// if (!isuploading) {
	// upload_icon_delay.postDelayed(new Runnable() {
	// public void run() {
	// mUploadMenuItem
	// .setIcon(R.drawable.ic_action_upload_finish);
	// long del_time = time - start_time;
	// float thru = (float) photosize
	// / ((float) del_time / 1000);
	// DecimalFormat formatter = new DecimalFormat("###.##");
	// if (thru < 128) {
	// Toast.makeText(
	// getBaseContext(),
	// "Photo Update Complete with "
	// + formatter.format(thru) + " bps",
	// Toast.LENGTH_SHORT).show();
	// //Log.v("test", formatter.format(thru) + " bps");
	// } else if (thru / 128 < 1024) {
	// thru /= 128;
	// Toast.makeText(
	// getBaseContext(),
	// "Photo Update Complete with "
	// + formatter.format(thru) + " Kbps",
	// Toast.LENGTH_SHORT).show();
	// //Log.v("test", formatter.format(thru) + " Kbps");
	// } else if (thru / 128 / 1024 < 1024) {
	// thru /= (128 * 1024);
	// Toast.makeText(
	// getBaseContext(),
	// "Photo Update Complete with "
	// + formatter.format(thru) + " Mbps",
	// Toast.LENGTH_SHORT).show();
	// //Log.v("test", formatter.format(thru) + " Mbps");
	// } else {
	// thru /= (128 * 1024 * 1024);
	// Toast.makeText(
	// getBaseContext(),
	// "Photo Update Complete with "
	// + formatter.format(thru) + " Gbps",
	// Toast.LENGTH_SHORT).show();
	// //Log.v("test", formatter.format(thru) + " Gbps");
	// }
	// }
	// }, 3000);
	// } else {
	// mUploadMenuItem.setIcon(R.drawable.ic_action_upload);
	// start_time = time;
	// }
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albums_pager);
		mDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mEnableTimeline = mDefaultPrefs.getBoolean(Prefs.KEY_ENABLE_TIMELINE,
				true);

		mPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new PhotoPagerAdapter(this, mPager);
		mAdapter.setEnableTimeline(mEnableTimeline);
		mPager.setAdapter(mAdapter);

		mIndicator = (PageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
		mIndicator.setOnPageChangeListener(new PagerChangeListener());

		mLoginButton = (Button) findViewById(R.id.login_button);

		if (!mDefaultPrefs.contains(Prefs.KEY_ENABLE_PROFILING)) {
			mDefaultPrefs.edit().putBoolean(Prefs.KEY_ENABLE_PROFILING, true)
					.commit();
		}

		if (!mDefaultPrefs.contains(Prefs.KEY_ENABLE_TIMELINE)) {
			mDefaultPrefs.edit().putBoolean(Prefs.KEY_ENABLE_TIMELINE, true)
					.commit();
			AlertDialog dialog = new AlertDialog.Builder(this)
					.setTitle("Please note")
					.setMessage(
							"You can now disable Timeline in Preference. "
									+ "This App may collect anonymous data for research on improving upload network efficiency, you can disable the collection in Preference.")
					.setPositiveButton("Preference",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									startActivity(new Intent(
											PhotoPagerActivity.this,
											PrefsActivity.class));
								}
							})
					.setNegativeButton("Got it",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).create();
			dialog.show();
		}

		/*
		 * Get existing access_token if any
		 */
		mPrefs = getSharedPreferences(Prefs.PREFS_NAME, MODE_PRIVATE);
		String access_token = mPrefs.getString(Prefs.KEY_ACCESS_TOKEN, null);
		long expires = mPrefs.getLong(Prefs.KEY_EXPIRES_IN, 0);
		if (access_token != null) {
			Prefs.facebook.setAccessToken(access_token);
		}
		if (expires != 0) {
			Prefs.facebook.setAccessExpires(expires);
		}

		lastRefreshTime = mPrefs.getLong(Prefs.KEY_LAST_REFRESH_TIME, -1);

		/*
		 * Setup UI and fetch data.
		 */
		if (Prefs.facebook.isSessionValid()) {
			mLoginButton.setVisibility(View.INVISIBLE);
			mPager.setVisibility(View.VISIBLE);
			if (lastRefreshTime == -1) {
				Util.log("last update was never, update");
				new AllAlbumsTask(this, Prefs.facebook, null).execute();
			}
		} else {
			mLoginButton.setVisibility(View.VISIBLE);
			mPager.setVisibility(View.INVISIBLE);
		}

		// String user_id = mPrefs.getString(Prefs.KEY_USER_ID, null);
		// if (user_id == null) {
		// new MeTask(PhotoPagerActivity.this, Prefs.facebook);
		// }

		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Prefs.facebook.authorize(PhotoPagerActivity.this, new String[] {
						"user_photos", "friends_photos", "photo_upload",
						"read_stream", "publish_stream", "status_update" },
						Facebook.FORCE_DIALOG_AUTH, new AuthDialogListener());
			}
		});
		conf = new ConfigFileManager(this);
		// isuploading = false;
		// IntentFilter uploadfilter = new IntentFilter();
		// uploadfilter.addAction("photo_upload_start");
		// registerReceiver(uploadeceiver, uploadfilter);
		// if (!mPrefs.contains(Prefs.KEY_ENABLE_TIMELINE)) {
		// mPrefs.edit().putBoolean(Prefs.KEY_ENABLE_TIMELINE, true).commit();
		// Toast.makeText(this, "Timeline can be disabled in Preference.",
		// Toast.LENGTH_LONG).show();
		// }
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(uploadeceiver);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (CODE_FACEBOOK_AUTH == requestCode) {
			Prefs.facebook.authorizeCallback(requestCode, resultCode, data);
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Prefs.facebook.extendAccessTokenIfNeeded(this, null);
		if (mEnableTimeline != mDefaultPrefs.getBoolean(
				Prefs.KEY_ENABLE_TIMELINE, true)) {
			mEnableTimeline = mDefaultPrefs.getBoolean(
					Prefs.KEY_ENABLE_TIMELINE, true);
			PhotoPagerAdapter adapter = (PhotoPagerAdapter) mPager.getAdapter();
			adapter.setEnableTimeline(mEnableTimeline);
			mPager.getAdapter().notifyDataSetChanged();
			mIndicator.notifyDataSetChanged();
		}
	}

	private class AuthDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.putString(Prefs.KEY_ACCESS_TOKEN,
					Prefs.facebook.getAccessToken());
			editor.putLong(Prefs.KEY_EXPIRES_IN,
					Prefs.facebook.getAccessExpires());
			editor.commit();

			mLoginButton.setVisibility(View.INVISIBLE);
			mPager.setVisibility(View.VISIBLE);

			// new AlbumListTask(PhotoPagerActivity.this, null).execute();
			new MeTask(PhotoPagerActivity.this, Prefs.facebook).execute();
		}

		@Override
		public void onFacebookError(FacebookError error) {
			Toast.makeText(PhotoPagerActivity.this,
					"Facebook login failed, please try later.",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(PhotoPagerActivity.this,
					"Facebook login failed, please try later.",
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(PhotoPagerActivity.this, "Facebook login canceled.",
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mPager.getCurrentItem() == PhotoPagerAdapter.POSITION_PRIVATE_FRAGMENT) {
			mShareMenuItem.setVisible(true);
		} else {
			mShareMenuItem.setVisible(false);
		}

		if (mPager.getCurrentItem() == PhotoPagerAdapter.POSITION_UPLOADS_FRAGMENT) {
			mClearFinishedUploadsMenuItem.setVisible(true);
		} else {
			mClearFinishedUploadsMenuItem.setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// -------------------------------------------------------------------------
		mShareMenuItem = menu.add("Start sharing");
		mShareMenuItem.setIcon(R.drawable.ic_action_share);
		mShareMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						PhotoByDateFragment f = (PhotoByDateFragment) mAdapter
								.instantiateItem(
										mPager,
										PhotoPagerAdapter.POSITION_PRIVATE_FRAGMENT);
						if (f != null) {
							f.mAdapter.mSelectMode = true;
							f.mActionMode = f.getSherlockActivity()
									.startActionMode(f.mActionModeCallback);
							f.mActionMode.setTitle("Select photos");
							return true;
						}
						return false;
					}
				});
		mShareMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// -------------------------------------------------------------------------
		mClearFinishedUploadsMenuItem = menu.add("Clear finished");
		mClearFinishedUploadsMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						UploadQueueFragment f = (UploadQueueFragment) mAdapter
								.instantiateItem(
										mPager,
										PhotoPagerAdapter.POSITION_UPLOADS_FRAGMENT);
						if (f != null) {
							getContentResolver()
									.delete(Provider.UPLOAD_CONTENT_URI,
											UploadTable.FINISH_TIME
													+ " not null", null);

							return true;
						}
						return false;
					}
				});
		mClearFinishedUploadsMenuItem
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		// -------------------------------------------------------------------------
		MenuItem item = menu.add("Preference");
		item.setIcon(R.drawable.action_settings);
		item.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(PhotoPagerActivity.this,
						PrefsActivity.class));
				return true;
			}
		});

		// //
		// -------------------------------------------------------------------------
		// item = menu.add("Logout");
		// item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		// item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		//
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// if (Prefs.facebook.isSessionValid()) {
		// com.facebook.android.AsyncFacebookRunner asyncRunner = new
		// com.facebook.android.AsyncFacebookRunner(
		// Prefs.facebook);
		// asyncRunner.logout(PhotoPagerActivity.this,
		// new LogoutRequestListener());
		// }
		// return true;
		// }
		// });

		// -------------------------------------------------------------------------
		// if (conf.getProfilerUI_enable()) {
		// MenuItem item2 = menu.add("Profile Viewer");
		// item2.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		// item2.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		//
		// @Override
		// public boolean onMenuItemClick(MenuItem item) {
		// Intent intent = new Intent();
		// intent.setAction("profilerUI_startup");
		// intent.addCategory("android.intent.category.DEFAULT");
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// return true;
		// }
		// });
		//
		// item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		// item2.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		// }

		//
		// mUploadMenuItem = menu.add("uploading");
		// if (!isuploading)
		// mUploadMenuItem.setIcon(R.drawable.ic_action_upload_finish);
		// else
		// mUploadMenuItem.setIcon(R.drawable.ic_action_upload);
		// mUploadMenuItem.setEnabled(false);
		//
		// mUploadMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		// mQueueMenuItem = menu.add("queue");
		// mQueueMenuItem.setIcon(R.drawable.ic_photos);
		// mQueueMenuItem.setEnabled(false);
		// mQueueMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		//
		// icon_refresh = new Handler();
		// icon_refresh.postDelayed(new Runnable() {
		// int pending_upload() {
		// Cursor cursor = getApplicationContext().getContentResolver()
		// .query(Provider.UPLOAD_CONTENT_URI,
		// new String[] { UploadTable.FINISH_TIME,
		// UploadTable._ID },
		// UploadTable.FINISH_TIME + " is null", null,
		// null);
		//
		// if (cursor == null || cursor.getCount() == 0) {
		// return 0;
		// }
		// cursor.moveToFirst();
		// Date d = new Date(cursor.getLong(cursor
		// .getColumnIndex(UploadTable.FINISH_TIME)));
		// Util.log(d.toLocaleString());
		// return cursor.getCount();
		// }
		//
		// BitmapDrawable icon_gen() {
		// // Drawable image = getApplicationContext().getResources()
		// // .getDrawable(R.drawable.ic_photos);
		// // final int IMAGE_WIDTH = image.getIntrinsicWidth();
		// // final int IMAGE_HEIGHT = image.getIntrinsicHeight();
		// // Bitmap canvasBitmap = Bitmap.createBitmap(IMAGE_WIDTH,
		// // IMAGE_HEIGHT, Bitmap.Config.ARGB_8888);
		// Bitmap canvasBitmap = BitmapFactory.decodeResource(
		// getApplicationContext().getResources(),
		// R.drawable.ic_photos).copy(Bitmap.Config.ARGB_8888,
		// true);
		// final int IMAGE_WIDTH = canvasBitmap.getWidth();
		// final int IMAGE_HEIGHT = canvasBitmap.getHeight();
		// Canvas imageCanvas = new Canvas(canvasBitmap);
		// Paint imagePaint = new Paint();
		// imagePaint.setTextAlign(Align.CENTER);
		// imagePaint.setTextSize((float) (IMAGE_WIDTH * 0.4));
		// imagePaint.setAntiAlias(true);
		// imagePaint.setColor(Color.RED);
		// imageCanvas.drawText(String.valueOf(pending_upload()),
		// IMAGE_WIDTH / 2, IMAGE_HEIGHT / 2, imagePaint);
		// BitmapDrawable finalImage = new BitmapDrawable(getResources(),
		// canvasBitmap);
		// Util.log("" + finalImage.getIntrinsicHeight() + " X "
		// + finalImage.getIntrinsicWidth());
		// return finalImage;
		// }
		//
		// @Override
		// public void run() {
		// int task = pending_upload();
		// if (task == 0) {
		// Drawable image = getApplicationContext().getResources()
		// .getDrawable(R.drawable.ic_photos);
		// mQueueMenuItem.setIcon(image);
		// } else {
		//
		// mQueueMenuItem.setIcon(icon_gen());
		// }
		// icon_refresh.postDelayed(this, 1000);
		// }
		// }, 1);

		return true;
	}

	private class PagerChangeListener extends
			ViewPager.SimpleOnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			super.onPageScrolled(position, positionOffset, positionOffsetPixels);
			invalidateOptionsMenu();
			if (position == PhotoPagerAdapter.POSITION_PRIVATE_FRAGMENT) {
				ActionMode mode = ((PhotoByDateFragment) mAdapter
						.instantiateItem(mPager, position)).mActionMode;
				if (mode != null) {
					mode.finish();
					// ((PhotoByDateFragment) mAdapter.instantiateItem(null,
					// position)).mAdapter.mSelectMode = false;
				}
			}
		}
	}

	private class LogoutRequestListener implements RequestListener {
		@Override
		public void onComplete(String response, final Object state) {
			SharedPreferences mPrefs = PhotoPagerActivity.this
					.getSharedPreferences(Prefs.PREFS_NAME,
							Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = mPrefs.edit();
			editor.clear();
			editor.commit();

			lastRefreshTime = -1;
			PhotoPagerActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mLoginButton.setVisibility(View.VISIBLE);
					mPager.setVisibility(View.INVISIBLE);
				}
			});
		}

		@Override
		public void onIOException(IOException e, Object state) {

		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {

		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {

		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {

		}
	}

}
