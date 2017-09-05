package com.pratilipi.editor;

/**
 * Created by jeeva on 04/09/17.
 */
public interface EditorPresenter {

    void init(EditorView editorView);

    void onEditorUpdated(String content);

    void onSubmit(String content);

    void onSave(String content);

    void onDiscard();

    void destroy();
}