package com.metaisle.photosync.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.metaisle.util.Util;

public class UploadTable implements BaseColumns{
	public static final String UPLOAD_TABLE = "upload_table";

	public static final String MEDIASTORE_ID = "mediastore_id";
	public static final String PATH = "path";
	public static final String MIME_TYPE = "mime_type";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String DATE_TAKEN= "date_taken";
	public static final String ORIENTATION = "orientation";
	public static final String SIZE = "size";
	
	public static final String RETRIED = "retried";
	public static final String START_TIME = "start_time";
	public static final String FINISH_TIME = "finish_time";
	

	
	public static void onCreate(SQLiteDatabase db){
		Util.log("Create table " + UPLOAD_TABLE);
		db.execSQL("CREATE TABLE " + UPLOAD_TABLE + " (" 
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ MEDIASTORE_ID + " INTEGER UNIQUE NOT NULL," 
				+ PATH + " TEXT NOT NULL," 
				+ MIME_TYPE + " TEXT NOT NULL," 
				+ LATITUDE + " REAL," 
				+ LONGITUDE + " REAL," 
				+ DATE_TAKEN + " INTEGER NOT NULL," 
				+ ORIENTATION + " INTEGER NOT NULL," 
				+ SIZE + " INTEGER NOT NULL," 
				+ RETRIED + " INTEGER NOT NULL," 
				+ START_TIME + " INTEGER," 
				+ FINISH_TIME + " INTEGER" 
				+ ");");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Util.log("onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + UPLOAD_TABLE);
		onCreate(db);
	}
}
