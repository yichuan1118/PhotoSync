package com.metaisle.photosync.data;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.metaisle.util.Util;

public class CommentsTable  implements BaseColumns {
	public static final String COMMENTS_TABLE = "comment_table";
	
	public static final String POST__ID = "post__id";

	public static final String COMMENT_ID = "comment_id";
	public static final String FROM_ID = "from_id";
	public static final String FROM_NAME = "from_name";

	public static final String MESSAGE = "message";
	public static final String CAN_REMOVE = "can_remove";
	public static final String CREATED_TIME = "created_time";
	public static final String LIKE_COUNT = "like_count";
	public static final String USER_LIKES = "user_likes";
	// ---------------------------------------------
	
	
	public static void onCreate(SQLiteDatabase db){
		Util.log("Create table " + COMMENTS_TABLE);
		db.execSQL("CREATE TABLE " + COMMENTS_TABLE+ " (" 
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ POST__ID + " INTEGER NOT NULL," 
				+ COMMENT_ID + " INTEGER UNIQUE NOT NULL," 
				+ FROM_ID + " INTEGER NOT NULL," 
				+ FROM_NAME + " TEXT NOT NULL," 
				+ MESSAGE + " TEXT NOT NULL," 
				+ CAN_REMOVE + " BOOLEAN NOT NULL," 
				+ CREATED_TIME + " INTEGER NOT NULL,"
				+ LIKE_COUNT + " INTEGER NOT NULL,"
				+ USER_LIKES + " BOOLEAN NOT NULL,"
				+ "FOREIGN KEY ("+ POST__ID +") REFERENCES "
				+ PostTable.POST_TABLE + " (" + PostTable._ID + ") "
				+ ");");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Util.log("onUpgrade");
		db.execSQL("DROP TABLE IF EXISTS " + COMMENTS_TABLE);
		onCreate(db);
	}
}
