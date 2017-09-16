package com.pratilipi.editor.rest;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jeeva on 16/09/17.
 */
public class PageDto {

    @SerializedName("pratilipiId")
    private String pratilipiId;

    @SerializedName("summary")
    private String summary;

    public PageDto() {
    }

    public PageDto(String pratilipiId, String summary) {
        this.pratilipiId = pratilipiId;
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPratilipiId() {
        return pratilipiId;
    }

    public void setPratilipiId(String pratilipiId) {
        this.pratilipiId = pratilipiId;
    }
}