package com.pratilipi.editor;

/**
 * Created by jeeva on 04/09/17.
 */
public interface EditorView {

    void showPageSavedMessage();

    void showPageDiscardMessage();

    void setEditorContent(String htmlContent);

    void setWordCount(int wordCount);

    void navigateToPreview(int pageId);

    void startSync();
}