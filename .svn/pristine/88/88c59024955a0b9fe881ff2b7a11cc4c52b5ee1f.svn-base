package com.metaisle.profiler.collector.periodical;

import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.provider.Browser;

import com.metaisle.profiler.collector.PeriodicalCollector;
import com.metaisle.util.Util;

public class BrowserHistory extends PeriodicalCollector {
	Cursor mCur;
	private static final String TAG = "BrowserHistory";

	public BrowserHistory(String filename, Context mContext) throws IOException {
		super(filename, mContext);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isEnableThread() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void _Start() {
		// TODO Auto-generated method stub
		GetReading();
	}

	@Override
	protected void _Stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSupported() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object GetReading() {
		// TODO Auto-generated method stub
		String s;
		try {
			WriteLineWithTime("");
			mCur = mContext.getContentResolver().query(Browser.BOOKMARKS_URI,
					Browser.HISTORY_PROJECTION, null, null, null);
			mCur.moveToFirst();
			if (mCur.moveToFirst() && mCur.getCount() > 0) {
				while (mCur.isAfterLast() == false) {
					s = mCur.getString(Browser.HISTORY_PROJECTION_DATE_INDEX)
							+ ","
							+ mCur.getString(Browser.HISTORY_PROJECTION_VISITS_INDEX)
							+ ","
							+ mCur.getString(Browser.HISTORY_PROJECTION_URL_INDEX);
					WriteLine(s);
					Util.log( s);
					mCur.moveToNext();
				}
			}
			mCur.close();
		} catch (Exception ex) {
		}

		return null;
	}

	@Override
	protected String GetTAG() {
		// TODO Auto-generated method stub
		return TAG;
	}

	@Override
	protected String GetExtension() {
		// TODO Auto-generated method stub
		return "csv";
	}
}
