package com.metaisle.photosync.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.metaisle.util.Util;

public class PostTable implements BaseColumns {
	public static final String POST_TABLE = "post_table";

	public static final String POST_ID = "post_id";
	public static final String FROM_ID = "from_id";
	public static final String FROM_NAME = "from_name";

	public static final String MESSAGE = "message";
	// ---------------------------------------------
	public static final String STORY = "story";
	public static final String LINK = "link";
	public static final String ALBUM_ID = "album_id";
	// ---------------------------------------------
	public static final String PICTURE = "picture";
	public static final String PICTURE_ID = "picture_id";// "object_id"
	
	public static final String COMMENTS_COUNT = "comments_count";
	public static final String LIKES_COUNT = "likes_count";

	public static final String CREATED_TIME = "created_time";
	public static final String CREATED_YEAR = "created_year";
	public static final String CREATED_MONTH = "created_month";
	public static final String CREATED_DAY = "created_day";
	public static final String UPDATED_TIME = "updated_time";
	public static final String UPDATED_YEAR = "updated_year";
	public static final String UPDATED_MONTH = "updated_month";
	public static final String UPDATED_DAY = "updated_day";
	
	
	
	public static void onCreate(SQLiteDatabase db){
		Util.log("Create table " + POST_TABLE);
		db.execSQL("CREATE TABLE " + POST_TABLE + " (" 
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ POST_ID + " INTEGER UNIQUE NOT NULL," 
				+ FROM_ID + " INTEGER NOT NULL," 
				+ FROM_NAME + " TEXT NOT NULL," 
				+ MESSAGE + " TEXT," 
				+ STORY + " TEXT," 
				+ LINK + " TEXT NOT NULL," 
				+ ALBUM_ID + " INTEGER NOT NULL," 
				+ PICTURE + " TEXT NOT NULL," 
				+ PICTURE_ID + " INTEGER NOT NULL," 
				+ COMMENTS_COUNT + " INTEGER NOT NULL," 
				+ LIKES_COUNT + " INTEGER NOT NULL," 
				+ CREATED_TIME + " INTEGER NOT NULL,"
				+ CREATED_YEAR + " INTEGER NOT NULL,"
				+ CREATED_MONTH + " INTEGER NOT NULL,"
				+ CREATED_DAY + " INTEGER NOT NULL,"
				+ UPDATED_TIME + " INTEGER NOT NULL,"
				+ UPDATED_YEAR + " INTEGER NOT NULL,"
				+ UPDATED_MONTH + " INTEGER NOT NULL,"
				+ UPDATED_DAY + " INTEGER NOT NULL"
				+ ");");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Util.log("onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + POST_TABLE);
		onCreate(db);
	}

	
}
