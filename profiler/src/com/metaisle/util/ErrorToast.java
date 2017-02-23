package com.metaisle.util;

import android.content.Context;
import android.widget.Toast;

public class ErrorToast implements Runnable {
	private Context mContext;
	private String mText;

	public ErrorToast(Context context, String text) {
		mContext = context;
		mText = text;
	}

	@Override
	public void run() {
		Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
	}

}
