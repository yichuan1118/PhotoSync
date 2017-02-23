package com.metaisle.photosync.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.metaisle.util.Util;


public class AlbumTable implements BaseColumns{
	public static final String ALBUM_TABLE = "album_table";

	public static final String ALBUM_ID= "album_id";
	public static final String FROM_ID = "from_id";
	public static final String FROM_NAME = "from_name";
	public static final String ALBUM_NAME = "album_name";
	public static final String COVER_ID = "cover_id";
	public static final String PRIVACY = "privacy";
	public static final String COUNT = "count";
	public static final String TYPE = "type";
	public static final String CREATED_TIME = "created_time";
	public static final String UPDATED_TIME = "updated_time";
	public static final String CAN_UPLOAD = "can_upload";
	
	//Comments?
	
	
	public static void onCreate(SQLiteDatabase db){
		Util.log("Create table " + ALBUM_TABLE);
		db.execSQL("CREATE TABLE " + ALBUM_TABLE + " (" 
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ALBUM_ID + " INTEGER UNIQUE NOT NULL," 
				+ FROM_ID + " INTEGER NOT NULL," 
				+ FROM_NAME + " TEXT NOT NULL," 
				+ ALBUM_NAME + " TEXT NOT NULL," 
				+ COVER_ID + " INTEGER," 
				+ PRIVACY + " TEXT NOT NULL," 
				+ COUNT + " INTEGER," 
				+ TYPE + " TEXT NOT NULL," 
				+ CREATED_TIME + " INTEGER NOT NULL,"
				+ UPDATED_TIME + " INTEGER NOT NULL,"
				+ CAN_UPLOAD + " BOOLEAN NOT NULL"
				+ ");");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Util.log("onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE);
		onCreate(db);
	}
}
