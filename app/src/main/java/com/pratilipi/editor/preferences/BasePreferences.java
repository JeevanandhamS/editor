package com.pratilipi.editor.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import static android.content.Context.MODE_PRIVATE;
import static com.pratilipi.editor.utils.AppConstants.EMPTY;
import static com.pratilipi.editor.utils.AppConstants.INVALID_ID;
import static com.pratilipi.editor.utils.AppConstants.ZERO;

public class BasePreferences {

    protected SharedPreferences mSharedPrefs;
    
    protected SharedPreferences.Editor mPrefsEditor;

    public BasePreferences(Context context, String name) {
        mSharedPrefs = context.getSharedPreferences(name, MODE_PRIVATE);
        mPrefsEditor = mSharedPrefs.edit();
    }

    public String getString(String key) {
        return mSharedPrefs.getString(key, EMPTY);
    }

    public int getInt(String key) {
        return mSharedPrefs.getInt(key, INVALID_ID);
    }

    public long getLong(String key) {
        return mSharedPrefs.getLong(key, ZERO);
    }

    public boolean getBoolean(String key) {
        return mSharedPrefs.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPrefs.getBoolean(key, defaultValue);
    }

    public void setString(String key, String value) {
        mPrefsEditor.putString(key, value);
        applyEditor();
    }

    public void setLongData(String key, long value) {
        mPrefsEditor.putLong(key, value);
        applyEditor();
    }

    public void setInt(String key, int value) {
        mPrefsEditor.putInt(key, value);
        applyEditor();
    }

    public void setBoolean(String key, boolean value) {
        mPrefsEditor.putBoolean(key, value);
        applyEditor();
    }

    public void saveObject(String tag, Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        mPrefsEditor.putString(tag, json);
        mPrefsEditor.commit();
    }

    public Object getObject(String tag, Type name) {
        Gson gson = new Gson();
        String json = getString(tag);
        return gson.fromJson(json, name);
    }

    public void clear(String key) {
        mPrefsEditor.remove(key);
        applyEditor();
    }

    public void clearAll() {
        mPrefsEditor.clear();
        applyEditor();
    }

    private void applyEditor() {
        mPrefsEditor.apply();
    }
}