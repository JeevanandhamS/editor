package com.pratilipi.editor.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.pratilipi.editor.database.DatabaseDictionary.ContentDictionary;

import static com.pratilipi.editor.database.DatabaseDictionary.CONTENT_URI;

/**
 * Created by jeeva on 02/09/17.
 */
public class MyDbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "contentDB.db";

	private ContentResolver mContentResolver;

	private MyDbHandler(Context context) {
		super(context, DATABASE_NAME, new LeaklessCursorFactory(), DATABASE_VERSION);
		mContentResolver = context.getContentResolver();
	}

	public static MyDbHandler newInstance(Context context) {
		return new MyDbHandler(context);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String CREATE_CONTENTS_TABLE = "CREATE TABLE " + ContentDictionary.TABLE_NAME
				+ "("
				+ ContentDictionary.PAGE_ID + " INTEGER PRIMARY KEY,"
				+ ContentDictionary.PAGE_CONTENT + " TEXT"
				+ ")";
		sqLiteDatabase.execSQL(CREATE_CONTENTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}

	public int addContent(Page page) {
		ContentValues values = new ContentValues();
		values.put(ContentDictionary.PAGE_CONTENT, page.getContent());

		Uri uri = mContentResolver.insert(CONTENT_URI, values);
		return Integer.parseInt(uri.getLastPathSegment());
	}

	public void updateContent(Page page) {
		ContentValues values = new ContentValues();
		values.put(ContentDictionary.PAGE_CONTENT, page.getContent());

		String selection = ContentDictionary.PAGE_ID + " = \"" + page.getPageId() + "\"";

		mContentResolver.update(CONTENT_URI, values, selection, null);
	}

	public Page getContent(int contentId) {
		String[] projection = {
				ContentDictionary.PAGE_ID,
				ContentDictionary.PAGE_CONTENT
		};

		String selection = ContentDictionary.PAGE_ID + " = \"" + contentId + "\"";

		Cursor cursor = mContentResolver.query(CONTENT_URI,
				projection, selection, null,
				null);

		Page page = new Page();
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			page.setPageId(Integer.parseInt(cursor.getString(0)));
			page.setContent(cursor.getString(1));
			cursor.close();
		} else {
			page = null;
		}

		return page;
	}

	public boolean deleteContent(int contentId) {
		String selection = ContentDictionary.PAGE_ID + " = \"" + contentId + "\"";
		return mContentResolver.delete(CONTENT_URI, selection, null) > 0;
	}
}