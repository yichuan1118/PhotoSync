package com.metaisle.photosync.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import com.metaisle.photosync.service.MediaTrackingService;

public class CameraMonitor extends BroadcastReceiver {

	public static final Uri PHONE_STORAGE_IMAGES_URI = MediaStore.Images.Media
			.getContentUri("phoneStorage");
	public static final Uri PHONE_STORAGE_VIDEO_URI = MediaStore.Video.Media
			.getContentUri("phoneStorage");
	public static final Uri[] MEDIA_STORE_URIS = new Uri[] {
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			MediaStore.Images.Media.INTERNAL_CONTENT_URI,
			PHONE_STORAGE_IMAGES_URI,
	// MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
	// MediaStore.Video.Media.INTERNAL_CONTENT_URI,
	// PHONE_STORAGE_VIDEO_URI
	};
	private static ContentObserver sMediaMonitor = null;

	@Override
	public void onReceive(Context context, Intent intent) {
//		Util.log("onReceive");
		context.startService(new Intent(context, MediaTrackingService.class));
	}

	public static void registerObservers(Context context) {
		if (sMediaMonitor == null) {
			sMediaMonitor = new MediaObserver(context, null);
		}
		for (int j = 0; j < MEDIA_STORE_URIS.length; j++)
			context.getContentResolver().registerContentObserver(
					MEDIA_STORE_URIS[j], true, sMediaMonitor);
	}

	public static void unregisterObservers(Context context) {
		if (sMediaMonitor != null) {
			sMediaMonitor = null;
			context.getContentResolver().unregisterContentObserver(
					sMediaMonitor);
		}
	}

	private static class MediaObserver extends ContentObserver {
		Context mContext;

		public MediaObserver(Context context, Handler handler) {
			super(handler);
			mContext = context;
		}

		@Override
		public void onChange(boolean selfChange) {
//			Util.log("onChange");
			mContext.startService(new Intent(mContext,
					MediaTrackingService.class));
			super.onChange(selfChange);
		}
	}

}
