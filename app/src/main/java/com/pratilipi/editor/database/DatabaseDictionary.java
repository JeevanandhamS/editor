package com.pratilipi.editor.database;

import android.net.Uri;

/**
 * Created by jeeva on 02/09/17.
 */
public interface DatabaseDictionary {

    String AUTHORITY = "com.pratilipi.editor.database.MyContentProvider";

    Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ContentDictionary.TABLE_NAME);

    interface ContentDictionary {
        String TABLE_NAME = "pages";
        String PAGE_ID = "pageId";
        String PAGE_CONTENT = "content";
    }
}