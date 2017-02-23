package com.metaisle.photosync.facebook;

import android.content.Context;
import android.widget.Toast;

public class FacebookErrorToast implements Runnable {
	private Context mContext;

	public FacebookErrorToast(Context context) {
		mContext = context;
	}

	@Override
	public void run() {
		Toast.makeText(mContext, "Facebook connection error.",
				Toast.LENGTH_SHORT).show();
	}

}
