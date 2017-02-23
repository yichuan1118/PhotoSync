package com.metaisle.photosync.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.metaisle.util.Util;


public class PhotoTable implements BaseColumns{
	public static final String PHOTO_TABLE = "photo_table";

	public static final String PHOTO_ID= "photo_id";
	public static final String FROM_ID = "from_id";
	public static final String PHOTO_NAME = "photo_name";
	public static final String PICTURE = "picture";
	public static final String SOURCE = "source";
	public static final String HEIGHT = "height";
	public static final String WIDTH = "width";
	public static final String CREATED_TIME = "created_time";
	public static final String CREATED_YEAR = "created_year";
	public static final String CREATED_MONTH = "created_month";
	public static final String CREATED_DAY = "created_day";
	public static final String POSITION = "position";
	public static final String UPDATED_TIME = "updated_time";
	public static final String UPDATED_YEAR = "updated_year";
	public static final String UPDATED_MONTH = "updated_month";
	public static final String UPDATED_DAY = "updated_day";	
	
	// Derived fields
	public static final String ALBUM_ID= "album_id";
	
	
	
	public static void onCreate(SQLiteDatabase db){
		Util.log("Create table " + PHOTO_TABLE);
		db.execSQL("CREATE TABLE " + PHOTO_TABLE + " (" 
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ PHOTO_ID + " INTEGER UNIQUE NOT NULL," 
				+ FROM_ID + " INTEGER NOT NULL," 
				+ PHOTO_NAME + " TEXT," 
				+ PICTURE + " TEXT NOT NULL," 
				+ SOURCE + " TEXT NOT NULL," 
				+ HEIGHT + " INTEGER NOT NULL," 
				+ WIDTH + " INTEGER NOT NULL," 
				+ CREATED_TIME + " INTEGER NOT NULL,"
				+ CREATED_YEAR + " INTEGER NOT NULL,"
				+ CREATED_MONTH + " INTEGER NOT NULL,"
				+ CREATED_DAY + " INTEGER NOT NULL,"
				+ POSITION + " INTEGER NOT NULL,"
				+ UPDATED_TIME + " INTEGER NOT NULL,"
				+ UPDATED_YEAR + " INTEGER NOT NULL,"
				+ UPDATED_MONTH + " INTEGER NOT NULL,"
				+ UPDATED_DAY + " INTEGER NOT NULL,"
				+ ALBUM_ID + " INTEGER NOT NULL"
				+ ");");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Util.log("onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + PHOTO_TABLE);
		onCreate(db);
	}
}
