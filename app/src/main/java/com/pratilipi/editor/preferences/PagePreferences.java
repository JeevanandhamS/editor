package com.pratilipi.editor.preferences;

import android.content.Context;

import com.pratilipi.editor.R;
import com.pratilipi.editor.utils.AppConstants.SharedKeys;

/**
 * Created by jeeva on 04/09/17.
 */
public class PagePreferences extends BasePreferences {

    private PagePreferences(Context context) {
        super(context, context.getString(R.string.app_name));
    }

    public static PagePreferences newInstance(Context context) {
        return new PagePreferences(context);
    }

    public int getSavedPageId() {
        return getInt(SharedKeys.PAGE_ID);
    }

    public void savePageId(int pageId) {
        setInt(SharedKeys.PAGE_ID, pageId);
    }

    public void clearPageId() {
        clear(SharedKeys.PAGE_ID);
    }
}