package com.metaisle.photosync.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;

import com.metaisle.util.Util;

public class Provider extends ContentProvider {
	private static final String AUTHORITY = "com.metaisle.photosync.data.Provider";

	private static final int MATCH_ALBUM = 111;
	private static final int MATCH_ALBUM_ID = 112;

	private static final int MATCH_PHOTO = 121;
	private static final int MATCH_PHOTO_ID = 122;

	private static final int MATCH_UPLOAD = 131;
	private static final int MATCH_UPLOAD_ID = 132;

	private static final int MATCH_FEED = 141;
	private static final int MATCH_FEED_ID = 142;

	public static final Uri ALBUM_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + AlbumTable.ALBUM_TABLE);
	public static final Uri PHOTO_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + PhotoTable.PHOTO_TABLE);
	public static final Uri UPLOAD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + UploadTable.UPLOAD_TABLE);
	public static final Uri FEED_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + PostTable.POST_TABLE);

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, AlbumTable.ALBUM_TABLE, MATCH_ALBUM);
		sURIMatcher.addURI(AUTHORITY, AlbumTable.ALBUM_TABLE + "/#",
				MATCH_ALBUM_ID);

		sURIMatcher.addURI(AUTHORITY, PhotoTable.PHOTO_TABLE, MATCH_PHOTO);
		sURIMatcher.addURI(AUTHORITY, PhotoTable.PHOTO_TABLE + "/#",
				MATCH_PHOTO_ID);

		sURIMatcher.addURI(AUTHORITY, UploadTable.UPLOAD_TABLE, MATCH_UPLOAD);
		sURIMatcher.addURI(AUTHORITY, UploadTable.UPLOAD_TABLE + "/#",
				MATCH_UPLOAD_ID);

		sURIMatcher.addURI(AUTHORITY, PostTable.POST_TABLE, MATCH_FEED);
		sURIMatcher.addURI(AUTHORITY, PostTable.POST_TABLE + "/#",
				MATCH_FEED_ID);
	}

	private Database database;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;

		String id = uri.getLastPathSegment();

		switch (uriType) {
		case MATCH_ALBUM:
			rowsDeleted = sqlDB.delete(AlbumTable.ALBUM_TABLE, selection,
					selectionArgs);
			break;

		case MATCH_ALBUM_ID:
			if (TextUtils.isEmpty(selection))
				selection = AlbumTable.ALBUM_ID + "=" + id;
			else
				selection = selection + " AND " + AlbumTable.ALBUM_ID + "="
						+ id;
			rowsDeleted = sqlDB.delete(AlbumTable.ALBUM_TABLE, selection,
					selectionArgs);
			break;

		case MATCH_PHOTO:
			rowsDeleted = sqlDB.delete(PhotoTable.PHOTO_TABLE, selection,
					selectionArgs);
			break;
		case MATCH_PHOTO_ID:
			if (TextUtils.isEmpty(selection))
				selection = PhotoTable.PHOTO_ID + "=" + id;
			else
				selection = selection + " AND " + PhotoTable.PHOTO_ID + "="
						+ id;
			rowsDeleted = sqlDB.delete(PhotoTable.PHOTO_TABLE, selection,
					selectionArgs);
			break;

		case MATCH_UPLOAD:
			rowsDeleted = sqlDB.delete(UploadTable.UPLOAD_TABLE, selection,
					selectionArgs);
			break;
		case MATCH_UPLOAD_ID:
			if (TextUtils.isEmpty(selection))
				selection = PhotoTable.PHOTO_ID + "=" + id;
			else
				selection = selection + " AND " + PhotoTable.PHOTO_ID + "="
						+ id;
			rowsDeleted = sqlDB.delete(UploadTable.UPLOAD_TABLE, selection,
					selectionArgs);
			break;

		case MATCH_FEED:
			rowsDeleted = sqlDB.delete(PostTable.POST_TABLE, selection,
					selectionArgs);
			break;
		case MATCH_FEED_ID:
			if (TextUtils.isEmpty(selection))
				selection = PostTable.POST_ID + "=" + id;
			else
				selection = selection + " AND " + PostTable.POST_ID + "=" + id;
			rowsDeleted = sqlDB.delete(UploadTable.UPLOAD_TABLE, selection,
					selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		long id = -1;
		String table_name = null;
		String where_time = null;

		// Util.log(uri.toString());
		// Util.log(values.toString());
		// Util.log("match " + match);

		switch (match) {
		case MATCH_ALBUM:
			String album_id = values.getAsString(AlbumTable.ALBUM_ID);
			id = db.insertWithOnConflict(AlbumTable.ALBUM_TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
			db.updateWithOnConflict(AlbumTable.ALBUM_TABLE, values,
					AlbumTable.ALBUM_ID + "=?", new String[] { album_id },
					SQLiteDatabase.CONFLICT_IGNORE);
			table_name = AlbumTable.ALBUM_TABLE;
			where_time = AlbumTable.UPDATED_TIME;
			break;

		case MATCH_PHOTO:
			String photo_id = values.getAsString(PhotoTable.PHOTO_ID);
			id = db.insertWithOnConflict(PhotoTable.PHOTO_TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
			db.updateWithOnConflict(PhotoTable.PHOTO_TABLE, values,
					PhotoTable.PHOTO_ID + "=?", new String[] { photo_id },
					SQLiteDatabase.CONFLICT_IGNORE);
			table_name = PhotoTable.PHOTO_TABLE;
			where_time = PhotoTable.UPDATED_TIME;
			break;

		case MATCH_UPLOAD:
			id = db.insertWithOnConflict(UploadTable.UPLOAD_TABLE, null,
					values, SQLiteDatabase.CONFLICT_IGNORE);
			table_name = UploadTable.UPLOAD_TABLE;
			where_time = UploadTable.DATE_TAKEN;
			break;

		case MATCH_FEED:
			String post_id = values.getAsString(PostTable.POST_ID);
			id = db.insertWithOnConflict(PostTable.POST_TABLE, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
			id = db.updateWithOnConflict(PostTable.POST_TABLE, values,
					PostTable.POST_ID + "=?", new String[] { post_id },
					SQLiteDatabase.CONFLICT_IGNORE);
			table_name = PostTable.POST_TABLE;
			where_time = PostTable.UPDATED_TIME;
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		// Clean up.
		long rec = DatabaseUtils.queryNumEntries(db, table_name);
		if (rec > Prefs.MAX_DB_RECORDS) {
			Time now = new Time();
			now.setToNow();
			// Delete entries more then 1 week old.
			// 7 * 24 * 60 * 60 * 1000
			Util.log("Clean up Database");
			db.delete(table_name, where_time + "<?", new String[] { String
					.valueOf(now.toMillis(true) - 604800000L) });
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(table_name + "/" + id);
	}

	@Override
	public boolean onCreate() {
		database = new Database(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Cursor cursor = null;

		String id = uri.getLastPathSegment();

		switch (match) {
		case MATCH_ALBUM:
			queryBuilder.setTables(AlbumTable.ALBUM_TABLE);
			break;
		case MATCH_ALBUM_ID:
			queryBuilder.setTables(AlbumTable.ALBUM_TABLE);
			queryBuilder.appendWhere(AlbumTable.ALBUM_ID + "=" + id);
			break;

		case MATCH_PHOTO:
			queryBuilder.setTables(PhotoTable.PHOTO_TABLE);
			break;
		case MATCH_PHOTO_ID:
			queryBuilder.setTables(PhotoTable.PHOTO_TABLE);
			queryBuilder.appendWhere(PhotoTable.PHOTO_ID + "=" + id);
			break;

		case MATCH_UPLOAD:
			queryBuilder.setTables(UploadTable.UPLOAD_TABLE);
			break;
		case MATCH_UPLOAD_ID:
			queryBuilder.setTables(UploadTable.UPLOAD_TABLE);
			queryBuilder.appendWhere(UploadTable._ID + "=" + id);
			break;

		case MATCH_FEED:
			queryBuilder.setTables(PostTable.POST_TABLE);
			break;
		case MATCH_FEED_ID:
			queryBuilder.setTables(PostTable.POST_TABLE);
			queryBuilder.appendWhere(PostTable.POST_ID + "=" + id);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		db = database.getWritableDatabase();
		// Util.log("" + queryBuilder.getTables());
		// Util.log("" + Arrays.toString(projection));
		// Util.log("" + selection);
		// Util.log("" + Arrays.toString(selectionArgs));
		cursor = queryBuilder.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int match = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();

		String id = uri.getLastPathSegment();
		int rowsUpdated = 0;
		switch (match) {

		case MATCH_UPLOAD_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(UploadTable.UPLOAD_TABLE, values,
						UploadTable.MEDIASTORE_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(UploadTable.UPLOAD_TABLE, values,
						UploadTable.MEDIASTORE_ID + "=" + id + " and "
								+ selection, selectionArgs);
			}
			break;

		case MATCH_ALBUM_ID:
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(AlbumTable.ALBUM_TABLE, values,
						AlbumTable.ALBUM_ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(UploadTable.UPLOAD_TABLE, values,
						AlbumTable.ALBUM_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

}
