package com.metaisle.photosync.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.metaisle.photosync.R;
import com.metaisle.photosync.data.Prefs;

public class SharePhotoFragment extends SherlockFragment {
	public SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = getActivity().getSharedPreferences(Prefs.PREFS_NAME,
				Context.MODE_PRIVATE);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_share_photo, null);

		SharePhotoAdapter adapter = new SharePhotoAdapter(this);
		ListView lv = (ListView) v.findViewById(R.id.fragment_share_photo_list);

		lv.setAdapter(adapter);
		return v;
	}
}
