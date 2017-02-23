package com.metaisle.photosync.app;

import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.metaisle.photosync.R;
import com.metaisle.photosync.lazyload.ImageLoader;
import com.metaisle.util.Util;

public class PhotoViewActivity extends android.app.Activity {

	ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_PROGRESS);

		String source = getIntent().getStringExtra("source");

		Util.log("source " + source);

		setContentView(R.layout.photo_webview);
		setProgressBarVisibility(true);

		WebView wv = (WebView) findViewById(R.id.photo_webview);
		wv.getSettings().setSupportZoom(true);
		wv.getSettings().setBuiltInZoomControls(true);

		wv.loadUrl(source);
		wv.setBackgroundColor(getResources().getColor(
				android.R.color.transparent));

		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int progress) {
				PhotoViewActivity.this.setProgress(progress * 100);
			}
		});
	}
}
