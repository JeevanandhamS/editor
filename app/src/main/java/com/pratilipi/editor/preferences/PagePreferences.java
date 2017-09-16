package com.pratilipi.editor.preferences;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.pratilipi.editor.R;
import com.pratilipi.editor.utils.AppConstants.SharedKeys;

import java.util.ArrayList;
import java.util.List;

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

    public void saveSyncId(String pageId) {
        List<String> syncList = getSyncList();
        if(!syncList.contains(pageId)) {
            syncList.add(pageId);
            saveSyncList(syncList);
        }
    }

    public void deleteSyncId(String pageId) {
        List<String> syncList = getSyncList();
        if(syncList.contains(pageId)) {
           syncList.remove(pageId);
            saveSyncList(syncList);
        }
    }

    private void saveSyncList(List<String> syncList) {
        saveObject(SharedKeys.SYNC_LIST, syncList);
    }

    public List<String> getSyncList() {
        List<String> syncList = (List<String>) getObject(SharedKeys.SYNC_LIST,
                new TypeToken<List<String>>() {
                }.getType());
        return null == syncList ? new ArrayList<String>() : syncList;
    }
}