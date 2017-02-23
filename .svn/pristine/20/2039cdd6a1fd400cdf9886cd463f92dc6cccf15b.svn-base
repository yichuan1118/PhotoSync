package com.metaisle.photosync.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.metaisle.util.Util;

public class Database extends SQLiteOpenHelper {
	
	public static final String DB_NAME = "photosync.db";
	public static final int DB_VERSION = 6;
	
	public Database(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		AlbumTable.onCreate(db);
		PhotoTable.onCreate(db);
		UploadTable.onCreate(db);
		PostTable.onCreate(db);
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Util.log("Upgrade DB");
		AlbumTable.onUpgrade(db, oldVersion, newVersion);
		PhotoTable.onUpgrade(db, oldVersion, newVersion);
		UploadTable.onUpgrade(db, oldVersion, newVersion);
		PostTable.onUpgrade(db, oldVersion, newVersion);
	}
}
