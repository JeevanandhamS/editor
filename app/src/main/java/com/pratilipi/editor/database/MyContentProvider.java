package com.pratilipi.editor.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.pratilipi.editor.database.DatabaseDictionary.ContentDictionary;

import static com.pratilipi.editor.database.DatabaseDictionary.AUTHORITY;

/**
 * Created by jeeva on 02/09/17.
 */
public class MyContentProvider extends ContentProvider {

	private static final int CONTENTS = 1;

	private static final int CONTENTS_ID = 2;

	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		sURIMatcher.addURI(AUTHORITY, ContentDictionary.TABLE_NAME, CONTENTS);
		sURIMatcher.addURI(AUTHORITY, ContentDictionary.TABLE_NAME + "/#", CONTENTS_ID);
	}

	private MyDbHandler mMyDbHandler;

	@Override
	public boolean onCreate() {
		mMyDbHandler = MyDbHandler.newInstance(getContext());
		return false;
	}

	@Nullable
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ContentDictionary.TABLE_NAME);

		int uriType = sURIMatcher.match(uri);

		switch (uriType) {
			case CONTENTS_ID:
				queryBuilder.appendWhere(ContentDictionary.PAGE_ID + "="
						+ uri.getLastPathSegment());
				break;
			case CONTENTS:
				break;
			default:
				throw new IllegalArgumentException("Unknown URI");
		}

		Cursor cursor = queryBuilder.query(mMyDbHandler.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Nullable
	@Override
	public String getType(@NonNull Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
		SQLiteDatabase sqlDB = mMyDbHandler.getWritableDatabase();

		long id = 0;
		switch (sURIMatcher.match(uri)) {
			case CONTENTS:
				id = sqlDB.insert(ContentDictionary.TABLE_NAME, null, values);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(ContentDictionary.TABLE_NAME + "/" + id);
	}

	@Override
	public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
		SQLiteDatabase sqlDB = mMyDbHandler.getWritableDatabase();
		int rowsDeleted = 0;

		switch (sURIMatcher.match(uri)) {
			case CONTENTS:
				rowsDeleted = sqlDB.delete(ContentDictionary.TABLE_NAME,
						selection,
						selectionArgs);
				break;

			case CONTENTS_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsDeleted = sqlDB.delete(ContentDictionary.TABLE_NAME,
							ContentDictionary.PAGE_ID + "=" + id,
							null);
				} else {
					rowsDeleted = sqlDB.delete(ContentDictionary.TABLE_NAME,
							ContentDictionary.PAGE_ID + "=" + id
									+ " and " + selection,
							selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
		SQLiteDatabase sqlDB = mMyDbHandler.getWritableDatabase();

		int rowsUpdated = 0;

		switch (sURIMatcher.match(uri)) {
			case CONTENTS:
				rowsUpdated = sqlDB.update(ContentDictionary.TABLE_NAME,
						values,
						selection,
						selectionArgs);
				break;
			case CONTENTS_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated =
							sqlDB.update(ContentDictionary.TABLE_NAME,
									values,
									ContentDictionary.PAGE_ID + "=" + id,
									null);
				} else {
					rowsUpdated =
							sqlDB.update(ContentDictionary.TABLE_NAME,
									values,
									ContentDictionary.PAGE_ID + "=" + id
											+ " and "
											+ selection,
									selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

    @Override
    public void shutdown() {
        super.shutdown();
    }
}