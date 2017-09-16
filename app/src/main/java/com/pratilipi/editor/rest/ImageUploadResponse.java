package com.pratilipi.editor.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeeva on 16/09/17.
 */
public class ImageUploadResponse {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}